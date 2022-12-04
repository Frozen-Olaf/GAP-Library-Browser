package delegate;

import java.awt.*;
import javax.swing.*;

public class MultiPages extends JPanel {
    
    private final JPanel pages = new JPanel(new CardLayout());

	public MultiPages() {
	    super(new BorderLayout());
	    add(pages, BorderLayout.CENTER);
	}
	
	public void addPage(Component page, String info) {
		pages.add(page, info);
	}

	public void changeToPage(String pageName) {
		((CardLayout)pages.getLayout()).show(pages, pageName);
	}


    private static final long serialVersionUID = 1L;
}