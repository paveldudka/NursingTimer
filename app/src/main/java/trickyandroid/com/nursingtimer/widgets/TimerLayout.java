package trickyandroid.com.nursingtimer.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import trickyandroid.com.nursingtimer.R;

/**
 * Created by paveld on 9/9/14.
 */
public abstract class TimerLayout extends RelativeLayout {

    public TimerLayout(Context context) {
        this(context, null);
    }

    public TimerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TimerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init() {
        inflate(getContext(), R.layout.timer_layout, this);
        setBackgroundColor(getContext().getResources().getColor(R.color.accentColorFallback));
        ((ImageView)(findViewById(R.id.timerIcon))).setImageResource(getIconResId());
    }

    abstract int getIconResId();
}
