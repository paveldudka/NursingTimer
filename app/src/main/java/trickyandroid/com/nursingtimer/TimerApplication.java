package trickyandroid.com.nursingtimer;

import android.app.Application;
import android.content.Context;

import trickyandroid.com.nursingtimer.dagger.BusModule;
import trickyandroid.com.nursingtimer.dagger.DaggerMainComponent;
import trickyandroid.com.nursingtimer.dagger.MainComponent;

/**
 * Created by paveld on 10/4/14.
 */
public class TimerApplication extends Application {
    private static TimerApplication instance;
    private MainComponent graph;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        buildObjectGraphAndInject();
    }

    public void buildObjectGraphAndInject() {
        graph = DaggerMainComponent.builder()
                .busModule(new BusModule())
                .build();
    }

    public MainComponent getGraph() {
        return graph;
    }

    public static TimerApplication get() {
        return instance;
    }
}
