package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import model.data.ModelDataValidator;
import model.data.ModelData;
import model.search.ModelSearchModule;
import model.search.SearchModule;

public class Model {

    private final PropertyChangeSupport notifier;
    private final ModelData modelData;
    private final ModelDataValidator modelDataValidator;
    private final SearchModule searchModule;

    private int searchState = SearchModule.STATE_IDLE;

    public Model() {
        notifier = new PropertyChangeSupport(this);
        modelData = new ModelData(notifier);
        modelDataValidator = new ModelDataValidator(modelData, notifier);
        searchModule = new ModelSearchModule(modelData, notifier);
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
        notifier.addPropertyChangeListener("save", listener);
        notifier.addPropertyChangeListener("valid", listener);
        notifier.addPropertyChangeListener("currinvld", listener);
        notifier.addPropertyChangeListener("fileinvld", listener);
        notifier.addPropertyChangeListener("empty", listener);
    }

    public ModelData getModelData() {
        return modelData;
    }

    public SearchModule getSearchModule() {
        return searchModule;
    }

    public int getSearchState() {
        return searchState;
    }

    public void setSearchState(int num) {
        searchState = num;
    }
    
    public void validateCurrentLoadedData() {
        modelDataValidator.validateCurrentLoadedData();
    }
    
    public void validateDumpFile(File file) throws IOException {
        modelDataValidator.validateDumpFile(file);
    }

}
