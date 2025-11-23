package ctu.cict.khanhtypo.forms;

import ctu.cict.khanhtypo.Main;
import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.books.BookStatus;
import ctu.cict.khanhtypo.forms.component.DatePicker;
import ctu.cict.khanhtypo.forms.component.IBsonRepresentableComponent;
import ctu.cict.khanhtypo.utils.MathUtils;
import ctu.cict.khanhtypo.utils.SpringUtilities;
import net.miginfocom.swing.MigLayout;
import org.bson.BsonString;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.function.Consumer;

public class FillableFormScreen {
    private static final int TEXT_PANEL_COLUMNS = 30;
    private final String dialogTitle;
    private JPanel basePanel;
    private final FormField[] fields;

    public FillableFormScreen(String dialogTitle, String performText, Consumer<Book> onConfirmed) {
        this.dialogTitle = dialogTitle;
        this.fields = new FormField[]{
                new FormField("Name*: ", "(Required) Name of the book."),
                new FormField("Pages*:", "(Required) Number of pages in the book. Range from [1 -> 999,999]", createTextFieldWithFilter("^\\d{1,6}$")),
                new FormField("Release Date*: ", "Date of the release of this book, now or manually type in.", new DatePicker()),
                new FormField("Book Status*", "Status of this book entry", IBsonRepresentableComponent.wrap(
                        MathUtils.make(new JComboBox<>(BookStatus.values()), comboBox -> {
                            comboBox.setRenderer(new BookStatus.Renderer());
                            comboBox.setSelectedIndex(0);
                        }), comboBox -> new BsonString(comboBox.getSelectedItem().toString())
                )),
                new FormField("ISBN*", "Required, ISBN v10 or v13 of the book.", createTextFieldWithFilter("^\\d{1,13}$")),
                new FormField("Authors: ", "Author(s) of the book, multiple names must be separated by a comma."),
                new FormField("Category: ", "Category(ies) of the book, multiple names must be separated by a comma."),
        };

        int numPairs = fields.length;
        JPanel p = new JPanel(new SpringLayout());
        basePanel.add(p, "span, wrap");
        Font textAreaFont = Main.FONT_CRIMSON;
        for (FormField field : fields) {
            JLabel l = new JLabel(field.name, JLabel.TRAILING);
            l.setToolTipText(field.tooltip);
            l.setFont(Main.FONT_PATUA);
            p.add(l);
            Component component = field.component.getComponent();
            component.setFont(textAreaFont);
            l.setLabelFor(component);
            p.add(component);
        }
        p.setBorder(
                MathUtils.make(
                        BorderFactory.createTitledBorder(new BevelBorder(BevelBorder.LOWERED), this.dialogTitle),
                        border -> border.setTitleFont(Main.FONT_CRIMSON_ITALIC)
                ));


        //Lay out the panel.
        SpringUtilities.makeCompactGrid(p,
                numPairs, 2,
                6, 6,
                6, 6);

        this.basePanel.add(MathUtils.make(new JButton(performText),
                b -> b.setFont(Main.FONT_PATUA)), "split 2");
        this.basePanel.add(new JButton("Cancel")).setFont(Main.FONT_PATUA);
    }

    public JPanel getBasePanel() {
        return basePanel;
    }

    private void createUIComponents() {
        this.basePanel = new JPanel(new MigLayout("", "[center]", "[center]"));
        this.basePanel.setPreferredSize(new Dimension(610, 370));
        //ScreenUtils.trackSize(this.basePanel);
    }

    private static JTextField createTextFieldWithFilter(String pattern) {
        return MathUtils.make(new JTextField(TEXT_PANEL_COLUMNS),
                t -> ((AbstractDocument) t.getDocument()).setDocumentFilter(new RegexBasedDocumentFilter(pattern)));
    }

    private record FormField(String name, String tooltip, IBsonRepresentableComponent component) {
        FormField(String name, String tooltip) {
            this(name, tooltip, TEXT_PANEL_COLUMNS);
        }

        FormField(String name, String tooltip, int textPanelColumns) {
            this(name, tooltip, new JTextField(textPanelColumns));
        }

        FormField(String name, String tooltip, JTextComponent textComponent) {
            this(name, tooltip, IBsonRepresentableComponent.wrap(textComponent));
        }
    }
}
