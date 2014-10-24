package com.metaphore.calendarpreview;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.metaphore.qbankcalendar.QBankCalendarFragment;


public class MainActivity extends FragmentActivity {

    public static final String TAG_CALENDAR_FRAGMENT = "tag_calendar_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QBankCalendarFragment calendarFragment = new QBankCalendarFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.calendar_placeholder, calendarFragment, TAG_CALENDAR_FRAGMENT)
                .commit();
    }
}
