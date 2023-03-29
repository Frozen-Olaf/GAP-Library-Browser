package search;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.data.Method;

@DisplayName("JUnit 5 Test")
class SearchOperationTest extends BaseTest {

    @Test
    @DisplayName("Test if all GAP operations are loaded successfully from the dumped file.")
    public void CoverAllOperationsTest() throws IOException {
        URL testUrl = SearchOperationTest.class.getResource("/dumps/operations.json");
        if (testUrl == null) {
            System.out.println("The dump operations.json file for testing is not found!");
            return;
        }
        URI uri = null;
        try {
            uri = testUrl.toURI();
        } catch (URISyntaxException e) {
            fail("");
        }
        BufferedReader br = new BufferedReader(new FileReader(new File(uri)));
        String line = br.readLine();
        br.close();
        if (line != null) {
            JSONArray operations = new JSONArray(line);
            List<String> optns = Arrays.stream(model.getModelData().JSONArrayToStringArray(operations))
                    .collect(Collectors.toList());
            assertTrue(modelData.getAllOperationsSortedInList().containsAll(optns));
        }
    }
    
    @Test
    @DisplayName("Test if searching some fundamental operations returns the expected results.")
    public void searchFundementalInstanceTest() {
        
        List<Method> res1 = searchModule.searchOpt("Order");
        assertFalse(res1.isEmpty());
        res1.stream().map(Method::getName).forEach(s -> assertTrue(s.startsWith("Order")));

        List<Method> res2 = searchModule.searchOpt("Size");
        assertFalse(res2.isEmpty());
        res2.stream().map(Method::getName).forEach(s -> assertTrue(s.startsWith("Size")));
    }

    @Test
    @DisplayName("Test if the leading and trailing whitespaces in input are ignored for searching.")
    public void searchWithLeadingAndTrailingSpacesTest() {
        List<Method> standardResult = searchModule.searchOpt("Order");
        assertFalse(standardResult.isEmpty());

        List<Method> testResult1 = searchModule.searchOpt("  Order");
        assertFalse(testResult1.isEmpty());
        List<Method> testResult2 = searchModule.searchOpt("Order  ");
        assertFalse(testResult2.isEmpty());
        List<Method> testResult3 = searchModule.searchOpt(" Order  ");
        assertFalse(testResult3.isEmpty());

        assertIterableEquals(standardResult, testResult1);
        assertIterableEquals(standardResult, testResult2);
        assertIterableEquals(standardResult, testResult3);
    }
    
    @Test
    @DisplayName("Test if searching with null input returns null.")
    public void searchWithNullInputTest() {
        assertNull(searchModule.searchOpt(null));
    }
    
    @Test
    @DisplayName("Test if searching with empty input returns null.")
    public void searchWithEmptyInputTest() {
        assertNull(searchModule.searchOpt(""));
    }

    @Test
    @DisplayName("Test if searching nonexistent operation returns null.")
    public void searchNonExistentOptTest() {
        assertNull(searchModule.searchOpt("OrderX,"));
        assertNull(searchModule.searchOpt("Oorder"));
        assertNull(searchModule.searchOpt("Orderr"));
        assertNull(searchModule.searchOpt("PermutCycleOp"));
    }

    @Test
    @DisplayName("Test if searching returns null when input is in unexpected letter case or compromised by special symbols.")
    public void searchWithIllegalInputTest() {
        assertNull(searchModule.searchOpt("order"));
        assertNull(searchModule.searchOpt("oRDer"));
        assertNull(searchModule.searchOpt("ordersTom"));
        assertNull(searchModule.searchOpt("permutationOp"));

        assertNull(searchModule.searchOpt("Order,"));
        assertNull(searchModule.searchOpt(",Order!"));
        assertNull(searchModule.searchOpt("Order;"));
        assertNull(searchModule.searchOpt("^Order*&"));
        assertNull(searchModule.searchOpt("Or der"));
        assertNull(searchModule.searchOpt("Permutation Op"));
    }

    @Test
    @DisplayName("Test if searching operations with name containing punctuation marks works as expected.")
    public void searchOptContainingPunctuationMarksTest() {
        List<Method> testResult1 = searchModule.searchOpt(".");
        assertFalse(testResult1.isEmpty());
        List<Method> testResult2 = searchModule.searchOpt(".:=");
        assertFalse(testResult2.isEmpty());

        List<Method> testResult3 = searchModule.searchOpt("[]");
        assertFalse(testResult3.isEmpty());
        List<Method> testResult4 = searchModule.searchOpt("[]:=");
        assertFalse(testResult4.isEmpty());

        List<Method> testResult5 = searchModule.searchOpt("{}");
        assertFalse(testResult5.isEmpty());
        List<Method> testResult6 = searchModule.searchOpt("{}:=");
        assertFalse(testResult6.isEmpty());

        List<Method> testResult7 = searchModule.searchOpt("^");
        assertFalse(testResult7.isEmpty());
        List<Method> testResult8 = searchModule.searchOpt("<");
        assertFalse(testResult8.isEmpty());

        List<Method> testResult9 = searchModule.searchOpt("/");
        assertFalse(testResult9.isEmpty());
        List<Method> testResult10 = searchModule.searchOpt("*");
        assertFalse(testResult10.isEmpty());
        List<Method> testResult11 = searchModule.searchOpt("+");
        assertFalse(testResult11.isEmpty());
        List<Method> testResult12 = searchModule.searchOpt("-");
        assertFalse(testResult12.isEmpty());
        List<Method> testResult13 = searchModule.searchOpt("=");
        assertFalse(testResult13.isEmpty());

        List<Method> testResult14 = searchModule.searchOpt("_GapToJsonStreamInternal");
        assertFalse(testResult14.isEmpty());
    }

}
