package com.metaphore.qbankcalendar;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.metaphore.qbankcalendar.dayview.DayView;
import com.metaphore.qbankcalendar.modepicker.ModePicker;
import com.metaphore.qbankcalendar.monthyearpicker.MonthYearPicker;

import java.util.Calendar;

public class QBankCalendarFragment extends DialogFragment {
    private static final String LOG_TAG = QBankCalendarFragment.class.getSimpleName();
    private QBankCalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qbank_calendar, container, false);

        calendarView = ((QBankCalendarView) view.findViewById(R.id.calendar_view));

        if (savedInstanceState == null) {
            Calendar begin = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.MONTH, 1);
            calendarView.setSelectedInterval(begin, end);
        }

        return view;
    }
}
