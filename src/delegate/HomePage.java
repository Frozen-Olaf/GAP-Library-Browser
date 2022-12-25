package delegate;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import model.Model;

public class HomePage extends Page {
    
    private JPanel header;
    private JLabel homeMsg;
    private JLabel logo;
    private JPanel homeDisplay;
    
    public HomePage(JFrame frame, Model model) {
        super(frame, model);
        prev = null;
        
        contentPanel = new JPanel(new BorderLayout());
        
        header = new JPanel(new BorderLayout());
        constructSearchBar(true);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        header.add(btnPanel, BorderLayout.NORTH);
        header.add(searchBar, BorderLayout.CENTER);
        
        contentPanel.add(header, BorderLayout.NORTH);
        
        homeDisplay = new JPanel(new BorderLayout());
        homeMsg = new JLabel("HOME");
        homeMsg.setFont(new Font("DialogInput", Font.PLAIN, 40));
        homeMsg.setHorizontalAlignment(JLabel.CENTER);
        logo = new JLabel(Model.createGAPIcon(200, 200));
        homeDisplay.add(logo, BorderLayout.CENTER);
        homeDisplay.add(homeMsg, BorderLayout.SOUTH);
        
        contentPanel.add(homeDisplay, BorderLayout.CENTER);
        
        
        JPanel invisible = new JPanel();
        invisible.setVisible(false);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                contentPanel, invisible);
        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerSize(0);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(1.0);
        
        add(splitPane, BorderLayout.CENTER);
        setName("home");

    }


    private static final long serialVersionUID = 1L;
}
