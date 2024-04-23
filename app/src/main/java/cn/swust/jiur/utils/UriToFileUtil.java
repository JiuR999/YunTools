package cn.swust.jiur.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

public class UriToFileUtil {

    public static String getFile(Context context, Uri uri){
        String filePath = "";
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return filePath;
    }

    //非媒体文件中查找
    public static String getFilePathForNonMediaUri(Context context, Uri uri) {
        String filePath = "";
        Cursor cursor = context.getContentResolver().query(uri,new String[]{"_data"}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow("_data");
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath;
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String imagePath = "";
        // 判断 URI 的 scheme 是不是 "file"
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            imagePath = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        imagePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                        return imagePath;
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.parseLong(id));
                    imagePath = getDataColumn(context, contentUri, null, null);
                    return imagePath;
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = MediaStore.Images.Media._ID + "=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    imagePath = getDataColumn(context, contentUri, selection,selectionArgs);
                    return imagePath;
                }

            }
        }
        assert imagePath != null;
        return imagePath;
    }

    public static String getRealPathFromUri(Context context, Uri uri) {
        String filePath = "";
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果当前Uri是document类型的Uri，则通过document id处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) {
                // 使用':'分割document id以获取媒体类型和id
                String[] split = documentId.split(":");
                String mediaType = split[0];
                String mediaId = split[1];
                // 构造查询条件
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{mediaId};
                // 根据媒体类型查询媒体文件
                Uri contentUri = null;
                switch (mediaType) {
                    case "image":
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "video":
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "audio":
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "document":
                        contentUri = MediaStore.Files.getContentUri("external");
                    default:
                        break;
                }
                // 查询媒体文件并返回路径
                String column = "_data";
                String[] projection = {column};
                Cursor cursor = context.getContentResolver().query(contentUri, projection,null,null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(column);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();
                }
            } else if (isDownloadsDocument(uri)) {
                // 如果当前Uri是下载类型的Uri，则通过id处理
                String downloadId = documentId;
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(downloadId));
                filePath = getDataColumn(context, contentUri, null, null);
            } else if (isExternalStorageDocument(uri)) {
                // 如果当前Uri是外部存储类型的Uri，则通过id处理
                String[] split = documentId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果当前Uri是content类型的Uri，则直接查询媒体文件并返回路径
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果当前Uri是file类型的Uri，则直接返回文件路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = MediaStore.Images.Media.DATA;
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public void dumpImageMetaData(Context context,Uri uri) {
        Cursor cursor = context.getContentResolver()
                .query(uri, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range")
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.e("HeHe", "Display Name: " + displayName);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    size = cursor.getString(sizeIndex);
                }else {
                    size = "Unknown";
                }
                Log.e("HeHe", "Size: " + size);
            }
        }finally {
            cursor.close();
        }
    }

    public static String getFileNameFromUri(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
        String fileName = "";
        int columnIndex;
        try {
            // 构造查询语句
            String[] projection = { OpenableColumns.DISPLAY_NAME };
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // 获取 _data 列索引
                columnIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);
                // 提取文件名
                fileName = cursor.getString(columnIndex);
            }
        } catch (IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            // 记得关闭 Cursor
            if (cursor != null) {
                cursor.close();
            }
        }

        return fileName;
    }
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}

