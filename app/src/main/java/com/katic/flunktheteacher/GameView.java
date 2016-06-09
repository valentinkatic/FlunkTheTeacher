package com.katic.flunktheteacher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Context myContext;
    private SurfaceHolder mySurfaceHolder;
    private Bitmap backgroundImg;

    private Bitmap playButtonUp;
    private Bitmap playButtonDown;

    private int screenW = 1;
    private int screenH = 1;
    private boolean running = false;
    private boolean onTitle = true;
    private FlunkTheTeacherThread thread;

    private int backgroundOrigW;
    private int backgroundOrigH;
    private float scaleW;
    private float scaleH;
    private float drawScaleW;
    private float drawScaleH;
    private Bitmap mask, mask2, mask3, mask4, mask5, mask6, mask7, mask8, mask9;
    private Bitmap head;

    private int head1x, head2x, head3x, head4x, head5x, head6x, head7x, head8x, head9x;
    private int head1y, head2y, head3y, head4y, head5y, head6y, head7y, head8y, head9y;

    private int activeHead = 0;
    private boolean headRising = true;
    private boolean headSinking = false;
    private int headRate = 1;
    private boolean headJustHit = false;

    private Bitmap whack;
    private boolean whacking = false;
    private int headsWhacked = 0;
    private int headsMissed = 0;

    private int fingerX, fingerY;

    private Paint blackPaint;

    private static SoundPool sounds;
    private static int whackSound;
    private static int missSound;
    public boolean soundOn = false;

    private boolean gameOver = false;
    private Bitmap gameOverDialog;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new FlunkTheTeacherThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {

            }
        });

        setFocusable(true);
    }

    public FlunkTheTeacherThread getThread() {
        return thread;
    }

    @SuppressWarnings("deprecation")
    class FlunkTheTeacherThread extends Thread {
        public FlunkTheTeacherThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            mySurfaceHolder = surfaceHolder;
            myContext = context;
            backgroundImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.title);
            playButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_up);
            playButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_down);
            backgroundOrigW = backgroundImg.getWidth();
            backgroundOrigH = backgroundImg.getHeight();
            sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            whackSound = sounds.load(myContext, R.raw.whack, 1);
            missSound = sounds.load(myContext, R.raw.miss, 1);
        }

        @Override
        public void run() {
            while (running) {
                Canvas c = null;
                try {
                    c = mySurfaceHolder.lockCanvas(null);
                    synchronized (mySurfaceHolder) {
                        if (!gameOver) {
                            animateHeads();
                        }
                        draw(c);
                    }
                } finally {
                    if (c != null) {
                        mySurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        private void draw(Canvas canvas) {
            try {
                canvas.drawBitmap(backgroundImg, 0, 0, null);
                playButtonDown = Bitmap.createScaledBitmap(playButtonDown,
                        (int) (playButtonDown.getWidth() * scaleW),
                        (int) (playButtonDown.getHeight() * scaleH), true);
                canvas.drawBitmap(playButtonDown, (int) 194 * drawScaleW, (int) 1008 * drawScaleH, null);


                if (!onTitle) {
                    canvas.drawBitmap(head, head1x, head1y, null);
                    canvas.drawBitmap(head, head2x, head2y, null);
                    canvas.drawBitmap(head, head3x, head3y, null);
                    canvas.drawBitmap(head, head4x, head4y, null);
                    canvas.drawBitmap(head, head5x, head5y, null);
                    canvas.drawBitmap(head, head6x, head6y, null);
                    canvas.drawBitmap(head, head7x, head7y, null);
                    canvas.drawBitmap(head, head8x, head8y, null);
                    canvas.drawBitmap(head, head9x, head9y, null);
                    canvas.drawBitmap(mask, (int) 164 * drawScaleW, (int) 900 * drawScaleH, null);
                    canvas.drawBitmap(mask2, (int) 475 * drawScaleW, (int) 900 * drawScaleH, null);
                    canvas.drawBitmap(mask3, (int) 786 * drawScaleW, (int) 900 * drawScaleH, null);
                    canvas.drawBitmap(mask4, (int) 164 * drawScaleW, (int) 1273 * drawScaleH, null);
                    canvas.drawBitmap(mask5, (int) 475 * drawScaleW, (int) 1273 * drawScaleH, null);
                    canvas.drawBitmap(mask6, (int) 786 * drawScaleW, (int) 1273 * drawScaleH, null);
                    canvas.drawBitmap(mask7, (int) 164 * drawScaleW, (int) 1650 * drawScaleH, null);
                    canvas.drawBitmap(mask8, (int) 475 * drawScaleW, (int) 1650 * drawScaleH, null);
                    canvas.drawBitmap(mask9, (int) 786 * drawScaleW, (int) 1650 * drawScaleH, null);
                    canvas.drawText("Whacked: " + Integer.toString(headsWhacked),
                            10, blackPaint.getTextSize() + 10, blackPaint);
                    canvas.drawText("Missed: " + Integer.toString(headsMissed),
                            screenW - (int) (200 * drawScaleW),
                            blackPaint.getTextSize() + 10, blackPaint);
                }
                if (whacking) {
                    canvas.drawBitmap(whack, fingerX - (whack.getWidth() / 2),
                            fingerY - (whack.getHeight() / 2), null);
                }
                if (gameOver) {
                    canvas.drawBitmap(gameOverDialog, (screenW / 2) -
                            (gameOverDialog.getWidth() / 2), (screenH / 2) -
                            (gameOverDialog.getHeight() / 2), null);
                }
            } catch (Exception e) {

            }
        }

        boolean doTouchEvent(MotionEvent event) {
            synchronized (mySurfaceHolder) {
                int eventaction = event.getAction();
                int X = (int) event.getX();
                int Y = (int) event.getY();

                switch (eventaction) {
                    case MotionEvent.ACTION_DOWN:
                        if (!gameOver) {
                            fingerX = X;
                            fingerY = Y;
                            if (!onTitle && detectHeadContact()) {
                                whacking = true;
                                if (soundOn) {
                                    AudioManager audioManager = (AudioManager)
                                            myContext.getSystemService(Context.AUDIO_SERVICE);
                                    float volume = (float)
                                            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    sounds.play(whackSound, volume, volume, 1, 0, 1);
                                }
                                headsWhacked++;
                            }
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        if (onTitle) {
                            backgroundImg = BitmapFactory.decodeResource(
                                    myContext.getResources(), R.drawable.background);
                            backgroundImg = Bitmap.createScaledBitmap(
                                    backgroundImg, screenW, screenH, true);
                            mask = BitmapFactory.decodeResource(
                                    myContext.getResources(), R.drawable.mask);
                            mask2 = BitmapFactory.decodeResource(
                                    myContext.getResources(), R.drawable.mask2);
                            mask3 = BitmapFactory.decodeResource(
                                    myContext.getResources(), R.drawable.mask3);
                            mask4 = BitmapFactory.decodeResource(
                                    myContext.getResources(), R.drawable.mask4);
                            mask5 = BitmapFactory.decodeResource(
                                    myContext.getResources(), R.drawable.mask5);
                            mask6 = BitmapFactory.decodeResource(
                                    myContext.getResources(), R.drawable.mask6);
                            mask7 = BitmapFactory.decodeResource(
                                    myContext.getResources(), R.drawable.mask7);
                            mask8 = BitmapFactory.decodeResource(
                                    myContext.getResources(), R.drawable.mask8);
                            mask9 = BitmapFactory.decodeResource(
                                    myContext.getResources(), R.drawable.mask9);
                            head = BitmapFactory.decodeResource(
                                    myContext.getResources(), R.drawable.head);
                            whack = BitmapFactory.decodeResource
                                    (myContext.getResources(), R.drawable.whack);
                            gameOverDialog = BitmapFactory.decodeResource
                                    (myContext.getResources(), R.drawable.gameover);

                            /*These lines determine the scaling variables you’ll use to resize
                            images based on how much you scaled the background. You just
                            divide the screen width/height of whatever device you’re on by
                            the original width/height of your background image.*/
                            scaleW = (float) screenW / (float) backgroundOrigW;
                            scaleH = (float) screenH / (float) backgroundOrigH;

                            /*The createScaledBitmap() method allows us to create new
                            bitmaps for your mask and mole images by multiplying their original
                            width and height by the image scaling factor.*/
                            mask = Bitmap.createScaledBitmap(mask,
                                    (int) (mask.getWidth() * scaleW),
                                    (int) (mask.getHeight() * scaleH), true);
                            mask2 = Bitmap.createScaledBitmap(mask2,
                                    (int) (mask2.getWidth() * scaleW),
                                    (int) (mask2.getHeight() * scaleH), true);
                            mask3 = Bitmap.createScaledBitmap(mask3,
                                    (int) (mask3.getWidth() * scaleW),
                                    (int) (mask3.getHeight() * scaleH), true);
                            mask4 = Bitmap.createScaledBitmap(mask4,
                                    (int) (mask4.getWidth() * scaleW),
                                    (int) (mask4.getHeight() * scaleH), true);
                            mask5 = Bitmap.createScaledBitmap(mask5,
                                    (int) (mask5.getWidth() * scaleW),
                                    (int) (mask5.getHeight() * scaleH), true);
                            mask6 = Bitmap.createScaledBitmap(mask6,
                                    (int) (mask6.getWidth() * scaleW),
                                    (int) (mask6.getHeight() * scaleH), true);
                            mask7 = Bitmap.createScaledBitmap(mask7,
                                    (int) (mask7.getWidth() * scaleW),
                                    (int) (mask7.getHeight() * scaleH), true);
                            mask8 = Bitmap.createScaledBitmap(mask8,
                                    (int) (mask8.getWidth() * scaleW),
                                    (int) (mask8.getHeight() * scaleH), true);
                            mask9 = Bitmap.createScaledBitmap(mask9,
                                    (int) (mask9.getWidth() * scaleW),
                                    (int) (mask9.getHeight() * scaleH), true);
                            head = Bitmap.createScaledBitmap(head,
                                    (int) (head.getWidth() * scaleW),
                                    (int) (head.getHeight() * scaleH), true);
                            whack = Bitmap.createScaledBitmap(whack,
                                    (int) (whack.getWidth() * scaleW),
                                    (int) (whack.getHeight() * scaleH), true);
                            gameOverDialog = Bitmap.createScaledBitmap
                                    (gameOverDialog, (int) (gameOverDialog.getWidth() * scaleW),
                                            (int) (gameOverDialog.getHeight() * scaleH), true);

                            onTitle = false;
                            pickActiveHead();
                        }
                        whacking = false;
                        if (gameOver) {
                            headsWhacked = 0;
                            headsMissed = 0;
                            activeHead = 0;
                            pickActiveHead();
                            gameOver = false;
                        }
                        break;
                }
            }
            return true;
        }

        //This line, which is invoked when the surface dimensions change,
        //is initially used to get and set the width and height of the screen.
        public void setSurfaceSize(int width, int height) {
            synchronized (mySurfaceHolder) {
                screenH = height;
                screenW = width;
                backgroundImg = Bitmap.createScaledBitmap(
                        backgroundImg, width, height, true);
                drawScaleW = (float) screenW / 1080;
                drawScaleH = (float) screenH / 1920;
                head1x = (int) (174 * drawScaleW);
                head2x = (int) (485 * drawScaleW);
                head3x = (int) (796 * drawScaleW);
                head4x = (int) (174 * drawScaleW);
                head5x = (int) (485 * drawScaleW);
                head6x = (int) (796 * drawScaleW);
                head7x = (int) (174 * drawScaleW);
                head8x = (int) (485 * drawScaleW);
                head9x = (int) (796 * drawScaleW);
                head1y = (int) (925 * drawScaleH);
                head2y = (int) (925 * drawScaleH);
                head3y = (int) (925 * drawScaleH);
                head4y = (int) (1298 * drawScaleH);
                head5y = (int) (1298 * drawScaleH);
                head6y = (int) (1298 * drawScaleH);
                head7y = (int) (1675 * drawScaleH);
                head8y = (int) (1675 * drawScaleH);
                head9y = (int) (1675 * drawScaleH);
                blackPaint = new Paint();
                blackPaint.setAntiAlias(true);
                blackPaint.setColor(Color.BLACK);
                blackPaint.setStyle(Paint.Style.STROKE);
                blackPaint.setTextAlign(Paint.Align.LEFT);
                blackPaint.setTextSize(drawScaleW * 40);
            }
        }

        public void setRunning(boolean b) {
            running = b;
        }

        private void pickActiveHead() {
            if (!headJustHit && activeHead > 0) {
                if (soundOn) {
                    AudioManager audioManager = (AudioManager)
                            myContext.getSystemService
                                    (Context.AUDIO_SERVICE);
                    float volume = (float)
                            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    sounds.play(missSound, volume, volume, 1, 0, 1);
                }
                headsMissed++;
                if (headsMissed >= 15) {
                    gameOver = true;
                }
            }
            activeHead = new Random().nextInt(7) + 1;
            headRising = true;
            headSinking = false;
            headJustHit = false;
            headRate = 1 + (int) (headsWhacked / 10);
        }

        private boolean detectHeadContact() {
            boolean contact = false;
            if (activeHead == 1 &&
                    fingerX >= head1x - (int) (150 * drawScaleW) &&
                    fingerX < head1x + (int) (150 * drawScaleW) &&
                    fingerY > head1y - (int) (50 * drawScaleH) &&
                    fingerY < (int) 900 * drawScaleH) {
                contact = true;
                headJustHit = true;
            }
            if (activeHead == 2 &&
                    fingerX >= head2x - (int) (150 * drawScaleW) &&
                    fingerX < head2x + (int) (150 * drawScaleW) &&
                    fingerY > head2y - (int) (50 * drawScaleH) &&
                    fingerY < (int) 900 * drawScaleH) {
                contact = true;
                headJustHit = true;
            }
            if (activeHead == 3 &&
                    fingerX >= head3x - (int) (150 * drawScaleW) &&
                    fingerX < head3x + (int) (150 * drawScaleW) &&
                    fingerY > head3y - (int) (50 * drawScaleH) &&
                    fingerY < (int) 900 * drawScaleH) {
                contact = true;
                headJustHit = true;
            }
            if (activeHead == 4 &&
                    fingerX >= head4x - (int) (150 * drawScaleW) &&
                    fingerX < head4x + (int) (150 * drawScaleW) &&
                    fingerY > head4y - (int) (50 * drawScaleH) &&
                    fingerY < (int) 1273 * drawScaleH) {
                contact = true;
                headJustHit = true;
            }
            if (activeHead == 5 &&
                    fingerX >= head5x - (int) (150 * drawScaleW) &&
                    fingerX < head5x + (int) (150 * drawScaleW) &&
                    fingerY > head5y - (int) (50 * drawScaleH) &&
                    fingerY < (int) 1273 * drawScaleH) {
                contact = true;
                headJustHit = true;
            }
            if (activeHead == 6 &&
                    fingerX >= head6x - (int) (150 * drawScaleW) &&
                    fingerX < head6x + (int) (150 * drawScaleW) &&
                    fingerY > head6y - (int) (50 * drawScaleH) &&
                    fingerY < (int) 1273 * drawScaleH) {
                contact = true;
                headJustHit = true;
            }
            if (activeHead == 7 &&
                    fingerX >= head7x - (int) (150 * drawScaleW) &&
                    fingerX < head7x + (int) (150 * drawScaleW) &&
                    fingerY > head7y - (int) (50 * drawScaleH) &&
                    fingerY < (int) 1650 * drawScaleH) {
                contact = true;
                headJustHit = true;
            }
            if (activeHead == 8 &&
                    fingerX >= head8x - (int) (150 * drawScaleW) &&
                    fingerX < head8x + (int) (150 * drawScaleW) &&
                    fingerY > head8y - (int) (50 * drawScaleH) &&
                    fingerY < (int) 1650 * drawScaleH) {
                contact = true;
                headJustHit = true;
            }
            if (activeHead == 9 &&
                    fingerX >= head9x - (int) (150 * drawScaleW) &&
                    fingerX < head9x + (int) (150 * drawScaleW) &&
                    fingerY > head9y - (int) (50 * drawScaleH) &&
                    fingerY < (int) 1650 * drawScaleH) {
                contact = true;
                headJustHit = true;
            }
            return contact;
        }

        private void animateHeads() {
            if (activeHead == 1) {
                if (headRising) {
                    head1y -= headRate;
                } else if (headSinking) {
                    head1y += headRate;
                }
                if (head1y >= (int) (925 * drawScaleH) || headJustHit) {
                    head1y = (int) (925 * drawScaleH);
                    pickActiveHead();
                }
                if (head1y <= (int) (775 * drawScaleH)) {
                    head1y = (int) (775 * drawScaleH);
                    headRising = false;
                    headSinking = true;
                }
            }
            if (activeHead == 2) {
                if (headRising) {
                    head2y -= headRate;
                } else if (headSinking) {
                    head2y += headRate;
                }
                if (head2y >= (int) (925 * drawScaleH) || headJustHit) {
                    head2y = (int) (925 * drawScaleH);
                    pickActiveHead();
                }
                if (head2y <= (int) (775 * drawScaleH)) {
                    head2y = (int) (775 * drawScaleH);
                    headRising = false;
                    headSinking = true;
                }
            }
            if (activeHead == 3) {
                if (headRising) {
                    head3y -= headRate;
                } else if (headSinking) {
                    head3y += headRate;
                }
                if (head3y >= (int) (925 * drawScaleH) || headJustHit) {
                    head3y = (int) (925 * drawScaleH);
                    pickActiveHead();
                }
                if (head3y <= (int) (775 * drawScaleH)) {
                    head3y = (int) (775 * drawScaleH);
                    headRising = false;
                    headSinking = true;
                }
            }
            if (activeHead == 4) {
                if (headRising) {
                    head4y -= headRate;
                } else if (headSinking) {
                    head4y += headRate;
                }
                if (head4y >= (int) (1298 * drawScaleH) || headJustHit) {
                    head4y = (int) (1298 * drawScaleH);
                    pickActiveHead();
                }
                if (head4y <= (int) (1148 * drawScaleH)) {
                    head4y = (int) (1148 * drawScaleH);
                    headRising = false;
                    headSinking = true;
                }
            }
            if (activeHead == 5) {
                if (headRising) {
                    head5y -= headRate;
                } else if (headSinking) {
                    head5y += headRate;
                }
                if (head5y >= (int) (1298 * drawScaleH) || headJustHit) {
                    head5y = (int) (1298 * drawScaleH);
                    pickActiveHead();
                }
                if (head5y <= (int) (1148 * drawScaleH)) {
                    head5y = (int) (1148 * drawScaleH);
                    headRising = false;
                    headSinking = true;
                }
            }
            if (activeHead == 6) {
                if (headRising) {
                    head6y -= headRate;
                } else if (headSinking) {
                    head6y += headRate;
                }
                if (head6y >= (int) (1298 * drawScaleH) || headJustHit) {
                    head6y = (int) (1298 * drawScaleH);
                    pickActiveHead();
                }
                if (head6y <= (int) (1148 * drawScaleH)) {
                    head6y = (int) (1148 * drawScaleH);
                    headRising = false;
                    headSinking = true;
                }
            }
            if (activeHead == 7) {
                if (headRising) {
                    head7y -= headRate;
                } else if (headSinking) {
                    head7y += headRate;
                }
                if (head7y >= (int) (1675 * drawScaleH) || headJustHit) {
                    head7y = (int) (1675 * drawScaleH);
                    pickActiveHead();
                }
                if (head7y <= (int) (1525 * drawScaleH)) {
                    head7y = (int) (1525 * drawScaleH);
                    headRising = false;
                    headSinking = true;
                }
            }
            if (activeHead == 8) {
                if (headRising) {
                    head8y -= headRate;
                } else if (headSinking) {
                    head8y += headRate;
                }
                if (head8y >= (int) (1675 * drawScaleH) || headJustHit) {
                    head8y = (int) (1675 * drawScaleH);
                    pickActiveHead();
                }
                if (head8y <= (int) (1525 * drawScaleH)) {
                    head8y = (int) (1525 * drawScaleH);
                    headRising = false;
                    headSinking = true;
                }
            }
            if (activeHead == 9) {
                if (headRising) {
                    head9y -= headRate;
                } else if (headSinking) {
                    head9y += headRate;
                }
                if (head9y >= (int) (1675 * drawScaleH) || headJustHit) {
                    head9y = (int) (1675 * drawScaleH);
                    pickActiveHead();
                }
                if (head9y <= (int) (1525 * drawScaleH)) {
                    head9y = (int) (1525 * drawScaleH);
                    headRising = false;
                    headSinking = true;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return thread.doTouchEvent(event);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRunning(false);
    }

}
