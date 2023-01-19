package delegate.searchbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.UIManager;

import delegate.UserInterface;

public class CornerRoundedPanel extends JPanel {
    
    private Dimension arcs = new Dimension(15, 15);
    private boolean paintBorder;
    private boolean isHighlight;

    public CornerRoundedPanel(LayoutManager lm, boolean isHighlight) {
        super(lm);
        this.isHighlight = isHighlight;
        setOpaque(false);
    }

    public void setPaintBorder(boolean paintsBorder) {
        paintBorder = paintsBorder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (UserInterface.getIsInDarkTheme())
            if(isHighlight)
                graphics.setColor(Color.DARK_GRAY);
            else
                graphics.setColor(UIManager.getColor("TextField.background"));
        else
            graphics.setColor(Color.WHITE);
        graphics.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcs.width, arcs.height);

        if (paintBorder) {
            graphics.setColor(Color.decode("#087cfc"));
            graphics.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcs.width, arcs.height);
        }
    }

    private static final long serialVersionUID = 1L;
}
