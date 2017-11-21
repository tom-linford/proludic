package icn.proludic.widgets;

/**
 * Author:  Bradley Wilson
 * Date: 13/04/2017
 * Package: icn.proludic.widgets
 * Project Name: proludic
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import icn.proludic.R;

public class CustomTrebucheTextView extends android.support.v7.widget.AppCompatTextView {
    AttributeSet attr;
    public CustomTrebucheTextView(Context context) {
        super(context);
        setCustomFont(context, attr);
    }

    public CustomTrebucheTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomTrebucheTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        String customFont = null;
        TypedArray a = null;
        if (attrs != null) {
            a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
            customFont = a.getString(R.styleable.CustomFontTextView_customFont);
        }
        if (customFont == null) customFont = "fonts/Trebuchet MS.ttf";
        setCustomFont(ctx, customFont);
        if (a != null) {
            a.recycle();
        }
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e("textView", "Could not get typeface", e);
            return false;
        }
        setTypeface(tf);
        return true;
    }
}
