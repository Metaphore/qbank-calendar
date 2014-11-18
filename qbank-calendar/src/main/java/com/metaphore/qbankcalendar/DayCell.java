package com.metaphore.qbankcalendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Comparator;

class DayCell extends FrameLayout {
    private static final String LOG_TAG = DayCell.class.getSimpleName();

    private static final int[] WITHIN_MONTH = {R.attr.state_within_month};
    private static final int[] WITHIN_INTERVAL = {R.attr.state_within_interval};
    private static final int[] CONTINUE_RIGHT = {R.attr.state_continue_right};
    private static final int[] CONTINUE_LEFT = {R.attr.state_continue_left};
    private static final int[] ACTIVE_EDGE = {R.attr.state_active_edge};
    private static final int[] PASSIVE_EDGE = {R.attr.state_passive_edge};
    private static final int[] BEYOND_TODAY = {R.attr.state_beyond_today};

    private final TextView dayNumber;
    private final View continueMarkerRight;
    private final View continueMarkerLeft;
    private final ColorStateList textColor;

    // You can find field descriptions to DayCell#updateViewState
    private boolean withinMonth;
    private boolean withinInterval;
    private boolean continueRight;
    private boolean continueLeft;
    private boolean activeEdge;
    private boolean passiveEdge;
    private boolean beyondToday;

    public DayCell(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.day_cell, this, true);

        dayNumber = ((TextView) findViewById(R.id.day_number));
        continueMarkerRight = findViewById(R.id.continue_marker_right);
        continueMarkerLeft = findViewById(R.id.continue_marker_left);

        textColor = getResources().getColorStateList(R.color.day_cell_text);
    }

    public void updateViewState(Calendar date, Calendar currentMonth, Calendar periodBegin,
                                Calendar periodEnd, EditMode editMode) {

        // Attributes calculations
        {
            Comparator<Calendar> comparator = InternalUtils.DATE_COMPARATOR;

            // Reset all attrs
            withinMonth = false;
            withinInterval = false;
            continueRight = false;
            continueLeft = false;
            activeEdge = false;
            passiveEdge = false;
            beyondToday = false;

            // withinMonth determines whenever day inside of current month
            withinMonth = date.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH);

            // withinMonth determines whenever day inside of selected period
            withinInterval = comparator.compare(date, periodBegin) >= 0 &&
                    comparator.compare(date, periodEnd) <= 0;

            // beyondToday determines whenever day is after actual current date
            beyondToday = comparator.compare(date, InternalUtils.getCurrentDate()) > 0;

            // activeEdge and passiveEdge are edges of selected period and depend on current editMode
            switch (editMode) {
                case BEGIN_DATE:
                    activeEdge = comparator.compare(date, periodBegin) == 0;
                    passiveEdge = comparator.compare(date, periodEnd) == 0;
                    break;
                case END_DATE:
                    activeEdge = comparator.compare(date, periodEnd) == 0;
                    passiveEdge = comparator.compare(date, periodBegin) == 0;
                    break;
                default:
                    throw new IllegalStateException("Unexpected editMode: " + editMode);
            }

            // continueRight and continueLeft determines whenever currDay is edge day of current month and within selected period
            if (withinMonth && withinInterval && !activeEdge && !passiveEdge) {
                int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);

                if (dayOfMonth == currentMonth.getActualMinimum(Calendar.DAY_OF_MONTH)) {
                    continueLeft = true;
                }
                if (dayOfMonth == currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    continueRight = true;
                }
            }
        }

        // Updating view
        {
            dayNumber.setVisibility(withinMonth ? VISIBLE : GONE);
            dayNumber.setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));

            continueMarkerRight.setVisibility(continueRight ? VISIBLE : GONE);
            continueMarkerLeft.setVisibility(continueLeft ? VISIBLE : GONE);

            refreshDrawableState();
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (textColor != null) {
            int colorForState = textColor.getColorForState(getDrawableState(), 0);
            dayNumber.setTextColor(colorForState);
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 7);
        if (withinMonth) {
            mergeDrawableStates(drawableState, WITHIN_MONTH);
        }
        if (withinInterval) {
            mergeDrawableStates(drawableState, WITHIN_INTERVAL);
        }
        if (continueRight) {
            mergeDrawableStates(drawableState, CONTINUE_RIGHT);
        }
        if (continueLeft) {
            mergeDrawableStates(drawableState, CONTINUE_LEFT);
        }
        if (activeEdge) {
            mergeDrawableStates(drawableState, ACTIVE_EDGE);
        }
        if (passiveEdge) {
            mergeDrawableStates(drawableState, PASSIVE_EDGE);
        }
        if (beyondToday) {
            mergeDrawableStates(drawableState, BEYOND_TODAY);
        }
        return drawableState;
    }
}
