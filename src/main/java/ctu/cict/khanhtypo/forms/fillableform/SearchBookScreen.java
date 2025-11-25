package ctu.cict.khanhtypo.forms.fillableform;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import ctu.cict.khanhtypo.books.BookStatus;
import ctu.cict.khanhtypo.forms.BookDatabaseScreen;
import ctu.cict.khanhtypo.forms.FillableFormScreen;
import ctu.cict.khanhtypo.forms.IBookDataBridge;
import ctu.cict.khanhtypo.forms.component.AlternativeTextRenderer;
import ctu.cict.khanhtypo.forms.component.IBsonRepresentableComponent;
import ctu.cict.khanhtypo.utils.MathUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bson.conversions.Bson;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class SearchBookScreen extends FillableFormScreen {
    private Object2ObjectMap<FormField, BiFunction<String, Component, Bson>> filtersMap;
    private List<Bson> compoundIndexBuilder;
    private @Nullable String finalizedIndex = null;

    public SearchBookScreen(BookDatabaseScreen databaseBridge, Dialog window) {
        super(databaseBridge, window, window.getTitle(), "Search");
    }

    @Override
    protected void preInit() {
        this.compoundIndexBuilder = new ObjectArrayList<>();
        this.filtersMap = new Object2ObjectLinkedOpenHashMap<>();
    }

    @Override
    protected FormField[] constructFields() {
        return new FormField[]{
                this.createTextField(new FormField("Title:", "Title of the book", "title")),
                this.createTextField(new FormField("ISBN:", "ISBN of the book", "isbn"
                        , createTextFieldWithFilter("^\\d{1,10}$"))),
                this.createField(
                        new FormField(
                                "Status:",
                                "Status of the book",
                                "status",
                                IBsonRepresentableComponent.wrap(
                                        MathUtils.make(new JComboBox<>(BookStatusFilter.values()), comboBox ->
                                                comboBox.setRenderer(new AlternativeTextRenderer<>(BookStatusFilter::getDisplay))
                                        ), comboBox -> Objects.requireNonNull(comboBox.getSelectedItem()).toString()
                                )
                        ), (key, component) -> ((BookStatusFilter) Objects.requireNonNull(((JComboBox<?>) component).getSelectedItem())).getFilter())
        };
    }

    private FormField createTextField(FormField formField) {
        return createField(formField, (bsonKey, component) -> {
                    String text = ((JTextComponent) component).getText();
                    if (text == null || text.isBlank()) return null;
                    //compoundIndexBuilder.add(Indexes.ascending(formField.bsonKey()));
                    return Filters.regex(formField.bsonKey(), Pattern.compile('^' + text));
                }
        );
    }

    private FormField createField(FormField formField, BiFunction<String, Component, Bson> filterFactory) {
        this.filtersMap.put(formField, filterFactory);
        return formField;
    }

    @Override
    protected void onConfirmed(IBookDataBridge databaseBridge) {
        if (!this.validateFields()) return;
        Bson filter = this.compose();
        if (filter == null) {
            super.displayStatus("No book found.");
            this.clearIndex();
            return;
        }
        System.out.println("Filter - " + filter.toBsonDocument().toString());
        int filtered = databaseBridge.searchBooks(filter);
        if (filtered == 0) {
            super.displayStatus("No book found.");
            this.clearIndex();
        } else closeScreen();
    }

    @Override
    protected void closeScreen() {
        super.closeScreen();
        this.clearIndex();
    }

    private @Nullable Bson compose() {
        this.clearIndex();
        List<Bson> filters = new ObjectArrayList<>();
        this.filtersMap.forEach((field, factory) ->
                {
                    Bson filter = factory.apply(field.bsonKey(), field.bsonValueMapper().getComponent());
                    if (filter == null) return;
                    filters.add(filter);
                    compoundIndexBuilder.add(Indexes.ascending(field.bsonKey()));
                }
        );
        if (filters.isEmpty()) return null;
        this.finalizedIndex = this.databaseScreen.getCollection().createIndex(Indexes.compoundIndex(compoundIndexBuilder));
        return Filters.and(filters);
    }

    private void clearIndex() {
        this.compoundIndexBuilder.clear();
        if (this.finalizedIndex != null) {
            this.databaseScreen.getCollection().dropIndex(finalizedIndex);
            finalizedIndex = null;
        }
    }

    private enum BookStatusFilter {
        IGNORED("---", null),
        RELEASED(BookStatus.PUBLISH),
        DISCONTINUED(BookStatus.DISCONTINUED),
        EARLY_ACCESS(BookStatus.MEAP);

        private final String display;
        private final Bson filter;

        BookStatusFilter(BookStatus bookStatus) {
            this(bookStatus.getDisplayText(), Filters.eq("status", bookStatus.toString()));
        }

        /// null = filter will not be included
        BookStatusFilter(String display, @Nullable Bson filter) {
            this.display = display;
            this.filter = filter;
        }

        public String getDisplay() {
            return display;
        }

        public Bson getFilter() {
            return filter;
        }
    }
}
