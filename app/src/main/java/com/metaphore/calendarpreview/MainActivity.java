package com.metaphore.calendarpreview;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import com.metaphore.qbankcalendar.QBankCalendarFragment;
import com.metaphore.qbankcalendar.QBankCalendarView;

import java.util.Calendar;


public class MainActivity extends FragmentActivity implements
        QBankCalendarFragment.QBankCalendarFragmentListener{

    private static final String TAG_CALENDAR_FRAGMENT = "tag_calendar_fragment";

    private QBankCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = ((QBankCalendarView) findViewById(R.id.calendar_view));

        findViewById(R.id.show_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog();
            }
        });
    }

    private void showCalendarDialog() {
        QBankCalendarFragment calendarFragment = new QBankCalendarFragment();
        calendarFragment.show(getSupportFragmentManager(), TAG_CALENDAR_FRAGMENT);
    }

    @Override
    public void onDateIntervalChanged(Calendar begin, Calendar end) {
        calendarView.setSelectedInterval(begin, end);
    }
}
