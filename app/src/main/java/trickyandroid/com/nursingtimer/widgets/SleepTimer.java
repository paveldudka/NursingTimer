package trickyandroid.com.nursingtimer.widgets;

import android.content.Context;
import android.util.AttributeSet;

import trickyandroid.com.nursingtimer.R;

/**
 * Created by paveld on 9/10/14.
 */
public class SleepTimer extends TimerLayout {
    public SleepTimer(Context context) {
        super(context);
    }

    public SleepTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SleepTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SleepTimer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    int getIconResId() {
        return R.drawable.sleep;
    }
}
