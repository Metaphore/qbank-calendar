package com.metaphore.qbankcalendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

class ModeItem extends FrameLayout implements Checkable {

    private final TextView selectedDate;
    private final TextView modeName;
    private final ColorStateList textColor;

    private final Calendar selectedDateValue = GregorianCalendar.getInstance();

    private boolean checked;
    private boolean broadcasting;

    private OnCheckedChangeListener onCheckedChangeListener;

    public ModeItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mode_pick_item, this, true);

        modeName = (TextView) findViewById(R.id.mode_name);
        selectedDate = (TextView) findViewById(R.id.selected_date);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ModeItem,
                0, 0);

        checked = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "checked", false);
        String modeNameText = a.getString(R.styleable.ModeItem_mode_name);
        textColor = a.getColorStateList(R.styleable.ModeItem_text_color);
        a.recycle();

        modeName.setText(modeNameText);

        setClickable(true);
    }

    public Calendar getSelectedDate() {
        return selectedDateValue;
    }

    public void setSelectedDate(Calendar date) {
        selectedDate.setText(InternalUtils.DATE_FORMAT.format(date.getTime()));
        selectedDateValue.setTime(date.getTime());
        invalidate();
        requestLayout();
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        onCheckedChangeListener = listener;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        // Updating text color depends on text_color attribute
        if (textColor != null) {
            int colorForState = textColor.getColorForState(getDrawableState(), 0);
            modeName.setTextColor(colorForState);
            selectedDate.setTextColor(colorForState);
        }
    }
//region Checkable implementation

    // Fix for not responding on checked state
    // http://stackoverflow.com/questions/11293399/custom-checkable-view-which-responds-to-selector
    private static final int[] CheckedStateSet = {
            android.R.attr.state_checked,
    };
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CheckedStateSet);
        }
        return drawableState;
    }

    @Override
    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            refreshDrawableState();

            // Avoid infinite recursions if setChecked() is called from a listener
            if (broadcasting) {
                return;
            }

            broadcasting = true;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onModeCheckedChanged(this, checked);
            }

            broadcasting = false;
        }
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        // Restricts uncheking by touch
        if (!checked) {
            setChecked(true);
        }
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }
    //endregion

    public static interface OnCheckedChangeListener {
        void onModeCheckedChanged(ModeItem modeItem, boolean checked);
    }
}
