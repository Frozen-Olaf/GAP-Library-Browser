package delegate;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import delegate.multipage.MultiPage;
import delegate.page.HomePage;
import model.Model;
import model.icon.IconVault;

public class UserInterface {

    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;

    private static boolean isInDarkTheme;
    private static PropertyChangeSupport themeChangeNotifier;

    private JFrame frame;
    private Model model;

    private JMenuBar menu;
    private JFileChooser fileChooser;
    private FileNameExtensionFilter jsonFilter;

    private static MultiPage multiPage;
    private HomePage homePage;

    public UserInterface(Model model, boolean initWithDarkTheme) {
        this.model = model;
        isInDarkTheme = initWithDarkTheme;
        themeChangeNotifier = new PropertyChangeSupport(this);
    }

    public static UserInterface createUI(Model model, boolean initWithDarkTheme) {
        UserInterface userInterface = new UserInterface(model, initWithDarkTheme);
        userInterface.init();
        return userInterface;
    }

    public JFrame getFrame() {
        return frame;
    }

    public static boolean getIsInDarkTheme() {
        return isInDarkTheme;
    }

    public static void setIsInDarkTheme(boolean isDarkTheme) {
        isInDarkTheme = isDarkTheme;
    }

    public static MultiPage getMultiPage() {
        return multiPage;
    }

    public void addObserver(PropertyChangeListener listener) {
        themeChangeNotifier.addPropertyChangeListener("dark", listener);
        themeChangeNotifier.addPropertyChangeListener("light", listener);
    }

    public void init() {
        initLafTheme();

        frame = new JFrame("GAP Library Browser");
        frame.getContentPane().setLayout(new BorderLayout());

        multiPage = new MultiPage(this);
        frame.add(multiPage, BorderLayout.CENTER);

        initHomePage();
        initMenu();

        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(IconVault.getGAPIconImage());
        frame.setVisible(true);
    }

    private void initLafTheme() {
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.awt.application.name", "GAP Library Browser");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        if (isInDarkTheme)
            FlatMacDarkLaf.setup();
        else
            FlatMacLightLaf.setup();
    }

    private void initHomePage() {
        homePage = new HomePage(this, model);
        multiPage.addPage(homePage.getName(), homePage);
    }

    private void initMenu() {
        menu = new JMenuBar();
        ChangeListener cl = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                if (changeEvent.getSource() instanceof JMenu) {
                    menu.requestFocusInWindow();
                }
            }
        };

        JMenu file = new JMenu("File");
        file.addChangeListener(cl);
        JMenuItem load = new JMenuItem("Load");
        file.add(load);
        // This is to avoid an undesired behaviour where when menu loses focus,
        // it returns the focus to the previous focus owner for a very short time.
        menu.add(file);

        fileChooser = new JFileChooser();
        jsonFilter = new FileNameExtensionFilter("JSON files", "json");
        load.addActionListener(new ActionListener() {
            // Only allow user to load json file.
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.resetChoosableFileFilters();
                fileChooser.addChoosableFileFilter(jsonFilter);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setDialogTitle("Choose a json file");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                model.getModelData().readFromJson(file);
                            } catch (IOException ioe) {
                                JOptionPane.showMessageDialog(frame, ioe.getMessage() + " ;(");
                            }
                        }
                    });

                }
            }
        });

        JMenu display = new JMenu("Display");
        JMenu theme = new JMenu("Theme");
        theme.addChangeListener(cl);
        JMenuItem lightTheme = new JMenuItem("Light theme");
        JMenuItem darkTheme = new JMenuItem("Dark theme");
        theme.add(lightTheme);
        theme.add(darkTheme);
        display.add(theme);
        menu.add(display);
        lightTheme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        changeLaf(false);
                    }
                });
            }
        });
        darkTheme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        changeLaf(true);
                    }
                });
            }
        });

        frame.setJMenuBar(menu);
    }

    private void changeLaf(boolean isDarkTheme) {
        try {
            if (isDarkTheme) {
                UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacDarkLaf");
                SwingUtilities.updateComponentTreeUI(frame);
                themeChangeNotifier.firePropertyChange("dark", null, null);
            } else {
                UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacLightLaf");
                SwingUtilities.updateComponentTreeUI(frame);
                themeChangeNotifier.firePropertyChange("light", null, null);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        isInDarkTheme = isDarkTheme;
        fileChooser.updateUI();
    }

}
