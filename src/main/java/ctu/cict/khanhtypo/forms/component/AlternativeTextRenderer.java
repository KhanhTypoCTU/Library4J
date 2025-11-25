package ctu.cict.khanhtypo.forms.component;

import ctu.cict.khanhtypo.utils.MathUtils;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public final class AlternativeTextRenderer<T> extends DefaultListCellRenderer {
    private final Function<T, String> factory;

    public AlternativeTextRenderer(
            Function<T, String> factory) {
        this(LEADING, factory);
    }

    public AlternativeTextRenderer(
            @MagicConstant(stringValues = {"SwingConstants.LEFT",
                    "SwingConstants.CENTER",
                    "SwingConstants.RIGHT",
                    "SwingConstants.LEADING",
                    "SwingConstants.TRAILING"})
            int horizontalAlignment,
            Function<T, String> factory) {
        this.factory = factory;
        super.setHorizontalAlignment(horizontalAlignment);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return MathUtils.make((JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus),
                label -> label.setText(this.factory.apply((T) value)));
    }
}
