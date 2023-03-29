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
class SearchMethodFormat3Test extends BaseTest {
    /**
     * Supported input format:
     * Format1: (~)method_name
     * Format2: (~)method_name{flt1, flt2, flt3, ...}
     * Format3: (~)method_name{[arg1_flt1, arg1_flt2, arg1_flt3, ...], [arg2_flt1, arg2_flt2, ...], [...], ...}
     */

    @Test
    @DisplayName("Test if searching some fundamental methods returns the expected results.")
    public void searchFundementalInstanceTest() {
        
        String input1 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsObject]}";
        List<Set<String>> args1 = Arrays.stream(StringUtils.substringsBetween(input1, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());
        res1.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(StringUtils.substringBefore(input1, "{"))));
        assertTrue(checkArgFiltersInOrder(args1, res1, false, new boolean[]{false}));
        
        String input2 = "Intersection2{[IsListOrCollection, IsCollection, IsObject], [IsListOrCollection, IsCollection, IsObject]}";
        List<Set<String>> args2 = Arrays.stream(StringUtils.substringsBetween(input2, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());
        res2.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(StringUtils.substringBefore(input2, "{"))));
        assertTrue(checkArgFiltersInOrder(args2, res2, false, new boolean[]{false, false}));
        
        String input3 = "Matrix{"
                + "[IsList, IsListOrCollection, IsObject], "
                + "[IsInt, IsRat, IsCyc, IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsZDFRE, IsAssociativeElement, IsAdditivelyCommutativeElement, IsCommutativeElement, IsCyclotomic, IsObject], "
                + "[IsCopyable, IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsVecOrMatObj, IsMatrixOrMatrixObj, IsObject]"
                + "}";
        List<Set<String>> args3 = Arrays.stream(StringUtils.substringsBetween(input3, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());
        res3.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(StringUtils.substringBefore(input3, "{"))));
        assertTrue(checkArgFiltersInOrder(args3, res3, false, new boolean[] {false, false, false}));
    }

    @Test
    @DisplayName("Test if searching with subset symbol works as expected.")
    public void searchWithSubsetSymbolTest() {
        
        String input1_without = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne]}";
        List<Method> without1 = searchModule.searchMethod(input1_without);
        assertFalse(without1.isEmpty());
        
        String input1_with1 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}";
        List<Set<String>> input1_args1 = Arrays.stream(StringUtils.substringsBetween(input1_with1, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input1_with1_res = searchModule.searchMethod(input1_with1);
        assertFalse(input1_with1_res.isEmpty());
        checkArgFiltersInOrder(input1_args1, input1_with1_res, false, new boolean[]{true});
        
        String input1_with2 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne], ...}";
        List<Set<String>> input1_args2 = Arrays.stream(StringUtils.substringsBetween(input1_with2, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input1_with2_res = searchModule.searchMethod(input1_with2);
        assertFalse(input1_with2_res.isEmpty());
        checkArgFiltersInOrder(input1_args2, input1_with2_res, true, new boolean[]{false});
        
        String input1_with3 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...], ...}";
        List<Set<String>> input1_args3 = Arrays.stream(StringUtils.substringsBetween(input1_with3, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input1_with3_res = searchModule.searchMethod(input1_with3);
        assertFalse(input1_with3_res.isEmpty());
        checkArgFiltersInOrder(input1_args3, input1_with3_res, true, new boolean[]{true});
        
        String input1_with4 = "Order{[...]}";
        List<Set<String>> input1_args4 = Arrays.stream(StringUtils.substringsBetween(input1_with4, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input1_with4_res = searchModule.searchMethod(input1_with4);
        assertFalse(input1_with4_res.isEmpty());
        input1_with4_res.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(StringUtils.substringBefore(input1_with4, "{"))));
        checkArgFiltersInOrder(input1_args4, input1_with4_res, false, new boolean[]{true});
        
        String input1_with5 = "Order{[...], ...}";
        List<Set<String>> input1_args5 = Arrays.stream(StringUtils.substringsBetween(input1_with5, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input1_with5_res = searchModule.searchMethod(input1_with5);
        assertFalse(input1_with5_res.isEmpty());
        input1_with5_res.stream().map(Method::getName).forEach(n -> assertTrue(n.startsWith(StringUtils.substringBefore(input1_with5, "{"))));
        checkArgFiltersInOrder(input1_args5, input1_with5_res, true, new boolean[]{true});

        assertTrue(input1_with1_res.size() >= without1.size());
        assertTrue(input1_with2_res.size() >= without1.size());
        assertTrue(input1_with3_res.size() >= without1.size());
        assertTrue(input1_with4_res.size() >= without1.size());
        assertTrue(input1_with5_res.size() >= without1.size());
        
        assertTrue(input1_with3_res.size() >= input1_with2_res.size());
        assertTrue(input1_with3_res.size() >= input1_with1_res.size());
        assertTrue(input1_with4_res.size() >= input1_with1_res.size());
        assertTrue(input1_with5_res.size() >= input1_with4_res.size());
        
        String input2_without = "Intersection2{[IsListOrCollection, IsCollection], [IsListOrCollection, IsCollection]}";
        List<Method> without2 = searchModule.searchMethod(input2_without);
        assertFalse(without2.isEmpty());
        
        String input2_with1 = "Intersection2{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection]}";
        List<Set<String>> input2_args1 = Arrays.stream(StringUtils.substringsBetween(input2_with1, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input2_with1_res = searchModule.searchMethod(input2_with1);
        assertFalse(input2_with1_res.isEmpty());
        checkArgFiltersInOrder(input2_args1, input2_with1_res, false, new boolean[]{true, false});
        
        String input2_with2 = "Intersection2{[IsListOrCollection, IsCollection], [IsListOrCollection, IsCollection, ...]}";
        List<Set<String>> input2_args2 = Arrays.stream(StringUtils.substringsBetween(input2_with2, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input2_with2_res = searchModule.searchMethod(input2_with1);
        assertFalse(input2_with2_res.isEmpty());
        checkArgFiltersInOrder(input2_args2, input2_with2_res, false, new boolean[]{false, true});
        
        String input2_with3 = "Intersection2{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection, ...]}";
        List<Set<String>> input2_args3 = Arrays.stream(StringUtils.substringsBetween(input2_with3, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input2_with3_res = searchModule.searchMethod(input2_with3);
        assertFalse(input2_with3_res.isEmpty());
        checkArgFiltersInOrder(input2_args3, input2_with3_res, false, new boolean[]{true, true});
        
        String input2_with4 = "Intersection2{[IsListOrCollection, IsCollection], [IsListOrCollection, IsCollection], ...}";
        List<Set<String>> input2_args4 = Arrays.stream(StringUtils.substringsBetween(input2_with4, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input2_with4_res = searchModule.searchMethod(input2_with4);
        assertFalse(input2_with4_res.isEmpty());
        checkArgFiltersInOrder(input2_args4, input2_with4_res, true, new boolean[]{false, false});
        
        String input2_with5 = "Intersection2{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection], ...}";
        List<Set<String>> input2_args5 = Arrays.stream(StringUtils.substringsBetween(input2_with5, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input2_with5_res = searchModule.searchMethod(input2_with5);
        assertFalse(input2_with5_res.isEmpty());
        checkArgFiltersInOrder(input2_args5, input2_with5_res, true, new boolean[]{true, false});
        
        String input2_with6 = "Intersection2{[IsListOrCollection, IsCollection], [IsListOrCollection, IsCollection, ...], ...}";
        List<Set<String>> input2_args6 = Arrays.stream(StringUtils.substringsBetween(input2_with6, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input2_with6_res = searchModule.searchMethod(input2_with6);
        assertFalse(input2_with6_res.isEmpty());
        checkArgFiltersInOrder(input2_args6, input2_with6_res, true, new boolean[]{false, true});
        
        String input2_with7 = "Intersection2{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection, ...], ...}";
        List<Set<String>> input2_args7 = Arrays.stream(StringUtils.substringsBetween(input2_with7, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input2_with7_res = searchModule.searchMethod(input2_with7);
        assertFalse(input2_with7_res.isEmpty());
        checkArgFiltersInOrder(input2_args7, input2_with7_res, true, new boolean[]{true, true});
        
        String input2_with8 = "Intersection2{[IsListOrCollection, IsCollection, ...], [...]}";
        List<Set<String>> input2_args8 = Arrays.stream(StringUtils.substringsBetween(input2_with8, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input2_with8_res = searchModule.searchMethod(input2_with8);
        assertFalse(input2_with8_res.isEmpty());
        checkArgFiltersInOrder(input2_args8, input2_with8_res, false, new boolean[]{true, true});
        
        String input2_with9 = "Intersection2{[...], [IsListOrCollection, IsCollection, ...]}";
        List<Set<String>> input2_args9 = Arrays.stream(StringUtils.substringsBetween(input2_with9, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input2_with9_res = searchModule.searchMethod(input2_with9);
        assertFalse(input2_with9_res.isEmpty());
        checkArgFiltersInOrder(input2_args9, input2_with9_res, false, new boolean[]{true, true});
        
        String input2_with10 = "Intersection2{[...], [...]}";
        List<Set<String>> input2_args10 = Arrays.stream(StringUtils.substringsBetween(input2_with10, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input2_with10_res = searchModule.searchMethod(input2_with10);
        assertFalse(input2_with10_res.isEmpty());
        checkArgFiltersInOrder(input2_args10, input2_with10_res, false, new boolean[]{true, true});
        
        String input2_with11 = "Intersection2{[...], [...], ...}";
        List<Set<String>> input2_args11 = Arrays.stream(StringUtils.substringsBetween(input2_with11, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> input2_with11_res = searchModule.searchMethod(input2_with11);
        assertFalse(input2_with11_res.isEmpty());
        checkArgFiltersInOrder(input2_args11, input2_with11_res, true, new boolean[]{true, true});
        
        assertTrue(input2_with1_res.size() >= without2.size());
        assertTrue(input2_with2_res.size() >= without2.size());
        assertTrue(input2_with3_res.size() >= without2.size());
        assertTrue(input2_with4_res.size() >= without2.size());
        assertTrue(input2_with5_res.size() >= without2.size());
        assertTrue(input2_with6_res.size() >= without2.size());
        assertTrue(input2_with7_res.size() >= without2.size());
        assertTrue(input2_with8_res.size() >= without2.size());
        assertTrue(input2_with9_res.size() >= without2.size());
        assertTrue(input2_with10_res.size() >= without2.size());
        assertTrue(input2_with11_res.size() >= without2.size());

        assertTrue(input2_with3_res.size() >= input2_with1_res.size());
        assertTrue(input2_with3_res.size() >= input2_with2_res.size());
        assertTrue(input2_with5_res.size() >= input2_with1_res.size());
        assertTrue(input2_with6_res.size() >= input2_with2_res.size());
        
        assertTrue(input2_with7_res.size() >= input2_with3_res.size());
        assertTrue(input2_with7_res.size() >= input2_with4_res.size());
        assertTrue(input2_with7_res.size() >= input2_with5_res.size());
        assertTrue(input2_with7_res.size() >= input2_with6_res.size());
        
        assertTrue(input2_with8_res.size() >= input2_with7_res.size());
        assertTrue(input2_with9_res.size() >= input2_with7_res.size());
        
        assertTrue(input2_with10_res.size() >= input2_with8_res.size());
        assertTrue(input2_with10_res.size() >= input2_with9_res.size());
        assertTrue(input2_with11_res.size() >= input2_with10_res.size());
    }
    
    @Test
    @DisplayName("Test if searching with only subset symbol works as expected.")
    public void searchWithOnlySubsetSymbolTest() {
        
        String input1 = "...{[...]}";
        List<Set<String>> args1 = Arrays.stream(StringUtils.substringsBetween(input1, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());
        checkArgFiltersInOrder(args1, res1, false, new boolean[]{true});
        
        String input1_more_args = "...{[...], ...}";
        List<Method> res1_more_args = searchModule.searchMethod(input1_more_args);
        assertFalse(res1_more_args.isEmpty());
        checkArgFiltersInOrder(args1, res1_more_args, true, new boolean[]{true});
        assertTrue(res1_more_args.size() >= res1.size());
        // cross format for method searching check
        assertTrue(res1_more_args.size() == searchModule.searchMethod("...{...}").size());
        
        String input2 = "...{[...], [...]}";
        List<Set<String>> args2 = Arrays.stream(StringUtils.substringsBetween(input2, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());
        checkArgFiltersInOrder(args2, res2, false, new boolean[]{true, true});
        
        String input2_more_args = "...{[...], [...], ...}";
        List<Method> res2_more_args = searchModule.searchMethod(input2_more_args);
        assertFalse(res2_more_args.isEmpty());
        checkArgFiltersInOrder(args2, res2_more_args, true, new boolean[]{true, true});
        assertTrue(res2_more_args.size() >= res2.size());
        
        String input3 = "...{[...], [...], [...]}";
        List<Set<String>> args3 = Arrays.stream(StringUtils.substringsBetween(input3, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());
        checkArgFiltersInOrder(args3, res3, false, new boolean[]{true, true, true});
        
        String input3_more_args = "...{[...], [...], [...], ...}";
        List<Method> res3_more_args = searchModule.searchMethod(input3_more_args);
        assertFalse(res3_more_args.isEmpty());
        checkArgFiltersInOrder(args3, res3_more_args, true, new boolean[]{true, true, true});
        assertTrue(res3_more_args.size() >= res3.size());
        
        String input4 = "...{[...], [...], [...], [...]}";
        List<Set<String>> args4 = Arrays.stream(StringUtils.substringsBetween(input4, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> res4 = searchModule.searchMethod(input4);
        assertFalse(res4.isEmpty());
        checkArgFiltersInOrder(args4, res4, false, new boolean[]{true, true, true, true});
        
        String input4_more_args = "...{[...], [...], [...], [...], ...}";
        List<Method> res4_more_args = searchModule.searchMethod(input4_more_args);
        assertFalse(res4_more_args.isEmpty());
        checkArgFiltersInOrder(args4, res4_more_args, true, new boolean[]{true, true, true, true});
        assertTrue(res4_more_args.size() >= res4.size());
        
        String input5 = "...{[...], [...], [...], [...], [...]}";
        List<Set<String>> args5 = Arrays.stream(StringUtils.substringsBetween(input5, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> res5 = searchModule.searchMethod(input5);
        assertFalse(res5.isEmpty());
        checkArgFiltersInOrder(args5, res5, false, new boolean[]{true, true, true, true, true});
        
        String input5_more_args = "...{[...], [...], [...], [...], [...], ...}";
        List<Method> res5_more_args = searchModule.searchMethod(input5_more_args);
        assertFalse(res5_more_args.isEmpty());
        checkArgFiltersInOrder(args5, res5_more_args, true, new boolean[]{true, true, true, true, true});
        assertTrue(res5_more_args.size() >= res5.size());
        
        String input6 = "...{[...], [...], [...], [...], [...], [...]}";
        List<Set<String>> args6 = Arrays.stream(StringUtils.substringsBetween(input6, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> res6 = searchModule.searchMethod(input6);
        assertFalse(res6.isEmpty());
        checkArgFiltersInOrder(args6, res6, false, new boolean[]{true, true, true, true, true, true});
        
        String input6_more_args = "...{[...], [...], [...], [...], [...], [...], ...}";
        List<Method> res6_more_args = searchModule.searchMethod(input6_more_args);
        assertFalse(res6_more_args.isEmpty());
        checkArgFiltersInOrder(args6, res6_more_args, true, new boolean[]{true, true, true, true, true, true});
        assertTrue(res6_more_args.size() == res6.size());
        
        assertTrue(res1_more_args.size() >= res2_more_args.size());
        assertTrue(res2_more_args.size() >= res3_more_args.size());
        assertTrue(res3_more_args.size() >= res4_more_args.size());
        assertTrue(res4_more_args.size() >= res5_more_args.size());
        assertTrue(res5_more_args.size() >= res6_more_args.size());
    }
    
    private boolean checkArgFiltersInOrder(List<Set<String>> args, List<Method> res, boolean argNumSubsetFlag, boolean[] argFilterSubsetFlag) {
        
        for (Method m : res) {
            String[][] argFilters = m.getArgFilters();
            Set<String> argFilterSet;
            
            if (argNumSubsetFlag) {
                if (argFilters.length < args.size())
                    return false;
            } else {
                if (argFilters.length != args.size())
                    return false;
            }
            for (int i=0; i<args.size(); i++) {
                argFilterSet = Arrays.stream(argFilters[i]).collect(Collectors.toSet());
                if (argFilterSubsetFlag[i]) {
                    if (!argFilterSet.containsAll(args.get(i)))
                        return false;
                } else {
                    if (!argFilterSet.equals(args.get(i)))
                        return false;
                }
            }
        }
        return true;
    }

    @Test
    @DisplayName("Test if searching illegal input with a subset symbol returns the same result as searching with only subset symbol(s).")
    public void searchWithIllegalArgumentFiltersWithSubsetSymbolTest() {
        
        String input1 = "Order{[...]}";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());

        String input2 = "Order{[isExtLElement, isExtrelement, IsultiplicativeElement, IsmultiplicativeelementwithOne, ...]}";
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());

        String input3 = "Order{[isExtlelement, IsExtWESlement, nothingElement, isMultiplicativeElementWithone, ...]}";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());

        assertIterableEquals(res1, res2);
        assertIterableEquals(res1, res3);
        
        String input4 = "Intersection2{[...], [...]}";
        List<Method> res4 = searchModule.searchMethod(input4);
        assertFalse(res4.isEmpty());

        String input5 = "Intersection2{[isListOrCollection, isCollection, ...], [isListOrCollection, isCollection, ...]}";
        List<Method> res5 = searchModule.searchMethod(input5);
        assertFalse(res5.isEmpty());

        String input6 = "Intersection2{[islistOrCollection, iscollection, nothingFilter, ...], [islistOrCollection, Iscollection, ...]}";
        List<Method> res6 = searchModule.searchMethod(input6);
        assertFalse(res6.isEmpty());

        assertIterableEquals(res4, res5);
        assertIterableEquals(res4, res6);
    }
    
    @Test
    @DisplayName("Test if searching with ~ symbol in method name returns the expected results.")
    public void searchWithContainSymbolTest() {
        
        String input1 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());

        String input2 = "~Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}";
        List<Set<String>> args2 = Arrays.stream(StringUtils.substringsBetween(input2, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());
        res2.stream().map(Method::getName).forEach(n -> assertTrue(n.contains(StringUtils.substringBefore(input2, "{").substring(1))));
        assertTrue(checkArgFiltersInOrder(args2, res2, false, new boolean[]{true}));

        assertTrue(res2.size() >= res1.size());

        String input3 = "Intersection2{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection, ...]}";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());

        String input4 = "~Intersection2{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection, ...]}";
        List<Set<String>> args4 = Arrays.stream(StringUtils.substringsBetween(input4, "[", "]")).map(f -> Arrays.stream(f.split(",")).map(String::trim).filter(e -> !e.equals("...")).collect(Collectors.toSet())).collect(Collectors.toList());
        List<Method> res4 = searchModule.searchMethod(input4);
        assertFalse(res4.isEmpty());
        res4.stream().map(Method::getName).forEach(n -> assertTrue(n.contains(StringUtils.substringBefore(input4, "{").substring(1))));
        assertTrue(checkArgFiltersInOrder(args4, res4, false, new boolean[]{true, true}));

        assertTrue(res4.size() >= res3.size());
    }
    
    @Test
    @DisplayName("Test if searching with partial method names returns methods of names starting with the input.")
    public void searchWithPartialNameTest() {
        
        String input1 = "O{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());

        String input2 = "Ord{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}";
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());

        String input3 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());
        
        assertTrue(res1.size() >= res2.size());
        assertTrue(res2.size() >= res3.size());
        
        String input4 = "I{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection, ...]}";
        List<Method> res4 = searchModule.searchMethod(input4);
        assertFalse(res4.isEmpty());

        String input5 = "Inter{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection, ...]}";
        List<Method> res5 = searchModule.searchMethod(input5);
        assertFalse(res5.isEmpty());

        String input6 = "Intersection2{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection, ...]}";
        List<Method> res6 = searchModule.searchMethod(input6);
        assertFalse(res6.isEmpty());
        
        assertTrue(res4.size() >= res5.size());
        assertTrue(res5.size() >= res6.size());
    }

    @Test
    @DisplayName("Test if any unnecessary whitespaces in input are ignored for searching.")
    public void searchWithWhitespacesTest() {
        
        String standardInput1 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}";
        List<Method> standardRes1 = searchModule.searchMethod(standardInput1);
        assertFalse(standardRes1.isEmpty());

        String input1 = " Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}";
        List<Method> testRes1 = searchModule.searchMethod(input1);
        assertFalse(testRes1.isEmpty());
        
        String input2 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}  ";
        List<Method> testRes2 = searchModule.searchMethod(input2);
        assertFalse(testRes2.isEmpty());
        
        String input3 = "  Order {[IsExtLElement,  IsExtRElement , IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}  ";
        List<Method> testRes3 = searchModule.searchMethod(input3);
        assertFalse(testRes3.isEmpty());
        
        String input4 = " Order { [ IsExtLElement, IsExtRElement, IsMultiplicativeElement,   IsMultiplicativeElementWithOne,  ...  ]  }  ";
        List<Method> testRes4 = searchModule.searchMethod(input4);
        assertFalse(testRes4.isEmpty());

        assertIterableEquals(standardRes1, testRes1);
        assertIterableEquals(standardRes1, testRes2);
        assertIterableEquals(standardRes1, testRes3);
        assertIterableEquals(standardRes1, testRes4);
        
        String standardInput2 = "Intersection2{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection, ...]}";
        List<Method> standardRes2 = searchModule.searchMethod(standardInput2);
        assertFalse(standardRes2.isEmpty());

        String input5 = " Intersection2{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection, ...]}";
        List<Method> testRes5 = searchModule.searchMethod(input5);
        assertFalse(testRes5.isEmpty());
        
        String input6 = "Intersection2{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection, ...]}  ";
        List<Method> testRes6 = searchModule.searchMethod(input6);
        assertFalse(testRes6.isEmpty());
        
        String input7 = "  Intersection2{ [ IsListOrCollection,  IsCollection, ... ], [IsListOrCollection, IsCollection, ...]}  ";
        List<Method> testRes7 = searchModule.searchMethod(input7);
        assertFalse(testRes7.isEmpty());
        
        String input8 = " Intersection2 { [ IsListOrCollection, IsCollection, ...],  [ IsListOrCollection  , IsCollection  , ...  ]  }  ";
        List<Method> testRes8 = searchModule.searchMethod(input8);
        assertFalse(testRes8.isEmpty());

        assertIterableEquals(standardRes2, testRes5);
        assertIterableEquals(standardRes2, testRes6);
        assertIterableEquals(standardRes2, testRes7);
        assertIterableEquals(standardRes2, testRes8);
    }

    @Test
    @DisplayName("Test if searching equivalent inputs returns the same result.")
    /**
     * Equivalent inputs are the inputs that contain exactly the same method name and filters for each argument, but the filters are in different orders.
     */
    public void searchEquivalentInputsTest() {
        
        String input1 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}";
        String eqvl1 = "Order{[IsMultiplicativeElementWithOne, IsMultiplicativeElement, IsExtRElement, IsExtLElement, ...]}";
        List<Method> inputRes1 = searchModule.searchMethod(input1);
        assertFalse(inputRes1.isEmpty());
        List<Method> eqvlRes1 = searchModule.searchMethod(eqvl1);
        assertFalse(eqvlRes1.isEmpty());
        assertIterableEquals(inputRes1, eqvlRes1);

        String input2 = "Intersection2{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection, ...]}";
        String eqvl2 = "Intersection2{[IsCollection, ..., IsListOrCollection], [IsCollection, ..., IsListOrCollection]}";
        List<Method> inputRes2 = searchModule.searchMethod(input2);
        assertFalse(inputRes2.isEmpty());
        List<Method> eqvlRes2 = searchModule.searchMethod(eqvl2);
        assertFalse(eqvlRes2.isEmpty());
        assertIterableEquals(inputRes2, eqvlRes2);

        String input3 = "Matrix{"
                + "[IsList, IsListOrCollection], "
                + "[IsInt, IsRat, IsCyc, IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsZDFRE, IsAssociativeElement, IsAdditivelyCommutativeElement, IsCommutativeElement, IsCyclotomic], "
                + "[IsCopyable, IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsVecOrMatObj, IsMatrixOrMatrixObj]"
                + "}";
        String eqvl3 = "Matrix{"
                + "[IsListOrCollection, IsList], "
                + "[IsRat, IsCyc, IsNearAdditiveElement, IsInt, IsNearAdditiveElementWithInverse, IsExtAElement, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsNearAdditiveElementWithZero, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsZDFRE, IsAssociativeElement, IsAdditivelyCommutativeElement, IsCommutativeElement, IsCyclotomic], "
                + "[IsNearAdditiveElement, IsCopyable, IsExtAElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsVecOrMatObj, IsMatrixOrMatrixObj]"
                + "}";
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
        assertNull(searchModule.searchMethod("Order{[]}"));
        assertNull(searchModule.searchMethod("{[IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection]}"));
        
    }

    @Test
    @DisplayName("Test if searching with non-existent method names and/or filters returns null.")
    public void searchNonExistentMethodTest() {
        assertNull(searchModule.searchMethod("...{[...], [...], [...], [...], [...], [...], [...]}"));
        assertNull(searchModule.searchMethod("NullMethod{[abcdefg, hahaha], [nullfilter, ...]}"));
        assertNull(searchModule.searchMethod("Order{[BadNullFilter1, BadNullFilter2, BadNullFilter]}"));
        assertNull(searchModule.searchMethod("Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne], [NullFilter]}"));
        assertNull(searchModule.searchMethod("NullMethodName{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...], [...]}"));
    }

    @Test
    @DisplayName("Test if searching illegal input returns null.")
    /**
     * input is in unexpected letter case subset symbol not in expected form
     */
    public void searchIllegalInputTest() {
        
        String input1 = "Order{[IsExtLElement  IsExtRElement  IsMultiplicativeElement IsMultiplicativeElementWithOne]}";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertNull(res1);

        String input2 = "Order{[IsExtLElement! IsExtRElement! IsMultiplicativeElement. IsMultiplicativeElementWithOne]}";
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
        
        String input6 = "Order([IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne])";
        List<Method> res6 = searchModule.searchMethod(input6);
        assertNull(res6);
        
        String input7 = "order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne]}";
        List<Method> res7 = searchModule.searchMethod(input7);
        assertNull(res7);
        
        String input8 = "oRder{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne]}";
        List<Method> res8 = searchModule.searchMethod(input8);
        assertNull(res8);
        
        String input9 = "ORDER{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne]}";
        List<Method> res9 = searchModule.searchMethod(input9);
        assertNull(res9);
        
        String input10 = "Order{[isExtLElement, isExtRElement, isMultiplicativeElement, isMultiplicativeElementWithOne]}";
        List<Method> res10 = searchModule.searchMethod(input10);
        assertNull(res10);

        String input11 = "Order{[isextlelement, isextRElement, ismUltiplicativeElement, iSMultiplicativeElementWithOne]}";
        List<Method> res11 = searchModule.searchMethod(input11);
        assertNull(res11);
        
        String input12 = "Order~{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne]}";
        List<Method> res12 = searchModule.searchMethod(input12);
        assertNull(res12);
        
        String input13 = "~~Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne]}";
        List<Method> res13 = searchModule.searchMethod(input13);
        assertNull(res13);

        String input14 = "Order{[IsExtLElement, IsExtRElement]}, IsMultiplicativeElement, IsMultiplicativeElementWithOne";
        List<Method> res14 = searchModule.searchMethod(input14);
        assertNull(res14);
        
        String input15 = "Order{[IsExtLElement, IsExtRElement), IsMultiplicativeElement, IsMultiplicativeElementWithOne]}";
        List<Method> res15 = searchModule.searchMethod(input15);
        assertNull(res15);
        
        String input16 = "Order{[IsExtLElement%&, IsExtRElement[, IsMultiplicativeElement), IsMultiplicative%^&ElementWithOne]}";
        List<Method> res16 = searchModule.searchMethod(input16);
        assertNull(res16);
        
        String input17 = "Intersection2{[IsListOrCollection  IsCollection], [IsListOrCollection, IsCollection, ...]}";
        List<Method> res17 = searchModule.searchMethod(input17);
        assertNull(res17);
        
        String input18 = "Intersection2{[IsListOrCollection, IsCollection], [IsListOrCollection  IsCollection]}";
        List<Method> res18 = searchModule.searchMethod(input18);
        assertNull(res18);
        
        String input19 = "Intersection2{[IsListOrCollection & IsCollection], [IsListOrCollection & IsCollection]}";
        List<Method> res19 = searchModule.searchMethod(input19);
        assertNull(res19);
        
        String input20 = "Intersection2{(IsListOrCollection  IsCollection), [IsListOrCollection, IsCollection]}";
        List<Method> res20 = searchModule.searchMethod(input20);
        assertNull(res20);
        
        String input21 = "Intersection2[(IsListOrCollection  IsCollection], (IsListOrCollection,  IsCollection]]";
        List<Method> res21 = searchModule.searchMethod(input21);
        assertNull(res21);
        
        String input22 = "Intersection2([IsListOrCollection  IsCollection], [IsListOrCollection,  IsCollection])";
        List<Method> res22 = searchModule.searchMethod(input22);
        assertNull(res22);
        
        String input23 = "~ Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne]}";
        List<Method> res23 = searchModule.searchMethod(input23);
        assertNull(res23);
        
        String input24 = "~ Intersection2{[IsListOrCollection, IsCollection], [IsListOrCollection, IsCollection]}";
        List<Method> res24 = searchModule.searchMethod(input24);
        assertNull(res24);
    }

    @Test
    @DisplayName("Test if searching returns null when input partly includes nonexistent filters or is partly in illegal format")
    void searchInputIncludingPartlyNonExistentFiltersTest() {
        
        String standard1 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne]}";
        List<Method> std1 = searchModule.searchMethod(standard1);
        assertFalse(std1.isEmpty());
        String input1 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, NULLFILTER]}";
        List<Method> res1 = searchModule.searchMethod(input1);
        assertFalse(res1.isEmpty());
        String input2 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ....]}";
        List<Method> res2 = searchModule.searchMethod(input2);
        assertFalse(res2.isEmpty());
        String input3 = "Order{[IsExtLElement, !@$!@%#$#%, IsExtRElement, IsMultiplicativeElement, %^&*^%&*, IsMultiplicativeElementWithOne, .....]}";
        List<Method> res3 = searchModule.searchMethod(input3);
        assertFalse(res3.isEmpty());
        
        assertIterableEquals(std1, res1);
        assertIterableEquals(std1, res2);
        assertIterableEquals(std1, res3);

        String standard2 = "Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}";
        List<Method> std2 = searchModule.searchMethod(standard2);
        assertFalse(std2.isEmpty());
        assertTrue(std2.size() >= res3.size());

        String input4 = "Order{[~~()&(), IsExtLElement, IsExtRElement, !@#$#%, IsMultiplicativeElement, IsMultiplicativeElementWithOne, ...]}";
        List<Method> res4 = searchModule.searchMethod(input4);
        assertFalse(res4.isEmpty());

        assertIterableEquals(std2, res4);
        
        String standard3 = "Intersection2{[IsListOrCollection, IsCollection], [IsListOrCollection, IsCollection]}";
        List<Method> std3 = searchModule.searchMethod(standard3);
        assertFalse(std3.isEmpty());
        String input5 = "Intersection2{[IsListOrCollection, IsCollection, badFilter], [IsListOrCollection, null, IsCollection]}";
        List<Method> res5 = searchModule.searchMethod(input5);
        assertFalse(res5.isEmpty());
        String input6 = "Intersection2{[!@$!@%#$#%, IsListOrCollection, IsCollection], [IsListOrCollection, IsCollection, .. .]}";
        List<Method> res6 = searchModule.searchMethod(input6);
        assertFalse(res6.isEmpty());
        String input7 = "Intersection2{[IsListOrCollection, IsCollection, ....], [IsListOrCollection, . . ., IsCollection]}";
        List<Method> res7 = searchModule.searchMethod(input7);
        assertFalse(res7.isEmpty());
        
        String standard4 = "Intersection2{[IsListOrCollection, IsCollection, ...], [IsListOrCollection, IsCollection, ...]}";
        List<Method> std4 = searchModule.searchMethod(standard4);
        assertFalse(std4.isEmpty());
        assertTrue(std4.size() >= std3.size());
        
        String input8= "Intersection2{[~~()&(), IsListOrCollection, IsCollection, ...], [IsListOrCollection, ..., IsCollection, !@#$#%]}";
        List<Method> res8 = searchModule.searchMethod(input8);
        assertFalse(res8.isEmpty());
        
        assertIterableEquals(std4, res8);
    }
    
    @Test
    @DisplayName("Test if searching methods with name containing punctuation marks works as expected.")
    public void searchInputContainingPunctuationMarksTest() {
        
        List<Method> testResult1 = searchModule.searchMethod(".{[...], [...]}");
        assertFalse(testResult1.isEmpty());
        List<Method> testResult2 = searchModule.searchMethod(".:={[...], [...], [...]}");
        assertFalse(testResult2.isEmpty());

        List<Method> testResult3 = searchModule.searchMethod("[]{[...], [...]}");
        assertFalse(testResult3.isEmpty());
        List<Method> testResult4 = searchModule.searchMethod("[]:={[...], [...], [...]}");
        assertFalse(testResult4.isEmpty());

        List<Method> testResult5 = searchModule.searchMethod("{}{[...], [...]}");
        assertFalse(testResult5.isEmpty());
        List<Method> testResult6 = searchModule.searchMethod("{}:={[...], [...], [...]}");
        assertFalse(testResult6.isEmpty());

        List<Method> testResult7 = searchModule.searchMethod("^{[...], [...]}");
        assertFalse(testResult7.isEmpty());
        List<Method> testResult8 = searchModule.searchMethod("<{[...], [...]}");
        assertFalse(testResult8.isEmpty());

        List<Method> testResult9 = searchModule.searchMethod("/{[...], [...]}");
        assertFalse(testResult9.isEmpty());
        List<Method> testResult10 = searchModule.searchMethod("*{[...], [...]}");
        assertFalse(testResult10.isEmpty());
        List<Method> testResult11 = searchModule.searchMethod("+{[...], [...]}");
        assertFalse(testResult11.isEmpty());
        List<Method> testResult12 = searchModule.searchMethod("-{[...], [...]}");
        assertFalse(testResult12.isEmpty());
        List<Method> testResult13 = searchModule.searchMethod("={[...], [...]}");
        assertFalse(testResult13.isEmpty());

        List<Method> testResult14 = searchModule.searchMethod("_GapToJsonStreamInternal{[...], [...]}");
        assertFalse(testResult14.isEmpty());
        
        List<Method> testResult15 = searchModule.searchMethod("~.{[...], [...]}");
        assertFalse(testResult15.isEmpty());
        List<Method> testResult16 = searchModule.searchMethod("~.:={[...], [...], [...]}");
        assertFalse(testResult16.isEmpty());
        
        List<Method> testResult17 = searchModule.searchMethod("~{}{[...], [...]}");
        assertFalse(testResult17.isEmpty());
        List<Method> testResult18 = searchModule.searchMethod("~{}:={[...], [...], [...]}");
        assertFalse(testResult18.isEmpty());
        
        List<Method> testResult19 = searchModule.searchMethod("~^{[...], [...]}");
        assertFalse(testResult19.isEmpty());
        
        List<Method> testResult20 = searchModule.searchMethod("~*{[...], [...]}");
        assertFalse(testResult20.isEmpty());
    }
    
}
