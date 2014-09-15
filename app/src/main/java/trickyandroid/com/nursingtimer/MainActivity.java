package trickyandroid.com.nursingtimer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectViews;
import trickyandroid.com.nursingtimer.widgets.TimerLayout;
import trickyandroid.com.nursingtimer.widgets.TimerTextView;


public class MainActivity extends Activity {
    @InjectViews({R.id.feedingTimer, R.id.poopTimer, R.id.sleepTimer, R.id.otherTimer})
    List<TimerLayout> timers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStop() {
        for (TimerLayout timer : timers) {
            if (timer.getTimerStatus() == TimerTextView.TimerStatus.STARTED) {
                timer.pauseTimer();
            }
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (TimerLayout timer : timers) {
            timer.setFragmentManager(getFragmentManager());
            if (timer.getTimerStatus() == TimerTextView.TimerStatus.PAUSED) {
                timer.resumeTimer();
            }
        }
    }
}
