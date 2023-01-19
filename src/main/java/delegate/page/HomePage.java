package delegate.page;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import delegate.UserInterface;
import model.Model;
import model.icon.IconVault;

public class HomePage extends Page {

    private JLabel themeLogo;
    private JLabel logo;
    private JPanel homeDisplay;

    public HomePage(UserInterface userInterface, Model model) {
        super(userInterface, model);
        prev = null;

        init();
        setName("home");
    }

    public void updateGuiDisplay(boolean isMethodCheckBoxSelected) {
        logo.setVisible(!isMethodCheckBoxSelected);
    }

    private void init() {
        contentPanel = new JPanel(new BorderLayout());

        header = new JPanel(new BorderLayout());
        constructSearchBar(true);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        header.add(btnPanel, BorderLayout.NORTH);
        header.add(searchBar, BorderLayout.CENTER);

        contentPanel.add(header, BorderLayout.NORTH);

        homeDisplay = new JPanel(new BorderLayout());

        Icon initIcon = UserInterface.getIsInDarkTheme() ? IconVault.getDarkerHomeGapIcon()
                : IconVault.getHomeGapIcon();
        logo = new JLabel(initIcon);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
        homeDisplay.add(logo, BorderLayout.CENTER);

        Icon initThemeLogo = UserInterface.getIsInDarkTheme() ? IconVault.getMoonIcon() : IconVault.getSunIcon();
        themeLogo = new JLabel(initThemeLogo);
        themeLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        homeDisplay.add(themeLogo, BorderLayout.SOUTH);

        contentPanel.add(homeDisplay, BorderLayout.CENTER);

        JPanel invisible = new JPanel();
        invisible.setVisible(false);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, contentPanel, invisible);
        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerSize(0);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(1.0);

        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() != userInterface)
            return;
        String propertyName = evt.getPropertyName();
        if (propertyName == "dark") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    logo.setIcon(IconVault.getDarkerHomeGapIcon());
                    themeLogo.setIcon(IconVault.getMoonIcon());
                }
            });
        } else if (propertyName == "light") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    logo.setIcon(IconVault.getHomeGapIcon());
                    themeLogo.setIcon(IconVault.getSunIcon());
                }
            });
        }
    }

    private static final long serialVersionUID = 1L;
}
