package ctu.cict.khanhtypo.books;

import ctu.cict.khanhtypo.utils.MathUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public enum BookStatus {
    PUBLISH("Released"),
    DISCONTINUED("Discontinued"),
    MEAP("Early-Access"),
    ;
    private final String displayText;

    BookStatus(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public static final class Renderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return MathUtils.make((JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus),
            label -> label.setText(((BookStatus) value).getDisplayText()));
        }
    }
}
