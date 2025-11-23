package ctu.cict.khanhtypo.utils;

import org.apache.commons.lang3.function.Consumers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.function.Consumer;

public class ScreenUtils {
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

    public static void packFrame(Window frame) {
        frame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        frame.setLocation(
                (screenWidth / 2) - (frame.getWidth() / 2),
                (screenHeight / 2) - (frame.getHeight() / 2)
        );
    }

    public static JPanel wrapPanel(Component... components) {
        return wrapPanel(Consumers.nop(), components);
    }

    public static JPanel wrapPanel(Consumer<JPanel> modifiers, Component... components) {
        return MathUtils.make(new JPanel(), panel -> {
            Arrays.stream(components).forEach(panel::add);
            modifiers.accept(panel);
        });
    }

    public static void trackSize(Component component) {
        component.addComponentListener(
                new  ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        System.out.println(component.getSize());
                    }
                }
        );
    }
}