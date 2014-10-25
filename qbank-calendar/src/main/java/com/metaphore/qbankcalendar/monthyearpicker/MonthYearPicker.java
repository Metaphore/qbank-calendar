package com.metaphore.qbankcalendar.monthyearpicker;

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
import com.metaphore.qbankcalendar.CommonUtils;
import com.metaphore.qbankcalendar.R;

import java.util.Calendar;
import java.util.Date;

public class MonthYearPicker extends FrameLayout implements ValuePicker.PickerItemListener {

    private static final String LOG_TAG = MonthYearPicker.class.getSimpleName();

    private final ValuePicker monthPicker;
    private final ValuePicker yearPicker;

    private MonthYearPickerListener monthYearPickerListener;

    private boolean broadcasting;

    public MonthYearPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.month_year_picker, this, true);

        monthPicker = ((ValuePicker) findViewById(R.id.month_picker));
        yearPicker = ((ValuePicker) findViewById(R.id.year_picker));

        // Set today
        setSelectedDate(Calendar.getInstance());

        monthPicker.setPickerItemListener(this);
        yearPicker.setPickerItemListener(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(MonthYearPicker.class.getName());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(MonthYearPicker.class.getName());
    }

    public void setMonthYearPickerListener(MonthYearPickerListener listener) {
        this.monthYearPickerListener = listener;
    }

    public void setSelectedDate(Calendar date) {
        Date time = date.getTime();
        String month = CommonUtils.MONTH_FORMAT.format(time);
        String year =  CommonUtils.YEAR_FORMAT.format(time);
        monthPicker.setText(month);
        yearPicker.setText(year);
    }

    @Override
    public void onNextPressed(ValuePicker valuePicker) {
        if (valuePicker == monthPicker) {
            Log.d(LOG_TAG, "Next month pressed");
            if (monthYearPickerListener != null) {
                monthYearPickerListener.onNextMonthClicked();
            }
        }
        if (valuePicker == yearPicker) {
            Log.d(LOG_TAG, "Next year pressed");
            if (monthYearPickerListener != null) {
                monthYearPickerListener.onNextYearClicked();
            }
        }
    }

    @Override
    public void onPreviousPressed(ValuePicker valuePicker) {
        if (valuePicker == monthPicker) {
            Log.d(LOG_TAG, "Previous month pressed");
            if (monthYearPickerListener != null) {
                monthYearPickerListener.onPreviousMonthClicked();
            }
        }
        if (valuePicker == yearPicker) {
            Log.d(LOG_TAG, "Previous year pressed");
            if (monthYearPickerListener != null) {
                monthYearPickerListener.onPreviousYearClicked();
            }
        }
    }

    @Override
    public void onSelected(ValuePicker valuePicker) {
        if (broadcasting) return;
        broadcasting = true;

        if (valuePicker == monthPicker) {
            Log.d(LOG_TAG, "Month picker selected");
            yearPicker.setSelectedState(false);
        }
        if (valuePicker == yearPicker) {
            Log.d(LOG_TAG, "Year picker selected");
            monthPicker.setSelectedState(false);
        }

        broadcasting = false;
    }

    public interface MonthYearPickerListener {
        void onNextYearClicked();
        void onPreviousYearClicked();
        void onNextMonthClicked();
        void onPreviousMonthClicked();
    }
}
