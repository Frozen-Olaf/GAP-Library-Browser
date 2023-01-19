package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import model.data.ModelData;
import model.search.ModelSearchClient;
import model.search.SearchClient;

public class Model {

    private final PropertyChangeSupport notifier;
    private final ModelData modelData;
    private final SearchClient searchClient;

    private int searchState = SearchClient.STATE_IDLE;

    public Model() {
        notifier = new PropertyChangeSupport(this);
        modelData = new ModelData(notifier);
        searchClient = new ModelSearchClient(modelData, notifier);
    }

    /**
     * Utility method to permit an observer to add themselves as an observer to the
     * model's change support object.
     * 
     * @param listener the listener to add
     */
    public void addObserver(PropertyChangeListener listener) {
        notifier.addPropertyChangeListener("illf", listener);
        notifier.addPropertyChangeListener("404", listener);
        notifier.addPropertyChangeListener("rtdir", listener);
        notifier.addPropertyChangeListener("newrtdir", listener);
        notifier.addPropertyChangeListener("illflt", listener);
        notifier.addPropertyChangeListener("success", listener);
    }

    public ModelData getModelData() {
        return modelData;
    }

    public SearchClient getSearchClient() {
        return searchClient;
    }

    public int getSearchState() {
        return searchState;
    }

    public void setSearchState(int num) {
        searchState = num;
    }

}
