package trickyandroid.com.nursingtimer.widgets;

import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import trickyandroid.com.nursingtimer.R;
import trickyandroid.com.nursingtimer.Utils;
import trickyandroid.com.nursingtimer.models.AlarmModel;
import trickyandroid.com.nursingtimer.models.TimedEvent;
import trickyandroid.com.nursingtimer.models.TimerModel;
import trickyandroid.com.nursingtimer.widgets.timepicker.RadialPickerLayout;
import trickyandroid.com.nursingtimer.widgets.timepicker.TimePickerDialog;

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
    @InjectView(R.id.timerDetailsSection)
    View timerDetailsSection;
    @InjectView(R.id.timerStart)
    ImageView timerStartBtn;
    @InjectView(R.id.timerStop)
    ImageView timerStopBtn;
    @InjectView(R.id.alarmIcon)
    ImageView alarmBtn;
    @InjectView(R.id.alarmText)
    TextView alarmText;

    TimerModel model;

    FragmentManager fragmentManager;

    private static final float INACTIVE_ALPHA = .5f;
    private static final float ACTIVE_ALPHA = 1;

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
        timerIcon.setImageResource(getIconResId());
        timerText.setOnClickListener(this);
        timerText.setTimerStartTimeMs(-1);
        timerStopBtn.setOnClickListener(this);
        timerStartBtn.setOnClickListener(this);
        timerDetailsSection.setOnClickListener(this);
        alarmBtn.setOnClickListener(this);
        applyAccents();

        timerText.setAlpha(INACTIVE_ALPHA);
        alarmBtn.setAlpha(INACTIVE_ALPHA);

        this.model = new TimerModel();
    }

    public void setModel(final TimerModel model) {
        this.model = model;

        if (this.model == null) {
            this.model = new TimerModel();
            stopTimer();
            return;
        }

        if (model.getEvent() != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    timerText.startTimer(model.getEvent().getEventStartTimestamp());
                    fadeInView(timerText);
                    if (model.getAlarmModel() != null && model.getAlarmModel().isAlarmEnabled()) {
                        enableAlarmView(model.getAlarmModel().getAlarm()[0], model.getAlarmModel().getAlarm()[1]);
                    }
                }
            }, 500);

        } else {
            stopTimer();
        }
    }

    public TimerModel getModel() {
        return this.model;
    }


    private void applyAccents() {
        ColorFilter cf = new PorterDuffColorFilter(getResources().getColor(R.color.accentColor), PorterDuff.Mode.SRC_ATOP);
        timerIcon.setColorFilter(cf);
    }

    abstract int getIconResId();

    @Override
    public void onClick(View view) {
        if (getTimerStatus() == TimerTextView.TimerStatus.STOPPED && !isControlPanelVisible) {
            showControlPanel();
            return;
        }
        switch (view.getId()) {
            case R.id.timerStop:
                stopTimer();
                hideControlPanel();
                break;
            case R.id.timerStart:
                resetTimer();
                hideControlPanel();
                Utils.getPulseAnimator(timerText, .95f, 1.05f).start();
                break;
            case R.id.timer:
                showControlPanel();
                break;
            case R.id.alarmIcon:
                showNumberPicker();
                break;
        }
    }

    public void setFragmentManager(FragmentManager fm) {
        fragmentManager = fm;
    }

    private void showNumberPicker() {
        int h = model.getAlarmModel() == null ? 0 : model.getAlarmModel().getAlarm()[0];
        int m = model.getAlarmModel() == null ? 0 : model.getAlarmModel().getAlarm()[1];
        TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                int[] alarm = {hourOfDay, minute};
                if (hourOfDay != 0 || minute != 0) {
                    model.setAlarmModel(new AlarmModel());
                    model.getAlarmModel().setAlarm(alarm);
                    model.getAlarmModel().setAlarmEnabled(true);
                    enableAlarmView(hourOfDay, minute);
                } else {
                    onDelete();
                }
            }

            @Override
            public void onDelete() {
                if (model.getAlarmModel() != null) {
                    model.getAlarmModel().setAlarmEnabled(false);
                    disableAlarmView();
                }
            }
        }, h, m, true);
        tpd.show(fragmentManager, "");
    }


    private void fadeInView(View view) {
        if (view.getAlpha() != ACTIVE_ALPHA) {
            ObjectAnimator.ofFloat(view, View.ALPHA, ACTIVE_ALPHA).start();
        }
    }

    private void fadeOutView(View view) {
        if (view.getAlpha() != INACTIVE_ALPHA) {
            ObjectAnimator.ofFloat(view, View.ALPHA, INACTIVE_ALPHA).start();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putParcelable("model", this.model);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("instanceState");
            this.model = bundle.getParcelable("model");

            if (model != null && model.getEvent() != null) {
                timerText.setTimerStartTimeMs(model.getEvent().getEventStartTimestamp());
                resumeTimer();
                if (model.getAlarmModel() != null && model.getAlarmModel().isAlarmEnabled()) {
                    enableAlarmView(model.getAlarmModel().getAlarm()[0], model.getAlarmModel().getAlarm()[1]);
                }
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

    public void stopTimer() {
        this.timerText.stopTimer();
        fadeOutView(this.timerText);
        disableAlarmView();
        this.model.setEvent(null);
    }

    public void resumeTimer() {
        this.timerText.startTimer();
        fadeInView(this.timerText);
    }

    public void resetTimer() {
        this.timerText.resetTimer();
        fadeInView(this.timerText);

        model.setEvent(new TimedEvent(System.currentTimeMillis()));
        if (model.getAlarmModel() != null && model.getAlarmModel().isAlarmEnabled()) {
            enableAlarmView(model.getAlarmModel().getAlarm()[0], model.getAlarmModel().getAlarm()[1]);
        }
    }

    private void disableAlarmView() {
        if (alarmText.getVisibility() == View.GONE) {
            //do not animate if already on the screen
            return;
        }
        alarmBtn.animate().alpha(INACTIVE_ALPHA).translationY(0).start();
        alarmText.animate().translationY(0).alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                alarmText.setVisibility(View.GONE);
            }
        }).start();
    }

    private void enableAlarmView(int hours, int minutes) {
        alarmText.setText(String.format("%02d:%02d", hours, minutes));

        if (alarmText.getVisibility() == View.VISIBLE) {
            //do not animate if already on the screen
            return;
        }

        alarmText.setVisibility(View.VISIBLE);
        alarmText.setAlpha(0);
        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.alarm_button_translate_y_factor, outValue, true);
        final float translateYFactor = outValue.getFloat();
        alarmText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                alarmText.getViewTreeObserver().removeOnPreDrawListener(this);
                alarmBtn.animate().alpha(1).translationY(-alarmText.getHeight() * translateYFactor).start();
                alarmText.animate().translationY(alarmText.getHeight() * translateYFactor).alpha(1).start();
                return true;
            }
        });
    }

    public void showControlPanel() {
        if (isControlPanelVisible) {
            return;
        }

        if (timerText.getCurrentTimerStatus() == TimerTextView.TimerStatus.STOPPED) {
            timerStartBtn.setImageResource(R.drawable.ic_action_play);
            timerStopBtn.setVisibility(View.GONE);
        } else {
            timerStartBtn.setImageResource(R.drawable.ic_action_replay);
            timerStopBtn.setVisibility(View.VISIBLE);
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
        if (!isControlPanelVisible) {
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

    public abstract String getTimerName();
}