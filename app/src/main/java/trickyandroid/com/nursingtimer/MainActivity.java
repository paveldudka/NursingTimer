package trickyandroid.com.nursingtimer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.colintmiller.simplenosql.NoSQL;
import com.colintmiller.simplenosql.NoSQLEntity;
import com.colintmiller.simplenosql.RetrievalCallback;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindViews;
import butterknife.ButterKnife;
import trickyandroid.com.nursingtimer.bus.TimerModelUpdatedEvent;
import trickyandroid.com.nursingtimer.models.TimerModel;
import trickyandroid.com.nursingtimer.widgets.TimerLayout;
import trickyandroid.com.nursingtimer.widgets.TimerTextView;


public class MainActivity extends Activity {

    @Inject
    Bus bus;

    @BindViews({ R.id.feedingTimer, R.id.poopTimer, R.id.sleepTimer, R.id.otherTimer })
    List<TimerLayout> timers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TimerApplication.get(this).inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            for (final TimerLayout t : timers) {
                NoSQL.with(this).using(TimerModel.class).bucketId("timers").entityId(t.getTimerName()).retrieve(new RetrievalCallback<TimerModel>() {
                    @Override
                    public void retrievedResults(List<NoSQLEntity<TimerModel>> noSQLEntities) {
                        if (noSQLEntities != null && !noSQLEntities.isEmpty()) {
                            t.setModel(noSQLEntities.get(0).getData());
                        }
                    }
                });
            }
        }
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

    @Subscribe
    public void onTimerModelUpdatedReceived(TimerModelUpdatedEvent event) {
        NoSQLEntity entity = new NoSQLEntity<TimerModel>("timers", String.valueOf(event.timerName));
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
