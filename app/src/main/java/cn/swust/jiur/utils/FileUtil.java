package cn.swust.jiur.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {
    private static String TAG = "FILE_PATH";
    private Context context;
    public FileUtil(Context context) {
        this.context = context;
    }
    public static void writeFile(InputStream in, String fileWritePath) throws IOException {
        FileOutputStream os = new FileOutputStream(fileWritePath);
        int len = 0;
        byte[] bytes = new byte[1024];
        while((len = in.read(bytes))!=-1){
            os.write(bytes,0,len);
        }
        os.flush();
        os.close();
        in.close();
    }

    public static String readFromAssets(Context context,String filename){
        try {
            InputStream inputStream = context.getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String value = "";
            while ((value = reader.readLine())!=null){
                sb.append(value);
                sb.append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getSuffix(String fileName){
        int lastIndexOf = fileName.lastIndexOf(".");
        String suffix = fileName.substring(lastIndexOf);
        return suffix;
    }
    public static boolean mkDir(String path) {
        File file = new File(path);
        if(!file.exists()){
            boolean suc = file.mkdir();
            return suc;
        }
        return true;
    }
}
