package ctu.cict.khanhtypo.forms.fillableform;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.TextSearchOptions;
import ctu.cict.khanhtypo.forms.BookDatabaseScreen;
import ctu.cict.khanhtypo.forms.FillableFormScreen;
import ctu.cict.khanhtypo.forms.IBookDB;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bson.conversions.Bson;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.List;
import java.util.function.BiFunction;

public class SearchBookScreen extends FillableFormScreen {
    private Object2ObjectMap<FormField, BiFunction<String, Component, Bson>> filtersMap;
    private static final TextSearchOptions TEXT_SEARCH_OPTIONS = new TextSearchOptions().caseSensitive(false).diacriticSensitive(false);

    public SearchBookScreen(BookDatabaseScreen databaseBridge, Dialog window) {
        super(databaseBridge, window, window.getTitle(), "Search");
    }

    @Override
    protected FormField[] constructFields() {
        this.filtersMap = new Object2ObjectLinkedOpenHashMap<>();
        return new FormField[]{
                //TODO
                this.createTextField(new FormField("Title:", "Title of the book", "title"))
        };
    }

    private FormField createTextField(FormField formField) {
        super.databaseScreen.getCollection().createIndex(Indexes.text(formField.bsonKey()));
        return createField(formField, (bsonKey, component) ->
                Filters.text(((JTextComponent) component).getText(), TEXT_SEARCH_OPTIONS)
        );
    }

    private FormField createField(FormField formField, BiFunction<String, Component, Bson> filterFactory) {
        this.filtersMap.put(formField, filterFactory);
        return formField;
    }

    @Override
    protected void onConfirmed(IBookDB book) {
        Bson filter = this.compose();
        book.searchBooks(filter);
        super.closeScreen();
    }

    private Bson compose() {
        List<Bson> filters = new ObjectArrayList<>();
        this.filtersMap.forEach((field, factory) ->
            filters.add(factory.apply(field.bsonKey(), field.bsonValueMapper().getComponent()))
        );
        return Filters.and(filters);
    }
}
