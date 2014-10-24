package com.metaphore.qbankcalendar.dayview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.metaphore.qbankcalendar.CalendarData;
import com.metaphore.qbankcalendar.CommonUtils;
import com.metaphore.qbankcalendar.R;
import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class CalendarDataAdapter extends BaseAdapter {
    private static final int DAYS_AMOUNT = 7*6;

    private final Context context;
    private final GregorianCalendar currentMonth = new GregorianCalendar();
    private final GregorianCalendar tmpCalendar = new GregorianCalendar();

    private CalendarData calendarData;
    private ArrayList<DateTime> fullWeeks;

    CalendarDataAdapter(Context context) {
        this.context = context;
        setCurrentDate(new Date());
    }

    public void setCalendarData(CalendarData calendarData) {
        this.calendarData = calendarData;
        notifyDataSetInvalidated();
    }

    public void setCurrentDate(Date date) {
        currentMonth.setTime(date);

        fullWeeks = CommonUtils.getFullWeeks(
                currentMonth.get(Calendar.YEAR),
                currentMonth.get(Calendar.MONTH)+1);

        notifyDataSetInvalidated();
    }

    public Date getCurrentDate() {
        return currentMonth.getTime();
    }

    @Override
    public int getCount() {
        return DAYS_AMOUNT;
    }

    @Override
    public Object getItem(int position) {
        return calendarData;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getDayNumberByPosition(int position) {
        return fullWeeks.get(position).getDay();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.day_item, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.dayNumber = (TextView) view.findViewById(R.id.day_number);
            viewHolder.continueMarker = view.findViewById(R.id.continue_marker);
            viewHolder.container = view.findViewById(R.id.container);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        int dayNumber = getDayNumberByPosition(position);
        viewHolder.dayNumber.setText(String.valueOf(dayNumber));

        return view;
    }

    private class ViewHolder {
        TextView dayNumber;
        View continueMarker;
        View container;
    }
}
