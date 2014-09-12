package trickyandroid.com.nursingtimer.models;

/**
 * Created by paveld on 9/11/14.
 */
public class TimedEvent {
    private long eventTimestamp;

    public TimedEvent(long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public long getEventTimestamp() {
        return eventTimestamp;
    }
}
