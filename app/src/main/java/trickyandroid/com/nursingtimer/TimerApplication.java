package trickyandroid.com.nursingtimer;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;
import trickyandroid.com.nursingtimer.dagger.BusModule;

/**
 * Created by paveld on 10/4/14.
 */
public class TimerApplication extends Application {
    private ObjectGraph graph;

    @Override
    public void onCreate() {
        super.onCreate();
        buildObjectGraphAndInject();
    }

    public void buildObjectGraphAndInject() {
        graph = ObjectGraph.create(new BusModule());
    }

    public void inject(Object o) {
        graph.inject(o);
    }

    public static TimerApplication get(Context context) {
        return (TimerApplication) context.getApplicationContext();
    }
}
