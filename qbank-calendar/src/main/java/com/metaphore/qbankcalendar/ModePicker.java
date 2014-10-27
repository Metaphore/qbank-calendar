package com.metaphore.qbankcalendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

import java.util.Calendar;

class ModePicker extends FrameLayout implements ModeItem.OnCheckedChangeListener {
    private static final String LOG_TAG = ModePicker.class.getSimpleName();
    private static final String KEY_INSTANCE_STATE = "instance_state";
    private static final String KEY_BEGIN_DATE = "begin_date";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_EDIT_MODE = "edit_mode";

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

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putLong(KEY_BEGIN_DATE, modeBeginItem.getSelectedDate().getTimeInMillis());
        bundle.putLong(KEY_END_DATE, modeEndItem.getSelectedDate().getTimeInMillis());
        bundle.putInt(KEY_EDIT_MODE, editMode.ordinal());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            Calendar beginDate = InternalUtils.fromMs(bundle.getLong(KEY_BEGIN_DATE));
            Calendar endDate = InternalUtils.fromMs(bundle.getLong(KEY_END_DATE));
            EditMode editMode = EditMode.values()[bundle.getInt(KEY_EDIT_MODE)];

            setSelectedInterval(beginDate, endDate);
            setSelectedMode(editMode);

            state = bundle.getParcelable(KEY_INSTANCE_STATE);
        }
        super.onRestoreInstanceState(state);
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

    public interface ModePickerListener {
        void onBeginPeriodModeSet();
        void onEndPeriodModeSet();
    }
}
