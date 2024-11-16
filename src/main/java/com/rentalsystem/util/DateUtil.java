package com.rentalsystem.util;

import org.jline.reader.LineReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    static {
        dateFormat.setLenient(false);  // Strict date parsing
    }

    public static Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            return null;
        }
    }

    public static Date readDate(LineReader reader, String prompt) {
        Date date = null;
        while (date == null) {
            String input = reader.readLine(prompt);
            try {
                date = dateFormat.parse(input);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
        return date;
    }

    public static Date readOptionalDate(LineReader reader, String prompt) {
        String input = reader.readLine(prompt);
        if (input.isEmpty()) {
            return null;
        }
        try {
            return dateFormat.parse(input);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            return null;
        }
    }
}
