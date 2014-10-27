package com.metaphore.qbankcalendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

import java.util.Calendar;

class ModePicker extends FrameLayout implements ModeItem.OnCheckedChangeListener {

    private static final String LOG_TAG = ModePicker.class.getSimpleName();

    private ModePickerListener modePickerListener;

    private ModeItem modeBeginItem;
    private ModeItem modeEndItem;

    private EditMode editMode;

    private boolean broadcasting;

    public ModePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mode_picker, this, true);

        modeBeginItem = ((ModeItem) findViewById(R.id.mode_period_begin));
        modeEndItem = ((ModeItem) findViewById(R.id.mode_period_end));
        modeBeginItem.setOnCheckedChangeListener(this);
        modeEndItem.setOnCheckedChangeListener(this);

        if (modeBeginItem.isChecked()) {
            editMode = EditMode.BEGIN_DATE;
        } else {
            editMode = EditMode.END_DATE;
        }
    }

    public void setModeChangeListener(ModePickerListener listener) {
        this.modePickerListener = listener;
    }

    @Override
    public void onModeCheckedChanged(ModeItem modeItem, boolean checked) {
        if (broadcasting || !checked) return;
        broadcasting = true;

        if (modeItem == modeBeginItem) {
            editMode = EditMode.BEGIN_DATE;
            modeEndItem.setChecked(false);
            if (modePickerListener != null) {
                modePickerListener.onBeginPeriodModeSet();
            }
        }
        if (modeItem == modeEndItem) {
            editMode = EditMode.END_DATE;
            modeBeginItem.setChecked(false);
            if (modePickerListener != null) {
                modePickerListener.onEndPeriodModeSet();
            }
        }

        broadcasting = false;
    }

    public void setSelectedInterval(Calendar begin, Calendar end) {
        modeBeginItem.setSelectedDate(begin);
        modeEndItem.setSelectedDate(end);
    }

    public EditMode getEditMode() {
        return editMode;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ModePicker.class.getName());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ModePicker.class.getName());
    }

    public void setSelectedMode(EditMode editMode) {
        switch (editMode) {
            case BEGIN_DATE:
                modeBeginItem.toggle();
                break;
            case END_DATE:
                modeEndItem.toggle();
                break;
            default:
                throw new IllegalStateException("Unexpected editMode: " + editMode);
        }
    }

    public interface ModePickerListener {
        void onBeginPeriodModeSet();
        void onEndPeriodModeSet();
    }
}
