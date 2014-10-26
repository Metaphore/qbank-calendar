package com.metaphore.qbankcalendar.dayview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.metaphore.qbankcalendar.EditMode;
import com.metaphore.qbankcalendar.R;
import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

class DayViewAdapter extends BaseAdapter {
    private static final int DAYS_AMOUNT = 7*6;

    private final Context context;
    private final Calendar beginDate = Calendar.getInstance();
    private final Calendar endDate = Calendar.getInstance();
    // Used to determine visible month
    private final Calendar currentMonth = Calendar.getInstance();
    private final CalendarComparator comparator = new CalendarComparator();

    private ArrayList<Calendar> fullWeeks;
    private EditMode editMode = EditMode.BEGIN_DATE;

    DayViewAdapter(Context context) {
        this.context = context;

        endDate.add(Calendar.MONTH, 1);
        generateWeeksData();
    }

    public Calendar getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(Calendar currentMonth) {
        this.currentMonth.setTime(currentMonth.getTime());

        generateWeeksData();
        notifyDataSetInvalidated();
    }

    public Calendar getBeginDate() {
        return beginDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEditMode(EditMode editMode) {
        this.editMode = editMode;

        generateWeeksData();
        notifyDataSetChanged();
    }

    public void setSelectionInterval(Calendar begin, Calendar end) {
        beginDate.setTime(begin.getTime());
        endDate.setTime(end.getTime());

        generateWeeksData();
        notifyDataSetChanged();
    }

    public void setSelectedDate(Calendar selectedDate) {
        switch (editMode) {
            case BEGIN_DATE:
                setSelectionInterval(selectedDate, endDate);
                break;
            case END_DATE:
                setSelectionInterval(beginDate, selectedDate);
                break;
        }
    }

    private void generateWeeksData() {
        if (comparator.compare(beginDate, endDate) >= 0) {
            throw new IllegalStateException("Begin date cannot be equal or greater than end date.");
        }
        fullWeeks = getFullWeeks(
                currentMonth.get(Calendar.YEAR),
                currentMonth.get(Calendar.MONTH) + 1);
    }

    @Override
    public int getCount() {
        return DAYS_AMOUNT;
    }

    @Override
    public Object getItem(int position) {
        return fullWeeks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return isInCurrentMonth(position) &&
               !isIntervalEdge(position) &&
               !isDateBesideOtherPeriodEdge(position);
    }

    private boolean isInCurrentMonth(int position) {
        Calendar date = fullWeeks.get(position);
        return date.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH);
    }

    private boolean isIntervalEdge(int position) {
        Calendar date = fullWeeks.get(position);
        return comparator.compare(date, beginDate) == 0 || comparator.compare(date, endDate) == 0;
    }

    private boolean isDateBesideOtherPeriodEdge(int position) {
        Calendar date = fullWeeks.get(position);
        switch (editMode) {
            case BEGIN_DATE:
                return comparator.compare(date, endDate) > 0;
            case END_DATE:
                return comparator.compare(date, beginDate) < 0;
            default:
                throw new IllegalStateException("Unexpected editMode state");
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        DayCell dayCell;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.day_grid_item, parent, false);

            dayCell = (DayCell) view.findViewById(R.id.day_cell);
            dayCell.setComparator(comparator);
            view.setTag(dayCell);
        } else {
            dayCell = (DayCell) view.getTag();
        }

        Calendar date = (Calendar) getItem(position);
        dayCell.updateViewState(date, currentMonth, beginDate, endDate, editMode);

        return view;
    }

    /**
     * Comparator that use only year, month and day fields to compare calendars
     */
    public static class CalendarComparator implements Comparator<Calendar> {
        @Override
        public int compare(Calendar l, Calendar r) {
            int yearDif = l.get(Calendar.YEAR) - r.get(Calendar.YEAR);
            if (yearDif != 0) return yearDif;

            int monthDif = l.get(Calendar.MONTH) - r.get(Calendar.MONTH);
            if (monthDif != 0) return monthDif;

            int dayDif = l.get(Calendar.DAY_OF_YEAR) - r.get(Calendar.DAY_OF_YEAR);
            return dayDif;
        }
    }

    /**
     * Retrieve all the dates for a given calendar month Include previous month,
     * current month and next month.
     */
    private ArrayList<Calendar> getFullWeeks(int year, int month) {
        //TODO переписать этот метод, так чтобы не использовать внешнюю либу

        int startDayOfWeek = 2; // monday
        boolean sixWeeksInCalendar = true;

        ArrayList<DateTime> datetimeList = new ArrayList<>();

        DateTime firstDateOfMonth = new DateTime(year, month, 1, 0, 0, 0, 0);
        DateTime lastDateOfMonth = firstDateOfMonth.plusDays(firstDateOfMonth
                .getNumDaysInMonth() - 1);

        // Add dates of first week from previous month
        int weekdayOfFirstDate = firstDateOfMonth.getWeekDay();

        // If weekdayOfFirstDate smaller than startDayOfWeek
        // For e.g: weekdayFirstDate is Monday, startDayOfWeek is Tuesday
        // increase the weekday of FirstDate because it's in the future
        if (weekdayOfFirstDate < startDayOfWeek) {
            weekdayOfFirstDate += 7;
        }

        while (weekdayOfFirstDate > 0) {
            DateTime dateTime = firstDateOfMonth.minusDays(weekdayOfFirstDate
                    - startDayOfWeek);
            if (!dateTime.lt(firstDateOfMonth)) {
                break;
            }

            datetimeList.add(dateTime);
            weekdayOfFirstDate--;
        }

        // Add dates of current month
        for (int i = 0; i < lastDateOfMonth.getDay(); i++) {
            datetimeList.add(firstDateOfMonth.plusDays(i));
        }

        // Add dates of last week from next month
        int endDayOfWeek = startDayOfWeek - 1;

        if (endDayOfWeek == 0) {
            endDayOfWeek = 7;
        }

        if (lastDateOfMonth.getWeekDay() != endDayOfWeek) {
            int i = 1;
            while (true) {
                DateTime nextDay = lastDateOfMonth.plusDays(i);
                datetimeList.add(nextDay);
                i++;
                if (nextDay.getWeekDay() == endDayOfWeek) {
                    break;
                }
            }
        }

        // Add more weeks to fill remaining rows
        if (sixWeeksInCalendar) {
            int size = datetimeList.size();
            int row = size / 7;
            int numOfDays = (6 - row) * 7;
            DateTime lastDateTime = datetimeList.get(size - 1);
            for (int i = 1; i <= numOfDays; i++) {
                DateTime nextDateTime = lastDateTime.plusDays(i);
                datetimeList.add(nextDateTime);
            }
        }

        ArrayList<Calendar> result = new ArrayList<>();
        for (DateTime dateTime : datetimeList) {
            result.add(prepareCalendar(dateTime));
        }
        return result;
    }

    private Calendar prepareCalendar(DateTime dateTime) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, dateTime.getYear());
        cal.set(Calendar.MONTH, dateTime.getMonth());
        cal.set(Calendar.DAY_OF_YEAR, dateTime.getDayOfYear());
        return cal;
    }
}