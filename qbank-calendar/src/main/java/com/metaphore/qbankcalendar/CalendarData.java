package com.metaphore.qbankcalendar;

import java.util.Calendar;

public class CalendarData {
    public static enum DatePickMode { BEGIN, END }
    public static enum MonthYearMode { MONTH, YEAR }

    private Calendar currentDate;
    private Calendar beginDate;
    private Calendar endDate;
    private DatePickMode datePickMode;
    private MonthYearMode monthYearMode;

    public CalendarData(Calendar currentDate, Calendar beginDate, Calendar endDate,
                        DatePickMode datePickMode, MonthYearMode monthYearMode) {
        this.currentDate = currentDate;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.datePickMode = datePickMode;
        this.monthYearMode = monthYearMode;
    }

    public Calendar getCurrentDate() {
        return currentDate;
    }
    public void setCurrentDate(Calendar currentDate) {
        this.currentDate = currentDate;
    }

    public Calendar getBeginDate() {
        return beginDate;
    }
    public void setBeginDate(Calendar beginDate) {
        this.beginDate = beginDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }
    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public DatePickMode getDatePickMode() {
        return datePickMode;
    }
    public void setDatePickMode(DatePickMode datePickMode) {
        this.datePickMode = datePickMode;
    }

    public MonthYearMode getMonthYearMode() {
        return monthYearMode;
    }
    public void setMonthYearMode(MonthYearMode monthYearMode) {
        this.monthYearMode = monthYearMode;
    }
}
