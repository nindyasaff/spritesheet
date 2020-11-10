
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


GameView gameView;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        setContentView(gameView);
        }

class GameView extends SurfaceView implements Runnable {

    Thread gameThread = null;

    SurfaceHolder ourHolder;

    volatile boolean playing;

    Canvas canvas;
    Paint paint;

    long fps;

    Bitmap bitmapBob;

    boolean isMoving = false;

    float walkSpeedPerSecond = 250;

    float bobXPosition = 10;

    private int frameWidth = 100;
    private int frameHeight = 50;

    private int frameCount = 5;

    private int currentFrame = 0;

    private long lastFrameChangeTime = 0;

    private Rect frameToDraw = new Rect(
            0,
            0,
            frameWidth,
            frameHeight);

    RectF whereToDraw = new RectF(
            bobXPosition, 0,
            bobXPosition + frameWidth,
            frameHeight);

    public GameView(Context context) {

        super(context);
        ourHolder = getHolder();
        paint = new Paint();


        bitmapBob = Bitmap.createScaledBitmap(bitmapBob,
                frameWidth * frameCount,
                frameHeight,
                false);


    }
    @Override
    public void run() {
        while (playing) {

            long startFrameTime = System.currentTimeMillis();

            update();

            draw();

            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    public void update() {

        if(isMoving){
            bobXPosition = bobXPosition + (walkSpeedPerSecond / fps);
        }
    }
    public void getCurrentFrame(){
        long time = System.currentTimeMillis();
        if(isMoving) {
            int frameLengthInMilliseconds = 100;
            if ( time > lastFrameChangeTime + frameLengthInMilliseconds) {
                lastFrameChangeTime = time;
                currentFrame++;
                if (currentFrame >= frameCount) {
                    currentFrame = 0;
                }
            }
        }

        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
    }

    public void draw() {
        if (ourHolder.getSurface().isValid()) {

            canvas = ourHolder.lockCanvas();

            canvas.drawColor(Color.argb(255, 26, 128, 182));

            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(45);
            canvas.drawText("FPS:" + fps, 20, 40, paint);

            whereToDraw.set((int)bobXPosition,
                    0,

                    (int)bobXPosition + frameWidth,
                    frameHeight);
            getCurrentFrame();
            canvas.drawBitmap(bitmapBob,

                    ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:


                isMoving = true;
                break;

            case MotionEvent.ACTION_UP:


                isMoving = false;
                break;
        }
        return true;
    }
}

    @Override
    protected void onResume() {
        super.draw();

        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.draw();

        gameView.pause();
    }
}