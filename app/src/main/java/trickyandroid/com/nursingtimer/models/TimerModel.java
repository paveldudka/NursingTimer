package trickyandroid.com.nursingtimer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by paveld on 9/18/14.
 */
public class TimerModel implements Parcelable{
    TimedEvent event;
    AlarmModel alarm;

    public TimerModel(){}

    TimerModel(Parcel in) {
        event = in.readParcelable(TimedEvent.class.getClassLoader());
        alarm = in.readParcelable(AlarmModel.class.getClassLoader());
    }

    public AlarmModel getAlarmModel() {
        return alarm;
    }

    public void setAlarmModel(AlarmModel alarm) {
        this.alarm = alarm;
    }

    public TimedEvent getEvent() {
        return event;
    }

    public void setEvent(TimedEvent event) {
        this.event = event;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(event, 0);
        parcel.writeParcelable(alarm, 0);
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
