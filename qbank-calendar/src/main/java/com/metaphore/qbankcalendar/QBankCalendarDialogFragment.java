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
import java.util.Date;
import java.util.GregorianCalendar;

public class QBankCalendarDialogFragment extends DialogFragment {
    private static final String LOG_TAG = QBankCalendarDialogFragment.class.getSimpleName();
    private static final String KEY_BEGIN_DATE = "key_begin_date";
    private static final String KEY_END_DATE = "key_end_date";
    private static final String KEY_EDIT_MODE = "key_edit_mode";
    private static final int WRONG_MODE = -1;

    private QBankCalendarView calendarView;
    private QBankCalendarFragmentListener listener;

    //region newInstance convenience methods

    /**
     * Instantiate new calendar dialog with period from today to today+month and BEGIN_DATE edit mode.
     */
    public static QBankCalendarDialogFragment newInstance() {
        return newInstance(EditMode.BEGIN_DATE);
    }

    /**
     * Instantiate new calendar dialog with period from today to today+month.
     */
    public static QBankCalendarDialogFragment newInstance(EditMode editMode) {
        Calendar calBegin = GregorianCalendar.getInstance();
        Calendar calEnd = GregorianCalendar.getInstance();
        calEnd.add(Calendar.MONTH, 1);

        return newInstance(calBegin, calEnd, editMode);
    }

    /**
     * Instantiate new calendar dialog with period of given dates.
     */
    public static QBankCalendarDialogFragment newInstance(Date beginDate, Date endDate, EditMode editMode) {
        if (beginDate == null) {
            throw new IllegalArgumentException("beginDate cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate cannot be null");
        }

        Calendar calBegin = GregorianCalendar.getInstance();
        Calendar calEnd = GregorianCalendar.getInstance();
        calBegin.setTime(beginDate);
        calEnd.setTime(endDate);

        return newInstance(calBegin, calEnd, editMode);
    }


    /**
     * Instantiate new calendar dialog with period of given dates.
     */
    public static QBankCalendarDialogFragment newInstance(Calendar beginDate, Calendar endDate, EditMode editMode) {
        if (beginDate == null) {
            throw new IllegalArgumentException("beginDate cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate cannot be null");
        }

        QBankCalendarDialogFragment fragment = new QBankCalendarDialogFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_BEGIN_DATE, beginDate.getTimeInMillis());
        args.putLong(KEY_END_DATE, endDate.getTimeInMillis());
        args.putInt(KEY_EDIT_MODE, editMode.ordinal());
        fragment.setArguments(args);

        return fragment;
    }
    //endregion

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
            int modeOrdinal = getArguments().getInt(KEY_EDIT_MODE, WRONG_MODE);
            long beginDateMs = getArguments().getLong(KEY_BEGIN_DATE, WRONG_MODE);
            long endDateMs = getArguments().getLong(KEY_END_DATE, WRONG_MODE);

            if (modeOrdinal == WRONG_MODE) {
                throw new IllegalStateException("You should use one of proper QBankCalendarDialogFragment#newInstance() methods to instantiate this class");
            }

            EditMode editMode = EditMode.values()[modeOrdinal];
            Calendar begin = GregorianCalendar.getInstance();
            Calendar end = GregorianCalendar.getInstance();
            begin.setTimeInMillis(beginDateMs);
            end.setTimeInMillis(endDateMs);

            calendarView.setSelectedInterval(begin, end);
            calendarView.setEditMode(editMode);
        }

        return view;
    }

    private void closeWithSuccess() {
        Calendar begin = calendarView.getBeginDate();
        Calendar end = calendarView.getEndDate();

        Log.d(LOG_TAG, "Calendar dialog is successfully closing with selected interval [ " +
                CalendarUtils.DATE_FORMAT.format(begin.getTime()) + " - " +
                CalendarUtils.DATE_FORMAT.format(end.getTime()) + " ]");

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
