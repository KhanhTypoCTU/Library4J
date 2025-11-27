package ctu.cict.khanhtypo.forms.fillableform;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import ctu.cict.khanhtypo.books.BookStatus;
import ctu.cict.khanhtypo.forms.BookDatabaseScreen;
import ctu.cict.khanhtypo.forms.FillableFormScreen;
import ctu.cict.khanhtypo.forms.IBookDataBridge;
import ctu.cict.khanhtypo.forms.components.DatePicker;
import ctu.cict.khanhtypo.forms.components.EnumTextCellRenderer;
import ctu.cict.khanhtypo.forms.components.IBsonRepresentableComponent;
import ctu.cict.khanhtypo.utils.MathUtils;
import org.apache.commons.validator.routines.ISBNValidator;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Objects;

public class CreateBookScreen extends FillableFormScreen {
    private static final ISBNValidator ISBN_VALIDATOR =  ISBNValidator.getInstance(false);
    public CreateBookScreen(BookDatabaseScreen databaseBridge, Dialog window) {
        super(databaseBridge, window, window.getTitle(), "Create");
    }

    CreateBookScreen(BookDatabaseScreen databaseBridge, Window window, String dialogTitle, String operationDisplayName) {
        super(databaseBridge, window, dialogTitle, operationDisplayName);
    }

    @Override
    protected void onConfirmed(IBookDataBridge databaseBridge) {
        String index = databaseBridge.getCollection().createIndex(Indexes.ascending("isbn"), new IndexOptions().unique(true));
        try {
            if (this.validateFields()) {
                databaseBridge.addBookEntry(this.composeBook());
                databaseBridge.getCollection().dropIndex(index);
                closeScreen();
            }
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                this.displayStatus("ISBN already exists for another book.");
            }
        } catch (Exception e) {
            this.displayStatus("Error creating book entry. " + e.getMessage());
        }
    }

    @Override
    protected FormField[] constructFields() {
        return new FormField[]{
                new FormField("Title*: ", "(Required) Name of the book.", "title"
                        , text -> ((JTextComponent) text).getText().isBlank() ? "Title can not be blank." : null),

                new FormField("Pages*:", "(Required) Number of pages in the book. Range from [1 -> 999,999]",
                        "pageCount", IBsonRepresentableComponent.wrap(
                        createTextFieldWithFilter("^\\d{1,6}$")
                        , field -> Integer.parseInt(field.getText())),
                        text -> ((JTextComponent) text).getText().isBlank() ? "Pages can not be blank." : null
                ),
                new FormField("Release Date*: ", "Date of the release of this book, now or manually type in.",
                        "publishedDate", new DatePicker(), n -> null),
                new FormField("Book Status*", "Status of this book entry", "status",
                        IBsonRepresentableComponent.wrap(
                                MathUtils.make(new JComboBox<>(BookStatus.values()), comboBox ->
                                        comboBox.setRenderer(new EnumTextCellRenderer())
                                ), comboBox -> Objects.requireNonNull(comboBox.getSelectedItem()).toString()
                        ), n -> null),
                new FormField("ISBN*", "Required, ISBN-10 or ISBN-13 of the book.",
                        "isbn", createTextFieldWithFilter("^\\d{1,13}$"),
                        text -> {
                            String isbn = ((JTextComponent) text).getText();
                            if (isbn.isBlank()) return "ISBN can not be blank.";
                            if (!ISBN_VALIDATOR.isValid(isbn)) return "Not a valid ISBN-10 or ISBN-13.";
                            return null;
                        }
                ),
                new FormField("Authors: ", "Author(s) of the book, multiple names must be separated by a comma."
                        , "authors", textToArrayMapper(),
                        n -> null
                ),
                new FormField("Category: ", "Category(ies) of the book, multiple names must be separated by a comma."
                        , "categories", textToArrayMapper(),
                        n -> null
                )
        };
    }
}