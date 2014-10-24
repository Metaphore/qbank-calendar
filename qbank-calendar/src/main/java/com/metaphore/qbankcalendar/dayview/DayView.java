package com.metaphore.qbankcalendar.dayview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.GridView;
import com.metaphore.qbankcalendar.CalendarData;
import com.metaphore.qbankcalendar.R;

import java.util.Date;

public class DayView extends FrameLayout {
    private static final String LOG_TAG = DayView.class.getSimpleName();

    private final GridView dayGrid;
    private final CalendarDataAdapter calendarDataAdapter;

    private DateViewListener dateViewListener;

    private boolean broadcasting;

    public DayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.day_view, this, true);

        dayGrid = ((GridView) findViewById(R.id.day_grid));
        calendarDataAdapter = new CalendarDataAdapter(context);
        dayGrid.setAdapter(calendarDataAdapter);
    }

    public void setCalendarData(CalendarData calendarData) {
        calendarDataAdapter.setCalendarData(calendarData);
    }

    public void showDate(Date date) {
        calendarDataAdapter.setCurrentDate(date);
    }

    public void setDateViewListener(DateViewListener listener) {
        dateViewListener = listener;
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
    }

}
