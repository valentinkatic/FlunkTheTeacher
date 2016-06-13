package com.katic.flunktheteacher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Context myContext;
    private SurfaceHolder mySurfaceHolder;
    private Bitmap backgroundImg;

    private Bitmap playButtonUp;
    private Bitmap playButtonDown;
    private Bitmap scoreButtonUp;
    private Bitmap scoreButtonDown;
    private Bitmap settingsButtonUp;
    private Bitmap settingsButtonDown;
    private boolean playButtonPressed = false;
    private boolean scoreButtonPressed = false;
    private boolean settingsButtonPressed = false;
    private Bitmap playAgainButtonUp;
    private Bitmap playAgainButtonDown;
    private Bitmap mainMenuButtonUp;
    private Bitmap mainMenuButtonDown;

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
    private int headRate = 2;
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

    private SharedPreferences prefs;
    private String[] score;
    private String nickname;
    private int brojac = 0;

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
            scoreButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.score_button_down);
            scoreButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.score_button_up);
            settingsButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.settings_button_down);
            settingsButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.settings_button_up);
            playAgainButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.play_again_button_down);
            playAgainButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.play_again_button_up);
            mainMenuButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.main_menu_down);
            mainMenuButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.main_menu_up);

            backgroundOrigW = backgroundImg.getWidth();
            backgroundOrigH = backgroundImg.getHeight();
            sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            whackSound = sounds.load(myContext, R.raw.whack, 1);
            missSound = sounds.load(myContext, R.raw.miss, 1);

            prefs = PreferenceManager.getDefaultSharedPreferences(myContext);

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
                if (onTitle) {
                    if (playButtonPressed) {
                        canvas.drawBitmap(playButtonDown,
                                (int) 194 * drawScaleW, (int) 1008 * drawScaleH, null);
                    } else {
                        canvas.drawBitmap(playButtonUp,
                                (int) 172 * drawScaleW, (int) 986 * drawScaleH, null);
                    }

                    if (scoreButtonPressed) {
                        canvas.drawBitmap(scoreButtonDown,
                                (int) 194 * drawScaleW, (int) 1260 * drawScaleH, null);
                    } else {
                        canvas.drawBitmap(scoreButtonUp,
                                (int) 172 * drawScaleW, (int) 1238 * drawScaleH, null);
                    }

                    if (settingsButtonPressed) {
                        canvas.drawBitmap(settingsButtonDown,
                                (int) 194 * drawScaleW, (int) 1520 * drawScaleH, null);
                    } else {
                        canvas.drawBitmap(settingsButtonUp,
                                (int) 172 * drawScaleW, (int) 1498 * drawScaleH, null);
                    }
                }

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
                        if (onTitle) {
                            if (X > (screenW - playButtonUp.getWidth()) / 2 &&
                                    X < ((screenW - playButtonUp.getWidth() / 2) + playButtonUp.getWidth()) &&
                                    Y > (int) ((int) 1008 * drawScaleH) &&
                                    Y < (int) ((int) 1008 * drawScaleH) + playButtonUp.getHeight()) {
                                playButtonPressed = true;
                            }
                            if (X > (screenW - scoreButtonUp.getWidth()) / 2 &&
                                    X < ((screenW - scoreButtonUp.getWidth() / 2) + scoreButtonUp.getWidth()) &&
                                    Y > (int) ((int) 1260 * drawScaleH) &&
                                    Y < (int) ((int) 1260 * drawScaleH) + scoreButtonUp.getHeight()) {
                                scoreButtonPressed = true;
                                //onTitle = false;
                            }
                            if (X > (screenW - settingsButtonUp.getWidth()) / 2 &&
                                    X < ((screenW - settingsButtonUp.getWidth() / 2) + settingsButtonUp.getWidth()) &&
                                    Y > (int) ((int) 1520 * drawScaleH) &&
                                    Y < (int) ((int) 1520 * drawScaleH) + settingsButtonUp.getHeight()) {
                                settingsButtonPressed = true;
                                //onTitle = false;
                            }
                        }

                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        if (playButtonPressed) {
                            backgroundImg = BitmapFactory.decodeResource(
                                    myContext.getResources(), R.drawable.background);
                            backgroundImg = Bitmap.createScaledBitmap(
                                    backgroundImg, screenW, screenH, true);
                            loadMasks();
                            onTitle = false;
                            playButtonPressed = false;
                            pickActiveHead();
                        }

                        if (scoreButtonPressed) {
                            showScoreDialog();
                            scoreButtonPressed = false;
                        }

                        settingsButtonPressed = false;
                        whacking = false;
                        if (gameOver) {
                            activeHead = 0;
                            showAlertDialog();
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

                scaleW = (float) screenW / (float) backgroundOrigW;
                scaleH = (float) screenH / (float) backgroundOrigH;

                scaleButtons();
            }
        }

        public void setRunning(boolean b) {
            running = b;
        }

        private void scaleButtons() {
            playButtonDown = Bitmap.createScaledBitmap(playButtonDown,
                    (int) (playButtonDown.getWidth() * scaleW),
                    (int) (playButtonDown.getHeight() * scaleH), true);
            playButtonUp = Bitmap.createScaledBitmap(playButtonUp,
                    (int) (playButtonUp.getWidth() * scaleW),
                    (int) (playButtonUp.getHeight() * scaleH), true);
            scoreButtonDown = Bitmap.createScaledBitmap(scoreButtonDown,
                    (int) (scoreButtonDown.getWidth() * scaleW),
                    (int) (scoreButtonDown.getHeight() * scaleH), true);
            scoreButtonUp = Bitmap.createScaledBitmap(scoreButtonUp,
                    (int) (scoreButtonUp.getWidth() * scaleW),
                    (int) (scoreButtonUp.getHeight() * scaleH), true);
            settingsButtonDown = Bitmap.createScaledBitmap(settingsButtonDown,
                    (int) (settingsButtonDown.getWidth() * scaleW),
                    (int) (settingsButtonDown.getHeight() * scaleH), true);
            settingsButtonUp = Bitmap.createScaledBitmap(settingsButtonUp,
                    (int) (settingsButtonUp.getWidth() * scaleW),
                    (int) (settingsButtonUp.getHeight() * scaleH), true);
            playAgainButtonDown = Bitmap.createScaledBitmap(playAgainButtonDown,
                    (int) (playAgainButtonDown.getWidth() * scaleW * 0.5625),
                    (int) (playAgainButtonDown.getHeight() * scaleH * 0.5625), true);
            playAgainButtonUp = Bitmap.createScaledBitmap(playAgainButtonUp,
                    (int) (playAgainButtonUp.getWidth() * scaleW * 0.5625),
                    (int) (playAgainButtonUp.getHeight() * scaleH * 0.5625), true);
            mainMenuButtonDown = Bitmap.createScaledBitmap(mainMenuButtonDown,
                    (int) (mainMenuButtonDown.getWidth() * scaleW * 0.5625),
                    (int) (mainMenuButtonDown.getHeight() * scaleH * 0.5625), true);
            mainMenuButtonUp = Bitmap.createScaledBitmap(mainMenuButtonUp,
                    (int) (mainMenuButtonUp.getWidth() * scaleW * 0.5625),
                    (int) (mainMenuButtonUp.getHeight() * scaleH * 0.5625), true);
        }

        private void loadMasks() {
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
        }

        public void showAlertDialog() {

            final Dialog d = new Dialog(myContext);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.alertdialog);

            d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            d.setCancelable(false);
            d.setCanceledOnTouchOutside(false);

            final ImageButton mainMenu = (ImageButton) d.findViewById(R.id.main_menu);
            final ImageButton playAgain = (ImageButton) d.findViewById(R.id.play_again);
            final EditText editText = (EditText) d.findViewById(R.id.editText);

            RelativeLayout.LayoutParams paramsText = (RelativeLayout.LayoutParams) editText.getLayoutParams();
            paramsText.setMargins((int) (300 * scaleW), (int) (470 * scaleH), (int) (300 * scaleW), 0);
            RelativeLayout.LayoutParams paramsMain = (RelativeLayout.LayoutParams) mainMenu.getLayoutParams();
            paramsMain.setMargins((int) (180 * scaleW), (int) (807 * scaleH), 0, (int) (120 * scaleH));
            RelativeLayout.LayoutParams paramsPlay = (RelativeLayout.LayoutParams) playAgain.getLayoutParams();
            paramsPlay.setMargins(0, (int) (807 * scaleH), (int) (180 * scaleW), (int) (120 * scaleH));

            mainMenu.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    backgroundImg = BitmapFactory.decodeResource(
                            myContext.getResources(), R.drawable.title);
                    backgroundImg = Bitmap.createScaledBitmap(
                            backgroundImg, screenW, screenH, true);

                    enterScore(editText.getText().toString());

                    onTitle = true;
                    headsWhacked = 0;
                    headsMissed = 0;
                    d.dismiss();
                }
            });

            playAgain.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterScore(editText.getText().toString());

                    d.dismiss();
                    headsWhacked = 0;
                    headsMissed = 0;
                    pickActiveHead();
                }
            });

            d.show();
        }

        private void enterScore(String nickname) {
            if (nickname.equals("")) nickname = "No name";
            SharedPreferences.Editor editor = prefs.edit();
            int tempBrojac = prefs.getInt("brojac", 0);
            if (tempBrojac < 10) {
                tempBrojac++;
                editor.putInt("brojac", tempBrojac);
                editor.putString("nickname" + tempBrojac, nickname);
                editor.putInt("score" + tempBrojac, headsWhacked);
                editor.commit();
                if (tempBrojac > 1) {
                    arrange();
                }
            } else if (tempBrojac >= 10) {
                for (int i = 10; i > 0; i--) {
                    if (prefs.getInt("score" + i, 0) < headsWhacked) {
                        int tempScore = prefs.getInt("score" + i, 0);
                        String tempNick = prefs.getString("nickname" + i, "No name");
                        editor.putString("nickname" + i, nickname);
                        editor.putInt("score" + i, headsWhacked);
                        editor.putString("nickname" + (i + 1), tempNick);
                        editor.putInt("score" + (i + 1), tempScore);
                        editor.commit();
                    }
                }
            }
        }

        private void arrange() {
            SharedPreferences.Editor editor = prefs.edit();
            brojac = prefs.getInt("brojac", 0);
            for (int i = brojac - 1; i > 0; i--) {
                if (prefs.getInt("score" + i, 0) < prefs.getInt("score" + (i + 1), 0)) {
                    int tempScore = prefs.getInt("score" + i, 0);
                    String tempNick = prefs.getString("nickname" + i, "No name");
                    editor.putString("nickname" + i, prefs.getString("nickname" + (i + 1), "No name"));
                    editor.putInt("score" + i, prefs.getInt("score" + (i + 1), 0));
                    editor.putString("nickname" + (i + 1), tempNick);
                    editor.putInt("score" + (i + 1), tempScore);
                    editor.commit();
                }
            }
        }

        public void showScoreDialog() {

            AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
            ListView modeList = new ListView(myContext);
            brojac = prefs.getInt("brojac", 0);
            score = new String[brojac];
            for (int i = 0; i < brojac; i++) {
                score[i] = prefs.getString("nickname" + (i + 1), "No name") + " " + prefs.getInt("score" + (i + 1), 0);
            }
            ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(myContext, android.R.layout.simple_list_item_1, android.R.id.text1, score);

            modeList.setAdapter(modeAdapter);

            builder.setView(modeList);
            final Dialog dialog = builder.create();

            dialog.show();
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
                if (headsMissed >= 5) {
                    gameOver = true;
                }
            }
            activeHead = new Random().nextInt(9) + 1;
            headRising = true;
            headSinking = false;
            headJustHit = false;
            headRate = 2 + (int) (headsWhacked / 10);
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
