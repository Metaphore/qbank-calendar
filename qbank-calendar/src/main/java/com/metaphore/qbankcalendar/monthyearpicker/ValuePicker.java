package com.metaphore.qbankcalendar.monthyearpicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import com.metaphore.qbankcalendar.R;

class ValuePicker extends FrameLayout implements CompoundButton.OnCheckedChangeListener {
    private static final String LOG_TAG = ValuePicker.class.getSimpleName();

    private final View arrowLeft;
    private final View arrowRight;
    private final ValueButton valueButton;

    private PickerItemListener pickerItemListener;

    public ValuePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.picker_item, this, true);

        arrowLeft = findViewById(R.id.arrow_left);
        arrowRight = findViewById(R.id.arrow_right);
        valueButton = ((ValueButton) findViewById(R.id.value_button));

        arrowLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickerItemListener != null) {
                    pickerItemListener.onPreviousPressed(ValuePicker.this);
                }
            }
        });

        arrowRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickerItemListener != null) {
                    pickerItemListener.onNextPressed(ValuePicker.this);
                }
            }
        });

        valueButton.setOnCheckedChangeListener(this);
        valueButton.setClickable(true);
        boolean checked = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "checked", false);
        valueButton.setChecked(checked);
    }

    public void setPickerItemListener(PickerItemListener listener) {
        this.pickerItemListener = listener;
    }

    public void setText(CharSequence text) {
        valueButton.setText(text);
        invalidate();
        requestLayout();
    }

    public void setSelectedState(boolean selected) {
        valueButton.setChecked(selected);
    }

    public boolean isInSelectedState() {
        return valueButton.isChecked();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
        arrowLeft.setVisibility(checked ? VISIBLE : GONE);
        arrowRight.setVisibility(checked ? VISIBLE : GONE);

        if (checked && pickerItemListener != null) {
            pickerItemListener.onSelected(this);
        }

        invalidate();
        requestLayout();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ValuePicker.class.getName());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ValuePicker.class.getName());
    }

    public interface PickerItemListener {
        void onNextPressed(ValuePicker valuePicker);
        void onPreviousPressed(ValuePicker valuePicker);
        void onSelected(ValuePicker valuePicker);
    }
}
