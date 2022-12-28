package delegate;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

public class ButtonTab extends JPanel{

    private static final JTabbedPane tabs = MultiPages.tabs;
    
    private JLabel ic;
    private JLabel text;
    
    private String title;
    
    public String getTitle() {
        return title;
    }
    
    public ButtonTab(ImageIcon icon, String title) {
        super(new BorderLayout());
        this.title = title;
        
        setOpaque(false);
        
        ic = new JLabel(icon);
        ic.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        ic.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        text = new JLabel(title);
        text.setHorizontalAlignment(JLabel.CENTER);
        text.setVerticalAlignment(JLabel.CENTER);
        text.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        setToolTipText(title);
        
        if (tabs.getTabCount() > 1) {
            JButton button = new TabCloseButton();
            add(button, BorderLayout.EAST);
        }
        
        setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
        add(ic, BorderLayout.WEST);
        add(text, BorderLayout.CENTER);
        setPreferredSize(new Dimension(100, getPreferredSize().height));
        addMouseListener(tabMouseListener);
    }
 
    private class TabCloseButton extends JButton implements ActionListener {

        public TabCloseButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("Close");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = tabs.indexOfTabComponent(ButtonTab.this);
            if (i != -1) {
                tabs.remove(i);
            }
        }
 
        //No need update UI for this button.
        public void updateUI() {}
 
        //Paint the cross close button.
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.DARK_GRAY);
            if (getModel().isRollover()) {
                g2.setColor(Color.BLACK);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
        
        private static final long serialVersionUID = 1L;
    }
    
    private static final MouseListener tabMouseListener = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof ButtonTab) {
                ButtonTab tab = (ButtonTab) component;
                Delegate.multiPages.changeToPage(tab.getTitle(), false);
            }
        }

        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof ButtonTab) {
                ButtonTab tab = (ButtonTab) component;
                tabs.setBackgroundAt(tabs.indexOfTabComponent(tab), Color.white);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof ButtonTab) {
                ButtonTab tab = (ButtonTab) component;
                tabs.setBackgroundAt(tabs.indexOfTabComponent(tab), tabs.getBackground());
            }
        }
    };
    
    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setOpaque(true);
                button.setBackground(Color.LIGHT_GRAY);
                //button.setBorderPainted(true);
            }
        }
 
        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setOpaque(false);
                button.setBackground(Color.WHITE);
                //button.setBorderPainted(false);
            }
        }
    };
    

    private static final long serialVersionUID = 1L;
}
