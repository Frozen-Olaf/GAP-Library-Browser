package delegate.ui.icon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.coobird.thumbnailator.Thumbnails;

public abstract class IconVault {

    public static final Dimension TAB_ICON_SIZE = new Dimension(17, 17);
    public static final Dimension HOMEPAGE_LOGO_SIZE = new Dimension(200, 200);

    private static final Dimension GUI_THEME_ICON_SIZE = new Dimension(30, 30);
    private static final Dimension HOME_ICON_SIZE = new Dimension(16, 16);
    private static final Dimension ARROW_ICON_SIZE = new Dimension(16, 16);
    private static final Dimension SEARCH_ICON_SIZE = new Dimension(23, 23);
    private static final Dimension ERASE_ICON_SIZE = new Dimension(27, 27);
    private static final Dimension INPUT_ICON_SIZE = new Dimension(21, 21);
    private static final Dimension CLEAR_ICON_SIZE = new Dimension(17, 17);
    private static final Dimension FILTER_ICON_SIZE = new Dimension(21, 21);

    private static final BufferedImage gapIconImage = createIconImage("/images/GAP-icon.png");

    private static final Icon tabGapIcon = createIcon(gapIconImage, TAB_ICON_SIZE);
    private static final Icon homeGapIcon = createIcon(gapIconImage, HOMEPAGE_LOGO_SIZE);
    private static final Icon darkerTabGapIcon = createIcon(AdjustBrightness(gapIconImage, 0.84f), TAB_ICON_SIZE);
    private static final Icon darkerHomeGapIcon = createIcon(AdjustBrightness(gapIconImage, 0.84f), HOMEPAGE_LOGO_SIZE);

    private static final Icon sunIcon = createIcon("/images/sun-icon.png", GUI_THEME_ICON_SIZE);
    private static final Icon moonIcon = createDarkerIcon("/images/moon-icon.png", GUI_THEME_ICON_SIZE, 0.9f);
    private static final Icon homeIcon = createIcon("/images/home-icon.png", HOME_ICON_SIZE);
    private static final Icon leftArrowIcon = createIcon("/images/left-arrow.png", ARROW_ICON_SIZE);
    private static final Icon rightArrowIcon = createIcon("/images/right-arrow.png", ARROW_ICON_SIZE);
    private static final Icon inputIcon = createIcon("/images/input-icon.png", INPUT_ICON_SIZE);
    private static final Icon eraseIcon = createIcon("/images/erase-icon.png", ERASE_ICON_SIZE);
    private static final Icon searchIcon = createIcon("/images/search-icon.png", SEARCH_ICON_SIZE);
    private static final Icon clearIcon = createIcon("/images/clear-icon.png", CLEAR_ICON_SIZE);
    private static final Icon filterIcon = createIcon("/images/filter-icon.png", FILTER_ICON_SIZE);

    public static Image getGAPIconImage() {
        return gapIconImage;
    }

    public static Icon getTabGapIcon() {
        return tabGapIcon;
    }

    public static Icon getHomeGapIcon() {
        return homeGapIcon;
    }

    public static Icon getDarkerTabGapIcon() {
        return darkerTabGapIcon;
    }

    public static Icon getDarkerHomeGapIcon() {
        return darkerHomeGapIcon;
    }

    public static Icon getSunIcon() {
        return sunIcon;
    }

    public static Icon getMoonIcon() {
        return moonIcon;
    }

    public static Icon getHomeIcon() {
        return homeIcon;
    }

    public static Icon getLeftArrowIcon() {
        return leftArrowIcon;
    }

    public static Icon getRightArrowIcon() {
        return rightArrowIcon;
    }

    public static Icon getInputIcon() {
        return inputIcon;
    }

    public static Icon getSearchIcon() {
        return searchIcon;
    }

    public static Icon getEraseIcon() {
        return eraseIcon;
    }

    public static Icon getClearIcon() {
        return clearIcon;
    }
    
    public static Icon getFilterIcon() {
        return filterIcon;
    }

    private static BufferedImage createIconImage(String path) {
        try {
            return ImageIO.read(IconVault.class.getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final Icon createIcon(BufferedImage image, Dimension d) {
        try {
            BufferedImage thumbnail = Thumbnails.of(image).size(d.width, d.height).keepAspectRatio(true)
                    .asBufferedImage();
            return new ImageIcon(thumbnail);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static BufferedImage AdjustBrightness(Image source, float brightnessPercentage) {
        BufferedImage output = new BufferedImage(source.getWidth(null), source.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        int[] pixel = new int[4];
        float[] hsbvals = new float[3];
        output.getGraphics().drawImage(source, 0, 0, null);

        for (int i = 0; i < output.getHeight(); i++) {
            for (int j = 0; j < output.getWidth(); j++) {
                // get the pixel data
                output.getRaster().getPixel(j, i, pixel);
                // converts its data to hsb to change brightness
                Color.RGBtoHSB(pixel[0], pixel[1], pixel[2], hsbvals);
                // create a new color with the changed brightness
                Color c = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2] * brightnessPercentage));
                output.getRaster().setPixel(j, i, new int[] { c.getRed(), c.getGreen(), c.getBlue(), pixel[3] });
            }
        }
        return output;
    }

    private static final Icon createIcon(String path, Dimension d) {
        try {
            BufferedImage original = ImageIO.read(IconVault.class.getResource(path));
            BufferedImage thumbnail = Thumbnails.of(original).size(d.width, d.height).keepAspectRatio(true)
                    .asBufferedImage();
            return new ImageIcon(thumbnail);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final Icon createDarkerIcon(String path, Dimension d, float brightnessRatio) {
        try {
            BufferedImage original = ImageIO.read(IconVault.class.getResource(path));
            BufferedImage thumbnail = Thumbnails.of(AdjustBrightness(original, brightnessRatio)).size(d.width, d.height)
                    .keepAspectRatio(true).asBufferedImage();
            return new ImageIcon(thumbnail);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
