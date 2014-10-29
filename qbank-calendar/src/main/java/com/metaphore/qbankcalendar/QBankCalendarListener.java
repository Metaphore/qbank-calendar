package com.metaphore.qbankcalendar;

import java.util.Calendar;

public interface QBankCalendarListener {
    void onDateIntervalChanged(Calendar begin, Calendar end);
}
