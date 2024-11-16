package com.rentalsystem.ui;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

public class ProgressDisplay {
    private final Terminal terminal;

    public ProgressDisplay(Terminal terminal) {
        this.terminal = terminal;
    }

    public void showProgress(String message, int current, int total) {
        int width = 50;
        int progress = (int) ((double) current / total * width);
        
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < width; i++) {
            if (i < progress) {
                bar.append("=");
            } else if (i == progress) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }
        bar.append("]");

        String progressString = String.format("%s %s %d/%d", message, bar.toString(), current, total);
        
        terminal.writer().print("\r" + new AttributedString(progressString, AttributedStyle.DEFAULT).toAnsi(terminal));
        terminal.writer().flush();

        if (current == total) {
            terminal.writer().println();
        }
    }
}