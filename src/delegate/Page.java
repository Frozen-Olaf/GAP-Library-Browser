package delegate;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import model.Model;

public abstract class Page extends JPanel {
    protected JFrame frame;
    protected Model model;
    protected SearchBar searchBar;
    
    protected JPanel contentPanel;
    protected JSplitPane splitPane;

    protected JPanel btnPanel;
    protected JButton homeBtn;
    protected JButton prevBtn;
    protected JButton nextBtn;
    protected Page prev;
    protected Page next;

    public Page(JFrame frame, Model model) {
        super(new BorderLayout());
        this.frame = frame;
        this.model = model;
        initTraverseBtn();
    }

    protected void constructSearchBar(boolean isPrimary) {
        searchBar = new SearchBar(frame, model, this, isPrimary);
    }

    protected void setDefaultSearchBtn() {
        searchBar.setDefaultSearch();
    }
    
    private void initTraverseBtn() {
        btnPanel = new JPanel(new BorderLayout());
        homeBtn = new JButton("home");
        prevBtn = new JButton(Model.getLeftArrowIcon());
        nextBtn = new JButton(Model.getRightArrowIcon());

        Dimension homeBtnSize = homeBtn.getPreferredSize();
        homeBtnSize.width-=10;
        homeBtnSize.height+=4;
        
        homeBtn.setToolTipText("Go home");
        homeBtn.setMinimumSize(homeBtnSize);
        homeBtn.setPreferredSize(homeBtnSize);
        homeBtn.setOpaque(true);
        homeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Delegate.multiPages.changeToPage("home");
            }
        });
        homeBtn.addMouseListener(btnMouseCursorAdapter);
        
        prevBtn.setToolTipText("Previous page");
        prevBtn.setOpaque(true);
        
        nextBtn.setToolTipText("Next page");
        nextBtn.setOpaque(true);
        
        prevBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (prev != null) {
                    Delegate.multiPages.changeToPage(prev);
                }
            }
        });
        prevBtn.addMouseListener(btnMouseCursorAdapter);
        nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (next != null) {
                    Delegate.multiPages.changeToPage(next);
                }
            }
        });
        nextBtn.addMouseListener(btnMouseCursorAdapter);       
        
        JPanel btnPanelTemp = new JPanel(new BorderLayout());
        btnPanelTemp.add(homeBtn, BorderLayout.WEST);
        btnPanelTemp.add(prevBtn, BorderLayout.CENTER);
        btnPanelTemp.add(nextBtn, BorderLayout.EAST);
        
        btnPanel.add(btnPanelTemp, BorderLayout.WEST);
    }
    
    protected final MouseAdapter btnMouseCursorAdapter = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(Cursor.getDefaultCursor());
        }
    };


    private static final long serialVersionUID = 1L;
}
