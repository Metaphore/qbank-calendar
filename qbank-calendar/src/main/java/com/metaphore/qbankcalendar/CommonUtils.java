package com.metaphore.qbankcalendar;

import hirondelle.date4j.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CommonUtils {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public static Calendar cpy(Calendar original) {
        Calendar copy = Calendar.getInstance(original.getTimeZone());
        copy.setTime(original.getTime());
        return copy;
    }
}
