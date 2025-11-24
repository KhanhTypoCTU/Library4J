package ctu.cict.khanhtypo.forms.component;

import ctu.cict.khanhtypo.Main;
import ctu.cict.khanhtypo.utils.MathUtils;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.IntStream;

public class DatePicker extends JPanel implements IBsonRepresentableComponent {
    private LocalDate date;
    private final JRadioButton now;

    public DatePicker() {
        super(new FlowLayout(FlowLayout.LEFT));
        this.date = LocalDate.now();
        JRadioButton now = (JRadioButton) super.add(new JRadioButton("Now."));
        this.now = now;
        now.setFont(Main.FONT_PATUA);
        JRadioButton handInput = (JRadioButton) super.add(new JRadioButton("Date:"));
        handInput.setFont(Main.FONT_PATUA);

        Component dateField = super.add(new DateField(this));
        dateField.setEnabled(false);

        ButtonGroup group = new ButtonGroup();
        group.add(now);
        group.add(handInput);
        group.setSelected(now.getModel(), true);

        now.addItemListener(event ->
                dateField.setEnabled(!now.isSelected())
        );
    }

    private void setDate(int date, int month, int year) {
        this.date = LocalDate.of(year, month, date);
    }

    @Override
    public Object getAsBsonValue() {
        return this.now.isSelected() ? Date.from(Instant.now()) :
                MathUtils.make(Calendar.getInstance(),
                        c -> c.set(this.date.getYear(), this.date.getMonthValue() - 1, this.date.getDayOfMonth())
                ).getTime();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @SuppressWarnings("DataFlowIssue")
    private static final class DateField extends JPanel {
        private JComboBox<Object> date, month, year;
        private final DatePicker datePicker;

        public DateField(DatePicker datePicker) {
            super(new FlowLayout(FlowLayout.LEFT));
            this.datePicker = datePicker;
            date = month = year = null;
            month = this.add(1, 12, new MonthRenderer());
            date = this.add(1, 31, null);
            year = this.add(1970, 2050, null);
            year.setSelectedIndex(2025 - 1970);
        }

        private void validateDay() {
            int month = (int) this.month.getSelectedItem();
            int day = MathUtils.clampInclusive(((int) this.date.getSelectedItem()), 1, Month.of(month).length(Year.isLeap((int) year.getSelectedItem())));
            this.date.setSelectedIndex(day - 1);
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.date.setEnabled(enabled);
            this.month.setEnabled(enabled);
            this.year.setEnabled(enabled);
            super.setEnabled(enabled);
        }

        @SuppressWarnings("unchecked")
        private JComboBox<Object> add(int from, int to, @Nullable CenteredCellRenderer renderer) {
            JComboBox<Object> comboBox = (JComboBox<Object>) super.add(new JComboBox<>(IntStream.rangeClosed(from, to).boxed().toArray(Integer[]::new)));
            comboBox.setRenderer(renderer == null ? new CenteredCellRenderer() : renderer);
            comboBox.setFont(Main.FONT_PATUA);
            comboBox.addItemListener(event -> {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    this.validateDay();
                    this.updateTime();
                }
            });
            return comboBox;
        }

        private void updateTime() {
            this.datePicker.setDate(
                    (int) this.date.getSelectedItem(),
                    (int) this.month.getSelectedItem(),
                    (int) this.year.getSelectedItem()
            );
        }

        private static final class MonthRenderer extends CenteredCellRenderer {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return MathUtils.make((JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus),
                        label -> label.setText(WordUtils.capitalizeFully(Month.of((int) value).toString())));
            }
        }

        private static class CenteredCellRenderer extends DefaultListCellRenderer {
            public CenteredCellRenderer() {
                super.setHorizontalAlignment(JLabel.CENTER);
            }
        }
    }
}
