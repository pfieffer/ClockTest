package np.com.ravi.clocktest;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView stopwatch_textView;

    Button startStopButton, stopButton, resetButton;

    //for stopButton watch
    long millisecondTime, startTime, timeBuff, updateTime = 0L;
    int seconds, minutes, milliSeconds;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopwatch_textView = (TextView) findViewById(R.id.stopwatch_textView);
        startStopButton = (Button) findViewById(R.id.stopwatch_start_stop_button);
        stopButton = (Button) findViewById(R.id.stopwatch_stop_button);
        resetButton = (Button) findViewById(R.id.stopwatch_reset_button);

        handler = new Handler();

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button startButton = (Button) view;
                if (startButton.getText().equals("Stop")) {
                    timeBuff += millisecondTime;

                    handler.removeCallbacks(runnable);

                    startButton.setText(R.string.start);
                    resetButton.setEnabled(true);
                } else {
                    startTime = SystemClock.uptimeMillis(); //uptimeMillis() returns time in milliseconds since boot
                    handler.postDelayed(runnable, 0);

                    startButton.setText(R.string.stop);
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
                stopwatch_textView.setText(R.string.zero_time);

            }
        });


    }

    public Runnable runnable = new Runnable() {

        public void run() {

            millisecondTime = SystemClock.uptimeMillis() - startTime;

            updateTime = timeBuff + millisecondTime;

            seconds = (int) (updateTime / 1000);

            minutes = seconds / 60;

            seconds = seconds % 60;

            milliSeconds = (int) (updateTime % 1000);

            //setting the stopwatch textview as it is running.
            stopwatch_textView.setText("" + minutes + ":"
                    + String.format("%02d", seconds) + ":"
                    + String.format("%03d", milliSeconds));

            handler.postDelayed(this, 0);
        }

    };

}
