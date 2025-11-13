package ctu.cict.khanhtypo.forms;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.regex.Pattern;

public class RegexBasedDocumentFilter extends DocumentFilter {
    private Pattern pattern;
    public RegexBasedDocumentFilter(String regex) {
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string != null && (fb.getDocument().getLength() == 0
                ? this.pattern.matcher(string).matches()
                : this.pattern.matcher(fb.getDocument().getText(0, fb.getDocument().getLength()) + string).matches())) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (text != null && (fb.getDocument().getLength() == 0
                ? this.pattern.matcher(text).matches()
                : this.pattern.matcher(fb.getDocument().getText(0, fb.getDocument().getLength() - length) + text).matches())) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
