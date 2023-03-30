package delegate.ui.searchsuggestion;

import java.awt.Point;
import java.util.List;

import javax.swing.JComponent;

public interface SuggestionClient<C extends JComponent> {

    public Point getPopupLocation(C invoker);

    public void setSelectedText(C invoker, String selectedValue);

    public List<SuggestionEntry> getSuggestions(C invoker, boolean isInMethodArgumentInputMode, boolean isTextRemoved);

}
