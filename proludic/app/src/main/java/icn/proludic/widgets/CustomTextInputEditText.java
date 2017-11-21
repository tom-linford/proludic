package icn.proludic.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import icn.proludic.R;

/**
 * Author:  Bradley Wilson
 * Date: 06/04/2017
 * Package: icn.proludic.widgets
 * Project Name: proludic
 */

public class CustomTextInputEditText extends TextInputEditText {
    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    public CustomTextInputEditText(Context context) {
        super(context);
    }

    public CustomTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomTextInputEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        String customFont = a.getString(R.styleable.CustomFontTextView_customFont);
        if (customFont == null) customFont = "fonts/Trebuchet MS.ttf";
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e("EditText", "Could not get typeface", e);
            return false;
        }
        setTypeface(tf);
        return true;
    }
}
