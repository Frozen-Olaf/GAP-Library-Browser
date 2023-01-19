package search;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.Model;
import model.data.ModelData;
import model.search.SearchClient;

abstract class BaseTest {

    protected static final Model model = initModelForTest();
    protected static final ModelData modelData = model.getModelData();
    protected static final SearchClient searchClient = model.getSearchClient();

    private static Model initModelForTest() {
        Model model = new Model();
        try {
            URL url = BaseTest.class.getResource("/dumps/dump-test.json");
            if (url == null) {
                System.err.println("The dumped json file for testing is not found!");
                return null;
            }
            model.getModelData().readFromJson(new File(url.toURI()));
            System.out.println("The json file for testing is loaded successfully!");
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return model;
    }

    @Test
    @DisplayName("Test if any GAP operation, method, filter was recognised and stored from loading.")
    public void initTest() {
        assertNotNull(modelData.getAllOperationsSortedInList());
        assertNotNull(modelData.getAllMethodsSortedInList());
        assertNotNull(modelData.getAllFiltersSortedInList());
        assertTrue(!modelData.getAllOperationsSortedInList().isEmpty());
        assertTrue(!modelData.getAllMethodsSortedInList().isEmpty());
        assertTrue(!modelData.getAllFiltersSortedInList().isEmpty());

        assertTrue(searchClient.getNonDuplicateSearchHisotryInList(SearchClient.STATE_SEARCH_OPERATION).isEmpty());
        assertTrue(searchClient.getNonDuplicateSearchHisotryInList(SearchClient.STATE_SEARCH_METHOD).isEmpty());
        assertTrue(searchClient.getNonDuplicateSearchHisotryInList(SearchClient.STATE_SEARCH_FILTER).isEmpty());
    }

}
