package trickyandroid.com.nursingtimer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by paveld on 9/15/14.
 */
public class TimerModel implements Parcelable {
    long eventStartTimestamp;

    AlarmModel alarmModel = new AlarmModel();

    TimerModel(Parcel in) {
        this.eventStartTimestamp = in.readLong();
        this.alarmModel = in.readParcelable(AlarmModel.class.getClassLoader());
    }

    public TimerModel(long eventStartTimestamp) {
        this.eventStartTimestamp = eventStartTimestamp;
    }

    public void setAlarmModel(AlarmModel alarm) {
        if (alarm != null) {
            this.alarmModel = alarm;
        }
    }

    public AlarmModel getAlarmModel() {
        return alarmModel;
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
        parcel.writeParcelable(alarmModel, 0);
    }

    public static final Parcelable.Creator<TimerModel> CREATOR
            = new Parcelable.Creator<TimerModel>() {
        public TimerModel createFromParcel(Parcel in) {
            return new TimerModel(in);
        }

        public TimerModel[] newArray(int size) {
            return new TimerModel[size];
        }
    };
}
