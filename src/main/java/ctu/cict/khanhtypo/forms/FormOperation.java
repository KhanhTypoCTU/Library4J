package ctu.cict.khanhtypo.forms;

public enum FormOperation {
    CREATE("Create");

    private final String displayName;

    FormOperation(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
