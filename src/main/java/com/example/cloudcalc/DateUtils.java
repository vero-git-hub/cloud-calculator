package com.example.cloudcalc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {

    public LocalDate convertProfileOrTypeBadgeStartDate(String startDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(startDate, formatter);
    }

    public LocalDate extractDateFromValue(String value) {
        Pattern pattern = Pattern.compile("(\\w+\\s\\d+,\\s\\d+)");
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            String cleanedStr = matcher.group(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
            return LocalDate.parse(cleanedStr, formatter);
        }
        throw new IllegalArgumentException("Invalid date format in string: " + value);
    }

    public static String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.now();
        return dtf.format(localDate);
    }

}
