package delegate.multipage;

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
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

import delegate.UserInterface;

public class ButtonTab extends JPanel {

    private final JTabbedPane tabs;
    private final Icon icon;
    private String title;

    private JLabel ic;
    private JLabel textLabel;
    private TabCloseButton closeBtn;

    public ButtonTab(JTabbedPane tabs, Icon icon, String title) {
        super(new BorderLayout());
        this.tabs = tabs;
        this.icon = icon;
        this.title = title;

        init();
    }

    public String getTitle() {
        return title;
    }
    
    public void updateTabName(String name) {
        title = name.substring(name.lastIndexOf("/") + 1);
        setToolTipText(name);
        textLabel.setText(title);
    }

    private void init() {
        ic = new JLabel(icon);
        ic.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        ic.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        textLabel = new JLabel(title);
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setVerticalAlignment(JLabel.CENTER);
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        setToolTipText(title);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
        add(ic, BorderLayout.WEST);
        add(textLabel, BorderLayout.CENTER);
        if (tabs.getTabCount() > 1) {
            closeBtn = new TabCloseButton();
            add(closeBtn, BorderLayout.EAST);
        }
        setPreferredSize(new Dimension(100, getPreferredSize().height));
        addMouseListener(tabMouseListener);
    }

    private final MouseListener tabMouseListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof ButtonTab) {
                ButtonTab tab = (ButtonTab) component;
                UserInterface.getMultiPage().changeToPage(tab.getTitle(), false);
            }
        }
    };

    private class TabCloseButton extends JButton implements ActionListener {

        public TabCloseButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("Close");
            addMouseListener(btnMouseListener);

            setUI(new BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            setRolloverEnabled(true);
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int i = tabs.indexOfTabComponent(ButtonTab.this);
            if (i != -1) {
                tabs.remove(i);
            }
        }

        // No need to update UI for this button.
        @Override
        public void updateUI() {
        }

        // Paint the cross close button.
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            // shift the image slightly when button is pressed.
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

        private final MouseAdapter btnMouseListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                updateButtonDisplay(e, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                updateButtonDisplay(e, false);
            }
        };

        private void updateButtonDisplay(MouseEvent e, boolean isEntering) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setOpaque(isEntering);
                if (isEntering)
                    button.setBackground(Color.LIGHT_GRAY);
                else
                    button.setBackground(Color.WHITE);
            }
            e.consume();
        }

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
