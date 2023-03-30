package model.data;

import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;

public class ModelData {
    private static String GAP_ROOT_DIRECTORY;

    public static final Set<String> emptyFilterSet = ImmutableSet.of();
    public static final Method emptyMethod = new Method(null, new String[0][0], null, null, null, null);

    private final SetMultimap<String, Method> optnToMethodMap = LinkedHashMultimap.create();
    private final SetMultimap<Set<String>, Method> filterToMethodMap = LinkedHashMultimap.create();
    private final Map<String, String> optnTypeMap = new HashMap<String, String>();

    private final Set<String> filterSet = new HashSet<String>();

    // Handle searches of duplicate input, having support in different categories
    // for searching.
    private final SetMultimap<Integer, String> searchHistoyMap = LinkedHashMultimap.create();

    private List<String> sortedOperationList;
    private List<Method> sortedMethodList;
    private List<String> sortedFilterList;

    private final PropertyChangeSupport notifier;

    public ModelData(PropertyChangeSupport notifier) {
        this.notifier = notifier;
    }

    public static String getGapRootDir() {
        return GAP_ROOT_DIRECTORY;
    }

    public SetMultimap<String, Method> getOperationToMethodMap() {
        return optnToMethodMap;
    }

    public SetMultimap<Set<String>, Method> getFilterToMethodMap() {
        return filterToMethodMap;
    }

    public Set<String> getAllFiltersInSet() {
        return filterSet;
    }

    public SetMultimap<Integer, String> getSearchHistoyMap() {
        return searchHistoyMap;
    }

    public List<String> getAllOperationsSortedInList() {
        return sortedOperationList;
    }

    public List<Method> getAllMethodsSortedInList() {
        return sortedMethodList;
    }

    public List<String> getAllFiltersSortedInList() {
        return sortedFilterList;
    }

    public String getOperationType(String optnName) {
        return optnTypeMap.get(optnName);
    }

    public String codeContentOf(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String codeContent = br.lines().collect(Collectors.joining(System.getProperty("line.separator")));
        br.close();
        return codeContent;
    }

    public void saveFile(String pathToFile, String content) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(pathToFile));
        bw.write(content);
        bw.close();
        notifier.firePropertyChange("save", null, pathToFile);
    }
    
    public boolean isEmptyWithoutConsiderSearchHistories() {
        return optnToMethodMap.isEmpty() && filterToMethodMap.isEmpty();
    }
    
    public boolean isEmpty() {
        return optnToMethodMap.isEmpty() && filterToMethodMap.isEmpty() && searchHistoyMap.isEmpty();
    }
    
    public boolean hasSearchHistories() {
        return !searchHistoyMap.isEmpty();
    }
    
    public void clearSearchHistories() {
        searchHistoyMap.clear();
    }
    
    public void clearAllData() {
        GAP_ROOT_DIRECTORY = null;
        optnToMethodMap.clear();
        filterToMethodMap.clear();
        filterSet.clear();
        optnTypeMap.clear();
        searchHistoyMap.clear();
        sortedOperationList = null;
        sortedMethodList = null;
        sortedFilterList = null;
    }

    /**
     * The dump file to read must start with the GAP root directory as its first line,
     * and the dumped content (dense) follows. No other file format than JSON is
     * permitted.
     * 
     * @param file
     * @throws IOException
     */
    public void readFromDumpFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        try {
            JSONObject rootDirInfo = new JSONObject(line);
            String rootDirPath = rootDirInfo.getString("GAP root directory");
            File rootDir = new File(rootDirPath);
            if (!rootDir.exists() || !rootDir.isDirectory()) {
                br.close();
                notifier.firePropertyChange("rtdir", null, rootDirPath);
                return;
            }
            if (GAP_ROOT_DIRECTORY != null && !GAP_ROOT_DIRECTORY.equals(rootDirPath)) {
                br.close();
                notifier.firePropertyChange("newrtdir", GAP_ROOT_DIRECTORY, rootDirPath);
                return;
            } else {
                GAP_ROOT_DIRECTORY = rootDirPath;
            }

            while ((line = br.readLine()) != null) {
                readLineFromDumpFile(line);
            }
            br.close();
            notifier.firePropertyChange("success", null, file.getCanonicalPath());
            sortedOperationList = optnToMethodMap.keySet().stream().sorted().collect(Collectors.toList());
            sortedMethodList = optnToMethodMap.values().stream().filter(m -> m.getName() != null).sorted()
                    .collect(Collectors.toList());
            sortedFilterList = filterSet.stream().sorted().collect(Collectors.toList());

        } catch (IndexOutOfBoundsException | JSONException | NumberFormatException e) {
            br.close();
            notifier.firePropertyChange("illf", null,
                    "Illegal content format in the dump file loaded from:\n" + file.getCanonicalPath());
            return;
        }
    }

    private void readLineFromDumpFile(String line) throws JSONException, NumberFormatException {
        JSONObject obj = new JSONObject(line);
        String optName;
        Set<String> fields = obj.keySet();
        String type;
        if (fields.contains("opt_name")) {
            optName = obj.getString("opt_name");
            type = "Operation";
        } else if (fields.contains("atr_name")) {
            optName = obj.getString("atr_name");
            type = "Attribute";
        } else if (fields.contains("prp_name")) {
            optName = obj.getString("prp_name");
            type = "Property";
        } else {
            throw new JSONException("");
        }

        optnTypeMap.put(optName, type);

        JSONArray methods = obj.getJSONArray("methods");
        String rootDirPattern = Pattern.quote(GAP_ROOT_DIRECTORY);

        if (methods.length() == 0) {
            optnToMethodMap.put(optName.trim(), emptyMethod);
            filterToMethodMap.put(emptyFilterSet, emptyMethod);
            return;
        }

        for (int i = 0; i < methods.length(); i++) {
            JSONObject method = (JSONObject) methods.get(i);
            String mthdName = method.getString("mthd_name").trim();
            JSONObject property = (JSONObject) method.get("property");

            JSONArray JSONArgFilter = property.getJSONArray("filters");
            int numArgs = JSONArgFilter.length();
            String[][] argFilters = new String[numArgs][];
            for (int j = 0; j < numArgs; j++) {
                String[] filters = JSONArrayToStringArray((JSONArray) JSONArgFilter.get(j));
                argFilters[j] = filters;
                filterSet.addAll(Arrays.asList(filters));
            }

            // Using type Object here to handle unexpected fraction number for rank
            Object rank = property.get("rank");
            JSONObject src = (JSONObject) method.get("src");
            String filePath = src.getString("file_path").replaceFirst(rootDirPattern, "./").trim();
            int lineNumStart = src.getInt("line_num_start");
            int lineNumEnd = src.getInt("line_num_end");

            Method mthd = new Method(mthdName, argFilters, parseFractionToInteger(rank.toString()), filePath,
                    lineNumStart, lineNumEnd);
            optnToMethodMap.put(optName.trim(), mthd);
            filterToMethodMap.put(Method.getUniqueFilters(argFilters), mthd);
        }
    }
    
    public String[] JSONArrayToStringArray(JSONArray jarr) {
        String[] res = new String[jarr.length()];
        for (int i = 0; i < res.length; i++) {
            res[i] = jarr.get(i).toString();
        }
        return res;
    }

    /**
     * This method is to handle potential rank value in fraction of some method in
     * GAP.
     * 
     * @param str
     * @return
     * @throws NumberFormatException
     */
    public int parseFractionToInteger(String str) throws NumberFormatException {
        if (StringUtils.countMatches(str, '/') == 1) {
            String[] nums = str.split("/");
            float res = Float.valueOf(nums[0]) / Float.valueOf(nums[1]);
            return Math.round(res);
        } else
            return Integer.valueOf(str);
    }

}
