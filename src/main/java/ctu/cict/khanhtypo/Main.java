package ctu.cict.khanhtypo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import ctu.cict.khanhtypo.forms.BaseScreen;
import ctu.cict.khanhtypo.utils.ResourceUtils;
import ctu.cict.khanhtypo.utils.ScreenUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bson.Document;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

public class Main {
    public static final String DATABASE_NAME = "Books";
    public static final String COLLECTION_BOOKS = "allBooks";
    public static boolean IN_DEV = false;

    public static void main(String[] args) {
        //https://github.com/ozlerhakan/mongodb-json-files/blob/master/datasets/books.json
        IN_DEV = ArrayUtils.contains(args, "devmode");
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        if (ScreenUtils.canScreenDisplay()) {
            JFrame frame = new JFrame();
            frame.setIconImage(ResourceUtils.getResourceOrThrow("icon.png",
                    input -> {
                        try {
                            return ImageIO.read(input);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            BaseScreen baseScreen = new BaseScreen();
            frame.setContentPane(baseScreen.getBasePanel());
            ScreenUtils.setFrame(frame);
            ScreenUtils.packFrame();
            frame.setResizable(false);
            frame.setVisible(true);
        } else System.out.println("Screen can not be opened because this Java Runtime Environment is headless.");
    }

}
