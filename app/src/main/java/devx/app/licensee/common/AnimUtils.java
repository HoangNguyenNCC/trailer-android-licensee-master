package devx.app.licensee.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimUtils {

    private static int animTimes = 0;
    private static RotateAnimation rotateAnimation;

    public static void drop(final View v, Animation.AnimationListener listener) {
        Animation anim = new ScaleAnimation(
                1f, 0f,
                1, 0,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setFillAfter(false);
        anim.setDuration(200);
        anim.setAnimationListener(listener);
        v.startAnimation(anim);
    }

    public static void drop(final View v) {
        Animation anim = new ScaleAnimation(
                1f, 0f,
                1, 0,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setFillAfter(true);
        anim.setDuration(200);
        Animation dd = new AlphaAnimation(1f, 0f);
        dd.setDuration(200);
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(anim);
        set.addAnimation(dd);
        v.startAnimation(set);
    }

    public static void pop(final View v, Animation.AnimationListener listener) {
        v.setVisibility(View.VISIBLE);
        Animation anim = new ScaleAnimation(
                0f, 1f,
                0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setFillAfter(true);
        anim.setDuration(200);
        anim.setAnimationListener(listener);
        v.startAnimation(anim);
    }

    public static void pop(final View v) {
        v.setVisibility(View.VISIBLE);
        Animation anim = new ScaleAnimation(
                0f, 1f,
                0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setFillAfter(true);
        anim.setDuration(200);
        v.startAnimation(anim);
    }

    public static void popup(View v) {
        Animation anim = new ScaleAnimation(
                0f, 1f,
                0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setFillAfter(true);
        anim.setDuration(200);
        v.startAnimation(anim);
    }

    public static void popup(View v, int duration) {
        Animation anim = new ScaleAnimation(
                0f, 1f,
                0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setFillAfter(true);
        anim.setDuration(duration);
        v.startAnimation(anim);
    }

    public static void bounceAnimation(View view, int duration) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", 0, 0, -40, 0, -20, 0, 0);
        anim.setInterpolator(new BounceInterpolator());
        anim.setDuration(duration);
        anim.start();
    }

    public static void pan(final View v) {
        final Animation anim = new ScaleAnimation(
                1f, 0.8f,
                1, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setFillAfter(true);
        anim.setDuration(200);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation anim1 = new ScaleAnimation(
                        0.8f, 1f,
                        0.8f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);

                anim1.setFillAfter(true);
                anim1.setDuration(200);
                v.startAnimation(anim1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        v.startAnimation(anim);
    }

    public static void scaleUp(final View v) {
        final Animation anim = new ScaleAnimation(
                1f, 1.2f,
                1, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setFillAfter(true);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation anim1 = new ScaleAnimation(
                        1.2f, 1f,
                        1.2f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);

                anim1.setFillAfter(true);
                anim1.setDuration(200);
                v.startAnimation(anim1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        v.startAnimation(anim);
    }

    public static void shake(final View v) {
        shake(v, 100);
    }

    public static void shake(final View v, final int duration) {
        if (v == null) return;
        Animation anim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );

        anim.setFillAfter(true);
        anim.setDuration(duration);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation anim1 = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.1f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f
                );

                anim1.setFillAfter(true);
                anim1.setDuration(duration);
                v.startAnimation(anim1);

                if (animTimes < 3) {
                    shake(v);
                    animTimes++;
                } else {
                    animTimes = 0;
                    return;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        v.startAnimation(anim);
    }

    public static void infiniteRotate(final View view) {
        rotateAnimation = new RotateAnimation(0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        rotateAnimation.setDuration(2000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new AccelerateInterpolator());
        view.startAnimation(rotateAnimation);
    }

    public static void slideRightToLeft(View viewToShow, View viewToHide, int animationTime) {
        viewToHide.animate()
                .alpha(0f)
                .translationXBy(-100f)
                .setDuration(animationTime)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewToHide.setVisibility(View.GONE);
                        viewToShow.setAlpha(1f);
                        viewToShow.setVisibility(View.VISIBLE);
                    }
                });
    }

    public static void slideLeftToRight(View viewToShow, View viewToHide, int animationTime) {
        viewToHide.animate()
                .alpha(0f)
                .translationXBy(200f)
                .setDuration(animationTime)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewToHide.setVisibility(View.GONE);
                        viewToShow.setAlpha(1f);
                        viewToShow.setVisibility(View.VISIBLE);
                    }
                });
    }

    public static void cancelInfiniteRotate() {
        if (rotateAnimation != null && rotateAnimation.isInitialized()) {
            rotateAnimation.cancel();
        }
    }

    public static void slideUp(View child) {
        child.clearAnimation();
        child.setVisibility(View.VISIBLE);
        child.setAlpha(0F);
        child.animate().translationY(0).setDuration(180).alpha(1.0F);
    }

    public static void slideUpVis(View child) {
        child.clearAnimation();
        child.setVisibility(View.VISIBLE);
        child.setAlpha(1F);
        child.animate().translationY(0).setDuration(180).alpha(0F);
    }

    public static void slideDown(View child) {
        child.clearAnimation();
        child.setAlpha(1.0F);
        child.clearAnimation();
        child.animate().alpha(0.0F).translationY(child.getHeight()).setDuration(150);
    }

    public static void slideDownVis(View child) {
        child.setAlpha(0.0F);
        child.clearAnimation();
        child.animate().alpha(1.0F).translationY(1).setDuration(180);
    }

    public static void panWithCallback(View v, Animation.AnimationListener animationListener) {
        final Animation anim = new ScaleAnimation(
                1f, 0.8f,
                1, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setFillAfter(true);
        anim.setDuration(100);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation anim1 = new ScaleAnimation(
                        0.8f, 1f,
                        0.8f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);

                anim1.setFillAfter(true);
                anim1.setDuration(100);
                anim1.setAnimationListener(animationListener);
                v.startAnimation(anim1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        v.startAnimation(anim);
    }

    public static void scale(View view) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        animator.setRepeatCount(1);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();
    }

    public static void touchStart(View view) {
        view.animate()
                .scaleX(1.04F)
                .scaleY(1.04F)
                .setDuration(100L);
    }

    public static void touchEnd(View view, View.OnClickListener onClickListener) {
        view.animate()
                .scaleX(1F)
                .scaleY(1F)
                .setDuration(200L)
                .withEndAction(() -> onClickListener.onClick(view));
    }

    void slideUp_height(View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0.0f);
        view.animate()
                .translationY(view.getHeight())
                .alpha(1.0f)
                .setDuration(200)
                .setListener(null);
    }
}
