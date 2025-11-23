package ctu.cict.khanhtypo.forms;

import ctu.cict.khanhtypo.Main;
import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.utils.ResourceUtils;

import javax.swing.*;
import javax.swing.plaf.basic.DefaultMenuLayout;
import java.awt.*;
import java.util.Arrays;

public class BookEntry {
    private static final ImageIcon removeIcon;
    private static final ImageIcon updateIcon;
    private final IBookDB bookDB;
    private final Book book;
    private JPanel basePanel;
    private JTextPane content;
    private JButton removeButton;
    private JButton updateButton;
    private JPanel operationsPanel;
    private JPanel panel1;
    private JLabel confirmLabel;
    private JPanel contentsPanel;
    private int panelHeight;

    public BookEntry(IBookDB bookDB, Book book) {
        this.bookDB = bookDB;
        this.book = book;
        this.panel1.setLayout(new BoxLayout(this.panel1, BoxLayout.Y_AXIS));
        this.content.setFont(Main.FONT_MORE_OFFC_PRO);
        transformButton(removeButton, removeIcon);
        transformButton(updateButton, updateIcon);
        basePanel.setLayout(new DefaultMenuLayout(basePanel, BoxLayout.Y_AXIS));
        contentsPanel.setLayout(new BoxLayout(contentsPanel, BoxLayout.X_AXIS));
        operationsPanel.setLayout(new BoxLayout(operationsPanel, BoxLayout.X_AXIS));
        removeButton.addActionListener(e -> {
            if (confirmLabel.isVisible()) {
                bookDB.deleteBookEntry(book);
                confirmLabel.setVisible(false);
            } else {
                confirmLabel.setVisible(true);
            }
            SwingUtilities.updateComponentTreeUI(panel1);
        });
        SwingUtilities.updateComponentTreeUI(operationsPanel);
        SwingUtilities.updateComponentTreeUI(basePanel);
    }

    private void transformButton(JButton button, ImageIcon icon) {
        button.setIcon(icon);
        button.setSize(32, 32);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
    }

    private void load(Book book) {
        this.content.setText(
                toMultiline(
                        book.title(),
                        "Authors: " + book.authorsString(),
                        "Categories: " + book.categoriesString(),
                        "Status: " + book.statusString(),
                        "Pages: " + book.pageCount() + "  |  ISBN : " + book.isbnString(),
                        "Release Date: " + book.dateString()
                )
        );
    }

    private String toMultiline(String line1, String... lines) {
        this.panelHeight =(int)(30f * (lines.length + 1));
        return Arrays.stream(lines).reduce(line1, (a, b) -> a + "\n" + b);
    }

    static {
        removeIcon = new ImageIcon(ResourceUtils.getImage("remove.png").getScaledInstance(16, 16, Image.SCALE_FAST));
        updateIcon = new ImageIcon(ResourceUtils.getImage("update.png").getScaledInstance(16, 16, Image.SCALE_FAST));
    }

    public Component getBasePanel(Component entriesContainer) {
        this.load(this.book);
        SwingUtilities.invokeLater(() -> {
            Dimension size = basePanel.getSize();
            size.height = this.panelHeight;
            size.width = entriesContainer.getWidth();
            basePanel.setMinimumSize(size);
            basePanel.setMaximumSize(size);
            basePanel.setPreferredSize(size);
            SwingUtilities.updateComponentTreeUI(basePanel);
        });
        return this.basePanel;
    }

    public void resizeWidth(int targetWidth) {
        SwingUtilities.invokeLater(() -> {
            Dimension size = basePanel.getSize();
            size.width = targetWidth;
            basePanel.setSize(size);
            SwingUtilities.updateComponentTreeUI(basePanel);
        });
    }
}
