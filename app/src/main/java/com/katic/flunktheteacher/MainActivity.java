package com.katic.flunktheteacher;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;

    private static final int TOGGLE_SOUND = 1;
    private boolean soundEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        gameView = (GameView) findViewById(R.id.head);

        gameView.setKeepScreenOn(true);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem toggleSound = menu.add(0, TOGGLE_SOUND, 0, "Toggle Sound");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case TOGGLE_SOUND:

                /*When the user toggles the sound option, a toast is displayed that
                indicates a change; default to a string with the text “Sound On”.
                If the sound is enabled, you toggle off both the boolean and the
                text. Otherwise, you toggle both of them on. Then you display the toast.*/
                String soundEnabledText = "Sound On";
                if (soundEnabled) {
                    soundEnabled = false;
                    gameView.soundOn = false;
                    soundEnabledText = "Sound Off";
                } else {
                    soundEnabled = true;
                    gameView.soundOn = true;
                }
                Toast.makeText(this, soundEnabledText, Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

}
