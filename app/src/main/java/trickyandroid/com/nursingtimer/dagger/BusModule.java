package trickyandroid.com.nursingtimer.dagger;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import trickyandroid.com.nursingtimer.MainActivity;
import trickyandroid.com.nursingtimer.widgets.FeedingTimer;
import trickyandroid.com.nursingtimer.widgets.OtherTimer;
import trickyandroid.com.nursingtimer.widgets.PoopTimer;
import trickyandroid.com.nursingtimer.widgets.SleepTimer;
import trickyandroid.com.nursingtimer.widgets.TimerLayout;

/**
 * Created by paveld on 10/4/14.
 */
@Module(injects = {MainActivity.class, FeedingTimer.class, PoopTimer.class, OtherTimer.class, SleepTimer.class})
public class BusModule {
    @Provides
    @Singleton
    Bus provideBus() {
        return new Bus();
    }
}
