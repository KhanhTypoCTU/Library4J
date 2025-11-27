package ctu.cict.khanhtypo.forms.components;

import com.mongodb.client.model.Filters;
import ctu.cict.khanhtypo.Main;
import org.bson.conversions.Bson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Date;
import java.util.Objects;
import java.util.function.BiFunction;

public class DateSearchField extends JPanel implements IBsonRepresentableComponent {

    private final DatePicker datePicker;
    private final JComboBox<?> order;

    public DateSearchField() {
        super(new FlowLayout(FlowLayout.LEFT));
        this.order = (JComboBox<?>) add(new JComboBox<>(SearchOrder.values()));
        order.setRenderer(new EnumTextCellRenderer());
        order.setSelectedIndex(0);
        order.setFont(Main.FONT_PATUA);
        this.datePicker = (DatePicker) add(new DatePicker());
        JRadioButton now = datePicker.getNow();
        now.setText("None.");
        now.addItemListener(e -> this.order.setEnabled(e.getStateChange()!=ItemEvent.SELECTED));
        order.setEnabled(false);
    }

    @Override
    public Object getAsBsonValue() {
        return this.getFilter("publishedDate");
    }

    @Override
    public Component getComponent() {
        return this;
    }

    public Bson getFilter(String bsonKey) {
        if (this.datePicker.isNow()) return null;

        return ((SearchOrder) Objects.requireNonNull(this.order.getSelectedItem())).makeFilter(bsonKey, this.datePicker.getAsBsonValue());
    }

    private enum SearchOrder implements IStringRepresentable {
        BEFORE("Before", Filters::lte),
        AFTER("After", Filters::gte);

        private final String display;
        private final BiFunction<String, Date, Bson> filter;

        SearchOrder(String display, BiFunction<String, Date, Bson> filter) {
            this.display = display;
            this.filter = filter;
        }

        Bson makeFilter(String bsonKey, Date time) {
            return this.filter.apply(bsonKey, time);
        }

        @Override
        public String getRepresentingString() {
            return this.display;
        }
    }
}
