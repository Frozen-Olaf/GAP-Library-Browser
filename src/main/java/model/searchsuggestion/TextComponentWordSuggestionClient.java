package model.searchsuggestion;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

import model.Model;
import model.search.SearchClient;

public class TextComponentWordSuggestionClient implements SuggestionClient<JTextComponent> {

    private final Model model;
    private final QuadFunction<String, Boolean, Boolean, List<SuggestionEntry>, List<SuggestionEntry>> suggestionProvider;

    private List<SuggestionEntry> previousMethodList = new ArrayList<SuggestionEntry>();
    private int previousCaretPosition = 0;
    private int earlierPreviousWordIndex = 0;
    private int previousSearchState = SearchClient.STATE_IDLE;

    public TextComponentWordSuggestionClient(Model model,
            QuadFunction<String, Boolean, Boolean, List<SuggestionEntry>, List<SuggestionEntry>> suggestionProvider) {
        this.model = model;
        this.suggestionProvider = suggestionProvider;
    }

    @Override
    public Point getPopupLocation(JTextComponent invoker) {
        int caretPosition = invoker.getCaretPosition();
        try {
            Rectangle2D rectangle2D = invoker.modelToView2D(caretPosition);
            return new Point((int) rectangle2D.getX(), (int) (rectangle2D.getY() + rectangle2D.getHeight()));
        } catch (BadLocationException e) {
        }
        return null;
    }

    @Override
    public void setSelectedText(JTextComponent tp, String selectedValue) {
        int cp = tp.getCaretPosition();
        try {
            if (cp == 0 || tp.getText(cp - 1, 1).isBlank()) {
                tp.getDocument().insertString(cp, selectedValue, null);
            } else {
                int specialLen = detectLengthOfTextContainingSpecialCharacter(tp, cp);
                int previousWordIndex = (specialLen > 1) ? cp - specialLen : Utilities.getPreviousWord(tp, cp);

                String text = tp.getText(previousWordIndex, cp - previousWordIndex);
                if (selectedValue.startsWith(text)) {
                    String remaining = selectedValue.substring(text.length());
                    if (tp.getText().length() - cp >= remaining.length()
                            && remaining.equals(tp.getText(cp, remaining.length()))) {
                        // if the caret is not at the end of input string,
                        // and the remaining of the selected suggestion text is the same as the
                        // corresponding part of the input text,
                        // then just set the caret in the right position, instead of having duplicate
                        // contents in input text.
                        tp.setCaretPosition(cp + remaining.length());
                    } else
                        tp.getDocument().insertString(cp, selectedValue.substring(text.length()), null);

                } else {
                    tp.getDocument().insertString(cp, selectedValue, null);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SuggestionEntry> getSuggestions(JTextComponent tp, boolean isInMethodArgumentInputMode,
            boolean isTextRemoved) {
        try {
            int cp = tp.getCaretPosition();
            if (cp > 0) {
                String text = tp.getText(cp - 1, 1);
                if (text.equals(",") || text.isBlank()) {
                    return null;
                }
                int specialLen = detectLengthOfTextContainingSpecialCharacter(tp, cp);
                int previousWordIndex = (specialLen > 1) ? cp - specialLen : Utilities.getPreviousWord(tp, cp);
                text = tp.getText(previousWordIndex, cp - previousWordIndex);
                int cpDiff = cp - previousCaretPosition;
                previousCaretPosition = cp;
                if (cpDiff <= 0 || earlierPreviousWordIndex != previousWordIndex) {
                    // cpDiff < 0 : user is deleting some content of the input
                    // cpDiff == 0 : user switches to a new search state while the search input is
                    // empty, and then types the first character in the search bar after this
                    // switch.
                    // earlierPreviousWordIndex != previousWordIndex : user selects a search
                    // suggestion to enter a word, and then types a comma to separate from words
                    // coming after.
                    previousMethodList.clear();
                }
                int currentSearchState = model.getSearchState();
                boolean hasSearchStateChanged = !(currentSearchState == previousSearchState);
                List<SuggestionEntry> resultList = suggestionProvider.apply(text, isInMethodArgumentInputMode,
                        hasSearchStateChanged, previousMethodList);
                previousMethodList = resultList;
                earlierPreviousWordIndex = previousWordIndex;
                previousSearchState = currentSearchState;
                return resultList;
            } else {
                // do not show search histories for method argument filter input bars when they
                // are empty.
                if (isInMethodArgumentInputMode)
                    return null;
                if (tp.getText().isEmpty()) {
                    // do not show search histories when the input is deleted to empty.
                    if (isTextRemoved)
                        return null;
                    // display search histories when search bar is focused and has empty input;
                    List<String> searchHistories = model.getSearchClient()
                            .getNonDuplicateSearchHisotryInList(model.getSearchState());
                    List<SuggestionEntry> suggestions = new ArrayList<SuggestionEntry>();
                    ListIterator<String> iterator = searchHistories.listIterator(searchHistories.size());
                    while (iterator.hasPrevious()) {
                        String sh = iterator.previous();
                        suggestions.add(new SuggestionEntry(true, sh));
                    }
                    return suggestions;
                }
                return null;
            }
        } catch (BadLocationException e) {
            return null;
        }
    }

    private int detectLengthOfTextContainingSpecialCharacter(JTextComponent tp, int cp) throws BadLocationException {
        if (cp < 2)
            return cp;
        if (Character.isLetterOrDigit(tp.getText(cp - 1, 1).charAt(0)))
            return 0;
        int len = 0;
        int t;
        while ((t = cp - len) > 0 && (cp - Utilities.getPreviousWord(tp, t)) == len + 1) {
            len++;
            // check if the character right before is a comma.
            // ignore this check when at the very beginning of input.
            // System.out.println(tp.getText(t - 2, 1) +", length: "+ tp.getText(t - 2,
            // 1).length());
            if (t > 1 && tp.getText(t - 2, 1).equals(","))
                break;
        }
        // This is to detect whether what comes right before the special character
        // sequence is a character or digit sequence.
        // If yes then consider the whole sequence as a single word.
        if (t > 0 && Character.isLetterOrDigit(tp.getText(t - 1, 1).charAt(0))) {
            return len + t - Utilities.getPreviousWord(tp, t);
        }
        return len;
    }
}
