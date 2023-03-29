package search;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//import org.json.JSONArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.data.Method;

@DisplayName("JUnit 5 Test")
class SearchFilterTest extends BaseTest {
    
    @Test
    @DisplayName("Test if searching sets of fundamental filters returns the expected results.")
    public void searchFundementalInstanceTest() {
        
        String input1 = "IsObject, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne";
        Set<String> set1 = Arrays.stream(input1.split(",")).map(String::trim).collect(Collectors.toSet());  
        List<Method> res1 = searchModule.searchFilter(input1);
        assertFalse(res1.isEmpty());
        res1.stream().map(m -> Method.getUniqueFilters(m.getArgFilters())).forEach(s -> assertTrue(s.containsAll(set1)));

        String input2 = "IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, IsObject";
        Set<String> set2 = Arrays.stream(input2.split(",")).map(String::trim).collect(Collectors.toSet());  
        List<Method> res2 = searchModule.searchFilter(input2);
        assertFalse(res2.isEmpty());
        res2.stream().map(m -> Method.getUniqueFilters(m.getArgFilters())).forEach(s -> assertTrue(s.containsAll(set2)));
    }

    @Test
    @DisplayName("Test if searching with or without the fundemental filter 'IsObject' in input returns the same result.")
    public void searchWithAndWithoutIsObjectFilterTest() {
        
        List<Method> with1 = searchModule.searchFilter(
                "IsObject, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne");
        assertFalse(with1.isEmpty());
        List<Method> without1 = searchModule
                .searchFilter("IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne");
        assertFalse(without1.isEmpty());
        assertIterableEquals(with1, without1);

        List<Method> with2 = searchModule.searchFilter(
                "IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, IsObject");
        assertFalse(with2.isEmpty());
        List<Method> without2 = searchModule.searchFilter(
                "IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection");
        assertFalse(without2.isEmpty());
        assertIterableEquals(with2, without2);
    }

    @Test
    @DisplayName("Test if searching with subset symbol returns more methods than without.")
    public void searchWithSubsetSymbolTest() {
        
        String inputWithout1 = "IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne";
        List<Method> without1 = searchModule.searchFilter(inputWithout1);
        assertFalse(without1.isEmpty());
        String inputWith1 = inputWithout1 + ", ...";
        List<Method> with1 = searchModule.searchFilter(inputWith1);
        assertFalse(with1.isEmpty());

        assertTrue(with1.size() >= without1.size());
        Set<String> inputFilterSet1 = Arrays.stream(inputWithout1.split(",")).map(String::trim)
                .collect(Collectors.toSet());
        with1.stream().filter(m -> !without1.contains(m)).map(m -> Method.getUniqueFilters(m.getArgFilters()))
                .forEach(f -> assertTrue(f.containsAll(inputFilterSet1)));

        String inputWithout2 = "IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection";
        List<Method> without2 = searchModule.searchFilter(inputWithout2);
        assertFalse(without2.isEmpty());
        String inputWith2 = inputWithout2 + ", ...";
        List<Method> with2 = searchModule.searchFilter(inputWith2);
        assertFalse(with2.isEmpty());

        assertTrue(with2.size() >= without2.size());
        Set<String> inputFilterSet2 = Arrays.stream(inputWithout2.split(",")).map(String::trim)
                .collect(Collectors.toSet());
        with2.stream().filter(m -> !without2.contains(m)).map(m -> Method.getUniqueFilters(m.getArgFilters()))
                .forEach(f -> assertTrue(f.containsAll(inputFilterSet2)));
    }

    @Test
    @DisplayName("Test if searching fewer filters with subset symbol returns at least the same number of methods as searching some more filters additionally with subset symbol.")
    public void searchFewerFiltersInSubsetAndMoreFiltersInSubsetComparisonTest() {
        
        String input1 = "IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...";
        String input2 = "IsExtLElement, IsExtRElement, IsMultiplicativeElement, ...";
        List<Method> res1 = searchModule.searchFilter(input1);
        assertFalse(res1.isEmpty());
        List<Method> res2 = searchModule.searchFilter(input2);
        assertFalse(res2.isEmpty());

        assertTrue(res2.size() >= res1.size());

        String input3 = "IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, ...";
        String input4 = "IsList, IsDenseList, IsCollection, ...";

        List<Method> res3 = searchModule.searchFilter(input3);
        List<Method> res4 = searchModule.searchFilter(input4);
        assertFalse(res3.isEmpty());
        assertFalse(res4.isEmpty());

        assertTrue(res4.size() >= res3.size());
    }

    @Test
    @DisplayName("Test if searching with a subset symbol at different postions in input returns the same result.")
    public void searchWithOneSubsetSymbolAtDifferentLocationTest() {
        
        String input1 = "IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...";
        List<Method> res1 = searchModule.searchFilter(input1);
        assertFalse(res1.isEmpty());

        String input2 = "..., IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne";
        List<Method> res2 = searchModule.searchFilter(input2);
        assertFalse(res2.isEmpty());

        String input3 = "IsExtLElement, IsExtRElement, IsMultiplicativeElement, ..., IsMultiplicativeElementWithOne";
        List<Method> res3 = searchModule.searchFilter(input3);
        assertFalse(res3.isEmpty());
        assertIterableEquals(res1, res2);
        assertIterableEquals(res1, res3);

        String input4 = "IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, ...";
        List<Method> res4 = searchModule.searchFilter(input4);
        assertFalse(res4.isEmpty());

        String input5 = "..., IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection";
        List<Method> res5 = searchModule.searchFilter(input5);
        assertFalse(res5.isEmpty());

        String input6 = "IsList, IsDenseList, IsHomogeneousList, ..., IsListOrCollection, IsCollection";
        List<Method> res6 = searchModule.searchFilter(input6);
        assertFalse(res6.isEmpty());
        assertIterableEquals(res4, res5);
        assertIterableEquals(res4, res6);
    }

    @Test
    @DisplayName("Test if searching with multiple subset symbols returns the same result.")
    public void searchWithMultipleSubsetSymbolsTest() {
        
        String input1 = "IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...";
        List<Method> res1 = searchModule.searchFilter(input1);
        assertFalse(res1.isEmpty());
        String input2 = "..., IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...";
        List<Method> res2 = searchModule.searchFilter(input2);
        assertFalse(res2.isEmpty());
        String input3 = "IsExtLElement, ..., IsExtRElement, IsMultiplicativeElement ..., IsMultiplicativeElementWithOne";
        List<Method> res3 = searchModule.searchFilter(input3);
        assertFalse(res3.isEmpty());

        assertIterableEquals(res1, res2);
        assertIterableEquals(res1, res3);

        String input4 = "IsList, IsDenseList, ..., IsHomogeneousList, IsListOrCollection, IsCollection, ...";
        List<Method> res4 = searchModule.searchFilter(input4);
        assertFalse(res4.isEmpty());
        String input5 = "..., IsList, IsDenseList, ..., IsHomogeneousList, IsListOrCollection, ..., IsCollection";
        List<Method> res5 = searchModule.searchFilter(input5);
        assertFalse(res5.isEmpty());
        String input6 = "IsList, IsDenseList, ..., IsHomogeneousList, ..., IsListOrCollection, IsCollection, ...";
        List<Method> res6 = searchModule.searchFilter(input6);
        assertFalse(res6.isEmpty());

        assertIterableEquals(res4, res5);
        assertIterableEquals(res4, res6);
    }

    @Test
    @DisplayName("Test if searching illegal input with a subset symbol returns the same result as searching with only a subset symbol.")
    public void searchIllegalFiltersWithSubsetSymbolTest() {
        
        String input1 = "...";
        List<Method> res1 = searchModule.searchFilter(input1);
        assertFalse(res1.isEmpty());

        String input2 = "isExtLElement, isExtrelement, IsultiplicativeElement, IsmultiplicativeelementwithOne, ...";
        List<Method> res2 = searchModule.searchFilter(input2);
        assertFalse(res2.isEmpty());

        String input3 = "isExtlelement, IsExtWESlement, nothingElement, isMultiplicativeElementWithone, ...";
        List<Method> res3 = searchModule.searchFilter(input3);
        assertFalse(res3.isEmpty());

        assertIterableEquals(res1, res2);
        assertIterableEquals(res1, res3);
    }

    @Test
    @DisplayName("Test if the leading and trailing whitespaces in input are ignored for searching.")
    public void searchWithLeadingAndTrailingSpacesTest() {
        
        List<Method> standardRes = searchModule.searchFilter(
                "IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsObject");
        assertFalse(standardRes.isEmpty());

        List<Method> testRes1 = searchModule.searchFilter(
                "IsExtLElement,  IsExtRElement,  IsMultiplicativeElement,  IsMultiplicativeElementWithOne,  IsObject");
        assertFalse(testRes1.isEmpty());
        List<Method> testResult2 = searchModule.searchFilter(
                "  IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsObject  ");
        assertFalse(testResult2.isEmpty());
        List<Method> testResult3 = searchModule.searchFilter(
                " IsExtLElement,IsExtRElement,IsMultiplicativeElement,IsMultiplicativeElementWithOne,    IsObject   ");
        assertFalse(testResult3.isEmpty());

        assertIterableEquals(standardRes, testRes1);
        assertIterableEquals(standardRes, testResult2);
        assertIterableEquals(standardRes, testResult3);
    }

    @Test
    @DisplayName("Test if searching equivalent inputs gives the same result.")
    /**
     * Equivalent inputs are the inputs that contain exactly the same filters but in
     * different orders.
     */
    public void searchEquivalentInputsTest() {
        
        String input1 = "IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsZDFRE, IsCommutativeElement, IsFloat";
        String eqvl1 = "IsNearAdditiveElementWithZero, IsNearAdditiveElement, IsNearAdditiveElementWithInverse, IsExtAElement, IsExtLElement, IsExtRElement, IsAdditiveElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsMultiplicativeElement, IsCommutativeElement, IsFloat, IsZDFRE";
        List<Method> inputRes1 = searchModule.searchFilter(input1);
        assertNotNull(inputRes1);
        List<Method> eqvlRes1 = searchModule.searchFilter(eqvl1);
        assertNotNull(eqvlRes1);
        assertIterableEquals(inputRes1, eqvlRes1);

        String input2 = "IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...";
        String eqvl2 = "IsMultiplicativeElementWithOne, IsExtLElement, IsMultiplicativeElement, IsExtRElement, ...";
        List<Method> inputRes2 = searchModule.searchFilter(input2);
        assertNotNull(inputRes2);
        List<Method> eqvlRes2 = searchModule.searchFilter(eqvl2);
        assertNotNull(eqvlRes2);
        assertIterableEquals(inputRes2, eqvlRes2);

        String input3 = "IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, ...";
        String eqvl3 = "..., IsCollection, IsList, IsDenseList, IsListOrCollection, IsHomogeneousList";
        List<Method> inputRes3 = searchModule.searchFilter(input3);
        assertNotNull(inputRes3);
        List<Method> eqvlRes3 = searchModule.searchFilter(eqvl3);
        assertFalse(eqvlRes3.isEmpty());
        assertIterableEquals(inputRes3, eqvlRes3);
    }
    
    @Test
    @DisplayName("Test if searching with null input returns null.")
    public void searchWithNullInputTest() {
        assertNull(searchModule.searchFilter(null));
    }
    
    @Test
    @DisplayName("Test if searching with empty input returns null.")
    public void searchWithEmptyInputTest() {
        assertNull(searchModule.searchFilter(""));
    }

    @Test
    @DisplayName("Test if searching non-existent filters returns null.")
    public void searchNonExistentFilterTest() {
        assertNull(searchModule.searchFilter("IsNullFilter"));
        assertNull(searchModule.searchFilter("IsNullFilter1, IsNullFilter2, IsNullFilter3, IsNullFilter4"));
        assertNull(searchModule.searchFilter("FilterNotExists, NULL1, Null2, RandomNull3"));
    }

    @Test
    @DisplayName("Test if searching entirely illegal input returns null.")
    /**
     * input is in unexpected letter case subset symbol not in expected form
     */
    public void searchEntirelyIllegalInputTest() {
        
        String input1 = "IsExtLElement  IsExtRElement  IsMultiplicativeElement IsMultiplicativeElementWithOne";
        List<Method> res1 = searchModule.searchFilter(input1);
        assertNull(res1);

        String input2 = "IsExtLElement; IsExtRElement. IsMultiplicativeElement! IsMultiplicativeElementWithOne?";
        List<Method> res2 = searchModule.searchFilter(input2);
        assertNull(res2);

        String input3 = "IsExtLElement;, IsExtRElement., IsMultiplicativeElement!, IsMultiplicativeElementWithOne?,";
        List<Method> res3 = searchModule.searchFilter(input3);
        assertNull(res3);

        String input4 = "IsExtLElement, && IsExtRElement, ~% IsMultiplicativeElement, 123 IsMultiplicativeElementWithOne";
        List<Method> res4 = searchModule.searchFilter(input4);
        assertNull(res4);
    }

    @Test
    @DisplayName("Test if searching returns null when input partly includes nonexistent filters or partly is in illegal format")
    void searchInputIncludingPartlyNonExistentFiltersTest() {
        
        String standard1 = "IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne";
        List<Method> std1 = searchModule.searchFilter(standard1);
        assertFalse(std1.isEmpty());
        String input1 = "IsExtLElement, IsExtRElement,, IsMultiplicativeElement, ,IsMultiplicativeElementWithOne";
        List<Method> res1 = searchModule.searchFilter(input1);
        assertFalse(res1.isEmpty());
        String input2 = "IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, NULLFILTER, Nothing";
        List<Method> res2 = searchModule.searchFilter(input2);
        assertFalse(res2.isEmpty());
        String input3 = "IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ....";
        List<Method> res3 = searchModule.searchFilter(input3);
        assertFalse(res3.isEmpty());
        String input4 = "IsExtLElement, !@$!@%#$#%, IsExtRElement, IsMultiplicativeElement, %^&*^%&*, IsMultiplicativeElementWithOne, .....";
        List<Method> res4 = searchModule.searchFilter(input4);
        assertFalse(res4.isEmpty());

        assertIterableEquals(std1, res1);
        assertIterableEquals(std1, res2);
        assertIterableEquals(std1, res3);
        assertIterableEquals(std1, res4);

        String standard2 = "IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...";
        List<Method> std2 = searchModule.searchFilter(standard2);
        assertFalse(std2.isEmpty());
        assertTrue(std2.size() >= res3.size());
        assertTrue(std2.size() >= res4.size());

        String input5 = "~~()&(), IsExtLElement, IsExtRElement, !@#$#%, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...";
        List<Method> res5 = searchModule.searchFilter(input5);
        assertFalse(res5.isEmpty());

        assertIterableEquals(std2, res5);
    }

}
