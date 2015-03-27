package ru.egor_d.vircitiesbot;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Egor Danilin on 25.03.2015.
 */
public class MainActivity extends Activity {
    @InjectView(R.id.start)
    protected Button startButton;
    @InjectView(R.id.stop)
    protected Button stopButton;
    @InjectView(R.id.minutes_text)
    protected TextView minutesText;
    @InjectView(R.id.minutes_seek_bar)
    protected SeekBar minutesSeekBar;

    private int minutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        final Utils utils = new Utils(this);
        stopButton.setEnabled(utils.loadStarted());
        startButton.setEnabled(!utils.loadStarted());
        minutes = utils.loadFarmMinutes();
        minutesText.setText(String.format("%d минут", minutes));
        minutesSeekBar.setProgress(minutes - 3);
        minutesSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minutes = progress + 3;
                minutesText.setText(String.format("%d минут", minutes));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                utils.saveStarted(true);
                utils.registerAlarm(MainActivity.this, minutes * Utils.MINUTE);
                utils.saveFarmMinutes(minutes);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                utils.saveStarted(false);
                utils.unregisterAlarm(MainActivity.this);
            }
        });
    }


}
