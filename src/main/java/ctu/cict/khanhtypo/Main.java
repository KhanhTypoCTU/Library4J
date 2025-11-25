package ctu.cict.khanhtypo;

import ctu.cict.khanhtypo.forms.BookDatabaseScreen;
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
    public static final Font FONT_MORE_OFFC_ITALIC;
    public static final Font FONT_CRIMSON;
    public static final Font FONT_CRIMSON_ITALIC;
    public static boolean IN_DEV = false;
    public static JFrame baseFrame;

    public static void main(String[] args) {
        //https://github.com/ozlerhakan/mongodb-json-files/blob/master/datasets/books.json
        IN_DEV = ArrayUtils.contains(args, "devmode");
        if (ScreenUtils.canScreenDisplay()) {
            SwingUtilities.invokeLater(() -> {
                        JFrame frame = new JFrame();
                        baseFrame = frame;
                        frame.setIconImage(ResourceUtils.getImage("icon.png"));
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        BookDatabaseScreen baseScreen = new BookDatabaseScreen();
                        frame.setContentPane(baseScreen.getBasePanel());
                        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        frame.setVisible(true);
                        frame.setPreferredSize(new Dimension(1140, 880));
                        ScreenUtils.packFrame(frame);
                        frame.setResizable(false);
                    }
            );


        } else System.out.println("Screen can not be opened because this Java Runtime Environment is headless.");
    }


    static {
        try {
            FONT_MORE_OFFC_PRO = Font.createFont(Font.TRUETYPE_FONT, ResourceUtils.getResourceOrThrow("B.font", Function.identity()))
                    .deriveFont(20f);
            FONT_MORE_OFFC_ITALIC = Font.createFont(Font.TRUETYPE_FONT, ResourceUtils.getResourceOrThrow("C.font", Function.identity()))
                    .deriveFont(20f);
            FONT_PATUA = Font.createFont(Font.TRUETYPE_FONT, ResourceUtils.getResourceOrThrow("A.font", Function.identity()))
                    .deriveFont(18f);
            FONT_CRIMSON = Font.createFont(Font.TRUETYPE_FONT, ResourceUtils.getResourceOrThrow("D.font", Function.identity()))
                    .deriveFont(18f);
            FONT_CRIMSON_ITALIC = Font.createFont(Font.TRUETYPE_FONT, ResourceUtils.getResourceOrThrow("D_italic.font", Function.identity()))
                    .deriveFont(18f);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
