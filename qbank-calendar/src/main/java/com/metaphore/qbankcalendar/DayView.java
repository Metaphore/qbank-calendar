package com.metaphore.qbankcalendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import java.util.Calendar;
import java.util.Comparator;

class DayView extends FrameLayout {
    private static final String LOG_TAG = DayView.class.getSimpleName();

    private final GridView dayGrid;
    private final DayViewAdapter dayViewAdapter;

    private DateViewListener dateViewListener;

    public DayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.day_view, this, true);

        dayGrid = ((GridView) findViewById(R.id.day_grid));
        dayViewAdapter = new DayViewAdapter(context);
        dayGrid.setAdapter(dayViewAdapter);

        dayGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Calendar newDate = ((Calendar) dayViewAdapter.getItem(position));
                onDateSelected(newDate);
            }
        });

        setClickable(true);
    }

    public void setSelectedInterval(Calendar begin, Calendar end) {
        dayViewAdapter.setSelectionInterval(begin, end);
        dayGrid.invalidateViews();
    }

    public void setCurrentDate(Calendar date) {
        dayViewAdapter.setCurrentMonth(date);
        dayGrid.invalidateViews();
    }

    public Calendar getCurrentDate() {
        return dayViewAdapter.getCurrentMonth();
    }

    public Calendar getEndDate() {
        return dayViewAdapter.getEndDate();
    }

    public Calendar getBeginDate() {
        return dayViewAdapter.getBeginDate();
    }

    public void setDateViewListener(DateViewListener listener) {
        dateViewListener = listener;
    }

    public void setEditMode(EditMode editMode) {
        dayViewAdapter.setEditMode(editMode);
    }

    private void onDateSelected(Calendar selectedDate) {
        EditMode editMode = dayViewAdapter.getEditMode();
        Calendar beginDate;
        Calendar endDate;

        switch (editMode) {
            case BEGIN_DATE:
                beginDate = selectedDate;
                endDate = dayViewAdapter.getEndDate();
                break;
            case END_DATE:
                beginDate = dayViewAdapter.getBeginDate();
                endDate = selectedDate;
                break;
            default:
                throw new IllegalStateException("Unexpected editMode: " + editMode);
        }

        if (dateViewListener != null) {
            dateViewListener.onNewPeriodSelected(beginDate, endDate);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(DayView.class.getName());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(DayView.class.getName());
    }

    public interface DateViewListener {
        void onNewPeriodSelected(Calendar begin, Calendar end);
        void onShowNextMonth();
        void onShowPreviousMonth();
    }
}
