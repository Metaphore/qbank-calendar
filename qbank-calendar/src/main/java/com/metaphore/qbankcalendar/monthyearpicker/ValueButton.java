package com.metaphore.qbankcalendar.monthyearpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

class ValueButton extends CompoundButton {

    public ValueButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
    }

    @Override
    public void toggle() {
        boolean checked = isChecked();

        // Restricts unchecking by touch
        if (!checked) {
            setChecked(true);
        }
    }
}
