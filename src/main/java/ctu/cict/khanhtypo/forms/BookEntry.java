package ctu.cict.khanhtypo.forms;

import com.mongodb.client.model.geojson.MultiLineString;
import ctu.cict.khanhtypo.Main;
import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.utils.ResourceUtils;
import org.apache.commons.lang3.Strings;

import javax.swing.*;
import javax.swing.plaf.basic.DefaultMenuLayout;
import java.awt.*;
import java.util.Arrays;

public class BookEntry {
    private static final ImageIcon removeIcon;
    private static final ImageIcon updateIcon;
    private JPanel basePanel;
    private JTextPane content;
    private JButton removeButton;
    private JButton updateButton;

    public BookEntry() {
        this.content.setFont(Main.FONT_MORE_OFFC_PRO);
        transformButton(removeButton, removeIcon);
        transformButton(updateButton, updateIcon);
        basePanel.setLayout(new DefaultMenuLayout(basePanel, BoxLayout.X_AXIS));
    }

    private void transformButton(JButton button, ImageIcon icon) {
        button.setIcon(icon);
        button.setSize(32, 32);
        button.setContentAreaFilled(false);
    }

    public JPanel load(Book book) {
        this.content.setText(
                toMultiline(
                        book.title(),
                        "Authors: " + book.authorsString(),
                        "ISBN : " + book.ISBN()
                )
        );
        return this.basePanel;
    }

    private String toMultiline(String line1, String... lines) {
        return Arrays.stream(lines).reduce(line1, (a, b) -> a + "\n" + b);
    }

    static {
        removeIcon = new ImageIcon(ResourceUtils.getImage("remove.png").getScaledInstance(16, 16, Image.SCALE_FAST));
        updateIcon = new ImageIcon(ResourceUtils.getImage("update.png").getScaledInstance(16, 16, Image.SCALE_FAST));
    }
}
