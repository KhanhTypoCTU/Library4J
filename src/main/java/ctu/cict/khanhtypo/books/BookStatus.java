package ctu.cict.khanhtypo.books;

import ctu.cict.khanhtypo.forms.components.IStringRepresentable;

public enum BookStatus implements IStringRepresentable {
    PUBLISH("Released"),
    DISCONTINUED("Discontinued"),
    MEAP("Early-Access"),
    ;
    private final String displayText;

    BookStatus(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

    @Override
    public String getRepresentingString() {
        return this.getDisplayText();
    }
}
