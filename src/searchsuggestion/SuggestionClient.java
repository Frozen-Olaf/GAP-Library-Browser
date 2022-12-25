package searchsuggestion;

import java.awt.Point;
import java.util.List;

import javax.swing.JComponent;

public interface SuggestionClient<C extends JComponent> {

	Point getPopupLocation(C invoker);

	void setSelectedText(C invoker, String selectedValue);

	List<String> getSuggestions(C invoker);

}
