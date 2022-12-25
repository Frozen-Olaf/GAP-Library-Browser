package delegate;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Model;

public class MultiPages extends JPanel {
    
    protected static final JTabbedPane tabs = new JTabbedPane();

	public MultiPages() {
	    super(new BorderLayout());
	    tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.setBackground(getBackground());
        tabs.addChangeListener(new ChangeListener() {
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
        tabs.setTabComponentAt(tabs.getTabCount()-1, new ButtonTab(Model.createGAPIcon(17, 17), pageName));
        tabs.validate();
	}

	public boolean changeToPage(String pageName) {
        int index = tabs.indexOfTab(pageName);
	    boolean res = (index != -1);
	    if (res) {
	        tabs.setSelectedIndex(index);
	    }
	    return res;
	}
	
	public boolean changeToPage(Page page) {
        int index = tabs.indexOfComponent(page);
        boolean res = (index != -1);
        if (res) {
            tabs.setSelectedIndex(index);
        }
        return res;
    }



    private static final long serialVersionUID = 1L;
}