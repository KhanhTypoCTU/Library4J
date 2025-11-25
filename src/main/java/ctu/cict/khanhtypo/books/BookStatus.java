package ctu.cict.khanhtypo.books;

public enum BookStatus {
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
}
