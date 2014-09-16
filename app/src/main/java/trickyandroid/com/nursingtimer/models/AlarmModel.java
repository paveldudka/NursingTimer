package trickyandroid.com.nursingtimer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by paveld on 9/15/14.
 */
public class AlarmModel implements Parcelable {
    boolean alarmEnabled;
    int[] alarm = new int[2];

    public AlarmModel(){}

    AlarmModel(Parcel in) {
        boolean flags[] = new boolean[1];
        in.readBooleanArray(flags);
        in.readIntArray(alarm);
    }

    public void setAlarm(int[] alarm) {
        this.alarm = alarm;
    }

    public int[] getAlarm() {
        return alarm;
    }

    public boolean isAlarmEnabled() {
        return alarmEnabled;
    }

    public void setAlarmEnabled(boolean enabled) {
        this.alarmEnabled = enabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        boolean[] flags = {alarmEnabled};
        parcel.writeBooleanArray(flags);
        parcel.writeIntArray(alarm);
    }

    public static final Parcelable.Creator<AlarmModel> CREATOR
            = new Parcelable.Creator<AlarmModel>() {
        public AlarmModel createFromParcel(Parcel in) {
            return new AlarmModel(in);
        }

        public AlarmModel[] newArray(int size) {
            return new AlarmModel[size];
        }
    };
}
