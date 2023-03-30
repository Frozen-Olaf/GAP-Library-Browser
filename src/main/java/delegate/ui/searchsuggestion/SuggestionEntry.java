package delegate.ui.searchsuggestion;

import java.util.Objects;

public class SuggestionEntry {

    private boolean isSearchHistory;
    private String text;

    public SuggestionEntry(boolean isSearchHistory, String text) {
        this.isSearchHistory = isSearchHistory;
        this.text = text;
    }

    public boolean getIsSearchHistory() {
        return isSearchHistory;
    }

    public String getText() {
        return text;
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj != null && obj.getClass() == getClass()) {
            SuggestionEntry se = (SuggestionEntry) obj;
            return text.equals(se.getText());
        }
        return false;
    }

}
