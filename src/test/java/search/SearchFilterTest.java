package search;

import static org.junit.jupiter.api.Assertions.*;

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
//import model.data.ModelData;

@DisplayName("JUnit 5 Test")
class SearchFilterTest extends BaseTest {

    @Test
    @DisplayName("Something")
    void test() {
        String[] filters1 = { "IsListOrCollection", "IsCollection", "CanEasilyCompareElements",
                "HasCanEasilyCompareElements", "CanEasilySortElements", "HasCanEasilySortElements", "CanComputeSize",
                "IsDuplicateFree", "HasIsDuplicateFree", "IsExtLElement", "CategoryCollections(IsExtLElement)",
                "IsExtRElement", "CategoryCollections(IsExtRElement)", "CategoryCollections(IsMultiplicativeElement)",
                "CategoryCollections(IsMultiplicativeElementWithOne)",
                "CategoryCollections(IsMultiplicativeElementWithInverse)", "IsGeneralizedDomain", "IsMagma",
                "IsMagmaWithOne", "IsMagmaWithInversesIfNonzero", "IsMagmaWithInverses",
                "IsGeneratorsOfMagmaWithInverses", "HasIsGeneratorsOfMagmaWithInverses", "IsAssociative",
                "HasIsAssociative", "HasMultiplicativeNeutralElement", "IsGeneratorsOfSemigroup",
                "HasIsGeneratorsOfSemigroup", "IsSimpleSemigroup", "HasIsSimpleSemigroup", "IsRegularSemigroup",
                "HasIsRegularSemigroup", "IsInverseSemigroup", "HasIsInverseSemigroup", "IsCompletelyRegularSemigroup",
                "HasIsCompletelyRegularSemigroup", "IsGroupAsSemigroup", "HasIsGroupAsSemigroup", "IsMonoidAsSemigroup",
                "HasIsMonoidAsSemigroup", "IsOrthodoxSemigroup", "HasIsOrthodoxSemigroup", "CanComputeSizeAnySubgroup",
                "KnowsHowToDecompose", "HasKnowsHowToDecompose", "IsSolvableGroup", "HasIsSolvableGroup",
                "IsPolycyclicGroup", "HasIsPolycyclicGroup", "CategoryCollections(IsPcpElement)", "IsObject" };

        String[][] filters2 = {
                { "IsListOrCollection", "IsCollection", "CanComputeSize", "IsDuplicateFree", "HasIsDuplicateFree",
                        "IsExtLElement", "CategoryCollections(IsExtLElement)", "IsExtRElement",
                        "CategoryCollections(IsExtRElement)", "CategoryCollections(IsMultiplicativeElement)",
                        "CategoryCollections(IsMultiplicativeElementWithOne)",
                        "CategoryCollections(IsMultiplicativeElementWithInverse)", "IsGeneralizedDomain", "IsMagma",
                        "IsMagmaWithOne", "IsMagmaWithInversesIfNonzero", "IsMagmaWithInverses", "IsAssociative",
                        "HasIsAssociative", "HasMultiplicativeNeutralElement", "IsGeneratorsOfSemigroup",
                        "HasIsGeneratorsOfSemigroup", "IsSimpleSemigroup", "HasIsSimpleSemigroup", "IsRegularSemigroup",
                        "HasIsRegularSemigroup", "IsInverseSemigroup", "HasIsInverseSemigroup",
                        "IsCompletelyRegularSemigroup", "HasIsCompletelyRegularSemigroup", "IsGroupAsSemigroup",
                        "HasIsGroupAsSemigroup", "IsMonoidAsSemigroup", "HasIsMonoidAsSemigroup", "IsOrthodoxSemigroup",
                        "HasIsOrthodoxSemigroup", "CanEasilyTestMembership", "CanComputeSizeAnySubgroup",
                        "IsHandledByNiceMonomorphism", "HasIsHandledByNiceMonomorphism", "IsObject" },
                { "IsListOrCollection", "IsCollection", "CanComputeSize", "IsDuplicateFree", "HasIsDuplicateFree",
                        "IsExtLElement", "CategoryCollections(IsExtLElement)", "IsExtRElement",
                        "CategoryCollections(IsExtRElement)", "CategoryCollections(IsMultiplicativeElement)",
                        "CategoryCollections(IsMultiplicativeElementWithOne)",
                        "CategoryCollections(IsMultiplicativeElementWithInverse)", "IsGeneralizedDomain", "IsMagma",
                        "IsMagmaWithOne", "IsMagmaWithInversesIfNonzero", "IsMagmaWithInverses", "IsAssociative",
                        "HasIsAssociative", "HasMultiplicativeNeutralElement", "IsGeneratorsOfSemigroup",
                        "HasIsGeneratorsOfSemigroup", "IsSimpleSemigroup", "HasIsSimpleSemigroup", "IsRegularSemigroup",
                        "HasIsRegularSemigroup", "IsInverseSemigroup", "HasIsInverseSemigroup",
                        "IsCompletelyRegularSemigroup", "HasIsCompletelyRegularSemigroup", "IsGroupAsSemigroup",
                        "HasIsGroupAsSemigroup", "IsMonoidAsSemigroup", "HasIsMonoidAsSemigroup", "IsOrthodoxSemigroup",
                        "HasIsOrthodoxSemigroup", "CanEasilyTestMembership", "CanComputeSizeAnySubgroup",
                        "IsHandledByNiceMonomorphism", "HasIsHandledByNiceMonomorphism" } };

        Set<String> allFilters = modelData.getAllFiltersInSet();
        assertTrue(!allFilters.isEmpty());

        assertTrue(allFilters.containsAll(Arrays.asList(filters1)));
        assertTrue(allFilters.containsAll(Arrays.stream(filters2).flatMap(Arrays::stream).collect(Collectors.toSet())));
    }

    @Test
    @DisplayName("Test if searching nonexistent filters returns null.")
    public void searchNonExistentFilterTest() {
        assertTrue(searchClient.searchFilter("IsNullFilter") == null);
    }

    @Test
    @DisplayName("Test if equivalent filter inputs give the same result.")
    void EquivalentFilterInputTest() {
        String input = "IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsZDFRE, IsCommutativeElement, IsFloat, IsObject";
        String equivalentInput = "IsNearAdditiveElementWithZero, IsNearAdditiveElement, IsNearAdditiveElementWithInverse, IsExtAElement, IsExtLElement, IsExtRElement, IsAdditiveElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsMultiplicativeElement, IsCommutativeElement, IsFloat, IsObject, IsZDFRE";
        List<Method> testResult1 = searchClient.searchFilter(input);
        assertNotNull(testResult1);
        List<Method> testResult2 = searchClient.searchFilter(equivalentInput);
        assertNotNull(testResult2);
        assertTrue(testResult1.equals(testResult2));
    }

    @Test
    @DisplayName("Test if equivalent filter input (only order of filters )")
    void InputIncludingNonExistentFilterTest() {
        String input = "IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsZDFRE, IsCommutativeElement, IsFloat, IsObject";
        String equivalentInput = "IsNearAdditiveElementWithZero, IsNearAdditiveElement, IsNearAdditiveElementWithInverse, IsExtAElement, IsExtLElement, IsExtRElement, IsAdditiveElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsMultiplicativeElement, IsCommutativeElement, IsFloat, IsObject, IsZDFRE";
        List<Method> test1 = searchClient.searchFilter(input);
        assertNotNull(test1);
        List<Method> test2 = searchClient.searchFilter(equivalentInput);
        assertNotNull(test2);
        assertTrue(test1.equals(test2));
    }

//    @Test
//    @DisplayName("Test if all GAP filters are loaded successfully from the dumped file.")
//    public void CoverAllOperationsTest() throws IOException {
//        URL testUrl = SearchOperationTest.class.getResource("/dumps/filters.json");
//        if (testUrl == null) {
//            System.out.println("The dumped filters.json file for testing is not found!");
//            return;
//        }
//        URI uri = null;
//        try {
//            uri = testUrl.toURI();
//        } catch (URISyntaxException e) {
//            fail("");
//        }
//        BufferedReader br = new BufferedReader(new FileReader(new File(uri)));
//        String line = br.readLine();
//        br.close();
//        if (line != null) {
//            JSONArray filters = new JSONArray(line);
//            List<String> flts = Arrays.stream(ModelData.JSONArrayToStringArray(filters)).collect(Collectors.toList());
//            for (int i=0; i<flts.size(); i++) {
//                System.out.println(i + ", " + flts.get(i));
//                assertTrue(modelData.getAllFiltersSortedInList().contains(flts.get(i)));
//            }
//            //(model.getAllOperationsInList().containsAll(flts));
//        }
//    }

}
