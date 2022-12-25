package delegate;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Model;

/**
 * This is the delegate part of this GUI application.
 */
public class Delegate implements PropertyChangeListener {

    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;

    protected static final MultiPages multiPages = new MultiPages();
    private JFrame frame;
    private JMenuBar menu;

    private JFileChooser fileChooser;
    private FileNameExtensionFilter json_filter;
    
    private HomePage homePage;
    
    private Model model;

    public Delegate(Model model) {
        this.model = model;

        init();

        model.addObserver(this);
    }
    
    private void init() {
        frame = new JFrame("GAP Library Browser");
        
        initHomePage();
        initMenu();
        
        //DO NOT add any component to JFrame before its layout is set, it won't display properly!!!
        frame.getContentPane().setLayout(new BorderLayout());
        frame.add(multiPages, BorderLayout.CENTER);

        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Model.getGAPIconImage());
        frame.setVisible(true);
    }
    
    private void initHomePage() {
        homePage = new HomePage(frame, model);
        multiPages.addPage(homePage.getName(), homePage);
    }
    

    private void initMenu() {
        menu = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem load = new JMenuItem("Load");
        file.add(load);
        menu.add(file);

        fileChooser = new JFileChooser();
        json_filter = new FileNameExtensionFilter("JSON files", "json");
        
        load.addActionListener(new ActionListener() {
            /**
             * Allow user to only load JSON file.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.resetChoosableFileFilters();
                fileChooser.addChoosableFileFilter(json_filter);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setDialogTitle("Choose a json file");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
                	
                    File file = fileChooser.getSelectedFile();
                    try {
                        model.readFromJson(file);
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(frame, ioe.getMessage() + " ;(");
                    }
                    
                }
            }
        });
        
        frame.setJMenuBar(menu);
    }

    /**
     * This receives and reacts to property change event from model.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() != model)
            return;
        String propertyName = evt.getPropertyName();
        if (propertyName == "illf") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(frame, evt.getNewValue());
                }
            });
        } else if (propertyName == "404") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(frame, evt.getNewValue() + " not found.");
                }
            });
        } else if (propertyName == "rtdir") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(frame, "GAP root directory not found or illegal:\n" + evt.getNewValue());
                }
            });
        } else if (propertyName == "illflt") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(frame, evt.getNewValue());
                }
            });
        } else if (propertyName == "success") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(frame, "Successfully loaded the JSON file from:\n" + evt.getNewValue());
                }
            });
        }

    }
}
