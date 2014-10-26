package com.metaphore.qbankcalendar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.Calendar;

public class QBankCalendarFragment extends DialogFragment {
    private static final String LOG_TAG = QBankCalendarFragment.class.getSimpleName();

    private QBankCalendarView calendarView;
    private QBankCalendarFragmentListener listener;

    //TODO добавить фабричный метод для создания с заданными датами

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qbank_calendar, container, false);

        calendarView = ((QBankCalendarView) view.findViewById(R.id.calendar_view));

        view.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWithSuccess();
            }
        });

        if (savedInstanceState == null) {
            Calendar begin = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.MONTH, 1);
            calendarView.setSelectedInterval(begin, end);
        }

        return view;
    }

    private void closeWithSuccess() {
        Calendar begin = calendarView.getBeginDate();
        Calendar end = calendarView.getEndDate();

        Log.d(LOG_TAG, "Interval dates was changed. New interval from " +
                CalendarUtils.DATE_FORMAT.format(begin.getTime()) + " to " +
                CalendarUtils.DATE_FORMAT.format(end.getTime()));

        // Notify listener
        if (listener != null) {
            listener.onDateIntervalChanged(begin, end);
        }

        // Close dialog
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof QBankCalendarFragmentListener) {
            listener = (QBankCalendarFragmentListener) activity;
        } else {
            throw new IllegalStateException("Underlying activity should implement QBankCalendarFragment.QBankCalendarFragmentListener to use QBankCalendarFragment fragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface QBankCalendarFragmentListener {
        void onDateIntervalChanged(Calendar begin, Calendar end);
    }
}
