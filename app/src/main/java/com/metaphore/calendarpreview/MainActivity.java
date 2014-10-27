package com.metaphore.calendarpreview;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.metaphore.qbankcalendar.EditMode;
import com.metaphore.qbankcalendar.QBankCalendarDialogFragment;
import com.metaphore.qbankcalendar.QBankCalendarView;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class MainActivity extends FragmentActivity implements
        QBankCalendarDialogFragment.QBankCalendarFragmentListener{

    private static final String TAG_CALENDAR_FRAGMENT = "tag_calendar_fragment";

    private QBankCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = ((QBankCalendarView) findViewById(R.id.calendar_view));

        if (savedInstanceState == null) {
            Calendar begin = GregorianCalendar.getInstance();
            begin.add(Calendar.MONTH, -1);
            Calendar end = GregorianCalendar.getInstance();
            calendarView.setSelectedInterval(begin, end);
        }

        findViewById(R.id.show_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog();
            }
        });
    }

    private void showCalendarDialog() {
        Calendar beginDate = calendarView.getBeginDate();
        Calendar endDate = calendarView.getEndDate();
        EditMode editMode = calendarView.getEditMode();

        QBankCalendarDialogFragment calendarFragment =
                QBankCalendarDialogFragment.newInstance(beginDate, endDate, editMode);

//        QBankCalendarDialogFragment calendarFragment =
//                QBankCalendarDialogFragment.newInstance(EditMode.END_DATE);

        calendarFragment.show(getSupportFragmentManager(), TAG_CALENDAR_FRAGMENT);
    }

    @Override
    public void onDateIntervalChanged(Calendar begin, Calendar end) {
        calendarView.setSelectedInterval(begin, end);
    }
}
