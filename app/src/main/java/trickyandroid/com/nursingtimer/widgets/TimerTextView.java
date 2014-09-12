package trickyandroid.com.nursingtimer.widgets;

import android.content.Context;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by paveld on 9/11/14.
 */
public class TimerTextView extends TextView {

    private Handler h = new Handler();
    private long timerStartTimeMs = -1;
    private boolean isTimerActive = false;
    private RelativeSizeSpan span = new RelativeSizeSpan(0.5f);

    public TimerTextView(Context context) {
        super(context);
    }

    public TimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimerTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void resetTimer() {
        stopTimer();
        startTimer();
    }

    public void stopTimer() {
        isTimerActive = false;
        h.removeCallbacks(timerTicker);
        setTimerStartTimeMs(-1);
    }

    public void startTimer() {
        startTimer(timerStartTimeMs);
    }

    public void startTimer(long timeMs) {
        if (!isTimerActive) {
            if (timeMs == -1) {
                timeMs = System.currentTimeMillis();
            }
            setTimerStartTimeMs(timeMs);
            isTimerActive = true;
            h.postDelayed(timerTicker, 1000);
        }
    }

    public void setTimerStartTimeMs(long timeMs) {
        timerStartTimeMs = timeMs;
        updateText();
    }

    private void updateText() {
        long elapsedTime = 0;
        if (timerStartTimeMs != -1) {
            elapsedTime = System.currentTimeMillis() - timerStartTimeMs;
        }
        String text = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(elapsedTime),
                TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60,
                TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60);
        Spannable spanned = new SpannableString(text);
        spanned.setSpan(span, text.length() - 3, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        setText(spanned);
    }


    private Runnable timerTicker = new Runnable() {
        @Override
        public void run() {
            if (isTimerActive) {
                updateText();
                h.postDelayed(timerTicker, 1000);
            }
        }
    };

    public boolean isTimerActive() {
        return isTimerActive;
    }

    public long getTimerStartTimeMs() {
        return timerStartTimeMs;
    }
}
