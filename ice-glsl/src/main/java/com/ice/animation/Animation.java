package com.ice.animation;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.ice.graphics.state_controller.GlStateController;

public abstract class Animation implements GlStateController {
    public static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    public static final LinearInterpolator LINEAR_INTERPOLATOR = new LinearInterpolator();

    private static final long NOT_STARTED = 0;

    public interface Listener {
        void onAnimationEnd();
    }

    public Animation() {
        interpolator = ACCELERATE_DECELERATE_INTERPOLATOR;
        startTime = NOT_STARTED;
    }

    public Animation(long duration) {
        this();

        setDuration(duration);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    protected void start() {
        finished = false;
        startTime = AnimationUtils.currentAnimationTimeMillis();
    }

    public void cancel() {
        cancel = true;
    }

    @Override
    public void attach() {

        if (cancel || finished) return;

        if (startTime == NOT_STARTED)
            start();

        long currentTime = AnimationUtils.currentAnimationTimeMillis();

        if (currentTime - startTime < offset)
            return;

        float normalizedTime = 1.0f;

        if (duration != 0) {
            normalizedTime = (currentTime - startTime - offset) / (float) duration;
            normalizedTime = Math.min(normalizedTime, 1.0f);
        }

        onAttach(interpolator.getInterpolation(normalizedTime));

        attached = true;
    }

    @Override
    public void detach() {
        if (attached) {
            onDetach();
            attached = false;
        }

        if (cancel || finished) return;

        long currentTime = AnimationUtils.currentAnimationTimeMillis();
        boolean over = currentTime - (startTime + offset) >= duration;

        try {
            if (over) {
                if (!cancel) {
                    onComplete();
                }
            }
        } finally {
            finished = over;
        }
    }


    public void onComplete() {

        if (fillAfter)
            applyFillAfter();

        if (listener != null)
            listener.onAnimationEnd();
    }

    protected void applyFillAfter() {

    }

    protected abstract void onAttach(float interpolatedTime);

    protected void onDetach() {

    }

    public long getDuration() {
        return duration;
    }

    public boolean isCompleted() {
        return finished;
    }


    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public boolean isCanceled() {
        return cancel;
    }

    public void setFillAfter(boolean fillAfter) {
        this.fillAfter = fillAfter;
    }

    public boolean isFillAfter() {
        return fillAfter;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    private boolean attached;

    private long offset;

    private boolean finished;

    private boolean fillAfter = true;

    protected long startTime;
    protected long duration;

    private boolean cancel;

    private Interpolator interpolator;
    private Listener listener;
}
