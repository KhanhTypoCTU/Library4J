package ctu.cict.khanhtypo.forms;

import ctu.cict.khanhtypo.Main;
import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.forms.component.IBsonRepresentableComponent;
import ctu.cict.khanhtypo.utils.MathUtils;
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

public abstract class FillableFormScreen {
    private static final int TEXT_PANEL_COLUMNS = 30;
    private final JLabel statusLabel;
    protected final BookDatabaseScreen databaseScreen;
    private final Window window;
    private JPanel basePanel;
    private final FormField[] fields;

    public FillableFormScreen(BookDatabaseScreen databaseBridge, Window window, String dialogTitle, String operationDisplayName) {
        this.databaseScreen = databaseBridge;
        this.window = window;
        this.fields = this.constructFields();

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
                MathUtils.make(new JButton(operationDisplayName),
                        b -> {
                            b.setFont(Main.FONT_PATUA);
                            b.addActionListener(event -> {
                                hideStatus();
                                onConfirmed(databaseBridge);
                            });
                        })
                , "split 2");

        JButton cancel = (JButton) this.basePanel.add(new JButton("Cancel"));
        cancel.setFont(Main.FONT_PATUA);
        cancel.addActionListener(event -> closeScreen());
    }

    protected abstract FormField[] constructFields();

    protected static Function<JTextField, Object> textToArrayMapper() {
        return field -> Arrays.stream(StringUtils.split(field.getText(), ",")).map(String::trim).toList();
    }

    protected abstract void onConfirmed(IBookDB book);

    protected boolean validateFields() {
        for (FormField field : this.fields) {
            if (field.getErrorMessage() != null) {
                this.displayStatus(field.getErrorMessage());
                return false;
            }
        }
        return true;
    }

    protected Book composeBook() {
        return Book.fromDocument(new Document(
                MathUtils.make(new Object2ObjectLinkedOpenHashMap<>(this.fields.length), map ->
                        Arrays.stream(this.fields)
                                .forEach(field -> map.put(field.bsonKey, field.bsonValueMapper.getAsBsonValue()))
                )));
    }

    public JPanel getBasePanel() {
        return basePanel;
    }

    protected void hideStatus() {
        this.basePanel.setSize(this.basePanel.getPreferredSize());
        this.statusLabel.setVisible(false);
    }

    protected void displayStatus(String status) {
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

    protected void closeScreen() {
        this.window.dispose();
    }

    protected static JTextField createTextFieldWithFilter(String pattern) {
        return MathUtils.make(new JTextField(TEXT_PANEL_COLUMNS),
                t -> ((AbstractDocument) t.getDocument()).setDocumentFilter(new RegexBasedDocumentFilter(pattern)));
    }

    public record FormField(String name, String tooltip, String bsonKey, IBsonRepresentableComponent bsonValueMapper,
                            Function<Component, String> errorFactory) {
        public FormField(String name, String tooltip, String bsonKey, Function<Component, String> errorFactory) {
            this(name, tooltip, bsonKey, TEXT_PANEL_COLUMNS, errorFactory);
        }

        public FormField(String name, String tooltip, String bsonKey, int textPanelColumns, Function<Component, String> errorFactory) {
            this(name, tooltip, bsonKey, new JTextField(textPanelColumns), errorFactory);
        }

        public FormField(String name, String tooltip, String bsonKey, JTextComponent textComponent, Function<Component, String> errorFactory) {
            this(name, tooltip, bsonKey, IBsonRepresentableComponent.wrap(textComponent), errorFactory);
        }

        public FormField(String name, String tooltip, String bsonKey, Function<JTextField, Object> mapper, Function<Component, String> errorFactory) {
            this(name, tooltip, bsonKey, IBsonRepresentableComponent.wrap(new JTextField(TEXT_PANEL_COLUMNS), mapper), errorFactory);
        }

        @Nullable
        public String getErrorMessage() {
            return this.errorFactory.apply(this.bsonValueMapper.getComponent());
        }
    }
}
