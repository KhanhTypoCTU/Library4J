package ctu.cict.khanhtypo.forms;

import ctu.cict.khanhtypo.Main;
import ctu.cict.khanhtypo.books.Book;
import ctu.cict.khanhtypo.utils.ScreenUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Arrays;

public class SearchResultScreen extends JDialog {
    private JPanel basePanel;
    private JScrollPane scrollPane;
    private JPanel entriesList;

    public SearchResultScreen(IBookDB databaseBridge, Book[] searchedBook) {
        super(Main.baseFrame, "Search Results", true);
        entriesList.setLayout(new MigLayout("wrap", "[left]",""));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        Arrays.stream(searchedBook).map(b -> new BookEntry(databaseBridge, b, false))
                .forEach(entry -> entry.loadEntryToContainer(entriesList));
        super.setContentPane(basePanel);
        entriesList.revalidate();
        entriesList.repaint();
        //SwingUtilities.updateComponentTreeUI(entriesList);
        SwingUtilities.invokeLater(this::repositionScrolls);
        ScreenUtils.packFrame(this);
        super.setVisible(true);
    }

    private void repositionScrolls() {
        this.scrollPane.getVerticalScrollBar().setValue(0);
        this.scrollPane.getHorizontalScrollBar().setValue(0);
    }
}
