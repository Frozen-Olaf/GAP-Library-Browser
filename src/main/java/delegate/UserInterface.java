package delegate;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
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
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import delegate.multipage.MultiPage;
import delegate.page.CodePage;
import delegate.page.HomePage;
import model.Model;
import model.icon.IconVault;
import model.searchsuggestion.DeferredDocumentListener;

public class UserInterface {

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 750;
    
    private static final int FRAME_MIN_WIDTH = 386;
    
    private static boolean isInDarkTheme;
    private static PropertyChangeSupport UIChangeNotifier;

    private JFrame frame;
    private Model model;

    private JMenuBar menu;
    private JFileChooser fileChooser;

    private static MultiPage multiPage;
    private HomePage homePage;

    public UserInterface(Model model, boolean initWithDarkTheme) {
        this.model = model;
        isInDarkTheme = initWithDarkTheme;
        UIChangeNotifier = new PropertyChangeSupport(this);
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
        UIChangeNotifier.addPropertyChangeListener("dark", listener);
        UIChangeNotifier.addPropertyChangeListener("light", listener);
        UIChangeNotifier.addPropertyChangeListener("response", listener);
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
        frame.setMinimumSize(new Dimension(FRAME_MIN_WIDTH, 0));
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

        initFileMenu(cl);
        initDisplayMenu(cl);
        initPageMenu(cl);
        initConfigureMenu(cl);

        frame.setJMenuBar(menu);
    }

    private void initFileMenu(ChangeListener cl) {
        JMenu file = new JMenu("File");
        // This is to avoid an undesired behaviour where when menu loses focus,
        // it returns the focus to the previous focus owner for a very short time.
        file.addChangeListener(cl);

        fileChooser = new JFileChooser();
        FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("JSON files", "json");
        JMenuItem load = new JMenuItem("Load");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.resetChoosableFileFilters();
                // Only allow user to load json file.
                fileChooser.addChoosableFileFilter(jsonFilter);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setDialogTitle("Choose a json file");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                model.getModelData().readFromJson(file);
                            } catch (IOException ioe) {
                                JOptionPane.showMessageDialog(frame, ioe.getMessage());
                            }
                        }
                    });
                }
            }
        });
        file.add(load);

        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component comp = multiPage.getCurrentPage();
                if (comp instanceof CodePage) {
                    fileChooser.resetChoosableFileFilters();
                    fileChooser.setAcceptAllFileFilterUsed(false);
                    fileChooser.setDialogTitle("Choose a directory");
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        File dir = fileChooser.getSelectedFile();
                        if (!dir.isDirectory()) {
                            JOptionPane.showMessageDialog(frame,
                                    "Please select a directory in which to save the file.");
                            return;
                        }
                        CodePage codePage = (CodePage) comp;

                        String fileName;
                        String filePath;
                        File file;
                        while (true) {
                            fileName = JOptionPane.showInputDialog(frame,
                                    "Please name this file, including its filename extension:");
                            if (fileName == null)
                                return;
                            else if (fileName.isBlank()) {
                                JOptionPane.showMessageDialog(frame, "Please enter a valid file name");
                                continue;
                            }
                            filePath = dir.getAbsolutePath() + "/" + fileName;
                            file = new File(filePath);
                            if (file.exists()) {
                                int choice = JOptionPane.showConfirmDialog(frame,
                                        "The file already exists in that directory. Do you want to override it?");
                                if (choice == JOptionPane.YES_OPTION) {
                                    saveToFileFromCodePage(codePage, filePath);
                                    break;
                                } else if (choice == JOptionPane.NO_OPTION) {
                                    continue;
                                } else
                                    return;
                            } else {
                                saveToFileFromCodePage(codePage, filePath);
                                return;
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Found nothing to save.");
                }
            }
        });
        file.add(save);

        int c = frame.getToolkit().getMenuShortcutKeyMaskEx();
        load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, c));

        menu.add(file);
    }

    private void initDisplayMenu(ChangeListener cl) {
        JMenu display = new JMenu("Display");
        display.addChangeListener(cl);
        JMenu theme = new JMenu("Theme");
        theme.addChangeListener(cl);
        JMenuItem lightTheme = new JMenuItem("Light Theme");
        JMenuItem darkTheme = new JMenuItem("Dark Theme");
        theme.add(lightTheme);
        theme.add(darkTheme);
        display.add(theme);
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

        int ctrl = frame.getToolkit().getMenuShortcutKeyMaskEx();
        int shift = InputEvent.SHIFT_DOWN_MASK;
        lightTheme.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ctrl | shift));
        darkTheme.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ctrl | shift));

        menu.add(display);
    }

    private void initPageMenu(ChangeListener cl) {
        JMenu page = new JMenu("Page");
        page.addChangeListener(cl);
        JMenu codePage = new JMenu("Code Page");
        codePage.addChangeListener(cl);
        JMenuItem newCodePage = new JMenuItem("New Page");
        newCodePage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        CodePage p = new CodePage(UserInterface.this, model);
                        UserInterface.getMultiPage().addPage(p.getName(), p);
                        UserInterface.getMultiPage().changeToPage(p, false);
                        p.getCodeTextArea().requestFocusInWindow();
                    }
                });
            }
        });

        JMenuItem find = new JMenuItem("Find...");
        find.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Component comp = multiPage.getCurrentPage();
                        if (comp instanceof CodePage) {
                            CodePage codePage = (CodePage) comp;
                            codePage.showFindDialog();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Can only find words in a code page.");
                        }
                    }
                });
            }
        });
        JMenuItem replace = new JMenuItem("Replace...");
        replace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Component comp = multiPage.getCurrentPage();
                        if (comp instanceof CodePage) {
                            CodePage codePage = (CodePage) comp;
                            codePage.showReplaceDialog();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Can only replace words in a code page.");
                        }
                    }
                });
            }
        });
        JMenuItem goToLine = new JMenuItem("Go To Line...");
        goToLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Component comp = multiPage.getCurrentPage();
                        if (comp instanceof CodePage) {
                            CodePage codePage = (CodePage) comp;
                            codePage.showGoToLineDialog();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Can only do this in a code page.");
                        }
                    }
                });
            }
        });

        int c = frame.getToolkit().getMenuShortcutKeyMaskEx();
        newCodePage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, c));
        find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, c));
        replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, c));
        goToLine.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, c));

        codePage.add(newCodePage);
        codePage.add(find);
        codePage.add(replace);
        codePage.add(goToLine);

        page.add(codePage);
        menu.add(page);
    }

    private void initConfigureMenu(ChangeListener cl) {
        JMenu config = new JMenu("Configure");
        config.addChangeListener(cl);
        JMenu searchSuggest = new JMenu("Search Suggestion Response");
        searchSuggest.addChangeListener(cl);

        JMenuItem faster = new JMenuItem("Faster");
        JMenuItem slower = new JMenuItem("Slower");
        JMenuItem custom = new JMenuItem("Custom");

        faster.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        UIChangeNotifier.firePropertyChange("response", 0,
                                DeferredDocumentListener.FASTER_RESPONSE_TIME);
                    }
                });
            }
        });
        slower.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        UIChangeNotifier.firePropertyChange("response", 0,
                                DeferredDocumentListener.SLOWER_RESPONSE_TIME);
                    }
                });
            }
        });
        custom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String delay;
                        int delayTime = DeferredDocumentListener.FASTER_RESPONSE_TIME;
                        while (true) {
                            delay = JOptionPane.showInputDialog(frame,
                                    "Please enter a response time in milliseconds (ms)\n"
                                            + "[suggested range: 250-1000ms]");
                            if (delay == null)
                                return;
                            try {
                                delayTime = Integer.parseInt(delay);
                                break;
                            } catch (NumberFormatException nfe) {
                                JOptionPane.showMessageDialog(frame, "Please enter a valid number");
                            }
                        }
                        UIChangeNotifier.firePropertyChange("response", 0, delayTime);
                    }
                });
            }
        });

        int ctrl = frame.getToolkit().getMenuShortcutKeyMaskEx();
        int shift = InputEvent.SHIFT_DOWN_MASK;
        faster.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrl | shift));
        slower.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ctrl | shift));
        custom.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ctrl | shift));

        searchSuggest.add(faster);
        searchSuggest.add(slower);
        searchSuggest.add(custom);

        config.add(searchSuggest);
        menu.add(config);
    }

    private void saveToFileFromCodePage(CodePage codePage, String filePath) {
        String text = codePage.getCodeTextContent();
        if (text != null) {
            try {
                model.getModelData().saveFile(filePath, text);
                codePage.updateFileInfoDisplay(filePath);
                multiPage.updateTabName(codePage, filePath);
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(frame, ioe.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Error: code content to be saved is null.");
        }
    }

    private void changeLaf(boolean isDarkTheme) {
        try {
            if (isDarkTheme) {
                UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacDarkLaf");
                UIChangeNotifier.firePropertyChange("dark", null, null);
            } else {
                UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacLightLaf");
                UIChangeNotifier.firePropertyChange("light", null, null);
            }
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        isInDarkTheme = isDarkTheme;
        fileChooser.updateUI();
    }

}
