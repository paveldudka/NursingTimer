package trickyandroid.com.nursingtimer.bus;

import trickyandroid.com.nursingtimer.models.TimerModel;

/**
 * Created by paveld on 10/4/14.
 */
public class TimerModelUpdatedEvent {
    public TimerModel model;
    public String timerName;

    public TimerModelUpdatedEvent(String timerName, TimerModel m) {
        this.model = m;
        this.timerName = timerName;
    }
}
