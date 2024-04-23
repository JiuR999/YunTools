package cn.swust.jiur.myView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

/**
 * @author ASUS
 */
public class MyImageView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener{
    private PointF start;
    private float dx,dy;
    private Matrix matrix2,saveMatric;
    private int width,height;
    private float mImageWidth;
    private float mImageHeight;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    /**
     * Remember some things for zooming
     */
    PointF mid = new PointF();
    float oldDist = 1f;
    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
        start = new PointF();
        this.width = getWidth();
        this.height = getHeight();
        matrix2 = new Matrix();
        saveMatric = new Matrix();
        setOnTouchListener(this);
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        ImageView imageView = (ImageView) view;
        saveMatric.set(getImageMatrix());
        float[] values=new float[9];
        saveMatric.getValues(values);
        mImageWidth=(getWidth()+values[Matrix.MTRANS_X])/values[Matrix.MSCALE_X];
        mImageHeight=(getHeight()+values[Matrix.MTRANS_Y])/values[Matrix.MSCALE_Y];
        //mImageHeight=(getHeight()+values[Matrix.MTRANS_Y]*2)/values[Matrix.MSCALE_Y];
        switch (motionEvent.getAction()&MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                saveMatric.set(matrix2);
                this.start.x = motionEvent.getX();
                this.start.y= motionEvent.getY();
                Log.d("DOWN",start.x+"=="+start.y);
                mode = DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    dx = motionEvent.getX()-start.x;
                    dy = motionEvent.getY()-start.y;
                    Log.d("TRANS1",getWidth()+"");
                    Log.d("TRANS",mImageHeight+"=="+mImageWidth);
                    matrix2.postTranslate(dx/10,dy/10);
                }
                else if (mode == ZOOM) {
                    float newDist = spacing(motionEvent);
                    Log.d("ZOOM", "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix2.set(saveMatric);
                        float scale = (float) (newDist / oldDist);
                        if(scale<5f){
                            matrix2.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                }
                setImageMatrix(matrix2);
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;
                saveMatric.set(matrix2);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(motionEvent);
                Log.d("ZOOM", "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    saveMatric.set(matrix2);
                    midPoint(mid, motionEvent);
                    mode = ZOOM;
                    Log.d("ZOOM", "mode=ZOOM");
                }
                break;
            default:
                break;
        }
        return true;
    }
    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        if(event.getPointerCount()==2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }
        return 0;
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setCenter();
        //设置完图片后，获取该图片的坐标变换矩阵
        saveMatric.set(getImageMatrix());
        float[] values=new float[9];
        saveMatric.getValues(values);
        //图片宽度为屏幕宽度除缩放倍数
        mImageWidth=getWidth()/values[Matrix.MSCALE_X];
        mImageHeight=(getHeight()+values[Matrix.MTRANS_Y])/values[Matrix.MSCALE_Y];
       // mImageHeight=(getHeight()+values[Matrix.MTRANS_Y]*2)/values[Matrix.MSCALE_Y];
    }
    private void setCenter() {
        //把图片转变为bitmap 然后获取图片的高宽度
        BitmapDrawable bitmapDrawable = (BitmapDrawable)getDrawable();
        Bitmap bitmap=bitmapDrawable.getBitmap();
        Matrix matrixImags = getImageMatrix();
        //2把矩阵移动 （ postTranslate）         这里就用到了我们获取的屏幕高宽
        //屏幕高，宽度 /2- 图片高，宽/2
        matrixImags.postTranslate(((width /2)-(bitmap.getWidth()/2)), ((height/2)-(bitmap.getHeight()/2)));
        //在把变化后的矩阵给设置进去
        setImageMatrix(matrixImags);
    }
}
