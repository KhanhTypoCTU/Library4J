package ctu.cict.khanhtypo.forms;

import com.google.common.collect.Lists;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import ctu.cict.khanhtypo.Main;
import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.books.BookStatus;
import ctu.cict.khanhtypo.forms.component.DatePicker;
import ctu.cict.khanhtypo.forms.component.IBsonRepresentableComponent;
import ctu.cict.khanhtypo.utils.DatabaseUtils;
import ctu.cict.khanhtypo.utils.MathUtils;
import ctu.cict.khanhtypo.utils.ScreenUtils;
import ctu.cict.khanhtypo.utils.SpringUtilities;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Function;

public class FillableFormScreen {
    private static final int TEXT_PANEL_COLUMNS = 30;
    private final JLabel statusLabel;
    private final BookDatabaseScreen databaseScreen;
    private final Window window;
    private JPanel basePanel;
    private final FormField[] fields;

    public FillableFormScreen(BookDatabaseScreen databaseBridge, Window window, String dialogTitle, FormOperation operation) {
        this.databaseScreen = databaseBridge;
        this.window = window;
        this.fields = new FormField[]{
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
                                MathUtils.make(new JComboBox<>(BookStatus.values()), comboBox -> {
                                    comboBox.setRenderer(new BookStatus.Renderer());
                                    comboBox.setSelectedIndex(0);
                                }), comboBox -> comboBox.getSelectedItem().toString()
                        ), n -> null),
                new FormField("ISBN*", "Required, isbn v10 or v13 of the book.",
                        "isbn", createTextFieldWithFilter("^\\d{1,13}$"),
                        text -> ((JTextComponent) text).getText().isBlank() ? "Pages can not be blank." : null
                ),
                new FormField("Authors: ", "Author(s) of the book, multiple names must be separated by a comma."
                        , "authors", field -> Lists.newArrayList(StringUtils.split(field.getText(), ",")),
                        n -> null
                ),
                new FormField("Category: ", "Category(ies) of the book, multiple names must be separated by a comma."
                        , "categories", field -> Lists.newArrayList(StringUtils.split(field.getText(), ",")),
                        n -> null
                )
        };

        int numPairs = fields.length;
        JPanel p = new JPanel(new MigLayout());
        basePanel.add(p, "span, wrap");
        Font textAreaFont = Main.FONT_CRIMSON;
        for (FormField field : fields) {
            JLabel l = new JLabel(field.name, JLabel.TRAILING);
            l.setToolTipText(field.tooltip);
            l.setFont(Main.FONT_PATUA);
            p.add(l);
            Component component = field.bsonValueMapper.getComponent();
            component.setFont(textAreaFont);
            l.setLabelFor(component);
            p.add(component, "wrap");
        }
        p.setBorder(
                MathUtils.make(
                        BorderFactory.createTitledBorder(new BevelBorder(BevelBorder.LOWERED), dialogTitle),
                        border -> border.setTitleFont(Main.FONT_CRIMSON_ITALIC)
                ));


        //Lay out the panel.
        SpringUtilities.makeCompactGrid(p,
                numPairs, 2,
                6, 6,
                6, 6);

        //status text
        this.statusLabel = new JLabel("aaaaaaaaaaaaaaa");
        p.add(this.statusLabel, "span");
        this.statusLabel.setFont(Main.FONT_CRIMSON);
        this.statusLabel.setForeground(new Color(0xcc0000));
        this.hideStatus();


        this.basePanel.add(
                MathUtils.make(new JButton(operation.getDisplayName()),
                        b -> {
                            b.setFont(Main.FONT_PATUA);
                            b.addActionListener(event -> onConfirmed(databaseBridge, operation));
                        })
                , "split 2");

        JButton cancel = (JButton) this.basePanel.add(new JButton("Cancel"));
        cancel.setFont(Main.FONT_PATUA);
        cancel.addActionListener(event -> closeScreen());
    }

    private void onConfirmed(IBookDB book, FormOperation operation) {
        this.hideStatus();
        switch (operation) {
            case CREATE: {
                System.out.println("Index created = " + DatabaseUtils.getBooks().createIndex(Indexes.ascending("isbn"), new IndexOptions().unique(true)));
                try {
                    if (this.validateFields()) {
                        book.addBookEntry(this.composeBook());
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
        }
    }

    private boolean validateFields() {
        for (FormField field : this.fields) {
            if (field.getErrorMessage() != null) {
                this.displayStatus(field.getErrorMessage());
                return false;
            }
        }
        return true;
    }

    private Book composeBook() {
        return Book.fromDocument(new Document(
                MathUtils.make(new Object2ObjectLinkedOpenHashMap<>(this.fields.length), map ->
                        Arrays.stream(this.fields)
                                .forEach(field -> map.put(field.bsonKey, field.bsonValueMapper.getAsBsonValue()))
                )), true);
    }

    public JPanel getBasePanel() {
        return basePanel;
    }

    private void hideStatus() {
        this.basePanel.setSize(this.basePanel.getPreferredSize());
        this.statusLabel.setVisible(false);
    }

    private void displayStatus(String status) {
        this.basePanel.setSize(this.basePanel.getMaximumSize());
        this.statusLabel.setText(status);
        this.statusLabel.setVisible(true);
    }

    private void createUIComponents() {
        this.basePanel = new JPanel(new MigLayout("", "[center]", "[center]"));
        //this.basePanel.setPreferredSize(new Dimension(610, 370));
        this.basePanel.setPreferredSize(new Dimension(610, 400));
        //ScreenUtils.trackSize(this.basePanel);
    }

    private void closeScreen() {
        this.window.dispose();
        this.databaseScreen.setButtonsEnabled(true);
    }

    private static JTextField createTextFieldWithFilter(String pattern) {
        return MathUtils.make(new JTextField(TEXT_PANEL_COLUMNS),
                t -> ((AbstractDocument) t.getDocument()).setDocumentFilter(new RegexBasedDocumentFilter(pattern)));
    }

    private record FormField(String name, String tooltip, String bsonKey, IBsonRepresentableComponent bsonValueMapper,
                             Function<Component, String> errorFactory) {
        FormField(String name, String tooltip, String bsonKey, Function<Component, String> errorFactory) {
            this(name, tooltip, bsonKey, TEXT_PANEL_COLUMNS, errorFactory);
        }

        FormField(String name, String tooltip, String bsonKey, int textPanelColumns, Function<Component, String> errorFactory) {
            this(name, tooltip, bsonKey, new JTextField(textPanelColumns), errorFactory);
        }

        FormField(String name, String tooltip, String bsonKey, JTextComponent textComponent, Function<Component, String> errorFactory) {
            this(name, tooltip, bsonKey, IBsonRepresentableComponent.wrap(textComponent), errorFactory);
        }

        FormField(String name, String tooltip, String bsonKey, Function<JTextField, Object> mapper, Function<Component, String> errorFactory) {
            this(name, tooltip, bsonKey, IBsonRepresentableComponent.wrap(new JTextField(TEXT_PANEL_COLUMNS), mapper), errorFactory);
        }

        @Nullable
        String getErrorMessage() {
            return this.errorFactory.apply(this.bsonValueMapper.getComponent());
        }
    }
}
