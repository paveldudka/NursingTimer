package trickyandroid.com.nursingtimer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by paveld on 9/15/14.
 */
public class TimedEvent implements Parcelable {
    long eventStartTimestamp;

    TimedEvent(Parcel in) {
        this.eventStartTimestamp = in.readLong();
    }

    public TimedEvent(long eventStartTimestamp) {
        this.eventStartTimestamp = eventStartTimestamp;
    }

    public long getEventStartTimestamp() {
        return eventStartTimestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(eventStartTimestamp);
    }

    public static final Parcelable.Creator<TimedEvent> CREATOR
            = new Parcelable.Creator<TimedEvent>() {
        public TimedEvent createFromParcel(Parcel in) {
            return new TimedEvent(in);
        }

        public TimedEvent[] newArray(int size) {
            return new TimedEvent[size];
        }
    };
}
