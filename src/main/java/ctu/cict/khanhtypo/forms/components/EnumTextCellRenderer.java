package ctu.cict.khanhtypo.forms.components;

public class EnumTextCellRenderer extends AlternativeTextRenderer<Object> {
    public EnumTextCellRenderer(int horizontalAlignment) {
        super(horizontalAlignment, EnumTextCellRenderer::enumToString);
    }

    public EnumTextCellRenderer() {
        super(EnumTextCellRenderer::enumToString);
    }

    private static String enumToString(Object object) {
        if (object instanceof IStringRepresentable representable) {
            return representable.getRepresentingString();
        }
        return object.toString();
    }
}
