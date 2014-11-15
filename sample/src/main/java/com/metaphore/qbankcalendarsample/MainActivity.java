package com.metaphore.qbankcalendarsample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.metaphore.qbankcalendar.*;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class MainActivity extends FragmentActivity implements
        QBankCalendarListener {

    private static final String TAG_CALENDAR_FRAGMENT = "tag_calendar_fragment";

    private QBankCalendarView calendarView;
    private QBankCalendarDialog dialog;

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

        findViewById(R.id.fragment_dialog_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragmentCalendarDialog();
            }
        });
        findViewById(R.id.regular_dialog_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegularCalendarDialog();
            }
        });
    }

    // You have to directly call onResume() for QBankCalendarView and QBankCalendarDialog
    @Override
    protected void onResume() {
        super.onResume();

        calendarView.onResume();

        if (dialog != null) {
            dialog.onResume();
        }
    }

    private void showFragmentCalendarDialog() {
        Calendar beginDate = calendarView.getBeginDate();
        Calendar endDate = calendarView.getEndDate();
        EditMode editMode = calendarView.getEditMode();

        QBankCalendarDialogFragment calendarFragment =
                QBankCalendarDialogFragment.newInstance(beginDate, endDate, editMode);

//        QBankCalendarDialogFragment calendarFragment =
//                QBankCalendarDialogFragment.newInstance(EditMode.END_DATE);

        calendarFragment.show(getSupportFragmentManager(), TAG_CALENDAR_FRAGMENT);
    }

    private void showRegularCalendarDialog() {
        Calendar beginDate = calendarView.getBeginDate();
        Calendar endDate = calendarView.getEndDate();
        EditMode editMode = calendarView.getEditMode();

        dialog = new QBankCalendarDialog(this, beginDate, endDate, editMode);
        dialog.setQBankCalendarListener(this);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
    }

    @Override
    public void onDateIntervalChanged(Calendar begin, Calendar end) {
        calendarView.setSelectedInterval(begin, end);
    }
}
