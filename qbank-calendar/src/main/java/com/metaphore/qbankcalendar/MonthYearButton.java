package com.metaphore.qbankcalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

class MonthYearButton extends CompoundButton {

    public MonthYearButton(Context context, AttributeSet attrs) {
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
