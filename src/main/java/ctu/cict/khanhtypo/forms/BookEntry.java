package ctu.cict.khanhtypo.forms;

import com.google.common.util.concurrent.Runnables;
import ctu.cict.khanhtypo.Main;
import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.books.BookId;
import ctu.cict.khanhtypo.forms.fillableform.UpdateBooksScreen;
import ctu.cict.khanhtypo.utils.ResourceUtils;
import ctu.cict.khanhtypo.utils.ScreenUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.plaf.basic.DefaultMenuLayout;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

public class BookEntry {
    private static final ImageIcon removeIcon;
    private static final ImageIcon updateIcon;
    private Book book;
    private JPanel basePanel;
    private JTextPane content;
    private JButton removeButton;
    private JButton updateButton;
    private JPanel operationsPanel;
    private JPanel panel1;
    private JLabel confirmLabel;
    private JPanel contentsPanel;
    private int panelHeight;
    private Runnable changeListener;

    public BookEntry(BookDatabaseScreen bookDB, Book book, boolean withButtons) {
        this.book = book;
        this.changeListener = Runnables.doNothing();
        this.panel1.setLayout(new BoxLayout(this.panel1, BoxLayout.Y_AXIS));
        this.content.setFont(Main.FONT_MORE_OFFC_PRO);
        transformButton(removeButton, removeIcon);
        transformButton(updateButton, updateIcon);
        basePanel.setLayout(new DefaultMenuLayout(basePanel, BoxLayout.Y_AXIS));
        contentsPanel.setLayout(new BoxLayout(contentsPanel, BoxLayout.X_AXIS));
        operationsPanel.setLayout(new BoxLayout(operationsPanel, BoxLayout.X_AXIS));
        if (withButtons) {
            removeButton.addActionListener(e -> {
                if (confirmLabel.isVisible()) {
                    deleteBookEntry(bookDB);
                    confirmLabel.setVisible(false);
                } else {
                    confirmLabel.setVisible(true);
                }
            });

            updateButton.addActionListener(e ->
                    SwingUtilities.invokeLater(() -> {
                        JDialog dialog = new JDialog(Main.baseFrame, "Updating a book", true);
                        FillableFormScreen updateScreen = new UpdateBooksScreen(bookDB, dialog, this);
                        dialog.setContentPane(updateScreen.getBasePanel());
                        ScreenUtils.packFrame(dialog);
                        dialog.setVisible(true);
                    })
            );
        } else operationsPanel.setVisible(false);
    }

    void deleteBookEntry(IBookDataBridge databaseBridge) {
        databaseBridge.deleteBookEntry(this);
        this.setChanged();
    }

    private void setChanged() {
        this.changeListener.run();
    }

    ;

    private void transformButton(JButton button, ImageIcon icon) {
        button.setIcon(icon);
        button.setSize(32, 32);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
    }

    public BookEntry setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
        return this;
    }

    public void load() {
        this.content.setText(
                toMultiline(
                        "Title: " + book.title(),
                        "Authors: " + book.authorsString(),
                        "Categories: " + book.categoriesString(),
                        "Status: " + book.statusString(),
                        "Pages: " + book.pageCount() + "  |  ISBN : " + book.isbnString(),
                        "Release Date: " + book.dateString()
                )
        );
    }


    private String toMultiline(String line1, String... lines) {
        this.panelHeight = (int) (30f * (lines.length + 1));
        return Arrays.stream(lines).reduce(line1, (a, b) -> a + "\n" + b);
    }

    static {
        removeIcon = new ImageIcon(ResourceUtils.getImage("remove.png").getScaledInstance(16, 16, Image.SCALE_FAST));
        updateIcon = new ImageIcon(ResourceUtils.getImage("update.png").getScaledInstance(16, 16, Image.SCALE_FAST));
    }

    public void loadEntryToContainer(JComponent list) {
        list.add(this.getBasePanel(), "span");
    }

    private int maxWidth;

    public Component getBasePanel() {
        this.load();
        this.maxWidth = this.content.getFontMetrics(this.content.getFont()).stringWidth(
                Arrays.stream(StringUtils.split(this.content.getText(), '\n'))
                        .max(Comparator.comparingInt(String::length))
                        .orElseThrow()
        );
        return this.basePanel;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public void resizeWidth(int targetedWidth) {
        Dimension size = this.content.getSize();
        size.height = this.panelHeight;
        size.width = targetedWidth;
        content.setPreferredSize(size);
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
        this.load();
    }

    public BookId getBookId() {
        return this.book.id();
    }
}
