package search;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.Model;
import model.data.ModelData;
import model.search.SearchModule;

abstract class BaseTest {

    protected static final Model model = initModelForTest();
    protected static final ModelData modelData = model.getModelData();
    protected static final SearchModule searchModule = model.getSearchModule();

    private static Model initModelForTest() {
        Model model = new Model();
        try {
            URL url = BaseTest.class.getResource("/dumps/dump-test.json");
            if (url == null) {
                System.err.println("The dump file for testing is not found!");
                return null;
            }
            model.getModelData().readFromDumpFile(new File(url.toURI()));
            System.out.println("The dump file for testing is loaded successfully!");
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return model;
    }
    
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        modelData.clearSearchHistories();
    }

    @Test
    @DisplayName("Test if any GAP operation, method, filter was recognised and stored from loading.")
    public void initTest() {
        assertNotNull(modelData.getAllOperationsSortedInList());
        assertNotNull(modelData.getAllMethodsSortedInList());
        assertNotNull(modelData.getAllFiltersSortedInList());
        assertFalse(modelData.getAllOperationsSortedInList().isEmpty());
        assertFalse(modelData.getAllMethodsSortedInList().isEmpty());
        assertFalse(modelData.getAllFiltersSortedInList().isEmpty());

        assertTrue(searchModule.getNonDuplicateSearchHistoryInList(SearchModule.STATE_SEARCH_OPERATION).isEmpty());
        assertTrue(searchModule.getNonDuplicateSearchHistoryInList(SearchModule.STATE_SEARCH_METHOD).isEmpty());
        assertTrue(searchModule.getNonDuplicateSearchHistoryInList(SearchModule.STATE_SEARCH_FILTER).isEmpty());
    }

}
