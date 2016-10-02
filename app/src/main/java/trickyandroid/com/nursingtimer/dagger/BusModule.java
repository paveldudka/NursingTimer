package trickyandroid.com.nursingtimer.dagger;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BusModule {
    @Provides
    @Singleton
    Bus provideBus() {
        return new Bus();
    }
}
