package ctu.cict.khanhtypo.forms;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.mongodb.client.model.Filters;
import ctu.cict.khanhtypo.Main;
import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.utils.DatabaseUtils;
import ctu.cict.khanhtypo.utils.MathUtils;
import ctu.cict.khanhtypo.utils.ScreenUtils;
import net.miginfocom.swing.MigLayout;
import org.bson.Document;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;

public class BaseScreen implements IBookDB {
    private static final int MAX_PER_PAGE = 15;
    private int maxPages;
    private int currentPage;
    private JPanel basePanel;
    private JButton addBookButton;
    private JButton searchButton;
    private JButton nextPage;
    private JButton previousPage;
    private JPanel crudPanel;
    private JLabel title;
    private JScrollPane booksScrollable;
    private JPanel booksContainer;
    private JLabel pagesDisplayLabel;
    private BookEntry[] bookEntries;

    public JPanel getBasePanel() {
        return basePanel;
    }

    public BaseScreen() {
        this.reloadTitle();
        pagesDisplayLabel.setFont(Main.FONT_PATUA);
        this.calculateMaxPages();
        title.setFont(Main.FONT_PATUA);
        booksScrollable.getVerticalScrollBar().setUnitIncrement(12);
        booksContainer.setLayout(new MigLayout("wrap" + (Main.IN_DEV ? ", debug" : ""), "[]", "[]"));
        this.loadBooks(1, false);
        nextPage.addActionListener(e ->
                loadBooks(currentPage + 1, false));
        previousPage.addActionListener(e ->
                loadBooks(currentPage - 1, false));
        booksScrollable.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeAllEntries();
            }
        });

        List<JButton> buttons = List.of(addBookButton, searchButton, nextPage, previousPage);
        buttons.forEach(b -> {
            b.setFont(Main.FONT_PATUA.deriveFont(15f));
            b.setFocusPainted(false);
        });
        addBookButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                JDialog dialog = new JDialog(Main.baseFrame, "Adding A New Book", true);
                buttons.forEach(b -> b.setEnabled(false));
                FillableFormScreen addScreen = new FillableFormScreen(dialog.getTitle(), "Create", this::onBookCreateRequested);
                dialog.setContentPane(addScreen.getBasePanel());
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        buttons.forEach(b -> b.setEnabled(true));
                    }
                });
                ScreenUtils.packFrame(dialog);
                dialog.setVisible(true);
            });
        });
    }

    private void resizeAllEntries() {
        SwingUtilities.invokeLater(() -> {
            int width = booksScrollable.getWidth();
            for (BookEntry bookEntry : bookEntries) {
                bookEntry.resizeWidth(width);
            }
        });
    }

    private void reloadTitle() {
        this.title.setText("BOOK LISTINGS | " + DatabaseUtils.getBooks().countDocuments() + " entries");
    }

    private void calculateMaxPages() {
        this.maxPages = (int) Math.ceil((double) DatabaseUtils.getBooks().countDocuments() / MAX_PER_PAGE);
    }


    private void loadBooks(int page, boolean keepScrollPosition) {
        this.currentPage = page;
        Preconditions.checkArgument(page > 0, "Page number must be greater than zero.");
        @SuppressWarnings("NullableProblems")
        Book[] books = Iterables.toArray(Iterables.filter(
                DatabaseUtils.getBooks().find().skip((page - 1) * MAX_PER_PAGE)
                        .limit(MAX_PER_PAGE)
                        .map(Book::fromDocument), Objects::nonNull), Book.class);


        booksContainer.removeAll();
        BookEntry[] entries = new BookEntry[books.length];
        for (int i = 0; i < books.length; i++) {
            Book book = books[i];
            BookEntry bookEntry = new BookEntry(this, book);
            SwingUtilities.invokeLater(() -> booksContainer.add(bookEntry.getBasePanel(booksScrollable), "span"));
            entries[i] = bookEntry;
        }
        this.bookEntries = entries;
        booksContainer.revalidate();
        booksContainer.repaint();
        onPageChanged();
        reloadScrollBar(keepScrollPosition);
    }

    private void onPageChanged() {
        if (currentPage == 1)
            this.previousPage.setEnabled(false);
        else if (currentPage >= maxPages)
            this.nextPage.setEnabled(false);
        else {
            this.nextPage.setEnabled(true);
            this.previousPage.setEnabled(true);
        }
        this.pagesDisplayLabel.setText(currentPage + "/" + maxPages);

    }

    public void deleteBookEntry(Book book) {
        Document deleted = DatabaseUtils.getBooks().findOneAndDelete(Filters.eq("_id", book.id().getAsGenericObject()));
        System.out.println("Deleted : " + deleted);
        this.refreshDatabaseScreen();
    }

    private void refreshDatabaseScreen() {
        this.calculateMaxPages();
        this.currentPage = MathUtils.clampInclusive(this.currentPage, 0, maxPages);
        this.loadBooks(this.currentPage, true);
        this.reloadTitle();
        SwingUtilities.updateComponentTreeUI(basePanel);
    }

    public void reloadScrollBar(boolean keepScrollPosition) {
        SwingUtilities.invokeLater(() -> {
                    JScrollBar verticalScrollBar = this.booksScrollable.getVerticalScrollBar();
                    if (keepScrollPosition)
                        verticalScrollBar.setValue(MathUtils.clampInclusive(verticalScrollBar.getValue(), verticalScrollBar.getMinimum(), verticalScrollBar.getMaximum()));
                    else verticalScrollBar.setValue(0);
                    booksScrollable.setVerticalScrollBar(verticalScrollBar);
                }
        );
    }

    private void onBookCreateRequested(Book book) {

    }

}