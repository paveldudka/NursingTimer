package trickyandroid.com.nursingtimer.widgets;

import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import trickyandroid.com.nursingtimer.R;
import trickyandroid.com.nursingtimer.TimerApplication;
import trickyandroid.com.nursingtimer.Utils;
import trickyandroid.com.nursingtimer.bus.TimerModelUpdatedEvent;
import trickyandroid.com.nursingtimer.databinding.TimerLayoutBinding;
import trickyandroid.com.nursingtimer.models.AlarmModel;
import trickyandroid.com.nursingtimer.models.TimedEvent;
import trickyandroid.com.nursingtimer.models.TimerModel;

public abstract class TimerLayout extends RelativeLayout implements View.OnClickListener {

    @Inject
    Bus bus;

    TimerModel model;

    FragmentManager fragmentManager;

    TimerLayoutBinding binding;

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
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.timer_layout, this, true);
        TimerApplication.get().getGraph().inject(this);
        binding.timerIcon.setImageResource(getIconResId());
        binding.timerText.setOnClickListener(this);
        binding.timerText.setTimerStartTimeMs(-1);
        binding.timerStop.setOnClickListener(this);
        binding.timerStart.setOnClickListener(this);
        binding.timerDetailsSection.setOnClickListener(this);
        binding.alarmBtn.setOnClickListener(this);
        applyAccents();

        binding.timerText.setAlpha(INACTIVE_ALPHA);
        binding.alarmBtn.setAlpha(INACTIVE_ALPHA);

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
            binding.timerText.startTimer(model.getEvent().getEventStartTimestamp());
            binding.timerText.setAlpha(ACTIVE_ALPHA);
            if (model.getAlarmModel() != null && model.getAlarmModel().isAlarmEnabled()) {
                enableAlarmView(model.getAlarmModel().getAlarm()[0], model.getAlarmModel().getAlarm()[1], false);
            }
        } else {
            stopTimer();
        }
    }

    public TimerModel getModel() {
        return this.model;
    }


    private void applyAccents() {
        ColorFilter cf = new PorterDuffColorFilter(getResources().getColor(R.color.accentColor), PorterDuff.Mode.SRC_ATOP);
        binding.timerIcon.setColorFilter(cf);
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
                Utils.getPulseAnimator(binding.timerText, .95f, 1.05f).start();
                break;
            case R.id.timer:
                showControlPanel();
                break;
            case R.id.alarmBtn:
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
        TimePickerDialog tpd = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            int[] alarm = { hourOfDay, minute };
            if (hourOfDay != 0 || minute != 0) {
                model.setAlarmModel(new AlarmModel());
                model.getAlarmModel().setAlarm(alarm);
                model.getAlarmModel().setAlarmEnabled(true);
                enableAlarmView(hourOfDay, minute, true);
                bus.post(new TimerModelUpdatedEvent(getTimerName(), model));
            } else {
                //                    onDelete();
            }
        }, h, m, true);
        tpd.show();
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
            Bundle bundle = (Bundle)state;
            state = bundle.getParcelable("instanceState");
            this.model = bundle.getParcelable("model");

            if (model != null && model.getEvent() != null) {
                binding.timerText.setTimerStartTimeMs(model.getEvent().getEventStartTimestamp());
                resumeTimer();
                if (model.getAlarmModel() != null && model.getAlarmModel().isAlarmEnabled()) {
                    enableAlarmView(model.getAlarmModel().getAlarm()[0], model.getAlarmModel().getAlarm()[1], false);
                }
            }
        }
        super.onRestoreInstanceState(state);
    }

    public TimerTextView.TimerStatus getTimerStatus() {
        return binding.timerText.getCurrentTimerStatus();
    }

    public void pauseTimer() {
        this.binding.timerText.pauseTimer();
    }

    public void stopTimer() {
        binding.timerText.stopTimer();
        fadeOutView(binding.timerText);
        disableAlarmView();
        this.model.setEvent(null);
        bus.post(new TimerModelUpdatedEvent(getTimerName(), model));
    }

    public void resumeTimer() {
        binding.timerText.startTimer();
        fadeInView(binding.timerText);
    }

    public void resetTimer() {
        binding.timerText.resetTimer();
        fadeInView(binding.timerText);

        model.setEvent(new TimedEvent(System.currentTimeMillis()));
        if (model.getAlarmModel() != null && model.getAlarmModel().isAlarmEnabled()) {
            enableAlarmView(model.getAlarmModel().getAlarm()[0], model.getAlarmModel().getAlarm()[1], true);
        }
        bus.post(new TimerModelUpdatedEvent(getTimerName(), model));
    }

    private void disableAlarmView() {
        if (binding.alarmText.getVisibility() == View.GONE) {
            //do not animate if already on the screen
            return;
        }
        binding.alarmBtn.animate().alpha(INACTIVE_ALPHA).translationY(0).start();
        binding.alarmText.animate().translationY(0).alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                binding.alarmText.setVisibility(View.GONE);
            }
        }).start();
    }

    private void enableAlarmView(int hours, int minutes, final boolean animate) {
        binding.alarmText.setText(String.format("%02d:%02d", hours, minutes));

        if (binding.alarmText.getVisibility() == View.VISIBLE) {
            //do not animate if already on the screen
            return;
        }

        binding.alarmText.setVisibility(View.VISIBLE);
        binding.alarmText.setAlpha(0);
        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.alarm_button_translate_y_factor, outValue, true);
        final float translateYFactor = outValue.getFloat();
        binding.alarmText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                binding.alarmText.getViewTreeObserver().removeOnPreDrawListener(this);
                float targetTranslateY = binding.alarmText.getHeight() * translateYFactor;
                if (animate) {
                    binding.alarmText.animate().translationY(targetTranslateY).alpha(ACTIVE_ALPHA).start();
                    binding.alarmBtn.animate().translationY(-targetTranslateY).alpha(ACTIVE_ALPHA).start();
                } else {
                    binding.alarmText.setAlpha(ACTIVE_ALPHA);
                    binding.alarmText.setTranslationY(targetTranslateY);
                    binding.alarmBtn.setAlpha(ACTIVE_ALPHA);
                    binding.alarmBtn.setTranslationY(-targetTranslateY);
                }
                return true;
            }
        });
    }

    public void showControlPanel() {
        if (isControlPanelVisible) {
            return;
        }

        if (binding.timerText.getCurrentTimerStatus() == TimerTextView.TimerStatus.STOPPED) {
            binding.timerStart.setImageResource(R.drawable.ic_action_play);
            binding.timerStop.setVisibility(View.GONE);
        } else {
            binding.timerStart.setImageResource(R.drawable.ic_action_replay);
            binding.timerStop.setVisibility(View.VISIBLE);
        }

        isControlPanelVisible = true;
        binding.controlPanel.setOnFocusChangeListener(controlPanelFocusChangeListener);
        binding.controlPanel.setVisibility(View.VISIBLE);
        binding.controlPanel.requestFocus();
        binding.controlPanel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                binding.controlPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                binding.controlPanel.setTranslationX(binding.controlPanel.getWidth());
                binding.controlPanel.animate().translationX(0).setInterpolator(new DecelerateInterpolator(2)).start();
                return true;
            }
        });
    }

    public void hideControlPanel() {
        if (!isControlPanelVisible) {
            return;
        }
        isControlPanelVisible = false;
        binding.controlPanel.setOnFocusChangeListener(null);
        binding.controlPanel.animate().translationX(binding.controlPanel.getWidth()).setInterpolator(new DecelerateInterpolator(2)).withEndAction(new Runnable() {
            @Override
            public void run() {
                binding.controlPanel.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isControlPanelVisible) {
            if (ev.getX() < binding.controlPanel.getLeft()) {
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

    /**
     * Hash code of the {@link #getTimerName()} string
     *
     * @return
     */
    public int getTimerId() {
        return getTimerName().hashCode();
    }
}