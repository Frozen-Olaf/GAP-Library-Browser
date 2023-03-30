package delegate.ui.page;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import delegate.ui.button.ButtonDecorator;
import delegate.ui.icon.IconVault;
import delegate.ui.searchbar.SearchBar;
import delegate.ui.UserInterface;
import model.Model;

public abstract class Page extends JPanel implements PropertyChangeListener {

    protected final UserInterface userInterface;
    protected final JFrame frame;
    protected final Model model;

    protected JPanel contentPanel;
    protected JSplitPane splitPane;

    protected JPanel header;
    protected JPanel btnPanel;
    protected JButton homeBtn;
    protected JButton prevBtn;
    protected JButton nextBtn;

    protected SearchBar searchBar;

    protected Page prev;
    protected Page next;

    public Page(UserInterface userInterface, Model model) {
        super(new BorderLayout());
        this.userInterface = userInterface;
        frame = userInterface.getFrame();
        this.model = model;

        initNavigationBtns();

        userInterface.addObserver(this);
    }

    public void setPrev(Page prev) {
        this.prev = prev;
    }

    public void setNext(Page next) {
        this.next = next;
    }

    public void setDefaultSearchBtn() {
        searchBar.setDefaultSearch();
    }

    protected void constructSearchBar(boolean isPrimary) {
        searchBar = new SearchBar(userInterface, model, this, isPrimary);
    }

    private void initNavigationBtns() {
        btnPanel = new JPanel(new BorderLayout());
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        homeBtn = new JButton(IconVault.getHomeIcon());
        prevBtn = new JButton(IconVault.getLeftArrowIcon());
        nextBtn = new JButton(IconVault.getRightArrowIcon());

        Dimension homeBtnSize = homeBtn.getPreferredSize();
        homeBtnSize.width += 10;
        homeBtnSize.height += 4;
        homeBtn.setPreferredSize(homeBtnSize);
        homeBtn.setToolTipText("Go home");
        homeBtn.setOpaque(true);
        homeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UserInterface.getMultiPage().changeToPage("home", false);
            }
        });
        ButtonDecorator.addDefaultMouseListener(homeBtn);

        prevBtn.setToolTipText("Previous page");
        prevBtn.setOpaque(true);

        nextBtn.setToolTipText("Next page");
        nextBtn.setOpaque(true);

        prevBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (prev != null) {
                    UserInterface.getMultiPage().changeToPage(prev, false);
                }
            }
        });
        ButtonDecorator.addDefaultMouseListener(prevBtn);
        nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (next != null) {
                    UserInterface.getMultiPage().changeToPage(next, false);
                }
            }
        });
        ButtonDecorator.addDefaultMouseListener(nextBtn);

        // To contain the buttons so that they don't re-scale size when window is
        // resized.
        JPanel btnPanelTemp = new JPanel(new BorderLayout());
        btnPanelTemp.add(homeBtn, BorderLayout.WEST);
        btnPanelTemp.add(prevBtn, BorderLayout.CENTER);
        btnPanelTemp.add(nextBtn, BorderLayout.EAST);

        btnPanel.add(btnPanelTemp, BorderLayout.WEST);
    }

    private static final long serialVersionUID = 1L;
}
