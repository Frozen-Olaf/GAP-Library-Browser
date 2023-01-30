package delegate.searchbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import delegate.UserInterface;

public class CornerRoundedTextPane extends JPanel {

    private static final Dimension ARC = new Dimension(15, 15);

    private boolean isHighlight;
    private JTextField inputField;

    private boolean paintBorder;

    private FocusListener defaultFocusListener = new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
            CornerRoundedTextPane.this.setPaintBorder(true);
        }

        @Override
        public void focusLost(FocusEvent e) {
            CornerRoundedTextPane.this.setPaintBorder(false);
        }
    };

    public CornerRoundedTextPane(LayoutManager lm, boolean isHighlight) {
        super(lm);
        this.isHighlight = isHighlight;
        init();
    }

    public void setPaintBorder(boolean paintsBorder) {
        paintBorder = paintsBorder;
        repaint();
    }

    public JTextField getTextField() {
        return inputField;
    }

    public String getText() {
        return inputField.getText();
    }

    public void setText(String text) {
        inputField.setText(text);
    }
    
    public void setTextFieldSize(int width, int height) {
        inputField.setPreferredSize(new Dimension(width, height));
        inputField.setMinimumSize(new Dimension(0, height));
    }
    
    public void setFixedTextFieldSize(int width, int height) {
        Dimension d = new Dimension(width, height);
        inputField.setPreferredSize(d);
        inputField.setMinimumSize(d);
    }

    private void init() {
        inputField = new JTextField();
        inputField.setOpaque(false);
        inputField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        inputField.addFocusListener(defaultFocusListener);

        add(inputField, BorderLayout.CENTER);
        setOpaque(false);
    }

    public void replaceFocusListener(FocusListener fl) {
        inputField.removeFocusListener(defaultFocusListener);
        inputField.addFocusListener(fl);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (UserInterface.getIsInDarkTheme())
            if (isHighlight)
                graphics.setColor(Color.DARK_GRAY);
            else
                graphics.setColor(UIManager.getColor("TextField.background"));
        else
            graphics.setColor(Color.WHITE);
        graphics.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC.width, ARC.height);

        if (paintBorder) {
            graphics.setColor(Color.decode("#087cfc"));
            graphics.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC.width, ARC.height);
        }
        super.paintComponent(g);
    }

    private static final long serialVersionUID = 1L;
}
