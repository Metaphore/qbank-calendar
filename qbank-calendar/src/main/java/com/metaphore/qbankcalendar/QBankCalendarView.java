package com.metaphore.qbankcalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.metaphore.qbankcalendar.dayview.DayView;
import com.metaphore.qbankcalendar.modepicker.ModePicker;
import com.metaphore.qbankcalendar.monthyearpicker.MonthYearPicker;

import java.util.Calendar;

public class QBankCalendarView extends FrameLayout implements
        ModePicker.ModePickerListener,
        MonthYearPicker.MonthYearPickerListener,
        DayView.DateViewListener {

    private static final String LOG_TAG = QBankCalendarView.class.getSimpleName();

    private final Calendar tmpCal = Calendar.getInstance();

    private final ModePicker modePicker;
    private final MonthYearPicker monthYearPicker;
    private final DayView dayPicker;

    public QBankCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.qbank_calendar_view, this, true);

        modePicker = ((ModePicker) findViewById(R.id.mode_picker));
        monthYearPicker = ((MonthYearPicker) findViewById(R.id.month_year_picker));
        dayPicker = ((DayView) findViewById(R.id.day_picker));

        modePicker.setModeChangeListener(this);
        monthYearPicker.setMonthYearPickerListener(this);
        dayPicker.setDateViewListener(this);
    }

//    private CalendarData initializeCalendarData() {
//        Calendar currentDate = Calendar.getInstance();
//        Calendar beginDate = Calendar.getInstance();
//        Calendar endDate = Calendar.getInstance();
//        endDate.add(Calendar.MONTH, 1);
//
//        return new CalendarData(
//                currentDate,
//                beginDate,
//                endDate,
//                CalendarData.DatePickMode.BEGIN,
//                CalendarData.MonthYearMode.MONTH
//        );
//    }

    public void setSelectedInterval(Calendar begin, Calendar end) {
        modePicker.setSelectedInterval(begin, end);
        dayPicker.setSelectedInterval(begin, end);
    }

    @Override
    public void onBeginPeriodModeSet() {
        Log.d(LOG_TAG, "onBeginPeriodModeSet()");

    }

    @Override
    public void onEndPeriodModeSet() {
        Log.d(LOG_TAG, "onEndPeriodModeSet()");

    }

    @Override
    public void onNextYearClicked() {
        Log.d(LOG_TAG, "onNextYearClicked()");

    }

    @Override
    public void onNextMonthClicked() {
        Log.d(LOG_TAG, "onNextMonthClicked()");
        showNextMonth();
    }

    @Override
    public void onPreviousMonthClicked() {
        Log.d(LOG_TAG, "onPreviousMonthClicked()");
        showPreviousMonth();

    }

    @Override
    public void onPreviousYearClicked() {
        Log.d(LOG_TAG, "onPreviousYearClicked()");
    }

    @Override
    public void onDateSelected(Calendar date) {
        Log.d(LOG_TAG, "onDateSelected() " + date.getTime());
        dayPicker.setSelectedDate(date);
        //TODO ...depends on mode
        modePicker.setSelectedInterval(date, dayPicker.getEndDate());
    }

    @Override
    public void onShowNextMonth() {
        Log.d(LOG_TAG, "onShowNextMonth()");
        showNextMonth();
    }

    @Override
    public void onShowPreviousMonth() {
        Log.d(LOG_TAG, "onShowPreviousMonth()");
        showPreviousMonth();
    }

    private void showNextMonth() {
        Calendar currentDate = dayPicker.getCurrentDate();

        Calendar newCurrentDate = tmpCal;
        newCurrentDate.setTime(currentDate.getTime());
        newCurrentDate.add(Calendar.MONTH, 1);

        dayPicker.setCurrentDate(newCurrentDate);
        monthYearPicker.setSelectedDate(newCurrentDate);
    }

    private void showPreviousMonth() {
        Calendar currentDate = dayPicker.getCurrentDate();

        Calendar newCurrentDate = tmpCal;
        newCurrentDate.setTime(currentDate.getTime());
        newCurrentDate.add(Calendar.MONTH, -1);

        dayPicker.setCurrentDate(newCurrentDate);
        monthYearPicker.setSelectedDate(newCurrentDate);
    }
}
