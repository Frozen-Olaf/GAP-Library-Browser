package search;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.data.Method;

@DisplayName("JUnit 5 Test")
class SearchMethodFormat1Test extends BaseTest {
    /**
     * Supported input format:
     * Format1: (~)method_name
     * Format2: (~)method_name{flt1, flt2, flt3, ...}
     * Format3: (~)method_name{[arg1_flt1, arg1_flt2, arg1_flt3, ...], [arg2_flt1, arg2_flt2, ...], [...], ...}
     */

    @Test
    @DisplayName("Test if searching some fundamental methods returns the expected results.")
    public void searchFundementalInstanceTest() {

        String input1 = "Order";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());
        res1.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(input1)));

        String input2 = "Size";
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());
        res2.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(input2)));
        
        String input3 = "_GapToJsonStreamInternal";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());
        res3.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(input3)));
    }

    @Test
    @DisplayName("Test if searching with 'contain' symbol in method name returns the expected results.")
    public void searchWithContainSymbolTest() {
        
        String input1 = "Order";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());

        String input2 = "~Order";
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());
        res2.stream().map(Method::getName).forEach(n -> assertTrue(n.contains(input2.substring(1))));

        assertTrue(res2.size() >= res1.size());

        String input3 = "Size";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());

        String input4 = "~Size";
        List<Method> res4 = searchModule.searchMethod(input4);
        assertFalse(res4.isEmpty());
        res4.stream().map(Method::getName).forEach(n -> assertTrue(n.contains(input4.substring(1))));

        assertTrue(res4.size() >= res3.size());
    }
    
    @Test
    @DisplayName("Test if searching with partial method names returns methods of names starting with the input.")
    public void searchWithPartialNameTest() {
        
        String input1 = "Ord";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());
        res1.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(input1)));

        String input2 = "Order";
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());

        assertTrue(res1.size() >= res2.size());

        String input3 = "Orders";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());
        res3.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(input3)));

        assertTrue(res2.size() >= res3.size());

        String input4 = "Si";
        List<Method> res4 = searchModule.searchMethod(input4);
        assertFalse(res4.isEmpty());
        res4.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(input4)));
        
        String input5 = "Size";
        List<Method> res5 = searchModule.searchMethod(input5);
        assertFalse(res5.isEmpty());

        assertTrue(res4.size() >= res5.size());
        
        String input6 = "Sizes";
        List<Method> res6 = searchModule.searchMethod(input6);
        assertFalse(res6.isEmpty());
        res6.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(input6)));

        assertTrue(res5.size() >= res6.size());
    }
    
    

    @Test
    @DisplayName("Test if the leading and trailing whitespaces in input are ignored for searching.")
    public void searchWithLeadingAndTrailingSpacesTest() {
        
        List<Method> standardRes = searchModule.searchMethod("Size");
        assertFalse(standardRes.isEmpty());

        List<Method> testRes1 = searchModule.searchMethod(" Size");
        assertFalse(testRes1.isEmpty());
        List<Method> testResult2 = searchModule.searchMethod("Size  ");
        assertFalse(testResult2.isEmpty());
        List<Method> testResult3 = searchModule.searchMethod("   Size   ");
        assertFalse(testResult3.isEmpty());

        assertIterableEquals(standardRes, testRes1);
        assertIterableEquals(standardRes, testResult2);
        assertIterableEquals(standardRes, testResult3);
    }
    
    @Test
    @DisplayName("Test if searching with null input returns null.")
    public void searchWithNullInputTest() {
        assertNull(searchModule.searchMethod(null));
    }
    
    @Test
    @DisplayName("Test if searching with empty input returns null.")
    public void searchWithEmptyInputTest() {
        assertNull(searchModule.searchMethod(""));
    }

    @Test
    @DisplayName("Test if searching with non-existent method names returns null.")
    public void searchNonExistentMethodTest() {
        assertNull(searchModule.searchMethod("NullMethod"));
        assertNull(searchModule.searchMethod("BadMethodName"));
        assertNull(searchModule.searchMethod("NotExists"));
    }

    @Test
    @DisplayName("Test if searching illegal input returns null.")
    /**
     * input is in unexpected letter case subset symbol not in expected form
     */
    public void searchWithIllegalMethodNameTest() {
        
        String input1 = "size";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertNull(res1);

        String input2 = "oRder";
        List<Method> res2 = searchModule.searchMethod(input2);
        assertNull(res2);

        String input3 = ".S.i.z.e.";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertNull(res3);

        String input4 = "Size,";
        List<Method> res4 = searchModule.searchMethod(input4);
        assertNull(res4);

        String input5 = "Order Size Intersection2";
        List<Method> res5 = searchModule.searchMethod(input5);
        assertNull(res5);

        String input6 = "Order, Size";
        List<Method> res6 = searchModule.searchMethod(input6);
        assertNull(res6);

        String input7 = "~Order Size";
        List<Method> res7 = searchModule.searchMethod(input7);
        assertNull(res7);
        
        String input8 = "~ Order";
        List<Method> res8 = searchModule.searchMethod(input8);
        assertNull(res8);
    }
    
    @Test
    @DisplayName("Test if searching methods with name containing punctuation marks works as expected.")
    public void searchInputContainingPunctuationMarksTest() {
        
        List<Method> testResult1 = searchModule.searchMethod(".");
        assertFalse(testResult1.isEmpty());
        List<Method> testResult2 = searchModule.searchMethod(".:=");
        assertFalse(testResult2.isEmpty());

        List<Method> testResult3 = searchModule.searchMethod("[]");
        assertFalse(testResult3.isEmpty());
        List<Method> testResult4 = searchModule.searchMethod("[]:=");
        assertFalse(testResult4.isEmpty());

        List<Method> testResult5 = searchModule.searchMethod("{}");
        assertFalse(testResult5.isEmpty());
        List<Method> testResult6 = searchModule.searchMethod("{}:=");
        assertFalse(testResult6.isEmpty());

        List<Method> testResult7 = searchModule.searchMethod("^");
        assertFalse(testResult7.isEmpty());
        List<Method> testResult8 = searchModule.searchMethod("<");
        assertFalse(testResult8.isEmpty());

        List<Method> testResult9 = searchModule.searchMethod("/");
        assertFalse(testResult9.isEmpty());
        List<Method> testResult10 = searchModule.searchMethod("*");
        assertFalse(testResult10.isEmpty());
        List<Method> testResult11 = searchModule.searchMethod("+");
        assertFalse(testResult11.isEmpty());
        List<Method> testResult12 = searchModule.searchMethod("-");
        assertFalse(testResult12.isEmpty());
        List<Method> testResult13 = searchModule.searchMethod("=");
        assertFalse(testResult13.isEmpty());

        List<Method> testResult14 = searchModule.searchMethod("_GapToJsonStreamInternal");
        assertFalse(testResult14.isEmpty());
    }

}
