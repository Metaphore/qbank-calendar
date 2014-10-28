package com.metaphore.qbankcalendar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import java.util.Calendar;

class DayView extends FrameLayout {
    private static final String LOG_TAG = DayView.class.getSimpleName();
    private static final String KEY_INSTANCE_STATE = "instanceState";
    private static final String KEY_CURRENT_DATE = "current_date";
    private static final String KEY_BEGIN_DATE = "begin_date";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_EDIT_MODE = "edit_mode";

    private final GridView dayGrid;
    private final DayViewAdapter dayViewAdapter;

    private DateViewListener dateViewListener;

    private final GestureDetector gestureDetector;

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
        setLongClickable(true);

        gestureDetector = new GestureDetector(context, new GestureListener());
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

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putLong(KEY_CURRENT_DATE, dayViewAdapter.getCurrentMonth().getTimeInMillis());
        bundle.putLong(KEY_BEGIN_DATE, dayViewAdapter.getBeginDate().getTimeInMillis());
        bundle.putLong(KEY_END_DATE, dayViewAdapter.getEndDate().getTimeInMillis());
        bundle.putInt(KEY_EDIT_MODE, dayViewAdapter.getEditMode().ordinal());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            Calendar currentDate = InternalUtils.fromMs(bundle.getLong(KEY_CURRENT_DATE));
            Calendar beginDate = InternalUtils.fromMs(bundle.getLong(KEY_BEGIN_DATE));
            Calendar endDate = InternalUtils.fromMs(bundle.getLong(KEY_END_DATE));
            EditMode editMode = EditMode.values()[bundle.getInt(KEY_EDIT_MODE)];

            setCurrentDate(currentDate);
            setSelectedInterval(beginDate, endDate);
            setEditMode(editMode);

            state = bundle.getParcelable(KEY_INSTANCE_STATE);
        }
        super.onRestoreInstanceState(state);
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

    //region Swipe to change month implementation
    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) &&
                    Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD &&
                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }

        private void onSwipeRight() {
            Log.d(LOG_TAG, "Swipe right");

            if (dateViewListener != null) {
                dateViewListener.onShowPreviousMonth();
            }
        }

        private void onSwipeLeft() {
            Log.d(LOG_TAG, "Swipe left");

            // If shown month is a actual month, we cancel change to next month action
            if (InternalUtils.DATE_COMPARATOR.compare(getCurrentDate(), InternalUtils.CURRENT_DATE) >= 0) {
                return;
            }

            if (dateViewListener != null) {
                dateViewListener.onShowNextMonth();
            }
        }
    }
    //endregion
}
