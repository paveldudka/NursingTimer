package trickyandroid.com.nursingtimer;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;

import com.colintmiller.simplenosql.NoSQL;
import com.colintmiller.simplenosql.NoSQLEntity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import trickyandroid.com.nursingtimer.bus.TimerModelUpdatedEvent;
import trickyandroid.com.nursingtimer.databinding.ActivityMainBinding;
import trickyandroid.com.nursingtimer.models.TimerModel;
import trickyandroid.com.nursingtimer.widgets.TimerLayout;
import trickyandroid.com.nursingtimer.widgets.TimerTextView;


public class MainActivity extends Activity {

    ActivityMainBinding binding;

    @Inject
    Bus bus;

    List<TimerLayout> timers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TimerApplication.get().getGraph().inject(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initTimers();

        if (savedInstanceState == null) {
            for (final TimerLayout t : timers) {
                NoSQL.with(this).using(TimerModel.class).bucketId("timers").entityId(t.getTimerName()).retrieve(entities -> {
                    if (entities != null && !entities.isEmpty()) {
                        t.setModel(entities.get(0).getData());
                    }
                });
            }
        }
    }

    private void initTimers() {
        this.timers.add(binding.feedingTimer);
        this.timers.add(binding.poopTimer);
        this.timers.add(binding.otherTimer);
        this.timers.add(binding.sleepTimer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStop() {
        bus.unregister(this);
        for (TimerLayout timer : timers) {
            if (timer.getTimerStatus() == TimerTextView.TimerStatus.STARTED) {
                timer.pauseTimer();
            }
        }
        super.onStop();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onTimerModelUpdatedReceived(TimerModelUpdatedEvent event) {
        NoSQLEntity<TimerModel> entity = new NoSQLEntity<>("timers", String.valueOf(event.timerName));
        entity.setData(event.model);
        NoSQL.with(this).using(TimerModel.class).save(entity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
        for (TimerLayout timer : timers) {
            timer.setFragmentManager(getFragmentManager());
            if (timer.getTimerStatus() == TimerTextView.TimerStatus.PAUSED) {
                timer.resumeTimer();
            }
        }
    }
}
