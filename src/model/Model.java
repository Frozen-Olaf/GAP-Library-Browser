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
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.primitives.Booleans;

import net.coobird.thumbnailator.Thumbnails;

public class Model {

    public static final int STATE_IDLE = 0;
    public static final int STATE_SEARCH_OPERATION = 1;
    public static final int STATE_SEARCH_FILTER = 2;
    public static final int STATE_SEARCH_METHOD = 3;
    private static int state = STATE_IDLE;

    private static String GAP_ROOT_DIRECTORY;
    private static final int ICON_SIZE = 16;
    private static final BufferedImage gapIconImage = createIconImage("/images/GAP-icon.png");
    private static final ImageIcon leftArrowIcon = createIcon(ICON_SIZE,ICON_SIZE,"/images/left-arrow.png");
    private static final ImageIcon rightArrowIcon = createIcon(ICON_SIZE,ICON_SIZE,"/images/right-arrow.png");

    private static final SetMultimap<String, Method> optnToMethodMap = LinkedHashMultimap.create();
    private static final SetMultimap<Set<String>, Method> filterToMethodMap = LinkedHashMultimap.create();

    private static final Set<String> filterSet = new HashSet<String>();
    private static final Set<Pair<String, Integer>> searchHistoySet = new LinkedHashSet<Pair<String, Integer>>();
    private static final Set<String> emptyFilterSet = new HashSet<String>();
    
    private static List<String> sortedOperationList;
    private static List<Method> sortedMethodList;
    private static List<String> sortedFilterList;

    private PropertyChangeSupport notifier;

    public Model() {
        notifier = new PropertyChangeSupport(this);
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
        notifier.addPropertyChangeListener("illflt", listener);
        notifier.addPropertyChangeListener("success", listener);
    }

    public static int getModelState() {
        return state;
    }

    public static void setModelState(int num) {
        state = num;
    }

    public static String getGapRootDir() {
        return GAP_ROOT_DIRECTORY;
    }

    public static List<String> getAllOperationsInList(){
        return sortedOperationList;
    }

    public static List<Method> getAllMethodsInList(){
        return sortedMethodList;
    }

    public static List<String> getAllFiltersInList(){
        return sortedFilterList;
    }

    public static List<String> getNonDuplicateSearchHisotryInList(int modelState) {
        return searchHistoySet.stream().filter(sh -> sh.getValue() == modelState)
                .map(Pair::getKey).collect(Collectors.toList());
    }

    public static Set<Pair<String, Integer>> getSearchHisotryInSet() {
        return searchHistoySet;
    }
    
    public static BufferedImage getGAPIconImage() {
        return gapIconImage;
    }

    public static ImageIcon getLeftArrowIcon() {
        return leftArrowIcon;
    }
    
    public static ImageIcon getRightArrowIcon() {
        return rightArrowIcon;
    }
    
    private static BufferedImage createIconImage(String path) {
        try {
            return ImageIO.read(Model.class.getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ImageIcon createGAPIcon(int width, int height) {
        try {
            BufferedImage thumbnail = Thumbnails.of(gapIconImage)
                    .size(width, height)
                    .keepAspectRatio(true)
                    .asBufferedImage();
            return new ImageIcon(thumbnail);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final ImageIcon createIcon(int width, int height, String path) {
        try {
            BufferedImage original = ImageIO.read(Model.class.getResource(path));
            BufferedImage thumbnail = Thumbnails.of(original)
                    .size(width, height)
                    .keepAspectRatio(true)
                    .asBufferedImage();
            return new ImageIcon(thumbnail);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            GAP_ROOT_DIRECTORY = rootDirPath;
            while ((line = br.readLine()) != null) {
                readLineFromJson(line);
            }
            br.close();
            notifier.firePropertyChange("success", null, file.getCanonicalPath());
            sortedOperationList = optnToMethodMap.keySet().stream().sorted().collect(Collectors.toList());
            sortedMethodList = filterToMethodMap.values().stream().sorted(Comparator.comparing(Method::getName)).collect(Collectors.toList());
            sortedFilterList = filterSet.stream().sorted().collect(Collectors.toList());
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
        String rootDirPattern = Pattern.quote(GAP_ROOT_DIRECTORY);

        for (int i=0; i<methods.length(); i++) {
            JSONObject method = (JSONObject) methods.get(i);
            String mthdName = method.getString("mthd_name").trim();
            JSONObject property = (JSONObject) method.get("property");

            JSONArray JSONArgFilter = property.getJSONArray("filters");
            int numArgs = JSONArgFilter.length();
            String[][] argFilters = new String[numArgs][];
            for (int j=0; j<numArgs; j++) {
                String[] filters = JSONArrayToStringArray((JSONArray)JSONArgFilter.get(j));
                argFilters[j] = filters;
                filterSet.addAll(Arrays.asList(filters));
            }

            // Handle unexpected fraction number for rank
            Object rank = property.get("rank");

            JSONObject src = (JSONObject) method.get("src");
            String filePath = src.getString("file_path").trim();
            filePath = filePath.replaceFirst(rootDirPattern, "./");

            int lineNumStart = src.getInt("line_num_start");
            int lineNumEnd = src.getInt("line_num_end");

            Method mthd = new Method(mthdName, argFilters, parseFractionToInteger(rank.toString()), filePath, lineNumStart, lineNumEnd);
            optnToMethodMap.put(optName.trim(), mthd);
            filterToMethodMap.put(Method.getUniqueFilters(argFilters), mthd);
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

    public List<Method> searchFilter(String toSearch) {
        return searchByFilter(toSearch, checkSubsetFlag(toSearch, false));
    }

    private List<Method> searchByFilter(String input, boolean isSubsetFlag){
        String[] filters = Arrays.stream(input.split(",")).map(String::trim).toArray(String[]::new);
        String last = filters[filters.length-1];
        if (filters.length == 1) {
            if (isSubsetFlag) 
                return getAllMethodsInList();
            else if (last.equals("\\void")) {
                // search input '\void' to search methods that take no arguments.
                List<Method> res = (filterToMethodMap.get(emptyFilterSet)).stream().collect(Collectors.toList());
                if (!res.isEmpty()) return res;
                notifier.firePropertyChange("404", null, "Methods under the specified filter");
                return null;
            }
        }

        Set<String> filtersFound = new HashSet<String>();
        List<String> filtersNotFound = new ArrayList<String>();
        if (!findMatchingFilters(filters, isSubsetFlag, filtersFound, filtersNotFound)) {
            notifier.firePropertyChange("404", null, "Entered filters all");
            return null;
        }
        displayFiltersNotFound(filtersFound, filtersNotFound);

        return findMatchingMethodsFromFilters(filtersFound, isSubsetFlag);
    }

    /**
     * Method search input format 1: method_name(filter1, filter2, ...)
     * Method search input format 2: method_name([arg1_filter1, arg1_filter2, ...], [arg2_filter1, arg2_filter2, ...], ...)
     * @param toSearch
     * @return
     */
    public List<Method> searchMethodWithFilter(String toSearch) {
        String temp = StringUtils.substringBefore(toSearch, "(").trim();
        boolean anyMethodNameFlag = temp.equals("...");
        boolean methodNamePrefixFlag = true;
        if (temp.startsWith("~")) {
            methodNamePrefixFlag = false;
            temp = temp.substring(1);
        }
        String mthdName = temp;
        
        int indexFore = toSearch.indexOf('(');
        int indexBack= toSearch.lastIndexOf(')');
        if (mthdName.length()==0 || indexFore==-1 || indexBack==-1 || indexFore>indexBack) {
            notifier.firePropertyChange("illf", null, "Unsupported input format for searching the method " +toSearch+ "\n"
                    + "try: method_name(filter1[, filter2[, ...]])");
            return null;
        }
        String filters = toSearch.substring(indexFore+1, indexBack).trim();

        List<Method> res;
        boolean isOfArgOrderFlag = checkArgOrderFlag(filters);
        boolean isSubsetFlag = checkSubsetFlag(filters, isOfArgOrderFlag);
        if (isOfArgOrderFlag) {
            return searchFilterOfArgumentOrder(mthdName, filters, anyMethodNameFlag, methodNamePrefixFlag, isSubsetFlag);
        }
        else {
            List<Method> ml = searchByFilter(filters, isSubsetFlag);
            if (ml == null) return null;
            
            if (anyMethodNameFlag) {
                if (!ml.isEmpty())
                    return ml;
            } 
            else {
                if (methodNamePrefixFlag) {
                    res = ml.stream().filter(mthd -> mthd.getName().startsWith(mthdName)).collect(Collectors.toList());
                } 
                else {
                    res = ml.stream().filter(mthd -> mthd.getName().contains(mthdName)).collect(Collectors.toList());
                }
                if (!res.isEmpty()) return res;
            }
            notifier.firePropertyChange("404", null, "Method with specified filters");
            return null;
        }
    }

    private List<Method> searchFilterOfArgumentOrder(String mthdName, String filtersToSearch, boolean anyMethodNameFlag, boolean methodNamePrefixFlag, boolean isArgNumSubsetFlag) {

        String[] argFilters = StringUtils.substringsBetween(filtersToSearch, "[", "]");
        int argNum = argFilters.length;
        List<Set<String>> filtersOfArg = new ArrayList<Set<String>>();

        Set<String> filtersFound = new HashSet<String>();
        List<String> filtersNotFound = new ArrayList<String>();
        boolean[] isSubsetFlags = new boolean[argNum];
        for (int i=0; i<argFilters.length; i++) {
            isSubsetFlags[i] = checkSubsetFlag(argFilters[i], false);
            String[] filters = Arrays.stream(argFilters[i].split(",")).map(String::trim).toArray(String[]::new);
            if (isSubsetFlags[i] && filters.length==1) {
                // if [,,,] is specified for an argument, then it will be treated as argument of any filter is applicable.
                filtersOfArg.add(emptyFilterSet);
                continue;
            }
            if (isSubsetFlags[i])
                filtersOfArg.add(Arrays.asList(filters).subList(0, filters.length-1).stream().collect(Collectors.toSet()));
            else {
                filtersOfArg.add(Arrays.stream(filters).collect(Collectors.toSet()));
            }
            if (!findMatchingFilters(filters, isSubsetFlags[i], filtersFound, filtersNotFound)) {
                notifier.firePropertyChange("404", null, "Entered filters at arg "+ i+1 +" all");
                return null;
            }
        }
        displayFiltersNotFound(filtersFound, filtersNotFound);

        List<Method> methodsMatchedFilters;
        List<Method> methodsMatchedFiltersAndNames;
        boolean isArgCtgrySubsetFlag = Booleans.contains(isSubsetFlags, true);

        methodsMatchedFilters = findMatchingMethodsFromFilters(filtersFound, (isArgNumSubsetFlag || isArgCtgrySubsetFlag));
        if (methodsMatchedFilters == null) return null;

        if (!anyMethodNameFlag) {
            methodsMatchedFiltersAndNames = new ArrayList<Method>();
            for (Method mthd : methodsMatchedFilters) {
                if (methodNamePrefixFlag) {
                    if (mthd.getName().startsWith(mthdName)) {
                        if (mthd.getArgNumber() == argNum) {
                            methodsMatchedFiltersAndNames.add(mthd);
                        }
                        else if (isArgNumSubsetFlag && mthd.getArgNumber()>argNum)
                            methodsMatchedFiltersAndNames.add(mthd);
                    }
                } 
                else {
                    if (mthd.getName().contains(mthdName)) {
                        if (mthd.getArgNumber() == argNum) {
                            methodsMatchedFiltersAndNames.add(mthd);
                        }
                        else if (isArgNumSubsetFlag && mthd.getArgNumber()>argNum)
                            methodsMatchedFiltersAndNames.add(mthd);
                    }
                }
            }
        }
        else {
            methodsMatchedFiltersAndNames = methodsMatchedFilters;
        }
        List<Method> res = new ArrayList<Method>();
        boolean isArgNameAndOrderMatch;
        if(!methodsMatchedFiltersAndNames.isEmpty()) {
            for (Method mthd : methodsMatchedFiltersAndNames) {
                isArgNameAndOrderMatch = true;
                String [][] mthdArgFilters = mthd.getArgFilters();
                for (int i=0; i<argNum; i++) {
                    Set<String> argCtgrySet = Arrays.stream(mthdArgFilters[i]).collect(Collectors.toSet());
                    if (isSubsetFlags[i]) {
                        if (!argCtgrySet.containsAll(filtersOfArg.get(i))) {
                            isArgNameAndOrderMatch = false;
                            break;
                        }
                    }
                    else {
                        if (!argCtgrySet.equals(filtersOfArg.get(i))) {
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
        notifier.firePropertyChange("404", null, "Method with specified filters");
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

    private boolean findMatchingFilters(String[] filters, boolean isSubsetFlag, Set<String> filtersFound, List<String> filtersNotFound) {
        for (int i=0; i<filters.length; i++) {
            if (isSubsetFlag && i==filters.length-1) break;
            if (filterSet.contains(filters[i])) {
                filtersFound.add(filters[i]);
            } 
            else {
                filtersNotFound.add(filters[i]);
            }
        }
        return !filtersFound.isEmpty();
    }

    private void displayFiltersNotFound(Set<String> filtersFound, List<String> filtersNotFound) {
        if (!filtersNotFound.isEmpty()) {
            String illInputs = filtersNotFound.stream().collect(Collectors.joining(", "));
            String validInputs = filtersFound.stream().collect(Collectors.joining(", "));
            String message = "In the entered filters: [" + illInputs + "] not found.\n"
                    + "Only searching for filters: [" + validInputs + "].";
            notifier.firePropertyChange("illflt", null, message);
        }
    }

    private List<Method> findMatchingMethodsFromFilters(Set<String> filtersFound, boolean isSubsetFlag) {
        List<Method> res;
        if (isSubsetFlag) {
            res = new ArrayList<Method>();
            List<Set<String>> matches = filterToMethodMap.keySet().stream()
                    .filter(c -> c.containsAll(filtersFound))
                    .collect(Collectors.toList());
            matches.forEach(k -> filterToMethodMap.get(k).forEach(e -> res.add(e)));
        }
        else {
            res = filterToMethodMap.get(filtersFound).stream().collect(Collectors.toList());
        }

        if (!res.isEmpty())
            return res;
        notifier.firePropertyChange("404", null, "Methods under the specified filter");
        return null;
    }
}
