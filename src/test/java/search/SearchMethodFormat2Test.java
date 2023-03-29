package search;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.data.Method;

@DisplayName("JUnit 5 Test")
class SearchMethodFormat2Test extends BaseTest {
    /**
     * Supported input format:
     * Format1: (~)method_name
     * Format2: (~)method_name{flt1, flt2, flt3, ...}
     * Format3: (~)method_name{[arg1_flt1, arg1_flt2, arg1_flt3, ...], [arg2_flt1, arg2_flt2, ...], [...], ...}
     */

    @Test
    @DisplayName("Test if searching some fundamental methods returns the expected results.")
    public void searchFundementalInstanceTest() {
        
        String input1 = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsObject}";
        Set<String> inputSet1 = Arrays.stream(StringUtils.substringBetween(input1, "{", "}").split(",")).map(String::trim).collect(Collectors.toSet());
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());
        res1.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(StringUtils.substringBefore(input1, "{"))));
        res1.stream().map(m -> Method.getUniqueFilters(m.getArgFilters())).forEach(s -> assertTrue(s.containsAll(inputSet1)));
        
        String input2 = "Size{IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, IsObject}";
        Set<String> inputSet2 = Arrays.stream(StringUtils.substringBetween(input2, "{", "}").split(",")).map(String::trim).collect(Collectors.toSet());
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());
        res2.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(StringUtils.substringBefore(input2, "{"))));
        res2.stream().map(m -> Arrays.stream(m.getArgFilters()[0]).collect(Collectors.toSet())).forEach(s -> assertTrue(s.containsAll(inputSet2)));

        String input3 = "+{IsListOrCollection, IsCollection, IsDuplicateFree, HasIsDuplicateFree, IsGeneralizedDomain}";
        Set<String> inputSet3 = Arrays.stream(StringUtils.substringBetween(input3, "{", "}").split(",")).map(String::trim).collect(Collectors.toSet());
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());
        res3.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(StringUtils.substringBefore(input3, "{"))));
        res3.stream().map(m -> Arrays.stream(m.getArgFilters()[0]).collect(Collectors.toSet())).forEach(s -> assertTrue(s.containsAll(inputSet3)));
    }

    @Test
    @DisplayName("Test if searching with subset symbol returns more methods than without.")
    public void searchWithSubsetSymbolTest() {
        
        String inputWithout1 = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne}";
        List<Method> without1 = searchModule.searchMethod(inputWithout1);
        assertFalse(without1.isEmpty());
        String inputWith1 = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...}";
        List<Method> with1 = searchModule.searchMethod(inputWith1);
        assertFalse(with1.isEmpty());

        assertTrue(with1.size() >= without1.size());
        Set<String> inputFilterSet1 = Arrays.stream(StringUtils.substringBetween(inputWithout1, "{", "}").split(",")).map(String::trim).collect(Collectors.toSet());
        with1.stream().filter(m -> !without1.contains(m)).map(m -> Method.getUniqueFilters(m.getArgFilters()))
                .forEach(s -> assertTrue(s.containsAll(inputFilterSet1)));

        String inputWithout2 = "Size{IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection}";
        List<Method> without2 = searchModule.searchMethod(inputWithout2);
        assertFalse(without2.isEmpty());
        String inputWith2 = "Size{IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, ...}";
        List<Method> with2 = searchModule.searchMethod(inputWith2);
        assertFalse(with2.isEmpty());

        assertTrue(with2.size() >= without2.size());
        Set<String> inputFilterSet2 = Arrays.stream(StringUtils.substringBetween(inputWithout2, "{", "}").split(",")).map(String::trim)
                .collect(Collectors.toSet());
        with2.stream().filter(m -> !without2.contains(m)).map(m -> Method.getUniqueFilters(m.getArgFilters()))
                .forEach(f -> assertTrue(f.containsAll(inputFilterSet2)));
    }

    @Test
    @DisplayName("Test if searching illegal input with a subset symbol returns the same result as searching with only a subset symbol.")
    public void searchWithIllegalArgumentFiltersWithSubsetSymbolTest() {
        
        String input1 = "Order{...}";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());

        String input2 = "Order{isExtLElement, isExtrelement, IsultiplicativeElement, IsmultiplicativeelementwithOne, ...}";
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());

        String input3 = "Order{isExtlelement, IsExtWESlement, nothingElement, isMultiplicativeElementWithone, ...}";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());

        assertIterableEquals(res1, res2);
        assertIterableEquals(res1, res3);
        
        String input4 = "Size{...}";
        List<Method> res4 = searchModule.searchMethod(input4);
        assertFalse(res4.isEmpty());

        String input5 = "Size{isList, isDenseList, isHomogeneousList, isListOrCollection, isCollection, ...}";
        List<Method> res5 = searchModule.searchMethod(input5);
        assertFalse(res5.isEmpty());

        String input6 = "Size{isList, isdenseList, nothing, nullFilter, isCollection, ...}";
        List<Method> res6 = searchModule.searchMethod(input6);
        assertFalse(res6.isEmpty());

        assertIterableEquals(res4, res5);
        assertIterableEquals(res4, res6);
    }
    
    @Test
    @DisplayName("Test if searching with ~ symbol in method name returns the expected results.")
    public void searchWithContainSymbolTest() {
        
        String input1 = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...}";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());

        String input2 = "~Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...}";
        Set<String> inputSet2 = Arrays.stream(StringUtils.substringBetween(input2, "{", "}").split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet());
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());
        res2.stream().map(Method::getName).forEach(n -> assertTrue(n.contains(StringUtils.substringBefore(input2, "{").substring(1))));
        res2.stream().map(m -> Method.getUniqueFilters(m.getArgFilters())).forEach(s -> assertTrue(s.containsAll(inputSet2)));

        assertTrue(res2.size() >= res1.size());

        String input3 = "Size{IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, ...}";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());

        String input4 = "~Size{IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, ...}";
        Set<String> inputSet4 = Arrays.stream(StringUtils.substringBetween(input4, "{", "}").split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet());
        List<Method> res4 = searchModule.searchMethod(input4);
        assertFalse(res4.isEmpty());
        res4.stream().map(Method::getName).forEach(n -> assertTrue(n.contains(StringUtils.substringBefore(input4, "{").substring(1))));
        res4.stream().map(m -> Method.getUniqueFilters(m.getArgFilters())).forEach(s -> assertTrue(s.containsAll(inputSet4)));

        assertTrue(res4.size() >= res3.size());
    }
    
    @Test
    @DisplayName("Test if searching with partial method names returns methods of names starting with the input.")
    public void searchWithPartialNameTest() {
        
        String input1 = "Ord{...}";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());

        String input2 = "Order{...}";
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());

        String input3 = "Orders{...}";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());
        
        assertTrue(res1.size() >= res2.size());
        assertTrue(res2.size() >= res3.size());
        
        String input4 = "Si{...}";
        List<Method> res4 = searchModule.searchMethod(input4);
        assertFalse(res4.isEmpty());

        String input5 = "Size{...}";
        List<Method> res5 = searchModule.searchMethod(input5);
        assertFalse(res5.isEmpty());

        String input6 = "Sizes{...}";
        List<Method> res6 = searchModule.searchMethod(input6);
        assertFalse(res6.isEmpty());
        
        assertTrue(res4.size() >= res5.size());
        assertTrue(res5.size() >= res6.size());
    }

    @Test
    @DisplayName("Test if any unnecessary whitespaces in input are ignored for searching.")
    public void searchWithWhitespacesTest() {
        
        String standardInput = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...}";
        List<Method> standardRes = searchModule.searchMethod(standardInput);
        assertFalse(standardRes.isEmpty());

        String input1 = " Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...}";
        List<Method> testRes1 = searchModule.searchMethod(input1);
        assertFalse(testRes1.isEmpty());
        
        String input2 = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...}  ";
        List<Method> testRes2 = searchModule.searchMethod(input2);
        assertFalse(testRes2.isEmpty());
        
        String input3 = "  Order{ IsExtLElement,   IsExtRElement,IsMultiplicativeElement,  IsMultiplicativeElementWithOne,  ...   }  ";
        List<Method> testRes3 = searchModule.searchMethod(input3);
        assertFalse(testRes3.isEmpty());
        
        String input4 = "  Order{ IsExtLElement, IsExtRElement,IsMultiplicativeElement,  IsMultiplicativeElementWithOne,  ...   }  ";
        List<Method> testRes4 = searchModule.searchMethod(input4);
        assertFalse(testRes4.isEmpty());

        assertIterableEquals(standardRes, testRes1);
        assertIterableEquals(standardRes, testRes2);
        assertIterableEquals(standardRes, testRes3);
        assertIterableEquals(standardRes, testRes4);
    }

    @Test
    @DisplayName("Test if searching equivalent inputs returns the same result.")
    /**
     * Equivalent inputs are the inputs that contain exactly the same method name and argument filters, but the filters are in different orders.
     */
    public void searchEquivalentInputsTest() {
        
        String input1 = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...}";
        String eqvl1 = "Order{IsMultiplicativeElementWithOne, IsExtLElement, IsMultiplicativeElement, IsExtRElement, ...}";
        List<Method> inputRes1 = searchModule.searchMethod(input1);
        assertFalse(inputRes1.isEmpty());
        List<Method> eqvlRes1 = searchModule.searchMethod(eqvl1);
        assertFalse(eqvlRes1.isEmpty());
        assertIterableEquals(inputRes1, eqvlRes1);

        String input2 = "Size{IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, ...}";
        String eqvl2 = "Size{IsListOrCollection, IsHomogeneousList, IsList, IsCollection, IsDenseList, ...}";
        List<Method> inputRes2 = searchModule.searchMethod(input2);
        assertFalse(inputRes2.isEmpty());
        List<Method> eqvlRes2 = searchModule.searchMethod(eqvl2);
        assertFalse(eqvlRes2.isEmpty());
        assertIterableEquals(inputRes2, eqvlRes2);

        String input3 = "+{IsListOrCollection, IsCollection, IsDuplicateFree, HasIsDuplicateFree, IsGeneralizedDomain, ...}";
        String eqvl3 = "+{..., IsDuplicateFree, IsGeneralizedDomain, IsListOrCollection, HasIsDuplicateFree, IsCollection}";
        List<Method> inputRes3 = searchModule.searchMethod(input3);
        assertFalse(inputRes3.isEmpty());
        List<Method> eqvlRes3 = searchModule.searchMethod(eqvl3);
        assertFalse(eqvlRes3.isEmpty());
        assertIterableEquals(inputRes3, eqvlRes3);
    }
    
    @Test
    @DisplayName("Test if searching with null input returns null.")
    public void searchWithNullInputTest() {
        assertNull(searchModule.searchMethod(null));
    }
    
    @Test
    @DisplayName("Test if searching with input containing empty part returns null.")
    public void searchWithInputContainingEmptyPartTest() {
        assertNull(searchModule.searchMethod(""));
        assertNull(searchModule.searchMethod("{...}"));
        assertNull(searchModule.searchMethod("Order{}"));
        assertNull(searchModule.searchMethod("{IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection}"));
        
    }

    @Test
    @DisplayName("Test if searching with non-existent method names and/or filters returns null.")
    public void searchNonExistentMethodTest() {
        assertNull(searchModule.searchMethod("NullMethod{abcdefg}"));
        assertNull(searchModule.searchMethod("Order{BadNullFilter1, BadNullFilter2, BadNullFilter}"));
        assertNull(searchModule.searchMethod("NullMethodName{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...}"));
    }

    @Test
    @DisplayName("Test if searching illegal input returns null.")
    /**
     * input is in unexpected letter case subset symbol not in expected form
     */
    public void searchIllegalInputTest() {
        
        String input1 = "Order{IsExtLElement  IsExtRElement  IsMultiplicativeElement IsMultiplicativeElementWithOne}";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertNull(res1);

        String input2 = "Order{IsExtLElement! IsExtRElement! IsMultiplicativeElement. IsMultiplicativeElementWithOne}";
        List<Method> res2 = searchModule.searchMethod(input2);
        assertNull(res2);

        String input3 = "Order[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne}";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertNull(res3);

        String input4 = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne]";
        List<Method> res4 = searchModule.searchMethod(input4);
        assertNull(res4);
        
        String input5 = "Order[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne]";
        List<Method> res5 = searchModule.searchMethod(input5);
        assertNull(res5);
        
        String input6 = "Order(IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne)";
        List<Method> res6 = searchModule.searchMethod(input6);
        assertNull(res6);
        
        String input7 = "order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne}";
        List<Method> res7 = searchModule.searchMethod(input7);
        assertNull(res7);
        
        String input8 = "oRder{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne}";
        List<Method> res8 = searchModule.searchMethod(input8);
        assertNull(res8);
        
        String input9 = "ORDER{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne}";
        List<Method> res9 = searchModule.searchMethod(input9);
        assertNull(res9);
        
        String input10 = "Order{isExtLElement, isExtRElement, isMultiplicativeElement, isMultiplicativeElementWithOne}";
        List<Method> res10 = searchModule.searchMethod(input10);
        assertNull(res10);

        String input11 = "Order{isextlelement, isextRElement, ismUltiplicativeElement, iSMultiplicativeElementWithOne}";
        List<Method> res11 = searchModule.searchMethod(input11);
        assertNull(res11);
        
        String input12 = "Order~{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne}";
        List<Method> res12 = searchModule.searchMethod(input12);
        assertNull(res12);
        
        String input13 = "~~Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne}";
        List<Method> res13 = searchModule.searchMethod(input13);
        assertNull(res13);

        String input14 = "Order{IsExtLElement, IsExtRElement}, IsMultiplicativeElement, IsMultiplicativeElementWithOne";
        List<Method> res14 = searchModule.searchMethod(input14);
        assertNull(res14);
        
        String input15 = "Order{IsExtLElement, IsExtRElement), IsMultiplicativeElement, IsMultiplicativeElementWithOne}";
        List<Method> res15 = searchModule.searchMethod(input15);
        assertNull(res15);
        
        String input16 = "Order{IsExtLElement%&, IsExtRElement[, IsMultiplicativeElement), IsMultiplicative%^&ElementWithOne}";
        List<Method> res16 = searchModule.searchMethod(input16);
        assertNull(res16);
        
        String input17 = "~ Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne}";
        List<Method> res17 = searchModule.searchMethod(input17);
        assertNull(res17);
    }

    @Test
    @DisplayName("Test if searching returns null when input partly includes nonexistent filters or is partly in illegal format")
    void searchInputIncludingPartlyNonExistentFiltersTest() {
        
        String standard1 = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne}";
        List<Method> std1 = searchModule.searchMethod(standard1);
        assertFalse(std1.isEmpty());
        String input1 = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, NULLFILTER}";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());
        String input2 = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ....}";
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());
        String input3 = "Order{IsExtLElement, !@$!@%#$#%, IsExtRElement, IsMultiplicativeElement, %^&*^%&*, IsMultiplicativeElementWithOne, .....}";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());
        
        assertIterableEquals(std1, res1);
        assertIterableEquals(std1, res2);
        assertIterableEquals(std1, res3);

        String standard2 = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...}";
        List<Method> std2 = searchModule.searchMethod(standard2);
        assertFalse(std2.isEmpty());
        assertTrue(std2.size() >= res3.size());

        String input4 = "Order{~~()&(), IsExtLElement, IsExtRElement, !@#$#%, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...}";
        List<Method> res4 = searchModule.searchMethod(input4);
        assertFalse(res4.isEmpty());

        assertIterableEquals(std2, res4);
    }
    
    @Test
    @DisplayName("Test if searching methods with name containing punctuation marks works as expected.")
    public void searchInputContainingPunctuationMarksTest() {
        
        List<Method> testResult1 = searchModule.searchMethod(".{...}");
        assertFalse(testResult1.isEmpty());
        List<Method> testResult2 = searchModule.searchMethod(".:={...}");
        assertFalse(testResult2.isEmpty());

        List<Method> testResult3 = searchModule.searchMethod("[]{...}");
        assertFalse(testResult3.isEmpty());
        List<Method> testResult4 = searchModule.searchMethod("[]:={...}");
        assertFalse(testResult4.isEmpty());

        List<Method> testResult5 = searchModule.searchMethod("{}{...}");
        assertFalse(testResult5.isEmpty());
        List<Method> testResult6 = searchModule.searchMethod("{}:={...}");
        assertFalse(testResult6.isEmpty());

        List<Method> testResult7 = searchModule.searchMethod("^{...}");
        assertFalse(testResult7.isEmpty());
        List<Method> testResult8 = searchModule.searchMethod("<{...}");
        assertFalse(testResult8.isEmpty());

        List<Method> testResult9 = searchModule.searchMethod("/{...}");
        assertFalse(testResult9.isEmpty());
        List<Method> testResult10 = searchModule.searchMethod("*{...}");
        assertFalse(testResult10.isEmpty());
        List<Method> testResult11 = searchModule.searchMethod("+{...}");
        assertFalse(testResult11.isEmpty());
        List<Method> testResult12 = searchModule.searchMethod("-{...}");
        assertFalse(testResult12.isEmpty());
        List<Method> testResult13 = searchModule.searchMethod("={...}");
        assertFalse(testResult13.isEmpty());

        List<Method> testResult14 = searchModule.searchMethod("_GapToJsonStreamInternal{...}");
        assertFalse(testResult14.isEmpty());
        
        List<Method> testResult15 = searchModule.searchMethod("~.{...}");
        assertFalse(testResult15.isEmpty());
        List<Method> testResult16 = searchModule.searchMethod("~.:={...}");
        assertFalse(testResult16.isEmpty());
        
        List<Method> testResult17 = searchModule.searchMethod("~{}{...}");
        assertFalse(testResult17.isEmpty());
        List<Method> testResult18 = searchModule.searchMethod("~{}:={...}");
        assertFalse(testResult18.isEmpty());
        
        List<Method> testResult19 = searchModule.searchMethod("~^{...}");
        assertFalse(testResult19.isEmpty());
        
        List<Method> testResult20 = searchModule.searchMethod("~*{...}");
        assertFalse(testResult20.isEmpty());
    }

}
