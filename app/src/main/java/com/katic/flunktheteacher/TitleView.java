package com.katic.flunktheteacher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;


public class TitleView extends View {

    private Bitmap backgroundImg;
    private Bitmap playButtonUp;
    private Bitmap playButtonDown;
    private boolean playButtonPressed;
    private Bitmap scoreButtonUp;
    private Bitmap scoreButtonDown;
    private boolean scoreButtonPressed;
    private Bitmap settingsButtonUp;
    private Bitmap settingsButtonDown;
    private boolean settingsButtonPressed;

    private int screenW;
    private int screenH;

    private Context myContext;

    public TitleView(Context context) {
        super(context);
        myContext = context;
        backgroundImg = BitmapFactory.decodeResource(getResources(), R.drawable.title);

        playButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_up);
        playButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_down);
        scoreButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.high_score_button_up);
        scoreButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.high_score_button_down);
        settingsButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.settings_button_up);
        settingsButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.settings_button_down);


    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenW = w;
        screenH = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(backgroundImg, 0, 0, null);

    }


}
