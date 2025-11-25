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
    private final DateField datePickerField;
    private LocalDate date;
    private final JRadioButton nowButton;
    private final JRadioButton datePickerButton;
    private final ButtonGroup group;

    public DatePicker() {
        super(new FlowLayout(FlowLayout.LEFT));
        this.date = LocalDate.now();
        this.nowButton = (JRadioButton) super.add(new JRadioButton("Now."));
        nowButton.setFont(Main.FONT_PATUA);
        this.datePickerButton = (JRadioButton) super.add(new JRadioButton("Date:"));
        datePickerButton.setFont(Main.FONT_PATUA);

        this.datePickerField = (DateField) super.add(new DateField(this));
        datePickerField.setEnabled(false);

        this.group = new ButtonGroup();
        group.add(nowButton);
        group.add(datePickerButton);
        group.setSelected(nowButton.getModel(), true);

        nowButton.addItemListener(event ->
                datePickerField.setEnabled(!nowButton.isSelected())
        );
    }

    private void setDate(int date, int month, int year) {
        this.date = LocalDate.of(year, month, date);
    }

    public void setDate(Date date) {
        if (date != null) {
            this.datePickerField.setDate(date);
            this.group.setSelected(datePickerButton.getModel(), true);
            this.datePickerField.setEnabled(true);
        }
    }

    @Override
    public Object getAsBsonValue() {
        return this.nowButton.isSelected() ?
                Date.from(Instant.now()) :
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
        private IntRangedComboBox date, month, year;
        private final DatePicker datePicker;

        public DateField(DatePicker datePicker) {
            super(new FlowLayout(FlowLayout.LEFT));
            this.datePicker = datePicker;
            date = month = year = null;
            month = this.add(1, 12, new AlternativeTextRenderer<Integer>(
                    JLabel.CENTER,
                    month -> WordUtils.capitalizeFully(Month.of(month).toString())
            ));
            date = this.add(1, 31, null);
            year = this.add(1970, 2050, null);
            year.setSelectedValue(2025);
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.date.setEnabled(enabled);
            this.month.setEnabled(enabled);
            this.year.setEnabled(enabled);
            super.setEnabled(enabled);
        }

        private IntRangedComboBox add(int from, int to, @Nullable DefaultListCellRenderer renderer) {
            IntRangedComboBox comboBox = (IntRangedComboBox) super.add(new IntRangedComboBox(from, to, renderer));
            comboBox.setFont(Main.FONT_PATUA);
            comboBox.addItemListener(event -> {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    this.validateDay();
                    this.updateTime();
                }
            });
            return comboBox;
        }

        private void validateDay() {
            int month = this.month.getSelectedValue();
            int day = MathUtils.clampInclusive(this.date.getSelectedValue(), 1, Month.of(month).length(Year.isLeap(year.getSelectedValue())));
            this.date.setSelectedValue(day);
        }

        private void updateTime() {
            this.datePicker.setDate(
                    this.date.getSelectedValue(),
                    this.month.getSelectedValue(),
                    this.year.getSelectedValue()
            );
        }

        private void setDate(Date date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            this.year.setSelectedValue(calendar.get(Calendar.YEAR));
            this.month.setSelectedValue(calendar.get(Calendar.MONTH) + 1);
            this.date.setSelectedValue(calendar.get(Calendar.DATE));
        }

        private static class IntRangedComboBox extends JComboBox<Object> {
            private final int from;

            public IntRangedComboBox(int from, int to, @Nullable DefaultListCellRenderer renderer) {
                super(IntStream.rangeClosed(from, to).boxed().toArray(Integer[]::new));
                this.from = from;
                if (renderer != null) {
                    super.setRenderer(renderer);
                }
                if (super.getRenderer() instanceof JLabel r)
                    r.setHorizontalAlignment(JLabel.CENTER);
            }

            @Deprecated
            @Override
            public void setSelectedItem(Object anObject) {
                super.setSelectedItem(anObject);
            }

            @Deprecated
            @Override
            public @Nullable Object getSelectedItem() {
                return super.getSelectedItem();
            }

            public void setSelectedValue(int value) {
                super.setSelectedIndex(value - this.from);
            }

            public int getSelectedValue() {
                return (int) super.getSelectedItem();
            }
        }
    }
}
