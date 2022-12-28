package searchsuggestion;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javax.swing.text.*;


public class TextComponentWordSuggestionClient implements SuggestionClient<JTextComponent> {

	private BiFunction<String, List<String>, List<String>> suggestionProvider;
	private List<String> previousMethodList = new ArrayList<String>();
	private int previousCaretPosition = 0;
	private int earlierPreviousWordIndex = 0;

	public TextComponentWordSuggestionClient(BiFunction<String, List<String>, List<String>> suggestionProvider) {
		this.suggestionProvider = suggestionProvider;
	}

	@Override
	public Point getPopupLocation(JTextComponent invoker) {
		int caretPosition = invoker.getCaretPosition();
		try {
			Rectangle2D rectangle2D = invoker.modelToView2D(caretPosition);
			return new Point((int) rectangle2D.getX(), (int) (rectangle2D.getY() + rectangle2D.getHeight()));
		} catch (BadLocationException e) {}
		return null;
	}

	@Override
	public void setSelectedText(JTextComponent tp, String selectedValue) {
		int cp = tp.getCaretPosition();
		try {
			if (cp == 0 || tp.getText(cp - 1, 1).trim().isEmpty()) {
				tp.getDocument().insertString(cp, selectedValue, null);
			} else {
				int previousWordIndex = Utilities.getPreviousWord(tp, cp);
				String text = tp.getText(previousWordIndex, cp - previousWordIndex);
				if (selectedValue.startsWith(text)) {
					tp.getDocument().insertString(cp, selectedValue.substring(text.length()), null);
				} else {
					tp.getDocument().insertString(cp, selectedValue, null);
				}
			}
		} catch (BadLocationException e) {}
	}

	@Override
	public List<String> getSuggestions(JTextComponent tp) {
		try {
			int cp = tp.getCaretPosition();
			if (cp != 0) {
				String text = tp.getText(cp-1, 1);
				if (text.equals(",") || text.trim().isEmpty()) {
					return null;
				}
				if (cp > 1) {
					text = tp.getText(cp-2, 2);
					if (text.equals("..")) {
						return null;
					}
					//Handles the special case of operation '.:='
//					else if (text.equals(".:")) {
//						return suggestionProvider.apply(text);
//					}
				}
//				if (cp>2) {
//					if (text.equals(".:=")) {
//						return suggestionProvider.apply(text);
//					}
//				}
			}
			int previousWordIndex = Utilities.getPreviousWord(tp, cp);
			String text = tp.getText(previousWordIndex, cp - previousWordIndex);
			
			int cpDiff = cp - previousCaretPosition;
			previousCaretPosition = cp;
            if (cpDiff <= 0 || earlierPreviousWordIndex != previousWordIndex) {
                // User is deleting some input, i.e., the caret position is decreasing.
                previousMethodList.clear();
            }
            List<String> resultList = suggestionProvider.apply(text.trim(), previousMethodList);
            previousMethodList = resultList;
            earlierPreviousWordIndex = previousWordIndex;
            return resultList;
			
		} catch (BadLocationException e) {}
		return null;
	}
}
