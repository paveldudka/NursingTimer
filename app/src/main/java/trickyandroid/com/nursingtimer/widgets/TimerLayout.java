package trickyandroid.com.nursingtimer.widgets;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import trickyandroid.com.nursingtimer.R;

/**
 * Created by paveld on 9/9/14.
 */
public abstract class TimerLayout extends RelativeLayout implements View.OnClickListener {

    @InjectView(R.id.timerIcon)
    ImageView timerIcon;
    @InjectView(R.id.timer)
    TimerTextView timerText;

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
        ButterKnife.inject(this);
        setBackgroundColor(getContext().getResources().getColor(R.color.accentColorFallback));
        timerIcon.setImageResource(getIconResId());
        timerText.setOnClickListener(this);
        timerText.setTimerStartTimeMs(-1);
        setSaveEnabled(true);
    }

    abstract int getIconResId();

    @Override
    public void onClick(View view) {
        timerText.resetTimer();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putBoolean("timerActive", this.timerText.isTimerActive());
        bundle.putLong("timerTime", this.timerText.getTimerStartTimeMs());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("instanceState");
            timerText.setTimerStartTimeMs(bundle.getLong("timerTime", 0));
            if (bundle.getBoolean("timerActive", false)) {
                timerText.startTimer();
            }
        }
        super.onRestoreInstanceState(state);
    }
}
