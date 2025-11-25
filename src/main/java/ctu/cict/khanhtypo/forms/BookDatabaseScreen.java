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
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;

public class BookDatabaseScreen implements IBookDataBridge {
    private static final int MAX_PER_PAGE = 15;
    private int maxPages;
    private int currentPage;
    private JPanel basePanel;
    private JButton addBookButton;
    private JButton searchButton;
    private JButton nextPage;
    private JButton previousPage;
    private JLabel title;
    private JLabel pagesDisplayLabel;
    private BookListScreen bookList;
    private JPanel contentsLeft;
    private JPanel pagesPanel;
    private JPanel contentsRight;
    private final List<JButton> leftSideButtons;

    public JPanel getBasePanel() {
        return basePanel;
    }

    public BookDatabaseScreen() {
        this.reloadTitle();
        pagesDisplayLabel.setFont(Main.FONT_PATUA);
        this.calculateMaxPages();
        title.setFont(Main.FONT_PATUA);
        this.bookList.setPreferredSize(968, 843);

        bookList.getVerticalScrollBar().setUnitIncrement(16);
        this.loadBooks(1, false);
        nextPage.addActionListener(e ->
                loadBooks(currentPage + 1, false));
        previousPage.addActionListener(e ->
                loadBooks(currentPage - 1, false));
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
        this.bookList.reload(this, books, true);
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
        ScreenUtils.packFrame(Main.baseFrame);
    }

    @Override
    public void deleteBookEntry(BookEntry bookEntry) {
        Document deleted = DatabaseUtils.getBooks()
                .findOneAndDelete(Filters.eq("_id", bookEntry.getBookId().getAsGenericObject()));
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
    public int searchBooks(Bson filter) {
        Book[] books = Iterables.toArray(this.getCollection().find(filter).map(Book::fromDocument), Book.class);
        if (books.length == 0) return 0;
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog(Main.baseFrame, "Book Results", true);
            BookListScreen screen = new BookListScreen();
            screen.reload(this, books, true);
            screen.getBasePane().setBorder(screen.createTitledBorder("SEARCH RESULTS : " + screen.getEntryCount() + " Entries."));
            screen.getEntriesList().addContainerListener(
                    new ContainerAdapter() {
                        @Override
                        public void componentRemoved(ContainerEvent e) {
                            screen.getBasePane().setBorder(screen.createTitledBorder("SEARCH RESULTS : " + screen.getEntryCount() + " Entries."));
                        }
                    }
            );
            dialog.setContentPane(screen.getBasePane());
            ScreenUtils.packFrame(dialog);
            dialog.setVisible(true);
            dialog.addWindowStateListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (screen.hasChanged())
                        BookDatabaseScreen.this.refreshDatabaseScreen();
                }
            });
        });
        return books.length;
    }

    /**
     * @param bookEntry - the book object that contains information of the book, must be an object in the list.
     */
    @Override
    public void updateBookEntry(BookEntry bookEntry, Book updatedBook) {
        Document previousData = this.getCollection().findOneAndUpdate(
                Filters.eq("_id", bookEntry.getBookId().getAsGenericObject()),
                new BsonDocument("$set", updatedBook.toDocument().toBsonDocument())
        );

        if (Main.IN_DEV)
            System.out.println("Updated : \n\tBefore: " + previousData + "\n\t" + "After :" + updatedBook.toDocument());

        updatedBook.setBookId(bookEntry.getBookId());
        bookEntry.setBook(updatedBook);
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
            JScrollBar verticalScrollBar = this.bookList.getVerticalScrollBar();
            if (keepScrollPosition)
                verticalScrollBar.setValue(MathUtils.clampInclusive(verticalScrollBar.getValue(), verticalScrollBar.getMinimum(), verticalScrollBar.getMaximum()));
            else verticalScrollBar.setValue(0);
        });
    }
}