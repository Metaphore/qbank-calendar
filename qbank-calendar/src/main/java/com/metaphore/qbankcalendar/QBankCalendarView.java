package com.metaphore.qbankcalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

public class QBankCalendarView extends FrameLayout implements
        ModePicker.ModePickerListener,
        MonthYearPicker.MonthYearPickerListener,
        DayView.DateViewListener {

    private static final String LOG_TAG = QBankCalendarView.class.getSimpleName();

    private final Calendar tmpCal = GregorianCalendar.getInstance();

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

    public void setSelectedInterval(Calendar begin, Calendar end) {
        normalizeDates(begin, end);
        modePicker.setSelectedInterval(begin, end);
        dayPicker.setSelectedInterval(begin, end);
    }

    public void setEditMode(EditMode editMode) {
        modePicker.setSelectedMode(editMode);

        switch (editMode) {
            case BEGIN_DATE:
                monthYearPicker.setSelectedDate(dayPicker.getBeginDate());
                dayPicker.setCurrentDate(dayPicker.getBeginDate());
                break;
            case END_DATE:
                monthYearPicker.setSelectedDate(dayPicker.getEndDate());
                dayPicker.setCurrentDate(dayPicker.getEndDate());
                break;
        }
    }

    public Calendar getBeginDate() {
        return dayPicker.getBeginDate();
    }

    public EditMode getEditMode() {
        return modePicker.getEditMode();
    }

    public Calendar getEndDate() {
        return dayPicker.getEndDate();
    }

    @Override
    public void onBeginPeriodModeSet() {
        dayPicker.setEditMode(EditMode.BEGIN_DATE);
    }

    @Override
    public void onEndPeriodModeSet() {
        dayPicker.setEditMode(EditMode.END_DATE);
    }

    @Override
    public void onNextMonthClicked() {
        changeCurrentDate(Calendar.MONTH, 1);
    }

    @Override
    public void onPreviousMonthClicked() {
        changeCurrentDate(Calendar.MONTH, -1);
    }

    @Override
    public void onNextYearClicked() {
        changeCurrentDate(Calendar.YEAR, 1);
    }

    @Override
    public void onPreviousYearClicked() {
        changeCurrentDate(Calendar.YEAR, -1);
    }

    @Override
    public void onNewPeriodSelected(Calendar beginDate, Calendar endDate) {
        normalizeDates(beginDate, endDate);
        dayPicker.setSelectedInterval(beginDate, endDate);
        modePicker.setSelectedInterval(dayPicker.getBeginDate(), dayPicker.getEndDate());
    }

    @Override
    public void onShowNextMonth() {
        changeCurrentDate(Calendar.MONTH, 1);
    }

    @Override
    public void onShowPreviousMonth() {
        changeCurrentDate(Calendar.MONTH, -1);
    }

    /** Convenience method to change current (visible) date */
    private void changeCurrentDate(int calendarField, int value) {
        if (calendarField != Calendar.YEAR && calendarField != Calendar.MONDAY) {
            throw new IllegalArgumentException("calendarField should be one of [Calendar.YEAR, Calendar.MONDAY]");
        }

        Calendar currentDate = dayPicker.getCurrentDate();

        Calendar newCurrentDate = tmpCal;
        newCurrentDate.setTime(currentDate.getTime());
        newCurrentDate.add(calendarField, value);

        dayPicker.setCurrentDate(newCurrentDate);
        monthYearPicker.setSelectedDate(newCurrentDate);
    }

    private void normalizeDates(Calendar beginDate, Calendar endDate) {
        Comparator<Calendar> comparator = InternalUtils.DATE_COMPARATOR;
        EditMode editMode = modePicker.getEditMode();

        switch (editMode) {
            case BEGIN_DATE:
                // In case of today picked as begin date, we set selected interval to [today-1day, today]
                if (comparator.compare(beginDate, InternalUtils.CURRENT_DATE) == 0) {
                    endDate.setTime(InternalUtils.CURRENT_DATE.getTime());
                    beginDate.setTime(InternalUtils.CURRENT_DATE.getTime());
                    beginDate.add(Calendar.DAY_OF_YEAR, -1);
                }

                // In case of begin date greater than end date, we moving end date to beginDate+1month (according to GR.1.6.14)
                if (comparator.compare(beginDate, endDate) > 0) {
                    endDate.setTime(beginDate.getTime());
                    endDate.add(Calendar.MONTH, 1);
                }
                break;
            case END_DATE:
                // In case of end date less than begin date, we moving begin date to endDate-1month (according to GR.1.6.14)
                if (comparator.compare(endDate, beginDate) < 0) {
                    beginDate.setTime(endDate.getTime());
                    beginDate.add(Calendar.MONTH, -1);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected editMode: " + editMode);
        }

        // In case of end date beyond today, set it to today
        if (comparator.compare(endDate, InternalUtils.CURRENT_DATE) > 0) {
            endDate.setTime(InternalUtils.CURRENT_DATE.getTime());
        }
    }
}
