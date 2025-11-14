package ctu.cict.khanhtypo.utils;

import javax.swing.*;
import java.awt.*;

public class ScreenUtils {
    private static JFrame frame;
    private static final boolean isHeadless = GraphicsEnvironment.isHeadless();

    private ScreenUtils() {
    }

    public static Rectangle getDefaultScreenBound() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth() / 2;
        int height = (int) screenSize.getHeight() / 2;
        int x = width / 2;
        int y = height / 2;
        return new Rectangle(x, y, width, height);
    }

    public static boolean canScreenDisplay() {
        return !isHeadless;
    }

    public static void setFrame(JFrame f) {
        frame = f;
    }

    public static void packFrame() {
        if (frame == null) return;
        frame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        frame.setLocation(
                (screenWidth / 2) - (frame.getWidth() / 2),
                (screenHeight / 2) - (frame.getHeight() / 2)
        );
    }
}