package com.metaphore.qbankcalendar.modepicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import com.metaphore.qbankcalendar.R;

public class ModePicker extends FrameLayout implements ModeItem.OnCheckedChangeListener {

    private static final String LOG_TAG = ModePicker.class.getSimpleName();

    private ModePickerListener modePickerListener;

    private ModeItem modeBeginItem;
    private ModeItem modeEndItem;

    private boolean broadcasting;

    public ModePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mode_picker, this, true);

        modeBeginItem = ((ModeItem) findViewById(R.id.mode_period_begin));
        modeEndItem = ((ModeItem) findViewById(R.id.mode_period_end));
        modeBeginItem.setOnCheckedChangeListener(this);
        modeEndItem.setOnCheckedChangeListener(this);
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

    public void setModeChangeListener(ModePickerListener listener) {
        this.modePickerListener = listener;
    }

    @Override
    public void onModeCheckedChanged(ModeItem modeItem, boolean checked) {
        if (broadcasting || !checked) return;
        broadcasting = true;

        if (modeItem == modeBeginItem) {
            Log.d(LOG_TAG, "Mode changed to \"Begin of period\"");
            modeEndItem.setChecked(false);
            if (modePickerListener != null) {
                modePickerListener.onPeriodBeginMode();
            }
        }
        if (modeItem == modeEndItem) {
            Log.d(LOG_TAG, "Mode changed to \"End of period\"");
            modeBeginItem.setChecked(false);
            if (modePickerListener != null) {
                modePickerListener.onPeriodEndMode();
            }
        }

        broadcasting = false;
    }

    public interface ModePickerListener {
        void onPeriodBeginMode();
        void onPeriodEndMode();
    }
}
