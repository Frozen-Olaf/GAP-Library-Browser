package search;

import static org.junit.jupiter.api.Assertions.*;

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
import model.data.ModelData;

@DisplayName("JUnit 5 Test")
class SearchOperationTest extends BaseTest {

    @Test
    @DisplayName("Test if all GAP operations are loaded successfully from the dumped file.")
    public void CoverAllOperationsTest() throws IOException {
        URL testUrl = SearchOperationTest.class.getResource("/dumps/operations.json");
        if (testUrl == null) {
            System.out.println("The dumped operations.json file for testing is not found!");
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
            List<String> optns = Arrays.stream(ModelData.JSONArrayToStringArray(operations))
                    .collect(Collectors.toList());
//            for (int i=0; i<optns.size(); i++) {
//                System.out.println(i + ", " + optns.get(i));
//                assertTrue(model.getAllOperationsInList().contains(optns.get(i)));
//            }
            assertTrue(modelData.getAllOperationsSortedInList().containsAll(optns));
        }
    }

    @Test
    @DisplayName("Test if the methods under the operation Order from the dumped JSON file are returned from searchClient.")
    public void searchAttributeOrderTest() {
        Method m1 = new Method("Order: system getter",
                new String[][] { { "IsComponentObjectRep", "IsAttributeStoringRep", "HasOrder", "IsObject" } }, 20007,
                "./lib/arith.gd", 2047, 2047);
        Method m2 = new Method("Order: straight line program elements",
                new String[][] { { "IsPositionalObjectRep", "IsExtLElement", "IsExtRElement", "IsMultiplicativeElement",
                        "IsMultiplicativeElementWithOne", "IsMultiplicativeElementWithInverse",
                        "StraightLineProgramElmRankFilter", "IsStraightLineProgElm", "IsObject" } },
                117, "./lib/straight.gi", 1056, 1062);
        Method m3 = new Method("Order: object with memory",
                new String[][] { { "IsComponentObjectRep", "IsExtLElement", "IsExtRElement", "IsMultiplicativeElement",
                        "IsMultiplicativeElementWithOne", "IsMultiplicativeElementWithInverse",
                        "IsObjWithMemoryRankFilter", "IsObjWithMemory", "IsObject" } },
                107, "./lib/memory.gi", 350, 353);
        Method m4 = new Method("Order: ordinary matrix of finite field elements", new String[][] { { "IsCopyable",
                "IsList", "IsDenseList", "IsHomogeneousList", "IsTable", "IsListOrCollection", "IsCollection",
                "IsExtAElement", "CategoryCollections(IsExtAElement)",
                "CategoryCollections(CategoryCollections(IsExtAElement))", "IsNearAdditiveElement",
                "CategoryCollections(IsNearAdditiveElement)",
                "CategoryCollections(CategoryCollections(IsNearAdditiveElement))", "IsNearAdditiveElementWithZero",
                "CategoryCollections(IsNearAdditiveElementWithZero)",
                "CategoryCollections(CategoryCollections(IsNearAdditiveElementWithZero))",
                "IsNearAdditiveElementWithInverse", "CategoryCollections(IsNearAdditiveElementWithInverse)",
                "CategoryCollections(CategoryCollections(IsNearAdditiveElementWithInverse))", "IsAdditiveElement",
                "CategoryCollections(IsAdditiveElement)", "CategoryCollections(CategoryCollections(IsAdditiveElement))",
                "IsExtLElement", "CategoryCollections(IsExtLElement)",
                "CategoryCollections(CategoryCollections(IsExtLElement))", "IsExtRElement",
                "CategoryCollections(IsExtRElement)", "CategoryCollections(CategoryCollections(IsExtRElement))",
                "IsMultiplicativeElement", "CategoryCollections(CategoryCollections(IsMultiplicativeElement))",
                "IsMultiplicativeElementWithOne",
                "CategoryCollections(CategoryCollections(IsMultiplicativeElementWithOne))",
                "IsMultiplicativeElementWithInverse",
                "CategoryCollections(CategoryCollections(IsMultiplicativeElementWithInverse))",
                "IsGeneralizedRowVector", "IsMultiplicativeGeneralizedRowVector",
                "CategoryCollections(CategoryCollections(IsZDFRE))", "IsOrdinaryMatrix", "IsAssociativeElement",
                "CategoryCollections(CategoryCollections(IsAssociativeElement))", "IsAdditivelyCommutativeElement",
                "CategoryCollections(CategoryCollections(IsAdditivelyCommutativeElement))",
                "CategoryCollections(CategoryCollections(IsCommutativeElement))",
                "CategoryCollections(CategoryCollections(IsFFE))", "IsVecOrMatObj", "IsMatrixOrMatrixObj",
                "IsGeneratorsOfSemigroup", "HasIsGeneratorsOfSemigroup", "IsObject" } }, 48, "./lib/matrix.gi", 1131,
                1154);
        Method m5 = new Method("Order: for a matrix of cyclotomics, with Minkowski kernel", new String[][] { {
                "IsCopyable", "IsList", "IsDenseList", "IsHomogeneousList", "IsTable", "IsListOrCollection",
                "IsCollection", "IsExtAElement", "CategoryCollections(IsExtAElement)",
                "CategoryCollections(CategoryCollections(IsExtAElement))", "IsNearAdditiveElement",
                "CategoryCollections(IsNearAdditiveElement)",
                "CategoryCollections(CategoryCollections(IsNearAdditiveElement))", "IsNearAdditiveElementWithZero",
                "CategoryCollections(IsNearAdditiveElementWithZero)",
                "CategoryCollections(CategoryCollections(IsNearAdditiveElementWithZero))",
                "IsNearAdditiveElementWithInverse", "CategoryCollections(IsNearAdditiveElementWithInverse)",
                "CategoryCollections(CategoryCollections(IsNearAdditiveElementWithInverse))", "IsAdditiveElement",
                "CategoryCollections(IsAdditiveElement)", "CategoryCollections(CategoryCollections(IsAdditiveElement))",
                "IsExtLElement", "CategoryCollections(IsExtLElement)",
                "CategoryCollections(CategoryCollections(IsExtLElement))", "IsExtRElement",
                "CategoryCollections(IsExtRElement)", "CategoryCollections(CategoryCollections(IsExtRElement))",
                "IsMultiplicativeElement", "CategoryCollections(CategoryCollections(IsMultiplicativeElement))",
                "IsMultiplicativeElementWithOne",
                "CategoryCollections(CategoryCollections(IsMultiplicativeElementWithOne))",
                "IsMultiplicativeElementWithInverse",
                "CategoryCollections(CategoryCollections(IsMultiplicativeElementWithInverse))",
                "IsGeneralizedRowVector", "IsMultiplicativeGeneralizedRowVector",
                "CategoryCollections(CategoryCollections(IsZDFRE))", "IsOrdinaryMatrix", "IsAssociativeElement",
                "CategoryCollections(CategoryCollections(IsAssociativeElement))", "IsAdditivelyCommutativeElement",
                "CategoryCollections(CategoryCollections(IsAdditivelyCommutativeElement))",
                "CategoryCollections(CategoryCollections(IsCommutativeElement))",
                "CategoryCollections(CategoryCollections(IsCyclotomic))", "IsVecOrMatObj", "IsMatrixOrMatrixObj",
                "IsGeneratorsOfSemigroup", "HasIsGeneratorsOfSemigroup", "IsObject" } }, 48, "./lib/matrix.gi", 1039,
                1125);
        Method m6 = new Method("Order: generic method for ordinary matrices", new String[][] { { "IsCopyable", "IsList",
                "IsDenseList", "IsHomogeneousList", "IsTable", "IsListOrCollection", "IsCollection", "IsExtAElement",
                "CategoryCollections(IsExtAElement)", "CategoryCollections(CategoryCollections(IsExtAElement))",
                "IsNearAdditiveElement", "CategoryCollections(IsNearAdditiveElement)",
                "CategoryCollections(CategoryCollections(IsNearAdditiveElement))", "IsNearAdditiveElementWithZero",
                "CategoryCollections(IsNearAdditiveElementWithZero)",
                "CategoryCollections(CategoryCollections(IsNearAdditiveElementWithZero))",
                "IsNearAdditiveElementWithInverse", "CategoryCollections(IsNearAdditiveElementWithInverse)",
                "CategoryCollections(CategoryCollections(IsNearAdditiveElementWithInverse))", "IsAdditiveElement",
                "CategoryCollections(IsAdditiveElement)", "CategoryCollections(CategoryCollections(IsAdditiveElement))",
                "IsExtLElement", "CategoryCollections(IsExtLElement)",
                "CategoryCollections(CategoryCollections(IsExtLElement))", "IsExtRElement",
                "CategoryCollections(IsExtRElement)", "CategoryCollections(CategoryCollections(IsExtRElement))",
                "IsMultiplicativeElement", "CategoryCollections(CategoryCollections(IsMultiplicativeElement))",
                "IsMultiplicativeElementWithOne", "IsMultiplicativeElementWithInverse", "IsGeneralizedRowVector",
                "IsMultiplicativeGeneralizedRowVector", "IsOrdinaryMatrix", "IsVecOrMatObj", "IsMatrixOrMatrixObj",
                "IsObject" } }, 37, "./lib/matrix.gi", 833, 864);
        Method m7 = new Method("Order: for a group",
                new String[][] { { "IsListOrCollection", "IsCollection", "IsDuplicateFree", "HasIsDuplicateFree",
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
                        "HasIsOrthodoxSemigroup", "IsObject" } },
                35, "./lib/grp.gi", 6048, 6051);
        Method m8 = new Method("Order: for a class function",
                new String[][] { { "IsList", "IsDenseList", "IsHomogeneousList", "IsListOrCollection", "IsCollection",
                        "IsFinite", "HasIsFinite", "IsExtAElement", "CategoryCollections(IsExtAElement)",
                        "IsNearAdditiveElement", "CategoryCollections(IsNearAdditiveElement)",
                        "IsNearAdditiveElementWithZero", "CategoryCollections(IsNearAdditiveElementWithZero)",
                        "IsNearAdditiveElementWithInverse", "CategoryCollections(IsNearAdditiveElementWithInverse)",
                        "IsAdditiveElement", "CategoryCollections(IsAdditiveElement)", "IsExtLElement",
                        "CategoryCollections(IsExtLElement)", "IsExtRElement", "CategoryCollections(IsExtRElement)",
                        "IsMultiplicativeElement", "CategoryCollections(IsMultiplicativeElement)",
                        "IsMultiplicativeElementWithOne", "CategoryCollections(IsMultiplicativeElementWithOne)",
                        "IsMultiplicativeElementWithInverse", "CategoryCollections(IsMultiplicativeElementWithInverse)",
                        "IsGeneralizedRowVector", "IsAssociativeElement", "IsCommutativeElement", "IsClassFunction",
                        "IsObject" } },
                31, "./lib/ctblfuns.gi", 460, 478);
        Method m9 = new Method("Order: for automorphisms",
                new String[][] { { "IsExtLElement", "IsExtRElement", "IsMultiplicativeElement",
                        "IsMultiplicativeElementWithOne", "IsMultiplicativeElementWithInverse", "IsAssociativeElement",
                        "IsGeneralMapping", "IsTotal", "HasIsTotal", "IsSingleValued", "HasIsSingleValued",
                        "RespectsMultiplication", "HasRespectsMultiplication", "RespectsOne", "HasRespectsOne",
                        "RespectsInverses", "HasRespectsInverses", "IsObject" } },
                17, "./lib/morpheus.gi", 26, 129);
        Method m10 = new Method("Order: for an internal FFE",
                new String[][] { { "IsFFE", "IsInternalRep", "IsExtAElement", "IsNearAdditiveElement",
                        "IsNearAdditiveElementWithZero", "IsNearAdditiveElementWithInverse", "IsAdditiveElement",
                        "IsExtLElement", "IsExtRElement", "IsMultiplicativeElement", "IsMultiplicativeElementWithOne",
                        "IsMultiplicativeElementWithInverse", "IsZDFRE", "IsAssociativeElement",
                        "IsAdditivelyCommutativeElement", "IsCommutativeElement", "IsLogOrderedFFE", "IsObject" } },
                17, "./lib/ffe.gi", 779, 798);
        Method m11 = new Method("Order: for element in Z/nZ (ModulusRep)",
                new String[][] { { "IsPositionalObjectRep", "IsExtAElement", "IsNearAdditiveElement",
                        "IsNearAdditiveElementWithZero", "IsNearAdditiveElementWithInverse", "IsAdditiveElement",
                        "IsExtLElement", "IsExtRElement", "IsMultiplicativeElement", "IsMultiplicativeElementWithOne",
                        "IsMultiplicativeElementWithInverse", "IsAssociativeElement", "IsAdditivelyCommutativeElement",
                        "IsCommutativeElement", "IsZmodnZObj", "IsModulusRep", "IsObject" } },
                16, "./lib/zmodnz.gi", 540, 550);
        Method m12 = new Method("Order: for a cyclotomic",
                new String[][] { { "IsCyc", "IsExtAElement", "IsNearAdditiveElement", "IsNearAdditiveElementWithZero",
                        "IsNearAdditiveElementWithInverse", "IsAdditiveElement", "IsExtLElement", "IsExtRElement",
                        "IsMultiplicativeElement", "IsMultiplicativeElementWithOne",
                        "IsMultiplicativeElementWithInverse", "IsZDFRE", "IsAssociativeElement",
                        "IsAdditivelyCommutativeElement", "IsCommutativeElement", "IsCyclotomic", "IsObject" } },
                16, "./lib/cyclotom.g", 901, 925);
        Method m13 = new Method("Order",
                new String[][] { { "IsFFE", "IsExtAElement", "IsNearAdditiveElement", "IsNearAdditiveElementWithZero",
                        "IsNearAdditiveElementWithInverse", "IsAdditiveElement", "IsExtLElement", "IsExtRElement",
                        "IsMultiplicativeElement", "IsMultiplicativeElementWithOne",
                        "IsMultiplicativeElementWithInverse", "IsZDFRE", "IsAssociativeElement",
                        "IsAdditivelyCommutativeElement", "IsCommutativeElement", "IsObject" } },
                15, "./lib/ffeconway.gi", 1395, 1413);
        Method m14 = new Method("Order: for a general FFE",
                new String[][] { { "IsFFE", "IsExtAElement", "IsNearAdditiveElement", "IsNearAdditiveElementWithZero",
                        "IsNearAdditiveElementWithInverse", "IsAdditiveElement", "IsExtLElement", "IsExtRElement",
                        "IsMultiplicativeElement", "IsMultiplicativeElementWithOne",
                        "IsMultiplicativeElementWithInverse", "IsZDFRE", "IsAssociativeElement",
                        "IsAdditivelyCommutativeElement", "IsCommutativeElement", "IsObject" } },
                15, "./lib/ffe.gi", 800, 819);
        Method m15 = new Method("Order: for a permutation",
                new String[][] { { "IsPerm", "IsExtLElement", "IsExtRElement", "IsMultiplicativeElement",
                        "IsMultiplicativeElementWithOne", "IsMultiplicativeElementWithInverse", "IsAssociativeElement",
                        "IsFiniteOrderElement", "IsObject" } },
                8, "./lib/permutat.g", 786, 789);
        Method m16 = new Method("Order: for a transformation",
                new String[][] { { "IsTransformation", "IsExtLElement", "IsExtRElement", "IsMultiplicativeElement",
                        "IsMultiplicativeElementWithOne", "IsMultiplicativeElementWithInverse", "IsAssociativeElement",
                        "IsObject" } },
                7, "./lib/trans.gi", 322, 323);
        Method m17 = new Method("Order: fp group element",
                new String[][] { { "IsExtLElement", "IsExtRElement", "IsMultiplicativeElement",
                        "IsMultiplicativeElementWithOne", "IsMultiplicativeElementWithInverse", "IsAssociativeElement",
                        "IsElementOfFpGroup", "IsObject" } },
                7, "./lib/grpfp.gi", 291, 299);
        Method m18 = new Method(
                "Order: method for a pc-element", new String[][] { { "IsExtLElement", "IsExtRElement",
                        "IsMultiplicativeElement", "IsMultiplicativeElementWithOne", "IsObject" } },
                7, "./lib/pcgspcg.gi", 1317, 1350);
        Method m19 = new Method("Order: free group element",
                new String[][] {
                        { "IsExtLElement", "IsExtRElement", "IsMultiplicativeElement", "IsMultiplicativeElementWithOne",
                                "IsMultiplicativeElementWithInverse", "IsAssociativeElement", "IsWord", "IsObject" } },
                7, "./lib/grpfree.gi", 395, 405);
        Method m20 = new Method("Order", new String[][] { { "IsExtLElement", "IsExtRElement", "IsMultiplicativeElement",
                "IsMultiplicativeElementWithOne", "IsMultiplicativeElementWithInverse", "IsPcpElement", "IsObject" } },
                6, "./pkg/polycyclic/gap/basic/pcpelms.gi", 205, 216);
        Method m21 = new Method(
                "Order: for a mult. element-with-one", new String[][] { { "IsExtLElement", "IsExtRElement",
                        "IsMultiplicativeElement", "IsMultiplicativeElementWithOne", "IsObject" } },
                4, "./lib/arith.gi", 684, 712);

        List<Method> result = searchClient.searchOpt("Order");
        assertNotNull(result);
        assertTrue(!result.isEmpty());
        assertTrue(result.contains(m1));
        assertTrue(result.contains(m2));
        assertTrue(result.contains(m3));
        assertTrue(result.contains(m4));
        assertTrue(result.contains(m5));
        assertTrue(result.contains(m6));
        assertTrue(result.contains(m7));
        assertTrue(result.contains(m8));
        assertTrue(result.contains(m9));
        assertTrue(result.contains(m10));
        assertTrue(result.contains(m11));
        assertTrue(result.contains(m12));
        assertTrue(result.contains(m13));
        assertTrue(result.contains(m14));
        assertTrue(result.contains(m15));
        assertTrue(result.contains(m16));
        assertTrue(result.contains(m17));
        assertTrue(result.contains(m18));
        assertTrue(result.contains(m19));
        assertTrue(result.contains(m20));
        assertTrue(result.contains(m21));
    }

    @Test
    @DisplayName("Test if the leading and trailing whitespaces in input are ignored for searching.")
    public void searchWithLeadingAndTrailingSpacesTest() {
        List<Method> standardResult = searchClient.searchOpt("Order");
        assertTrue(!standardResult.isEmpty());

        List<Method> testResult1 = searchClient.searchOpt("  Order");
        assertTrue(!testResult1.isEmpty());
        List<Method> testResult2 = searchClient.searchOpt("Order  ");
        assertTrue(!testResult2.isEmpty());
        List<Method> testResult3 = searchClient.searchOpt(" Order  ");
        assertTrue(!testResult3.isEmpty());

        assertEquals(standardResult, testResult1);
        assertEquals(standardResult, testResult2);
        assertEquals(standardResult, testResult3);
    }

    @Test
    @DisplayName("Test if searching nonexistent operation returns null.")
    public void searchNonExistentOptTest() {
        assertTrue(searchClient.searchOpt("OrderX,") == null);
        assertTrue(searchClient.searchOpt("Oorder") == null);
        assertTrue(searchClient.searchOpt("Orderr") == null);
        assertTrue(searchClient.searchOpt("PermutCycleOp") == null);
    }

    @Test
    @DisplayName("Test if searching returns null when input is in unexpected letter case or compromised by punctuation marks.")
    public void searchWithIllegalInputTest() {
        assertTrue(searchClient.searchOpt("order") == null);
        assertTrue(searchClient.searchOpt("oRDer") == null);
        assertTrue(searchClient.searchOpt("ordersTom") == null);
        assertTrue(searchClient.searchOpt("permutationOp") == null);

        assertTrue(searchClient.searchOpt("Order,") == null);
        assertTrue(searchClient.searchOpt(",Order!") == null);
        assertTrue(searchClient.searchOpt("Order;") == null);
        assertTrue(searchClient.searchOpt("Order.") == null);
    }

    @Test
    @DisplayName("Test if searching operations of name consisting of punctuation marks works.")
    public void searchOptContainingPunctuationMarksTest() {
        List<Method> testResult1 = searchClient.searchOpt(".");
        assertTrue(!testResult1.isEmpty());
        List<Method> testResult2 = searchClient.searchOpt(".:=");
        assertTrue(!testResult2.isEmpty());

        List<Method> testResult3 = searchClient.searchOpt("[]");
        assertTrue(!testResult3.isEmpty());
        List<Method> testResult4 = searchClient.searchOpt("[]:=");
        assertTrue(!testResult4.isEmpty());

        List<Method> testResult5 = searchClient.searchOpt("{}");
        assertTrue(!testResult5.isEmpty());
        List<Method> testResult6 = searchClient.searchOpt("{}:=");
        assertTrue(!testResult6.isEmpty());

        List<Method> testResult7 = searchClient.searchOpt("^");
        assertTrue(!testResult7.isEmpty());
        List<Method> testResult8 = searchClient.searchOpt("<");
        assertTrue(!testResult8.isEmpty());

        List<Method> testResult9 = searchClient.searchOpt("/");
        assertTrue(!testResult9.isEmpty());
        List<Method> testResult10 = searchClient.searchOpt("*");
        assertTrue(!testResult10.isEmpty());
        List<Method> testResult11 = searchClient.searchOpt("+");
        assertTrue(!testResult11.isEmpty());
        List<Method> testResult12 = searchClient.searchOpt("-");
        assertTrue(!testResult12.isEmpty());
        List<Method> testResult13 = searchClient.searchOpt("=");
        assertTrue(!testResult13.isEmpty());

        List<Method> testResult14 = searchClient.searchOpt("_GapToJsonStreamInternal");
        assertTrue(!testResult14.isEmpty());
    }

}
