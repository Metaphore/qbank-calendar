package com.metaphore.qbankcalendar;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import java.util.Calendar;

public class QBankCalendarDialog extends Dialog {

    private QBankCalendarListener dialogListener;

    public QBankCalendarDialog(Context context, Calendar beginDate, Calendar endDate, EditMode editMode) {
        super(context, R.style.DialogStyle);

        if (beginDate == null) {
            throw new IllegalArgumentException("beginDate cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate cannot be null");
        }
        if (editMode == null) {
            throw new IllegalArgumentException("editMode cannot be null");
        }

        this.setContentView(R.layout.qbank_dialog);

        final QBankCalendarView calendarView = ((QBankCalendarView) findViewById(R.id.calendar_view));
        calendarView.setSelectedInterval(beginDate, endDate);
        calendarView.setEditMode(editMode);

        findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.onDateIntervalChanged(
                            calendarView.getBeginDate(),
                            calendarView.getEndDate());
                }

                dismiss();
            }
        });
    }

    public void setQBankCalendarListener(QBankCalendarListener dialogListener) {
        this.dialogListener = dialogListener;
    }
}
