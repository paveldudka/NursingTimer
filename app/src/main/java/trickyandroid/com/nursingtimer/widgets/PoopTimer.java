package trickyandroid.com.nursingtimer.widgets;

import android.content.Context;
import android.util.AttributeSet;

import trickyandroid.com.nursingtimer.R;

/**
 * Created by paveld on 9/10/14.
 */
public class PoopTimer extends TimerLayout {
    public PoopTimer(Context context) {
        super(context);
    }

    public PoopTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PoopTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PoopTimer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    int getIconResId() {
        return R.drawable.poop;
    }

    @Override
    public String getTimerName() {
        return "poop timer";
    }
}
