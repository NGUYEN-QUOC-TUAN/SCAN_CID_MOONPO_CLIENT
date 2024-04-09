package com.moonpo.service;

import com.moonpo.model.Employee;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmployeeService {
    public String checkEmployeeFormView(Employee employee) {
        String id = employee.getId();
        String no = employee.getNo();
        String name = employee.getName();
        String photo = employee.getPhoto();
        String birthday = employee.getDateOfBirth();
        String gender = employee.getGender();
        String issueDate = employee.getIssueDate();
        String address = employee.getAddress();
        if (id.isEmpty() || no.isEmpty() || name.isEmpty() || photo.isEmpty() || birthday.isEmpty() || gender.isEmpty() || issueDate.isEmpty() || address.isEmpty()) {
            return "Please fill all the fields !";
        } else if (!isNumeric(id) || id.length() < 5) {
            return "Please check ' Id ' (Number >= 5) !";
        } else if (!isNumeric(no) || no.length() != 12) {
            return "Please check ' No ' (No = 12) !";
        } else if (isNumeric(name) || name.isEmpty()) {
            return "Please check ' Name ' !";
        } else if (!isValidDateFormat(birthday)) {
            return "Invalid format for ' Birthday ' (year-month-day) !";
        } else if (!isValidDateFormat(issueDate)) {
            return "Invalid format for ' Issue Date ' (year-month-day) !";
        } else if (gender.isEmpty()) {
            return "Please check ' Gender ' !";
        } else {
            return "Success";
        }
    }

    public boolean isValidDateFormat(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return false;
        }
        if (dateStr.length() != 10) {
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public String[] splitString(String input, char delimiter) {
        List<String> parts = new ArrayList<>();
        int startIndex = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == delimiter) {
                parts.add(input.substring(startIndex, i));
                startIndex = i + 1;
            }
        }
        parts.add(input.substring(startIndex));
        return parts.toArray(new String[0]);
    }

    public String formatDateToForm(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
