//package search;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.net.URL;
//
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import model.Model;
//
//@DisplayName("JUnit 5 Test")
//class SearchMethodTest {
//    
//    private static final Model model = new Model();
//
//    @BeforeAll
//    static void beforeAll() {
//        try {
//            URL url = SearchMethodTest.class.getResource("/dumps/dump-25-Dec-2022-02-06-43.json");
//            if (url == null) {
//                System.out.println("The json file for testing is not found!");
//                return;
//            }
//            model.readFromJson(new File(url.toURI()));
//        } catch (IOException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//        System.out.println("The json file for testing is loaded successfully!");
//    }
//
//    @BeforeEach
//    void beforeEach() {}
//
//    @AfterEach
//    void afterEach() {}
//
//    @AfterAll
//    static void afterAll() {}
//
//
//    @Test
//    void test() {
//        fail("Not yet implemented");
//    }
//
//}
