package model;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.primitives.Booleans;

public class Model {

    public static final int STATE_IDLE = 0;
    public static final int SEARCH_OPERATION = 1;
    public static final int SEARCH_CATEGORY = 2;
    public static final int SEARCH_METHOD = 3;
    private static int state = STATE_IDLE;

    private static String gapRootDir;
    private static BufferedImage iconImage;

    private static final SetMultimap<String, Method> optnToMethodMap = LinkedHashMultimap.create();
    private static final SetMultimap<Set<String>, Method> ctgryToMethodMap = LinkedHashMultimap.create();

    private static final Set<String> ctgrySet = new HashSet<String>();
    private static final Set<Pair<String, Integer>> searchHistoySet = new LinkedHashSet<Pair<String, Integer>>();
    private static final Set<String> emptyCtgrySet = new HashSet<String>();

    private PropertyChangeSupport notifier;

    public Model() {
        notifier = new PropertyChangeSupport(this);
        try {
            iconImage = ImageIO.read(Model.class.getResource("/images/GAP_icon.png"));
        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     * Utility method to permit an observer to add themselves as an observer to the
     * model's change support object.
     * 
     * @param listener the listener to add
     */
    public void addObserver(PropertyChangeListener listener) {
        notifier.addPropertyChangeListener("illf", listener);
        notifier.addPropertyChangeListener("404", listener);
        notifier.addPropertyChangeListener("rtdir", listener);
        notifier.addPropertyChangeListener("illctg", listener);
        notifier.addPropertyChangeListener("success", listener);
    }

    public static int getModelState() {
        return state;
    }

    public static void setModelState(int num) {
        state = num;
    }

    public static String getGapRootDir() {
        return gapRootDir;
    }

    public static List<String> getAllOperationsInList(){
        return optnToMethodMap.keySet().stream().collect(Collectors.toList());
    }

    public static List<Method> getAllMethodsInList(){
        return ctgryToMethodMap.values().stream().collect(Collectors.toList());
    }

    public static List<String> getAllCategoriesInList(){
        return ctgrySet.stream().collect(Collectors.toList());
    }

    public static List<String> getNonDuplicateSearchHisotryInList() {
        return (searchHistoySet.stream().map(Pair::getKey).collect(Collectors.toSet()))
                .stream().collect(Collectors.toList());
    }

    public static Set<Pair<String, Integer>> getSearchHisotrySet() {
        return searchHistoySet;
    }

    public static BufferedImage getIconImage() {
        return iconImage;
    }
    

    /**
     * The argument file must start with the GAP rtdir as its first line, and the dumped content (dense) follows.
     * No other file format than JSON is permitted.
     * @param file
     * @return
     * @throws IOException
     */
    public void readFromJson(File file) throws IOException {
        
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        try {
            String rootDirPath = line.split(": ")[1];
            File rootDir = new File(rootDirPath);
            if (!rootDir.exists() || !rootDir.isDirectory()) {
                br.close();
                notifier.firePropertyChange("rtdir", null, rootDir.getCanonicalPath());
                return;
            }
            Model.gapRootDir = rootDirPath;
            while ((line = br.readLine()) != null) {
                readLineFromJson(line);
            }
            br.close();
            notifier.firePropertyChange("success", null, file.getCanonicalPath());
        } catch (IndexOutOfBoundsException | JSONException | NumberFormatException e) {
            br.close();
            notifier.firePropertyChange("illf", null, "Illegal content format in the JSON file loaded from:\n" + file.getCanonicalPath());
            return;
        }

    }

    private void readLineFromJson(String line) throws JSONException, NumberFormatException {
        
        JSONObject obj = new JSONObject(line);
        String optName;
        Set<String> fields = obj.keySet();
        if (fields.contains("opt_name")) {
            optName = obj.getString("opt_name");
        }
        else if (fields.contains("atr_name")) {
            optName = obj.getString("atr_name");
        }
        else if (fields.contains("cst_name")) {
            optName = obj.getString("cst_name");
        }
        else if (fields.contains("prp_name")) {
            optName = obj.getString("prp_name");
        }
        else if (fields.contains("set_name")) {
            optName = obj.getString("set_name");
        }
        else if (fields.contains("rep_name")) {
            optName = obj.getString("rep_name");
        }
        else if (fields.contains("flt_name")) {
            optName = obj.getString("flt_name");
        }
        else {
            throw new JSONException("");
        }

        JSONArray methods = obj.getJSONArray("methods");
        String rootDirPattern = Pattern.quote(gapRootDir);

        for (int i=0; i<methods.length(); i++) {
            JSONObject method = (JSONObject) methods.get(i);
            String mthdName = method.getString("mthd_name").trim();
            JSONObject property = (JSONObject) method.get("property");

            JSONArray JSONArgCtgry = property.getJSONArray("categories");
            int numArgs = JSONArgCtgry.length();
            String[][] argCtgrys = new String[numArgs][];
            for (int j=0; j<numArgs; j++) {
                String[] categories = JSONArrayToStringArray((JSONArray)JSONArgCtgry.get(j));
                argCtgrys[j] = categories;
                ctgrySet.addAll(Arrays.asList(categories));
            }

            // Handle unexpected fraction number for rank
            Object rank = property.get("rank");

            JSONObject src = (JSONObject) method.get("src");
            String filePath = src.getString("file_path").trim();
            filePath = filePath.replaceFirst(rootDirPattern, ".");

            int lineNumStart = src.getInt("line_num_start");
            int lineNumEnd = src.getInt("line_num_end");

            Method mthd = new Method(mthdName, argCtgrys, parseFractionToInteger(rank.toString()), filePath, lineNumStart, lineNumEnd);
            optnToMethodMap.put(optName.trim(), mthd);
            ctgryToMethodMap.put(Method.getUniqueCategories(argCtgrys), mthd);
        }
    }

    private int parseFractionToInteger(String str) throws NumberFormatException {
        
        if (StringUtils.countMatches(str, '/')==1) {
            String[] nums = str.split("/");
            float res = Float.valueOf(nums[0])/Float.valueOf(nums[1]);
            return Math.round(res);
        }
        else return Integer.valueOf(str);
    }

    private String[] JSONArrayToStringArray(JSONArray jarr) {
        
        String[] res = new String[jarr.length()];
        for(int i = 0; i < res.length; i++){
            res[i] = jarr.get(i).toString();
        }
        return res;
    }
    

    public List<Method> searchOpt(String toSearch) {

        Set<Method> matches = optnToMethodMap.get(toSearch.trim());
        if (matches.size()!=0)
            return matches.stream().collect(Collectors.toList());
        notifier.firePropertyChange("404", null, "Operation");
        return null;
    }

    public List<Method> searchCategory(String toSearch) {
        
        return searchCategoryHelper(toSearch, checkSubsetFlag(toSearch, false));
    }

    private List<Method> searchCategoryHelper(String input, boolean isSubsetFlag){
        
        String[] ctgrys = Arrays.stream(input.split(",")).map(String::trim).toArray(String[]::new);
        String last = ctgrys[ctgrys.length-1];
        if (ctgrys.length == 1) {
            if (isSubsetFlag) 
                return getAllMethodsInList();
            else if (last.equals("\\void")) {
                // search input '\void' to search methods that take no arguments.
                List<Method> res = (ctgryToMethodMap.get(emptyCtgrySet)).stream().collect(Collectors.toList());
                if (!res.isEmpty()) return res;
                notifier.firePropertyChange("404", null, "Methods under the specified category");
                return null;
            }
        }

        Set<String> ctgrysFound = new HashSet<String>();
        List<String> ctgrysNotFound = new ArrayList<String>();
        if (!findMatchingCategories(ctgrys, isSubsetFlag, ctgrysFound, ctgrysNotFound)) {
            notifier.firePropertyChange("404", null, "Entered categories all");
            return null;
        }
        displayCategoriesNotFound(ctgrysFound, ctgrysNotFound);

        return findMatchingMethodsFromCategories(ctgrysFound, isSubsetFlag);
    }

    /**
     * Method search input format 1: method_name(ctgry1, ctgry2, ...)
     * Method search input format 2: method_name([arg1_ctgry1, arg1_ctgry2, ...], [arg2_ctgry1, arg2_ctgry2, ...], ...)
     * @param toSearch
     * @return
     */
    public List<Method> searchMethodWithCategory(String toSearch) {

        String mthdName = StringUtils.substringBefore(toSearch, "(").trim();
        if (mthdName.equals("..."))
            return getAllMethodsInList();

        int indexFore = toSearch.indexOf('(');
        int indexBack= toSearch.lastIndexOf(')');
        if (mthdName.length()==0 || indexFore==-1 || indexBack==-1 || indexFore>indexBack) {
            notifier.firePropertyChange("illf", null, "Unsupported input format for searching the method " +toSearch+ "\n"
                    + "try: method_name(category1[, category2[, ...]])");
            return null;
        }
        String ctgrys = toSearch.substring(indexFore+1, indexBack).trim();

        List<Method> res;
        boolean isOfArgOrderFlag = checkArgOrderFlag(ctgrys);
        boolean isSubsetFlag = checkSubsetFlag(ctgrys, isOfArgOrderFlag);
        if (isOfArgOrderFlag) {
            return searchCategoryOfArgumentOrder(mthdName, ctgrys, isSubsetFlag);
        }
        else {
            List<Method> ml = searchCategoryHelper(ctgrys, isSubsetFlag);
            if (ml == null) return null;

            res = ml.stream().filter(mthd -> mthd.getName().contains(mthdName)).collect(Collectors.toList());
            if (!res.isEmpty()) return res;
            notifier.firePropertyChange("404", null, "Method with specified categories");
            return null;
        }
    }

    private List<Method> searchCategoryOfArgumentOrder(String mthdName, String ctgrysToSearch, boolean isArgNumSubsetFlag) {

        String[] argCtgrys = StringUtils.substringsBetween(ctgrysToSearch, "[", "]");
        int argNum = argCtgrys.length;
        List<Set<String>> ctgrysOfArg = new ArrayList<Set<String>>();

        Set<String> ctgrysFound = new HashSet<String>();
        List<String> ctgrysNotFound = new ArrayList<String>();
        boolean[] isSubsetFlags = new boolean[argNum];
        for (int i=0; i<argCtgrys.length; i++) {
            isSubsetFlags[i] = checkSubsetFlag(argCtgrys[i], false);
            String[] ctgrys = Arrays.stream(argCtgrys[i].split(",")).map(String::trim).toArray(String[]::new);
            if (isSubsetFlags[i] && ctgrys.length==1) {
                // if [,,,] is specified for an argument, then it will be treated as argument of any category is applicable.
                ctgrysOfArg.add(emptyCtgrySet);
                continue;
            }
            if (isSubsetFlags[i])
                ctgrysOfArg.add(Arrays.asList(ctgrys).subList(0, ctgrys.length-1).stream().collect(Collectors.toSet()));
            else {
                ctgrysOfArg.add(Arrays.stream(ctgrys).collect(Collectors.toSet()));
            }
            if (!findMatchingCategories(ctgrys, isSubsetFlags[i], ctgrysFound, ctgrysNotFound)) {
                notifier.firePropertyChange("404", null, "Entered categories at arg "+ i+1 +" all");
                return null;
            }
        }
        displayCategoriesNotFound(ctgrysFound, ctgrysNotFound);

        List<Method> methodsMatchedCtgrys;
        List<Method> methodsMatchedCtgrysAndNames = new ArrayList<Method>();
        boolean isArgCtgrySubsetFlag = Booleans.contains(isSubsetFlags, true);

        methodsMatchedCtgrys = findMatchingMethodsFromCategories(ctgrysFound, (isArgNumSubsetFlag || isArgCtgrySubsetFlag));
        if (methodsMatchedCtgrys==null) return null;
        for (Method mthd : methodsMatchedCtgrys) {
            if (mthd.getName().contains(mthdName)) {
                if (mthd.getArgNumber()==argNum) {
                    methodsMatchedCtgrysAndNames.add(mthd);
                }
                else if (isArgNumSubsetFlag && mthd.getArgNumber()>argNum)
                    methodsMatchedCtgrysAndNames.add(mthd);
            }
        }
        List<Method> res = new ArrayList<Method>();
        boolean isArgNameAndOrderMatch;
        if(!methodsMatchedCtgrysAndNames.isEmpty()) {
            for (Method mthd : methodsMatchedCtgrysAndNames) {
                isArgNameAndOrderMatch = true;
                String [][] mthdArgCategories = mthd.getArgCategories();
                for (int i=0; i<argNum; i++) {
                    Set<String> argCtgrySet = Arrays.stream(mthdArgCategories[i]).collect(Collectors.toSet());
                    if (isSubsetFlags[i]) {
                        if (!argCtgrySet.containsAll(ctgrysOfArg.get(i))) {
                            isArgNameAndOrderMatch = false;
                            break;
                        }
                    }
                    else {
                        if (!argCtgrySet.equals(ctgrysOfArg.get(i))) {
                            isArgNameAndOrderMatch = false;
                            break;
                        }
                    }
                }
                if (isArgNameAndOrderMatch) 
                    res.add(mthd);
            }
            if (!res.isEmpty()) return res;
        }
        notifier.firePropertyChange("404", null, "Method with specified categories");
        return null;
    }

    private boolean checkArgOrderFlag(String input) {

        int index = input.indexOf('[');
        if (index == -1) return false;
        return (index < input.indexOf(']'));
    }


    private boolean checkSubsetFlag(String input, boolean isOfArgOrderFlag) {  
        
        if (isOfArgOrderFlag) {
            String lastPart = input.substring(input.lastIndexOf(']')+1);
            int index = lastPart.indexOf(',');
            if (index == -1) 
                return false;
            return lastPart.substring(index+1).trim().equals("...");
        }
        else {
            int lastIndex = input.lastIndexOf(',');
            if (lastIndex == -1) {
                return input.trim().equals("...");
            }
            return input.substring(lastIndex+1).trim().equals("...");
        }
    }

    private boolean findMatchingCategories(String[] ctgrys, boolean isSubsetFlag, Set<String> ctgrysFound, List<String> ctgrysNotFound) {
        
        for (int i=0; i<ctgrys.length; i++) {
            if (isSubsetFlag && i==ctgrys.length-1) break;
            if (ctgrySet.contains(ctgrys[i])) {
                ctgrysFound.add(ctgrys[i]);
            } 
            else {
                ctgrysNotFound.add(ctgrys[i]);
            }
        }
        return !ctgrysFound.isEmpty();
    }

    private void displayCategoriesNotFound(Set<String> ctgrysFound, List<String> ctgrysNotFound) {
        
        if (!ctgrysNotFound.isEmpty()) {
            String illInputs = ctgrysNotFound.stream().collect(Collectors.joining(", "));
            String validInputs = ctgrysFound.stream().collect(Collectors.joining(", "));
            String message = "In the entered categories: [" + illInputs + "] not found.\n"
                    + "Only searching for categories: [" + validInputs + "].";
            notifier.firePropertyChange("illctg", null, message);
        }
    }

    private List<Method> findMatchingMethodsFromCategories(Set<String> ctgrysFound, boolean isSubsetFlag) {
        
        List<Method> res;
        if (isSubsetFlag) {
            res = new ArrayList<Method>();
            List<Set<String>> matches = ctgryToMethodMap.keySet().stream()
                    .filter(c -> c.containsAll(ctgrysFound))
                    .collect(Collectors.toList());
            matches.forEach(k -> ctgryToMethodMap.get(k).forEach(e -> res.add(e)));
        }
        else {
            res = ctgryToMethodMap.get(ctgrysFound).stream().collect(Collectors.toList());
        }

        if (!res.isEmpty())
            return res;
        notifier.firePropertyChange("404", null, "Methods under the specified category");
        return null;
    }
}
