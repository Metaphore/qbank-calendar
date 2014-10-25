package com.metaphore.qbankcalendar.dayview;

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
import com.metaphore.qbankcalendar.EditMode;
import com.metaphore.qbankcalendar.R;

import java.util.Calendar;

//TODO реализовать разделение на два режима: редактирование начала периода и конца
public class DayView extends FrameLayout implements AdapterView.OnItemClickListener {
    private static final String LOG_TAG = DayView.class.getSimpleName();

    private final GridView dayGrid;
    private final DayViewAdapter dayViewAdapter;

    private DateViewListener dateViewListener;

    private boolean broadcasting;

    public DayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.day_view, this, true);

        dayGrid = ((GridView) findViewById(R.id.day_grid));
        dayViewAdapter = new DayViewAdapter(context);
        dayGrid.setAdapter(dayViewAdapter);

        dayGrid.setOnItemClickListener(this);
    }

    public void setSelectedInterval(Calendar begin, Calendar end) {
        dayViewAdapter.setSelectionInterval(begin, end);
        dayGrid.invalidateViews();
    }

    public void setCurrentDate(Calendar date) {
        dayViewAdapter.setCurrentDate(date);
        dayGrid.invalidateViews();
    }

    public Calendar getCurrentDate() {
        return dayViewAdapter.getCurrentDate();
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

    public void setSelectedDate(Calendar date) {
        dayViewAdapter.setSelectedDate(date);
    }

    public void setEditMode(EditMode editMode) {
        dayViewAdapter.setEditMode(editMode);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Calendar newDate = ((Calendar) dayViewAdapter.getItem(position));

        if (dateViewListener != null) {
            dateViewListener.onDateSelected(newDate);
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
        void onDateSelected(Calendar date);
        void onShowNextMonth();
        void onShowPreviousMonth();
    }
}
