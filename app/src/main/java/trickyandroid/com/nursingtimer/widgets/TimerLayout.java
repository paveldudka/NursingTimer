package trickyandroid.com.nursingtimer.widgets;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
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
    @InjectView(R.id.timerControlPanel)
    View controlPanel;

    boolean isControlPanelVisible = false;

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
        findViewById(R.id.timerStop).setOnClickListener(this);
        findViewById(R.id.timerReset).setOnClickListener(this);
        setSaveEnabled(true);
        applyAccents();
    }

    private void applyAccents() {
        ColorFilter cf = new PorterDuffColorFilter(getResources().getColor(R.color.accentColor), PorterDuff.Mode.SRC_ATOP);
        timerIcon.setColorFilter(cf);
    }

    abstract int getIconResId();

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.timerStop:
                stopTimer();
                hideControlPanel();
                break;
            case R.id.timerReset:
                resetTimer();
                hideControlPanel();
                break;
            case R.id.timer:
                showControlPanel();
                break;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("timerStatus", this.timerText.getCurrentTimerStatus().ordinal());
        bundle.putLong("timerTime", this.timerText.getTimerStartTimeMs());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("instanceState");
            timerText.setTimerStartTimeMs(bundle.getLong("timerTime", 0));
            TimerTextView.TimerStatus status = TimerTextView.TimerStatus.values()[bundle.getInt("timerStatus")];
            if (status == TimerTextView.TimerStatus.STARTED) {
                resumeTimer();
            }
        }
        super.onRestoreInstanceState(state);
    }

    public TimerTextView.TimerStatus getTimerStatus() {
        return this.timerText.getCurrentTimerStatus();
    }

    public void pauseTimer() {
        this.timerText.pauseTimer();
    }

    public void resumeTimer() {
        this.timerText.startTimer();
    }

    public void stopTimer() {
        this.timerText.stopTimer();
    }

    public void resetTimer() {
        this.timerText.resetTimer();
    }

    public void showControlPanel() {
        if (controlPanel.getVisibility() == View.VISIBLE) {
            return;
        }

        isControlPanelVisible = true;
        controlPanel.setOnFocusChangeListener(controlPanelFocusChangeListener);
        controlPanel.setVisibility(View.VISIBLE);
        controlPanel.requestFocus();
        controlPanel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                controlPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                controlPanel.setTranslationX(controlPanel.getWidth());
                controlPanel.animate().translationX(0).setInterpolator(new DecelerateInterpolator(2)).start();
                return true;
            }
        });
    }

    public void hideControlPanel() {
        if (controlPanel.getVisibility() != View.VISIBLE) {
            return;
        }
        isControlPanelVisible = false;
        controlPanel.setOnFocusChangeListener(null);
        controlPanel.animate().translationX(controlPanel.getWidth()).setInterpolator(new DecelerateInterpolator(2)).withEndAction(new Runnable() {
            @Override
            public void run() {
                controlPanel.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isControlPanelVisible) {
            if (ev.getX() < controlPanel.getLeft()) {
                hideControlPanel();
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    OnFocusChangeListener controlPanelFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (!b) {
                hideControlPanel();
            }
        }
    };
}