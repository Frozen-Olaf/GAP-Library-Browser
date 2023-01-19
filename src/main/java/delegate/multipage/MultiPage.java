package delegate.multipage;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import delegate.UserInterface;
import delegate.page.Page;
import model.icon.IconVault;

public class MultiPage extends JPanel implements PropertyChangeListener {

    private final UserInterface userInterface;
    private final JFrame frame;
    private JTabbedPane tabs;

    public MultiPage(UserInterface userInterface) {
        super(new BorderLayout());
        this.userInterface = userInterface;
        this.frame = userInterface.getFrame();
        init();

        userInterface.addObserver(this);
    }

    private void init() {
        tabs = new JTabbedPane();
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.setBackground(getBackground());
        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (e.getSource() instanceof JTabbedPane) {
                    int index = tabs.getSelectedIndex();
                    Page page = (Page) tabs.getComponentAt(index);
                    page.setDefaultSearchBtn();
                }
            }
        });
        add(tabs, BorderLayout.CENTER);
    }

    public void addPage(String pageName, Component page) {
        tabs.addTab(pageName, page);
        Icon tabIcon = UserInterface.getIsInDarkTheme() ? IconVault.getDarkerTabGapIcon() : IconVault.getTabGapIcon();
        tabs.setTabComponentAt(tabs.getTabCount() - 1, new ButtonTab(tabs, tabIcon, pageName));
        tabs.validate();
    }

    public boolean changeToPage(String pageName, boolean isCalledFromASearch) {
        int index = tabs.indexOfTab(pageName);
        if (isCalledFromASearch && tabs.getSelectedIndex() == index) {
            JOptionPane.showMessageDialog(frame, "You are already in your destination.");
        }
        boolean found = (index != -1);
        if (found) {
            tabs.setSelectedIndex(index);
        }
        return found;
    }

    public boolean changeToPage(Page page, boolean isCalledFromASearch) {
        int index = tabs.indexOfComponent(page);
        if (isCalledFromASearch && tabs.getSelectedIndex() == index) {
            JOptionPane.showMessageDialog(frame, "You are already in your destination.");
        }
        boolean res = (index != -1);
        if (res) {
            tabs.setSelectedIndex(index);
        }
        return res;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() != userInterface)
            return;
        String propertyName = evt.getPropertyName();
        if (propertyName == "dark") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    for (int i = 0; i < tabs.getTabCount(); i++) {
                        tabs.setIconAt(i, IconVault.getDarkerTabGapIcon());
                    }
                }
            });
        } else if (propertyName == "light") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    for (int i = 0; i < tabs.getTabCount(); i++) {
                        tabs.setIconAt(i, IconVault.getTabGapIcon());
                    }
                }
            });
        }
    }

    private static final long serialVersionUID = 1L;
}