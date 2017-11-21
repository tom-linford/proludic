package icn.proludic.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewAnimator;

import icn.proludic.R;

/**
 * Author:  Bradley Wilson
 * Date: 04/04/2017
 * Package: icn.proludic.misc
 * Project Name: proludic
 */

public class CustomViewAnimator extends ViewAnimator {

    private Context context;

    public CustomViewAnimator(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec+((int)context.getResources().getDimension(R.dimen.login_va_height)));
    }
}