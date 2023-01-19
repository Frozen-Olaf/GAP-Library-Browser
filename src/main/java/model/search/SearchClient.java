package model.search;

import java.util.List;

import model.data.Method;

public interface SearchClient {

    public static final int STATE_IDLE = 0;
    public static final int STATE_SEARCH_OPERATION = 1;
    public static final int STATE_SEARCH_FILTER = 2;
    public static final int STATE_SEARCH_METHOD = 3;

    public List<String> getNonDuplicateSearchHisotryInList(int modelState);

    public boolean addSearchHistory(int modelState, String toSearch);

    public String hasEquivalentSearchHistory(int modelState, String toSearch);

    public List<Method> searchOpt(String toSearch);

    public List<Method> searchFilter(String toSearch);

    public List<Method> searchMethodByNameAndFilters(String toSearch);

}
