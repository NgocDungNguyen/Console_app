package com.rentalsystem.ui;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import java.util.Arrays;
import java.util.List;

public class MenuBuilder {
    private final LineReader reader;
    private final Terminal terminal;

    public MenuBuilder(LineReader reader, Terminal terminal) {
        this.reader = reader;
        this.terminal = terminal;
    }

    public String showMainMenu() {
        List<String> options = Arrays.asList("add", "update", "delete", "list", "search", "report", "exit");
        return showMenu("Main Menu", options);
    }

    public String showEntityTypeMenu(String action) {
        List<String> options = Arrays.asList("tenant", "owner", "host", "property", "agreement");
        return showMenu(action + " Entity Type", options);
    }

    public String showReportMenu() {
        List<String> options = Arrays.asList("income", "occupancy", "tenant");
        return showMenu("Report Type", options);
    }

    private String showMenu(String title, List<String> options) {
        terminal.writer().println(new AttributedString(title, AttributedStyle.BOLD).toAnsi(terminal));
        for (int i = 0; i < options.size(); i++) {
            terminal.writer().println((i + 1) + ". " + options.get(i));
        }
        terminal.writer().flush();

        while (true) {
            String input = reader.readLine("Enter your choice: ").trim().toLowerCase();
            if (options.contains(input)) {
                return input;
            }
            try {
                int choice = Integer.parseInt(input);
                if (choice > 0 && choice <= options.size()) {
                    return options.get(choice - 1);
                }
            } catch (NumberFormatException ignored) {
            }
            terminal.writer().println("Invalid choice. Please try again.");
        }
    }
}