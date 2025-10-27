package com.example.medmanage.processor;

import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringProcessor {

    // Allows letters, numbers, spaces, and hyphens. Does not allow empty strings.
    private static final Pattern namePattern = Pattern.compile("^[a-zA-Z0-9- ]+$");

    /**
     * Validates a medication or brand name.
     * Allows only alphanumeric characters, spaces, and hyphens. Cannot be empty.
     */
    public static boolean isValidName(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return namePattern.matcher(input.trim()).matches();
    }

    /**
     * Converts a string to Title Case.
     * Example: "panado extra" -> "Panado Extra"
     */
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        input = input.trim().toLowerCase();
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }

    /**
     * Formats the dosage string.
     * Ensures it ends with a unit (defaulting to "mg") and standardizes spacing.
     * Example: "500" -> "500 mg"
     * Example: "20ml" -> "20 ml"
     * Example: "100 mg" -> "100 mg"
     */
    public static String formatDosage(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        input = input.trim();
        String lowerInput = input.toLowerCase();

        String numberPart = "";
        String unitPart = "";

        // Regex to find number and unit (e.g., "500", "mg", "ml")
        Pattern pattern = Pattern.compile("([0-9.]+)\\s*(mg|ml)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            numberPart = matcher.group(1); // The number
            unitPart = matcher.group(2);   // The unit (mg or ml)
        } else {
            // No number found, extract only digits and decimal point
            numberPart = input.replaceAll("[^0-9.]", "");
        }

        // Clean up number part (e.g., remove trailing decimal if it's .0)
        if (!TextUtils.isEmpty(numberPart)) {
            try {
                double num = Double.parseDouble(numberPart);
                if (num == Math.floor(num)) { // Check if it's a whole number
                    numberPart = String.valueOf((int) num);
                } else {
                    numberPart = String.valueOf(num); // Keep decimal if needed
                }
            } catch (NumberFormatException e) {
                // Handle error or return empty/default
                numberPart = "0"; // Or handle error appropriately
            }
        }


        if (TextUtils.isEmpty(unitPart)) {
            if (lowerInput.contains("ml")) {
                unitPart = "ml";
            } else {
                unitPart = "mg"; // Default to "mg"
            }
        } else {
            unitPart = unitPart.toLowerCase(); // Ensure unit is lowercase
        }

        return numberPart + " " + unitPart;
    }

    /**
     * Extracts the numerical value from a dosage string.
     * Example: "500 mg" -> 500.0
     * Example: "20ml" -> 20.0
     * Returns 0.0 if no valid number is found.
     */
    public static double parseDosageValue(String input) {
        if (input == null || input.isEmpty()) {
            return 0.0;
        }
        input = input.trim();

        // Regex to find the first sequence of digits and optional decimal point
        Pattern pattern = Pattern.compile("([0-9.]+)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0.0; // Invalid number format
            }
        }
        return 0.0; // No number found
    }
}

