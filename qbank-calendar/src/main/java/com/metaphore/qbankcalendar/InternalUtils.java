package com.metaphore.qbankcalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.util.Calendar.*;

class InternalUtils {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final DateFormat MONTH_FORMAT = new SimpleDateFormat("MMMM");
    public static final DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    public static final Calendar CURRENT_DATE = GregorianCalendar.getInstance();

    /**
     * Retrieve all the dates for a given calendar month Include previous month,
     * current month and next month. Prepares date array for 7x6 grid.
     */
    public static ArrayList<Calendar> getFullWeeks(int year, int month) {
        int startDayOfWeek = 2; // monday

        ArrayList<Calendar> calendarList = new ArrayList<>();

        Calendar firstDateOfMonth = createFirstDayOfMonth(year, month);
        Calendar lastDateOfMonth = createLastDayOfMonth(year, month);

        // Add dates of first week from previous month
        int weekdayOfFirstDate = firstDateOfMonth.get(DAY_OF_WEEK);

        // If weekdayOfFirstDate smaller than startDayOfWeek
        // For e.g: weekdayFirstDate is Monday, startDayOfWeek is Tuesday
        // increase the weekday of FirstDate because it's in the future
        if (weekdayOfFirstDate < startDayOfWeek) {
            weekdayOfFirstDate += 7;
        }

        while (weekdayOfFirstDate > 0) {
            Calendar date = cpyAndAddDays(firstDateOfMonth, startDayOfWeek - weekdayOfFirstDate);
            if (date.compareTo(firstDateOfMonth) >= 0) {
                System.out.println();
                break;
            }

            calendarList.add(date);
            weekdayOfFirstDate--;
        }

        // Add dates of current month
        for (int i = 0; i < lastDateOfMonth.get(DAY_OF_MONTH); i++) {
            calendarList.add(cpyAndAddDays(firstDateOfMonth, i));
        }

        // Add dates of last week from next month
        int endDayOfWeek = startDayOfWeek - 1;

        if (endDayOfWeek == 0) {
            endDayOfWeek = 7;
        }

        if (lastDateOfMonth.get(DAY_OF_WEEK) != endDayOfWeek) {
            int i = 1;
            while (true) {
                Calendar nextDay = cpyAndAddDays(lastDateOfMonth, i);
                calendarList.add(nextDay);
                i++;
                if (nextDay.get(DAY_OF_WEEK) == endDayOfWeek) {
                    break;
                }
            }
        }

        // Add more weeks to fill remaining rows
        int size = calendarList.size();
        int row = size / 7;
        int numOfDays = (6 - row) * 7;
        Calendar lastDateTime = calendarList.get(size - 1);
        for (int i = 1; i <= numOfDays; i++) {
            Calendar nextDateTime = cpyAndAddDays(lastDateTime, i);
            calendarList.add(nextDateTime);
        }

        return calendarList;
    }

    public static Calendar cpy(Calendar source) {
        Calendar result = GregorianCalendar.getInstance();
        result.setTime(source.getTime());
        return result;
    }

    private static Calendar createFirstDayOfMonth(int year, int month) {
        Calendar result = GregorianCalendar.getInstance();
        result.set(YEAR, year);
        result.set(MONTH, month);
        result.set(DAY_OF_MONTH, result.getActualMinimum(Calendar.DAY_OF_MONTH));
        return result;
    }

    private static Calendar createLastDayOfMonth(int year, int month) {
        Calendar result = GregorianCalendar.getInstance();
        result.set(YEAR, year);
        result.set(MONTH, month);
        result.set(DAY_OF_MONTH, result.getActualMaximum(Calendar.DAY_OF_MONTH));
        return result;
    }

    private static Calendar cpyAndAddDays(Calendar calendar, int days) {
        Calendar result = cpy(calendar);
        result.add(DAY_OF_MONTH, days);
        return result;
    }
}
