package com.rentalsystem.ui;

import com.rentalsystem.model.*;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableFormatter {
    private final Terminal terminal;

    // ANSI color codes
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public TableFormatter(Terminal terminal) {
        this.terminal = terminal;
    }

    public void printTable(String title, List<String> options, String color) {
        int maxWidth = Math.max(
            title.length(),
            options.stream().mapToInt(String::length).max().orElse(0)
        ) + 6; // Increased padding

        printBorder('╔', '╗', '═', maxWidth);
        printCenteredText(title, maxWidth);
        printBorder('╠', '╣', '═', maxWidth);

        for (int i = 0; i < options.size(); i++) {
            printText((i + 1) + ". " + options.get(i), maxWidth);
        }

        printBorder('╚', '╝', '═', maxWidth);
    }

    private void printText(String text, int width) {
        if (text.length() > width - 3) {
            // If the text is too long, truncate it and add ellipsis
            text = text.substring(0, width - 6) + "...";
        }
        int padding = Math.max(0, width - text.length() - 3);
        System.out.println("║ " + text + " ".repeat(padding) + "║");
    }

    public void printDataTable(List<String> headers, List<List<String>> rows, String color) {
        List<Integer> columnWidths = getColumnWidths(headers, rows);
        
        printDataBorder('┌', '┐', '─', '┬', columnWidths);
        printDataRow(headers, columnWidths, true);
        printDataBorder('├', '┤', '─', '┼', columnWidths);

        for (List<String> row : rows) {
            printDataRow(row, columnWidths, false);
        }
        printDataBorder('└', '┘', '─', '┴', columnWidths);
    }

    private void printBorder(char left, char right, char fill, int width) {
        System.out.println(left + String.valueOf(fill).repeat(width - 2) + right);
    }

    private void printCenteredText(String text, int width) {
        int padding = (width - text.length()) / 2;
        System.out.println("║" + " ".repeat(padding) + text + " ".repeat(width - text.length() - padding - 2) + "║");
    }

    private void printDataBorder(char left, char right, char fill, char separator, List<Integer> columnWidths) {
        System.out.print(left);
        for (int i = 0; i < columnWidths.size(); i++) {
            System.out.print(String.valueOf(fill).repeat(columnWidths.get(i) + 2));
            if (i < columnWidths.size() - 1) {
                System.out.print(separator);
            }
        }
        System.out.println(right);
    }

    private void printDataRow(List<String> cells, List<Integer> columnWidths, boolean isHeader) {
        System.out.print("│");
        for (int i = 0; i < cells.size(); i++) {
            String cell = cells.get(i);
            System.out.print(" " + String.format("%-" + columnWidths.get(i) + "s", cell) + " ");
            if (i < cells.size() - 1) {
                System.out.print("│");
            }
        }
        System.out.println("│");
    }

    private List<Integer> getColumnWidths(List<String> headers, List<List<String>> rows) {
        List<Integer> widths = new ArrayList<>(headers.size());
        for (int i = 0; i < headers.size(); i++) {
            int maxWidth = headers.get(i).length();
            for (List<String> row : rows) {
                maxWidth = Math.max(maxWidth, row.get(i).length());
            }
            widths.add(maxWidth);
        }
        return widths;
    }

    public void printStatusBar(String currentUser, String date, String time) {
        List<String> headers = Arrays.asList("Current User", "Date", "Time");
        List<List<String>> data = Arrays.asList(Arrays.asList(currentUser, date, time));
        printDataTable(headers, data, ANSI_YELLOW);
    }
    
    public void printOwnerTable(List<Owner> owners) {
        List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
        List<List<String>> rows = new ArrayList<>();

        for (Owner owner : owners) {
            rows.add(Arrays.asList(
                owner.getId(),
                owner.getFullName(),
                owner.getDateOfBirth().toString(),
                owner.getContactInformation()
            ));
        }

        printDataTable(headers, rows, ANSI_CYAN);
    }

    public void printHostTable(List<Host> hosts) {
        List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
        List<List<String>> rows = new ArrayList<>();

        for (Host host : hosts) {
            rows.add(Arrays.asList(
                host.getId(),
                host.getFullName(),
                host.getDateOfBirth().toString(),
                host.getContactInformation()
            ));
        }

        printDataTable(headers, rows, ANSI_CYAN);
    }

    public void printPropertyTable(List<Property> properties) {
        List<String> headers = Arrays.asList("ID", "Address", "Price", "Status", "Owner");
        List<List<String>> rows = new ArrayList<>();

        for (Property property : properties) {
            rows.add(Arrays.asList(
                property.getPropertyId(),
                property.getAddress(),
                String.format("%.2f", property.getPrice()),
                property.getStatus().toString(),
                property.getOwner().getFullName()
            ));
        }

        printDataTable(headers, rows, ANSI_CYAN);
    }

    public void printRentalAgreementTable(List<RentalAgreement> agreements) {
        List<String> headers = Arrays.asList("ID", "Property", "Tenant", "Start Date", "End Date", "Rent Amount", "Status");
        List<List<String>> rows = new ArrayList<>();

        for (RentalAgreement agreement : agreements) {
            rows.add(Arrays.asList(
                agreement.getAgreementId(),
                agreement.getProperty().getPropertyId(),
                agreement.getMainTenant().getFullName(),
                agreement.getStartDate().toString(),
                agreement.getEndDate().toString(),
                String.format("%.2f", agreement.getRentAmount()),
                agreement.getStatus().toString()
            ));
        }

        printDataTable(headers, rows, ANSI_CYAN);
    }
}