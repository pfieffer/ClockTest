package np.com.ravi.clocktest;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView stopwatchTextView, timerTextView;

    Button startStopButton, resetButton;

    //for stopButton watch
    long millisecondTime, startTime, timeBuff, updateTime = 0L;
    int seconds, minutes, milliSeconds;

    //for timer watch
    long timerMillisecondTime, timerStartTime, timerTimeBuff, timerUpdateTime = 0L;
    int timerSeconds, timerMinutes, timerHours;


    Handler stopwatchHandler, timerHandler;

    //shared prefs data
    //public static final String PREFS_NAME = "TimerData";
    SharedPreferences timerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("In ", "onCreate");

        timerData = getSharedPreferences("timerSharedPrefs", MODE_PRIVATE);
        //check for shared prefs value.
        if (!timerData.contains("current_timer_value_ms") || !timerData.contains("current_time_ms")) {
            Log.d("NO ", "value in shared prefs");
        } else {
            Log.d("timerData ", "does contain data");
            //these values are now old values
            Long prevTimerMillisecondTime, prevTime;

            prevTimerMillisecondTime = timerData.getLong("current_timer_value_ms", 0L);
            prevTime = timerData.getLong("current_time_ms", 0L);
            Log.d("Got", " from shared prefs " + prevTimerMillisecondTime);
            Log.d("Got", " from shared prefs " + prevTime);

            // pass currentStartTime to timeBuff of timer runnable.
            timerStartTime = SystemClock.uptimeMillis();
            timerTimeBuff = prevTimerMillisecondTime + (SystemClock.uptimeMillis() - prevTime);
            timerHandler = new Handler();
            timerHandler.post(timerRunnable);
        }

        stopwatchTextView = (TextView) findViewById(R.id.stopwatch_textView);
        timerTextView = (TextView) findViewById(R.id.timer_textView);

        startStopButton = (Button) findViewById(R.id.stopwatch_start_stop_button);
        resetButton = (Button) findViewById(R.id.stopwatch_reset_button);

        stopwatchHandler = new Handler();

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startStopButton.getText().equals("Stop")) {
                    timeBuff += millisecondTime;
                    stopwatchHandler.removeCallbacks(stopwatchRunnable);

                    //start timer on stopwatch stop
                    clearTimerTime();
                    timerStartTime = SystemClock.uptimeMillis();
                    timerHandler = new Handler();
                    timerHandler.post(timerRunnable);

                    startStopButton.setText(R.string.start);
                    resetButton.setEnabled(true);
                } else {
                    startTime = SystemClock.uptimeMillis(); //uptimeMillis() returns time in milliseconds since boot
                    stopwatchHandler.postDelayed(stopwatchRunnable, 0);


                    startStopButton.setText(R.string.stop);
                    resetButton.setEnabled(false);
                }


            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //setting variable to zero
                millisecondTime = 0L;
                startTime = 0L;
                timeBuff = 0L;
                updateTime = 0L;
                seconds = 0;
                minutes = 0;
                milliSeconds = 0;

                //setting stopwatch textview to zero
                stopwatchTextView.setText(R.string.zero_time);

            }
        });


    }

    private void clearTimerTime() {
        //clear all timer value
        timerMillisecondTime = 0L;
        timerStartTime = 0L;
        timerTimeBuff = 0L;
        timerUpdateTime = 0L;

        timerSeconds = 0;
        timerMinutes = 0;
        timerHours = 0;
    }

    public Runnable stopwatchRunnable = new Runnable() {

        public void run() {

            millisecondTime = SystemClock.uptimeMillis() - startTime;

            updateTime = timeBuff + millisecondTime;

            seconds = (int) (updateTime / 1000);

            minutes = seconds / 60;

            seconds = seconds % 60;

            milliSeconds = (int) (updateTime % 1000);

            //setting the stopwatch textview as it is running.
            stopwatchTextView.setText("" + minutes + ":"
                    + String.format("%02d", seconds) + ":"
                    + String.format("%03d", milliSeconds));

            stopwatchHandler.postDelayed(this, 0);
        }

    };


    public Runnable timerRunnable = new Runnable() {

        public void run() {

            timerMillisecondTime = SystemClock.uptimeMillis() - timerStartTime;

            timerUpdateTime = timerTimeBuff + timerMillisecondTime;

            timerSeconds = (int) (timerUpdateTime / 1000);

            timerMinutes = timerSeconds / 60;

            timerHours = timerMinutes / 60;

            timerSeconds = timerSeconds % 60;
            timerMinutes = timerMinutes % 60; //not tested, should work

            //timerMilliSeconds = (int) (timerUpdateTime % 1000);

            //setting the timer textview as it is running.
            timerTextView.setText("" + String.format("%02d", timerHours) + ":"
                    + String.format("%02d", timerMinutes) + ":"
                    + String.format("%02d", timerSeconds));

            timerHandler.postDelayed(this, 0);
        }

    };

    @Override
    protected void onStop() {
        // As per the docs, don't rely on the onDestroy() method to persist your data.
        // Use onPause() instead.

        Log.d("IN ", "onStop() called");
        Log.d("TimerTime", String.valueOf(timerUpdateTime)); //String.valueOf(timerMillisecondTime)
        Log.d("CurrentTimeMS", String.valueOf(SystemClock.uptimeMillis()));

        //putting values to shared prefs
        SharedPreferences.Editor editor = timerData.edit();
        editor.clear().apply(); //clearing content of the shared pref.
        editor.putLong("current_timer_value_ms", timerUpdateTime); //timerMilliSeconds
        editor.putLong("current_time_ms", SystemClock.uptimeMillis());

        //Also, you could try to use the method apply() instead of commit() on your SharedPreferences.
        // Editor because it saves your data faster, asynchronously, and there's no return value to handle at all.
        editor.apply();
        Log.d("WroteToPref", String.valueOf(timerData.getLong("current_timer_value_ms", 0L)));
        Log.d("WroteToPref", String.valueOf(timerData.getLong("current_time_ms", 0L)));

        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
