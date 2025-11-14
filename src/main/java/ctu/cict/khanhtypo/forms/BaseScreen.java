package ctu.cict.khanhtypo.forms;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import ctu.cict.khanhtypo.Main;
import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.utils.DatabaseUtils;
import ctu.cict.khanhtypo.utils.ScreenUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class BaseScreen {
    private static int MAX_PER_PAGE = 20;
    private JPanel basePanel;
    private JButton addBookButton;
    private JButton searchButton;
    private JButton nextPage;
    private JButton previousPage;
    private JPanel pageTurnPannel;
    private JPanel crudPanel;
    private JLabel title;
    private JScrollPane booksScrollable;
    private JPanel booksContainer;

    public JPanel getBasePanel() {
        return basePanel;
    }

    public BaseScreen() {
        title.setFont(Main.FONT_SKIP_PRO);
        this.booksScrollable.getVerticalScrollBar().setUnitIncrement(12);
        booksContainer.setLayout(new BoxLayout(booksContainer, BoxLayout.Y_AXIS));
        //TODO: Load 20 books at a time to the main panel
        this.loadBooks(1);
    }

    private void loadBooks(int page) {
        Preconditions.checkArgument(page > 0, "Page number must be greater than zero.");
        Book[] books = Iterables.toArray(
                DatabaseUtils.getBooks().find().skip((page - 1) * MAX_PER_PAGE)
                        .limit(MAX_PER_PAGE)
                        .map(Book::fromDocument), Book.class);


        booksContainer.removeAll();
        for (Book book : books) {
            booksContainer.add(new BookEntry().load(book));
        }
        booksContainer.revalidate();
        pageTurnPannel.repaint();
        ScreenUtils.packFrame();
    }
}
