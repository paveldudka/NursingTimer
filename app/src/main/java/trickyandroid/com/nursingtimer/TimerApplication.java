package trickyandroid.com.nursingtimer;

import android.app.Application;

import trickyandroid.com.nursingtimer.di.BusModule;
import trickyandroid.com.nursingtimer.di.DaggerDiGraph;
import trickyandroid.com.nursingtimer.di.DiGraph;

/**
 * Created by paveld on 10/4/14.
 */
public class TimerApplication extends Application {
    private static TimerApplication instance;
    private DiGraph graph;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        buildObjectGraphAndInject();
    }

    public void buildObjectGraphAndInject() {
        graph = DaggerDiGraph.builder()
                .busModule(new BusModule())
                .build();
    }

    public DiGraph getGraph() {
        return graph;
    }

    public static TimerApplication get() {
        return instance;
    }
}
