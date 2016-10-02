/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package trickyandroid.com.nursingtimer;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.os.Build;
import android.text.format.Time;
import android.view.View;

import java.util.Calendar;

/**
 * Utility helper functions for time and date pickers.
 */
public class Utils {

    public static final int PULSE_ANIMATOR_DURATION = 544;

    /**
     * Render an animator to pulsate a view in place.
     *
     * @param labelToAnimate the view to pulsate.
     * @return The animator object. Use .start() to begin.
     */
    public static ObjectAnimator getPulseAnimator(View labelToAnimate, float decreaseRatio,
                                                  float increaseRatio) {
        Keyframe k0 = Keyframe.ofFloat(0f, 1f);
        Keyframe k1 = Keyframe.ofFloat(0.275f, decreaseRatio);
        Keyframe k2 = Keyframe.ofFloat(0.69f, increaseRatio);
        Keyframe k3 = Keyframe.ofFloat(1f, 1f);

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X, k0, k1, k2, k3);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y, k0, k1, k2, k3);
        ObjectAnimator pulseAnimator =
                ObjectAnimator.ofPropertyValuesHolder(labelToAnimate, scaleX,
                        scaleY);
        pulseAnimator.setDuration(PULSE_ANIMATOR_DURATION);

        return pulseAnimator;
    }
}