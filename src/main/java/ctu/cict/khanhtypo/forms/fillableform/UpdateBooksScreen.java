package ctu.cict.khanhtypo.forms.fillableform;

import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.forms.BookDatabaseScreen;
import ctu.cict.khanhtypo.forms.BookEntry;
import ctu.cict.khanhtypo.forms.IBookDataBridge;
import ctu.cict.khanhtypo.forms.components.DatePicker;

import javax.swing.*;
import java.awt.*;

public class UpdateBooksScreen extends CreateBookScreen {

    private final BookEntry bookEntry;

    public UpdateBooksScreen(BookDatabaseScreen databaseBridge, Dialog window, BookEntry bookEntry) {
        super(databaseBridge, window, window.getTitle(), "Update");
        this.bookEntry = bookEntry;
        Book book = bookEntry.getBook();
        super.getComponentForField("title")
                .setText(book.title());
        super.getComponentForField("pageCount")
                .setText(String.valueOf(book.pageCount()));
        super.getComponentForField("isbn")
                .setText(book.isbn());
        super.getComponentForField("authors")
                .setText(book.authors());
        super.getComponentForField("categories")
                .setText(book.categories());
        super.getComponentForField("status", JComboBox.class)
                .setSelectedItem(book.status());
        DatePicker publishedDate = super.getComponentForField("publishedDate", DatePicker.class);
        publishedDate.setDate(book.publishedDate());
    }

    @Override
    protected void onConfirmed(IBookDataBridge databaseBridge) {
        if (super.validateFields()) {
            Book composed = super.composeBook();
            databaseBridge.updateBookEntry(this.bookEntry, composed);
            this.bookEntry.setChanged();
            super.closeScreen();
        }
    }
}
