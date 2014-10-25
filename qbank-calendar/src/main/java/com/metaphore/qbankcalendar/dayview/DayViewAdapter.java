package com.metaphore.qbankcalendar.dayview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.metaphore.qbankcalendar.R;
import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

class DayViewAdapter extends BaseAdapter {
    private static final int DAYS_AMOUNT = 7*6;
    public static final int COLOR_BG_INSIDE_INTERVAL = Color.parseColor("#f1ffcc");
    public static final int COLOR_BG_INTERVAL_EDGE = Color.parseColor("#daefa0");

    private final Context context;
    private final Calendar beginDate = Calendar.getInstance();
    private final Calendar endDate = Calendar.getInstance();
    // Used to determine visible month
    private final Calendar currentDate = Calendar.getInstance();
    private final CalendarComparator comparator = new CalendarComparator();

    private ArrayList<Calendar> fullWeeks;

    DayViewAdapter(Context context) {
        this.context = context;

        endDate.add(Calendar.MONTH, 1);
        generateWeeksData();
    }

    public CalendarComparator getComparator() {
        return comparator;
    }

    public Calendar getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Calendar currentDate) {
        this.currentDate.setTime(currentDate.getTime());

        generateWeeksData();
        notifyDataSetInvalidated();
    }

    public Calendar getBeginDate() {
        return beginDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setSelectionInterval(Calendar begin, Calendar end) {
        beginDate.setTime(begin.getTime());
        endDate.setTime(end.getTime());

        generateWeeksData();
        notifyDataSetChanged();
    }

    private void generateWeeksData() {
        if (comparator.compare(beginDate, endDate) >= 0) {
            throw new IllegalStateException("Begin date cannot be equal or greater than end date.");
        }
        fullWeeks = getFullWeeks(
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH)+1);
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
        return isInCurrentMonth(position) && !isIntervalEdge(position);
    }

    private boolean isInCurrentMonth(int position) {
        Calendar date = fullWeeks.get(position);
        return date.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH);
    }

    private boolean isInsideInterval(int position) {
        Calendar date = fullWeeks.get(position);
        return comparator.compare(date, beginDate) >= 0 && comparator.compare(date, endDate) <= 0;
    }

    private boolean isIntervalEdge(int position) {
        Calendar date = fullWeeks.get(position);
        return comparator.compare(date, beginDate) == 0 || comparator.compare(date, endDate) == 0;
    }

    private boolean isDayWithContinue(int position) {
        if (!isInsideInterval(position) || isIntervalEdge(position)) return false;

        Calendar date = fullWeeks.get(position);

        int firstDayOfMonth = currentDate.getActualMinimum(Calendar.DAY_OF_MONTH);
        int lastDayOfMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);

        return dayOfMonth == firstDayOfMonth || dayOfMonth == lastDayOfMonth;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
//        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.day_item, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.dayNumber = (TextView) view.findViewById(R.id.day_number);
            viewHolder.continueMarker = view.findViewById(R.id.continue_marker);
            viewHolder.container = view.findViewById(R.id.container);

            view.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) view.getTag();
//        }

        Calendar date = (Calendar) getItem(position);

        if (isInCurrentMonth(position)) {
            viewHolder.dayNumber.setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));

            if (isInsideInterval(position)) {
                if (isIntervalEdge(position)) {
                    viewHolder.container.setBackgroundColor(COLOR_BG_INTERVAL_EDGE);
                } else {
                    viewHolder.container.setBackgroundColor(COLOR_BG_INSIDE_INTERVAL);
                    if (isDayWithContinue(position)) {
                        viewHolder.continueMarker.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            viewHolder.dayNumber.setVisibility(View.INVISIBLE);
        }

        return view;
    }

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

    private class ViewHolder {
        TextView dayNumber;
        View continueMarker;
        View container;
    }

    /**
     * Retrieve all the dates for a given calendar month Include previous month,
     * current month and next month.
     */
    private ArrayList<Calendar> getFullWeeks(int year, int month) {
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