package com.katic.flunktheteacher;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;

    private static final int TOGGLE_SOUND = 1;
    private boolean soundEnabled = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        gameView = (GameView) findViewById(R.id.head);

        gameView.setKeepScreenOn(true);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

}
