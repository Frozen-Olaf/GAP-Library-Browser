package search;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.search.SearchModule;

@DisplayName("JUnit 5 Test")
class SearchHistoryTest extends BaseTest {

    @Test
    @DisplayName("Test if search history from searching operations works as expected.")
    public void operationSearchHistoryTest() throws IOException {

        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "Order");
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "Size");
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "Intersection2");
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "+");
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "SetSize");
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "Union2");
        
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "Order"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "Size"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "Intersection2"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "+"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "SetSize"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "Union2"));

        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "SetOrder"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_OPERATION, "Intersection10"));

    }

    @Test
    @DisplayName("Test if search history from searching filters works as expected.")
    public void filterSearchHistoryTest() throws IOException {

        String input1 = "IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne";
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_FILTER, input1);
        
        String input2 = "IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection";
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_FILTER, input2);
        
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_FILTER, input1));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_FILTER, input2));

        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_FILTER, "IsMultiplicativeElementWithOne, IsExtRElement, IsMultiplicativeElement, IsExtLElement"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_FILTER, "IsExtRElement, IsMultiplicativeElementWithOne, IsExtLElement, IsMultiplicativeElement"));

        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_FILTER, "IsDenseList, IsList, IsCollection, IsListOrCollection, IsHomogeneousList"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_FILTER, "IsCollection, IsListOrCollection, IsList, IsDenseList, IsHomogeneousList"));
        
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_FILTER, "IsExtLElement, IsExtRElement, IsMultiplicativeElement"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_FILTER, "IsList, IsDenseList, IsHomogeneousList"));

    }

    @Test
    @DisplayName("Test if search history from searching methods in input format 1 works as expected.")
    public void methodFormat1SearchHistoryTest() throws IOException {
        
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Order");
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Size");
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Intersection2");
        
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Order"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Size"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Intersection2"));

        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "SetOrder"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Union2"));

    }

    @Test
    @DisplayName("Test if search history from searching methods in input format 2 works as expected.")
    public void methodFormat2SearchHistoryTest() throws IOException {

        String input1 = "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsObject}";
        String input2 = "~Size{IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, IsObject}";
        String input3 = "+{IsListOrCollection, IsCollection, IsDuplicateFree, HasIsDuplicateFree, IsGeneralizedDomain, ...}";
        
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_METHOD, input1);
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_METHOD, input2);
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_METHOD, input3);
        
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, input1));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, input2));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, input3));
        
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Order{IsMultiplicativeElement, IsExtRElement, IsExtLElement, IsMultiplicativeElementWithOne, IsObject}"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Order{IsMultiplicativeElement, IsExtRElement, IsExtLElement, IsMultiplicativeElementWithOne, IsObject}"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "~Size{IsHomogeneousList, IsDenseList, IsObject, IsListOrCollection, IsList, IsCollection}"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "~Size{IsListOrCollection, IsList, IsDenseList, IsHomogeneousList, IsCollection, IsObject}"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "+{IsCollection, IsListOrCollection, IsDuplicateFree, HasIsDuplicateFree, IsGeneralizedDomain, ...}"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "+{IsListOrCollection, IsDuplicateFree, HasIsDuplicateFree, IsCollection, IsGeneralizedDomain, ...}"));
        
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Order{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Ord{IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsObject}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "~Size{IsList, IsDenseList, IsListOrCollection, IsCollection}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Size{IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, IsObject}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "~Size(IsList, IsDenseList, IsHomogeneousList, IsListOrCollection, IsCollection, IsObject)"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "+{IsListOrCollection, IsCollection, IsDuplicateFree, HasIsDuplicateFree, ...}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "-{IsListOrCollection, IsCollection, IsDuplicateFree, HasIsDuplicateFree, IsGeneralizedDomain, ...}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "+{IsListOrCollection, IsCollection, IsDuplicateFree, HasIsDuplicateFree, IsGeneralizedDomain}"));

    }
    
    @Test
    @DisplayName("Test if search history from searching methods in input format 3 works as expected.")
    public void methodFormat3SearchHistoryTest() throws IOException {
        
        String input1 = "~Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsObject], ...}";
        String input2 = "Intersection2{[IsListOrCollection, IsCollection, IsObject, ...], [IsListOrCollection, IsCollection, IsObject, ...]}";
        String input3 = "Matrix{"
                + "[IsList, IsListOrCollection, IsObject], "
                + "[IsInt, IsRat, IsCyc, IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsZDFRE, IsAssociativeElement, IsAdditivelyCommutativeElement, IsCommutativeElement, IsCyclotomic, IsObject], "
                + "[IsCopyable, IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsVecOrMatObj, IsMatrixOrMatrixObj, IsObject]"
                + "}";
        
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_METHOD, input1);
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_METHOD, input2);
        searchModule.addSearchHistory(SearchModule.STATE_SEARCH_METHOD, input3);
        
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, input1));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, input2));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, input3));
        
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, " ~Order { [IsMultiplicativeElementWithOne,  IsObject, IsMultiplicativeElement,  IsExtRElement, IsExtLElement ] , ... } "));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, " ~Order { [ IsExtRElement,IsMultiplicativeElement,IsExtLElement,IsMultiplicativeElementWithOne,IsObject ] , ... } "));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Intersection2  {  [IsCollection, IsObject, IsListOrCollection, ...], [IsObject,  ..., IsListOrCollection,  IsCollection]}"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Intersection2{  [IsListOrCollection, IsCollection, IsObject, ... ],   [IsListOrCollection, ..., IsCollection, IsObject  ]  }"));
        assertNotNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Matrix{"
                + "[IsList, IsObject, IsListOrCollection], "
                + "[IsExtAElement, IsInt, IsCyc, IsNearAdditiveElementWithZero, IsRat, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsNearAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsZDFRE, IsAssociativeElement, IsAdditivelyCommutativeElement, IsCommutativeElement, IsCyclotomic, IsObject], "
                + "[IsNearAdditiveElement, IsExtAElement, IsNearAdditiveElementWithZero, IsCopyable, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsVecOrMatObj, IsMatrixOrMatrixObj, IsObject]"
                + "}"));
        
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "~Order{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne], ...}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Ord{[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsObject], ...}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Order{[IsMultiplicativeElementWithOne, IsObject, IsMultiplicativeElement, IsExtRElement, IsExtLElement], ...}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "~  Order{[IsMultiplicativeElementWithOne, IsObject, IsMultiplicativeElement, IsExtRElement, IsExtLElement], ...}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "~Order{[IsMultiplicativeElementWithOne, IsObject, IsMultiplicativeElement, IsExtRElement, IsExtLElement]}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Intersection2{[IsListOrCollection, *#@$#@$, IsCollection, ...], [IsListOrCollection, HasSize, IsObject, ...]}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Intersection3{[IsListOrCollection, IsCollection, IsObject, ...], [IsListOrCollection, IsCollection, IsObject, ...]}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Matrix{"
                + "[IsInt, IsRat, IsCyc, IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsZDFRE, IsAssociativeElement, IsAdditivelyCommutativeElement, IsCommutativeElement, IsCyclotomic, IsObject], "
                + "[IsCopyable, IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsVecOrMatObj, IsMatrixOrMatrixObj, IsObject]"
                + "}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Matrix{"
                + "[IsList  IsListOrCollection  IsObject], "
                + "[IsInt, IsRat, IsCyc, IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsZDFRE, IsAssociativeElement, IsAdditivelyCommutativeElement, IsCommutativeElement, IsCyclotomic, IsObject], "
                + "[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsVecOrMatObj, IsMatrixOrMatrixObj, IsObject]"
                + "}"));
        assertNull(searchModule.hasEquivalentSearchHistory(SearchModule.STATE_SEARCH_METHOD, "Matrix{"
                + "[IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsVecOrMatObj, IsMatrixOrMatrixObj, IsObject], "
                + "[IsInt, IsRat, IsCyc, IsExtAElement, IsNearAdditiveElement, IsNearAdditiveElementWithZero, IsNearAdditiveElementWithInverse, IsAdditiveElement, IsExtLElement, IsExtRElement, IsMultiplicativeElement, IsMultiplicativeElementWithOne, IsMultiplicativeElementWithInverse, IsZDFRE, IsAssociativeElement, IsAdditivelyCommutativeElement, IsCommutativeElement, IsCyclotomic, IsObject], "
                + "[IsList  IsListOrCollection  IsObject]"
                + "}"));

    }

}
