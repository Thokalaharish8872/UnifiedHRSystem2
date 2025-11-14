package com.unifiedhr.system.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Utils {
    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    public static String generateEmployeeId(String companyId, int employeeCount) {
        String fallbackPrefix = "EMP";
        if (companyId != null) {
            String cleaned = companyId.replaceAll("[^A-Za-z0-9]", "");
            if (!cleaned.isEmpty()) {
                String padded = (cleaned + fallbackPrefix).toUpperCase(Locale.getDefault());
                fallbackPrefix = padded.substring(0, Math.min(3, padded.length()));
                if (fallbackPrefix.length() < 3) {
                    fallbackPrefix = String.format(Locale.getDefault(), "%-3s", fallbackPrefix).replace(' ', 'X');
                }
            }
        }

        int nextNumber = Math.max(0, employeeCount) + 1;
        return fallbackPrefix + String.format(Locale.getDefault(), "%04d", nextNumber);
    }
}








