package trickyandroid.com.nursingtimer.dagger;

import javax.inject.Singleton;

import dagger.Component;
import trickyandroid.com.nursingtimer.MainActivity;
import trickyandroid.com.nursingtimer.widgets.TimerLayout;

@Singleton
@Component(modules = BusModule.class)
public interface MainComponent {
    void inject(MainActivity mainActivity);

    void inject(TimerLayout timerLayout);
}
