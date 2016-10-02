package trickyandroid.com.nursingtimer.di;

import javax.inject.Singleton;

import dagger.Component;
import trickyandroid.com.nursingtimer.MainActivity;
import trickyandroid.com.nursingtimer.widgets.TimerLayout;

@Singleton
@Component(modules = BusModule.class)
public interface DiGraph {
    void inject(MainActivity mainActivity);

    void inject(TimerLayout timerLayout);
}
