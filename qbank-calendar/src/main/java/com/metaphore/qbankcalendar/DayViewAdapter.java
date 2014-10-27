package com.metaphore.qbankcalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

class DayViewAdapter extends BaseAdapter {
    private static final int DAYS_AMOUNT = 7*6;
    private static final Comparator<Calendar> COMPARATOR = InternalUtils.DATE_COMPARATOR;

    private final Context context;
    private final Calendar beginDate = GregorianCalendar.getInstance();
    private final Calendar endDate = GregorianCalendar.getInstance();
    // Used to determine visible month
    private final Calendar currentMonth = GregorianCalendar.getInstance();

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
        if (this.editMode == editMode) return;

        this.editMode = editMode;

        generateWeeksData();
        notifyDataSetChanged();
    }

    public EditMode getEditMode() {
        return editMode;
    }

    public void setSelectionInterval(Calendar begin, Calendar end) {
        if (COMPARATOR.compare(beginDate, begin) == 0 && COMPARATOR.compare(endDate, end) == 0) return;

        beginDate.setTime(begin.getTime());
        endDate.setTime(end.getTime());

        generateWeeksData();
        notifyDataSetChanged();
    }

    private void generateWeeksData() {
        if (COMPARATOR.compare(beginDate, endDate) >= 0) {
            throw new IllegalStateException("Begin date cannot be equal or greater than end date.");
        }
        fullWeeks = InternalUtils.getFullWeeks(
                currentMonth.get(Calendar.YEAR),
                currentMonth.get(Calendar.MONTH));
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
               !isBeyondToday(position) &&
               !isIntervalEdge(position);
    }

    private boolean isInCurrentMonth(int position) {
        Calendar date = fullWeeks.get(position);
        return date.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH);
    }

    private boolean isBeyondToday(int position) {
        Calendar date = fullWeeks.get(position);
        return COMPARATOR.compare(date, InternalUtils.CURRENT_DATE) > 0;
    }

    private boolean isIntervalEdge(int position) {
        Calendar date = fullWeeks.get(position);
        return COMPARATOR.compare(date, beginDate) == 0 || COMPARATOR.compare(date, endDate) == 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        DayCell dayCell;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.day_grid_item, parent, false);

            dayCell = (DayCell) view.findViewById(R.id.day_cell);
            view.setTag(dayCell);
        } else {
            dayCell = (DayCell) view.getTag();
        }

        Calendar date = (Calendar) getItem(position);
        dayCell.updateViewState(date, currentMonth, beginDate, endDate, editMode);

        return view;
    }
}