package ctu.cict.khanhtypo;

import ctu.cict.khanhtypo.forms.BaseScreen;
import ctu.cict.khanhtypo.utils.ResourceUtils;
import ctu.cict.khanhtypo.utils.ScreenUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.function.Function;

public class Main {
    public static final int MIN_FRAME_WIDTH = 900;
    public static final Font FONT_PATUA;
    public static final Font FONT_MORE_OFFC_PRO;
    public static boolean IN_DEV = false;

    public static void main(String[] args) {
        //https://github.com/ozlerhakan/mongodb-json-files/blob/master/datasets/books.json
        IN_DEV = ArrayUtils.contains(args, "devmode");
        if (ScreenUtils.canScreenDisplay()) {
            SwingUtilities.invokeLater(() -> {
                        JFrame frame = new JFrame();
                        frame.setIconImage(ResourceUtils.getImage("icon.png"));
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        BaseScreen baseScreen = new BaseScreen();
                        frame.setContentPane(baseScreen.getBasePanel());
                        ScreenUtils.setFrame(frame);
                        ScreenUtils.packFrame();
                        //frame.setResizable(false);
                        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        frame.setVisible(true);
                        frame.setMinimumSize(new Dimension(MIN_FRAME_WIDTH, 0));

                    }
            );
        } else System.out.println("Screen can not be opened because this Java Runtime Environment is headless.");
    }

    static {
        try {
            FONT_MORE_OFFC_PRO = Font.createFont(Font.TRUETYPE_FONT, ResourceUtils.getResourceOrThrow("B.font", Function.identity()))
                    .deriveFont(20f);
            FONT_PATUA = Font.createFont(Font.TRUETYPE_FONT, ResourceUtils.getResourceOrThrow("A.font", Function.identity()))
                    .deriveFont(18f);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
