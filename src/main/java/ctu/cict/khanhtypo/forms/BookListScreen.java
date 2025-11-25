package ctu.cict.khanhtypo.forms;

import ctu.cict.khanhtypo.Main;
import ctu.cict.khanhtypo.books.Book;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;

public class BookListScreen {
    private JPanel basePanel;
    private JScrollPane scrollPane;
    private JPanel entriesList;
    private boolean hasChanged;

    public BookListScreen() {
        entriesList.setLayout(new MigLayout("wrap", "[left]", ""));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }

    public void reload(BookDatabaseScreen databaseBridge, Book[] searchedBook, boolean withButtons) {
        this.entriesList.removeAll();
        BookEntry[] arr = Arrays.stream(searchedBook)
                .map(b -> new BookEntry(databaseBridge, b, withButtons) {
                    @Override
                    void deleteBookEntry(IBookDataBridge databaseBridge) {
                        super.deleteBookEntry(databaseBridge);
                        entriesList.remove(this.getBasePanel());
                    }
                }
                        .setChangeListener(() -> hasChanged = true))
                .toArray(BookEntry[]::new);

        for (BookEntry bookEntry : arr) {
            bookEntry.loadEntryToContainer(this.entriesList);
        }
        SwingUtilities.invokeLater(() -> {
            this.entriesList.revalidate();
            this.entriesList.repaint();
            repositionScrolls();
        });
        int maxWidth = Arrays.stream(arr).mapToInt(BookEntry::getMaxWidth).max().orElseThrow();
        for (BookEntry bookEntry : arr) {
            bookEntry.resizeWidth(
                    Math.max(maxWidth, this.basePanel.getPreferredSize().width)
            );
        }
    }

    public boolean hasChanged() {
        return this.hasChanged;
    }

    private void repositionScrolls() {
        this.scrollPane.getVerticalScrollBar().setValue(0);
        this.scrollPane.getHorizontalScrollBar().setValue(0);
    }

    public JPanel getBasePane() {
        return this.basePanel;
    }

    public JScrollBar getVerticalScrollBar() {
        return this.scrollPane.getVerticalScrollBar();
    }

    public void setPreferredSize(int width, int height) {
        this.basePanel.setPreferredSize(new Dimension(width, height));
    }

    public JPanel getEntriesList() {
        return this.entriesList;
    }

    public int getEntryCount() {
        return this.entriesList.getComponents().length;
    }

    public TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                new BevelBorder(BevelBorder.LOWERED), title,
                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, Main.FONT_PATUA
        );
    }
}
