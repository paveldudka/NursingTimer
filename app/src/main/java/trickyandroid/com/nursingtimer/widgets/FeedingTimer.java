package trickyandroid.com.nursingtimer.widgets;

import android.content.Context;
import android.util.AttributeSet;

import trickyandroid.com.nursingtimer.R;

/**
 * Created by paveld on 9/10/14.
 */
public class FeedingTimer extends TimerLayout {
    public FeedingTimer(Context context) {
        super(context);
    }

    public FeedingTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeedingTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FeedingTimer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    int getIconResId() {
        return R.drawable.food;
    }

    @Override
    public String getTimerName() {
        return "feeding timer";
    }
}
