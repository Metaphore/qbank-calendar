package com.metaphore.qbankcalendar.dayview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.metaphore.qbankcalendar.EditMode;
import com.metaphore.qbankcalendar.R;

import java.util.Calendar;
import java.util.Comparator;

class DayCell extends FrameLayout {
    private static final String LOG_TAG = DayCell.class.getSimpleName();

    private static final int[] WITHIN_MONTH = {R.attr.state_within_month};
    private static final int[] WITHIN_INTERVAL = {R.attr.state_within_interval};
    private static final int[] CONTINUE_EDGE = {R.attr.state_continue_edge};
    private static final int[] ACTIVE_EDGE = {R.attr.state_active_edge};
    private static final int[] PASSIVE_EDGE = {R.attr.state_passive_edge};

    private final TextView dayNumber;
    private final View continueMarker;
    private final ColorStateList textColor;

    // You can find field descriptions to DayCell#updateViewState
    private boolean withinMonth;
    private boolean withinInterval;
    private boolean continueEdge;
    private boolean activeEdge;
    private boolean passiveEdge;

    private Comparator<Calendar> comparator;

    public DayCell(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.day_cell, this, true);

        dayNumber = ((TextView) findViewById(R.id.day_number));
        continueMarker = findViewById(R.id.continue_marker);

        textColor = getResources().getColorStateList(R.color.day_cell_text);
    }

    public void setComparator(Comparator<Calendar> comparator) {
        this.comparator = comparator;
    }

    public void updateViewState(Calendar currDay, Calendar currentMonth, Calendar periodBegin,
                                Calendar periodEnd, EditMode editMode) {

        if (comparator == null) {
            throw new IllegalStateException("Comparator should be set at this moment.");
        }

        // Attributes calculations
        {
            // Reset all attrs
            withinMonth = false;
            withinInterval = false;
            continueEdge = false;
            activeEdge = false;
            passiveEdge = false;

            // withinMonth determines whenever day inside of current month
            withinMonth = currDay.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH);

            // withinMonth determines whenever day inside of selected period
            withinInterval = comparator.compare(currDay, periodBegin) >= 0 &&
                    comparator.compare(currDay, periodEnd) <= 0;

            // activeEdge and passiveEdge are edges of selected period and depend on current editMode
            switch (editMode) {
                case BEGIN_DATE:
                    activeEdge = comparator.compare(currDay, periodBegin) == 0;
                    passiveEdge = comparator.compare(currDay, periodEnd) == 0;
                    break;
                case END_DATE:
                    activeEdge = comparator.compare(currDay, periodEnd) == 0;
                    passiveEdge = comparator.compare(currDay, periodBegin) == 0;
                    break;
                default:
                    throw new IllegalStateException("Unexpected editMode: " + editMode);
            }

            // continueEdge determines whenever currDay is edge day of current month and within selected period
            if (withinMonth && withinInterval && !activeEdge && !passiveEdge) {
                int firstDayOfMonth = currentMonth.getActualMinimum(Calendar.DAY_OF_MONTH);
                int lastDayOfMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
                int dayOfMonth = currDay.get(Calendar.DAY_OF_MONTH);

                continueEdge = dayOfMonth == firstDayOfMonth || dayOfMonth == lastDayOfMonth;
            }
        }

        // Updating view
        {
            dayNumber.setVisibility(withinMonth ? VISIBLE : GONE);
            dayNumber.setText(String.valueOf(currDay.get(Calendar.DAY_OF_MONTH)));

            continueMarker.setVisibility(continueEdge ? VISIBLE : GONE);

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
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 5);
        if (withinMonth) {
            mergeDrawableStates(drawableState, WITHIN_MONTH);
        }
        if (withinInterval) {
            mergeDrawableStates(drawableState, WITHIN_INTERVAL);
        }
        if (continueEdge) {
            mergeDrawableStates(drawableState, CONTINUE_EDGE);
        }
        if (activeEdge) {
            mergeDrawableStates(drawableState, ACTIVE_EDGE);
        }
        if (passiveEdge) {
            mergeDrawableStates(drawableState, PASSIVE_EDGE);
        }
        return drawableState;
    }
}
