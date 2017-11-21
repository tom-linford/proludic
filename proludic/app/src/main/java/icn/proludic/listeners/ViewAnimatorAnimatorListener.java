package icn.proludic.listeners;

import android.animation.Animator;
import android.content.Context;
import android.widget.ViewAnimator;

import icn.proludic.R;

/**
 * Author:  Bradley Wilson
 * Date: 04/04/2017
 * Package: icn.proludic.listeners
 * Project Name: proludic
 */

public class ViewAnimatorAnimatorListener implements Animator.AnimatorListener {

    private ViewAnimator ll;
    private Context context;

    public ViewAnimatorAnimatorListener(ViewAnimator ll, Context context) {
        this.ll = ll;
        this.context = context;
    }

    @Override
    public void onAnimationStart(Animator animator) {
        // This is the key...
        //set the coordinates for the bounds (left, top, right, bottom) based on the offset value (50px) in a resource XML
        ll.layout(0, -(int)context.getResources().getDimension(R.dimen.login_va_height),
                ll.getWidth(), ll.getHeight() + (int)context.getResources().getDimension(R.dimen.login_va_height));
    }

    @Override
    public void onAnimationEnd(Animator animator) {

    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
