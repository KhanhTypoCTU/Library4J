package ctu.cict.khanhtypo.forms;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import ctu.cict.khanhtypo.Main;
import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.forms.fillableform.CreateBookScreen;
import ctu.cict.khanhtypo.forms.fillableform.SearchBookScreen;
import ctu.cict.khanhtypo.utils.DatabaseUtils;
import ctu.cict.khanhtypo.utils.MathUtils;
import ctu.cict.khanhtypo.utils.ScreenUtils;
import net.miginfocom.swing.MigLayout;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.Objects;

public class BookDatabaseScreen implements IBookDB {
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
    private final List<JButton> leftSideButtons;

    public JPanel getBasePanel() {
        return basePanel;
    }

    public BookDatabaseScreen() {
        this.reloadTitle();
        pagesDisplayLabel.setFont(Main.FONT_PATUA);
        this.calculateMaxPages();
        title.setFont(Main.FONT_PATUA);
        booksScrollable.getVerticalScrollBar().setUnitIncrement(12);
        booksContainer.setLayout(new MigLayout("wrap" + (Main.IN_DEV ? ", debug" : ""), "[left]", "[]"));
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

        leftSideButtons = List.of(addBookButton, searchButton, nextPage, previousPage);
        leftSideButtons.forEach(b -> {
            b.setFont(Main.FONT_PATUA.deriveFont(15f));
            b.setFocusPainted(false);
        });
        addBookButton.addActionListener(e ->
                SwingUtilities.invokeLater(() -> {
                    JDialog dialog = new JDialog(Main.baseFrame, "Adding A New Book", true);
                    dialog.setResizable(false);
                    //leftSideButtons.forEach(b -> b.setEnabled(false));
                    FillableFormScreen addScreen = new CreateBookScreen(this, dialog);
                    dialog.setContentPane(addScreen.getBasePanel());
                    ScreenUtils.packFrame(dialog);
                    dialog.setVisible(true);
                }));
        searchButton.addActionListener(e ->
                SwingUtilities.invokeLater(() -> {
                    JDialog dialog = new JDialog(Main.baseFrame, "Looking For Book(s)", true);
                    dialog.setResizable(false);
                    FillableFormScreen addScreen = new SearchBookScreen(this, dialog);
                    dialog.setContentPane(addScreen.getBasePanel());
                    ScreenUtils.packFrame(dialog);
                    dialog.setVisible(true);
                }));
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


        displayBooks(keepScrollPosition, books);
    }

    public void displayBooks(boolean keepScrollPosition, Book[] books) {
        booksContainer.removeAll();
        BookEntry[] entries = new BookEntry[books.length];
        for (int i = 0; i < books.length; i++) {
            Book book = books[i];
            BookEntry bookEntry = new BookEntry(this, book, false);
            SwingUtilities.invokeLater(() ->
                    booksContainer.add(bookEntry.getBasePanel(booksScrollable), "span")
            );
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

    @Override
    public void addBookEntry(Book book) throws MongoWriteException {
        InsertOneResult insertOneResult = DatabaseUtils.getBooks().insertOne(book.toDocument());
        System.out.println("Created book \"" + book.title() + "\", ID : " + insertOneResult.getInsertedId());
        this.refreshDatabaseScreen();
    }

    @Override
    public void searchBooks(Bson filter) {
        Book[] books = Iterables.toArray(this.getCollection().find(filter).map(Book::fromDocument), Book.class);
        SwingUtilities.invokeLater(() -> {
            SearchResultScreen screen = new SearchResultScreen(this, books);
        });
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

    @Override
    public String toString() {
        return "MAIN SCREEN";
    }
}