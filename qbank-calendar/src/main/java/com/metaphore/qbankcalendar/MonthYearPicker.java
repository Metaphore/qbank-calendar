package com.metaphore.qbankcalendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import java.util.Calendar;
import java.util.GregorianCalendar;

class MonthYearPicker extends FrameLayout {
    private static final String LOG_TAG = MonthYearPicker.class.getSimpleName();

    private final MonthYearButton monthButton;
    private final MonthYearButton yearButton;
    private final View arrowMonthLeft;
    private final View arrowMonthRight;
    private final View arrowYearLeft;
    private final View arrowYearRight;

    private MonthYearPickerListener monthYearPickerListener;

    public MonthYearPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.month_year_picker, this, true);

        monthButton = (MonthYearButton) findViewById(R.id.month_button);
        yearButton = (MonthYearButton) findViewById(R.id.year_button);
        arrowMonthLeft = findViewById(R.id.month_arrow_left);
        arrowMonthRight = findViewById(R.id.month_arrow_right);
        arrowYearLeft = findViewById(R.id.year_arrow_left);
        arrowYearRight = findViewById(R.id.year_arrow_right);

        monthButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onMonthChecked();
                }
            }
        });
        yearButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onYearChecked();
                }
            }
        });
        arrowMonthLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prevMonthPressed();
            }
        });
        arrowMonthRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthPressed();
            }
        });
        arrowYearLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prevYearPressed();
            }
        });
        arrowYearRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextYearPressed();
            }
        });

        // Set today by default
        setSelectedDate(GregorianCalendar.getInstance());
    }

    public void setMonthYearPickerListener(MonthYearPickerListener listener) {
        this.monthYearPickerListener = listener;
    }

    public void setSelectedDate(Calendar date) {
        String[] monthList = getResources().getStringArray(R.array.month_list);
        String month = monthList[date.get(Calendar.MONTH)];
        String year =  InternalUtils.YEAR_FORMAT.format(date.getTime());
        monthButton.setText(month);
        yearButton.setText(year);

        updateArrowsEnabledState(date);
    }

    private void updateArrowsEnabledState(Calendar date) {
        boolean edgeDate = date.get(Calendar.YEAR) >= InternalUtils.CURRENT_DATE.get(Calendar.YEAR) &&
                        date.get(Calendar.MONTH) >= InternalUtils.CURRENT_DATE.get(Calendar.MONTH);

        arrowMonthRight.setEnabled(!edgeDate);
        arrowYearRight.setEnabled(!edgeDate);
    }

    private void onMonthChecked() {
        yearButton.setChecked(false);
        arrowMonthLeft.setVisibility(VISIBLE);
        arrowMonthRight.setVisibility(VISIBLE);
        arrowYearLeft.setVisibility(GONE);
        arrowYearRight.setVisibility(GONE);
    }

    private void onYearChecked() {
        monthButton.setChecked(false);
        arrowMonthLeft.setVisibility(GONE);
        arrowMonthRight.setVisibility(GONE);
        arrowYearLeft.setVisibility(VISIBLE);
        arrowYearRight.setVisibility(VISIBLE);
    }

    private void prevMonthPressed() {
        if (monthYearPickerListener != null) {
            monthYearPickerListener.onPreviousMonthClicked();
        }
    }

    private void nextMonthPressed() {
        if (monthYearPickerListener != null) {
            monthYearPickerListener.onNextMonthClicked();
        }
    }

    private void prevYearPressed() {
        if (monthYearPickerListener != null) {
            monthYearPickerListener.onPreviousYearClicked();
        }
    }

    private void nextYearPressed() {
        if (monthYearPickerListener != null) {
            monthYearPickerListener.onNextYearClicked();
        }
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

    public interface MonthYearPickerListener {
        void onNextYearClicked();
        void onPreviousYearClicked();
        void onNextMonthClicked();
        void onPreviousMonthClicked();
    }
}
