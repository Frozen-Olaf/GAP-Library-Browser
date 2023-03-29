package model.data;

import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;

public class ModelDataValidator {
    
    private static String GAP_ROOT_DIRECTORY;
    
    private final SetMultimap<String, Method> optnToMethodMap = LinkedHashMultimap.create();
    private final Set<String> filterSet = new HashSet<String>();
    
    private final ModelData modelData;
    private final PropertyChangeSupport notifier;
    
    public ModelDataValidator(ModelData modelData, PropertyChangeSupport notifier) {
        this.modelData = modelData;
        this.notifier = notifier;
    }
    
    public void validateCurrentLoadedData() {
        if (modelData.isEmptyWithoutConsiderSearchHistories()) {
            notifier.firePropertyChange("empty", null, null);
        } else {
            if (checkFundamentalOperations(true) && checkFundamentalMethods(true) && checkFundamentalFilters(true)) {
                notifier.firePropertyChange("valid", null, "Current loaded data");
            }
        }
    }
    
    public void validateDumpFile(File file) throws IOException {
        clear();
        if (readFromDumpFile(file)) {
            if (checkFundamentalOperations(false) && checkFundamentalMethods(false) && checkFundamentalFilters(false)) {
                notifier.firePropertyChange("valid", null, "The dump file at " + file.getCanonicalPath());
            }
        }
        clear();
    }

    private void clear() {
        optnToMethodMap.clear();
        filterSet.clear();
    }

    private boolean checkFundamentalOperations(boolean isValidatingCurrentLoadedData) {
        Set<String> optns;
        if (isValidatingCurrentLoadedData) {
            optns = modelData.getOperationToMethodMap().keySet();
        } else {
            optns = optnToMethodMap.keySet();
        }
        if (optns.size() < ModelDataValidation.MINIMUM_OPERATION_NUMBER) {
            if (isValidatingCurrentLoadedData) {
                notifier.firePropertyChange("curinvld", null, "The number of loaded GAP operations does not meet the minimum threshold: " + ModelDataValidation.MINIMUM_OPERATION_NUMBER + ".");
            } else {
                notifier.firePropertyChange("fileinvld", null, "The number of GAP operations in the dump file does not meet the minimum threshold: " + ModelDataValidation.MINIMUM_OPERATION_NUMBER + ".");
            }
            return false;
        }
        for (String optn: ModelDataValidation.FUNDAMENTAL_OPERATION_NAMES) {
            if (!optns.contains(optn)) {
                if (isValidatingCurrentLoadedData) {
                    notifier.firePropertyChange("curinvld", null, "GAP operation " + optn + " is not found.");
                } else {
                    notifier.firePropertyChange("fileinvld", null, "GAP operation " + optn + " is not found in the dump file.");
                }
                return false;
            }
        }
        return true;
    }
    
    private boolean checkFundamentalMethods(boolean isValidatingCurrentLoadedData) {
        List<Method> mthds;
        if (isValidatingCurrentLoadedData) {
            mthds = modelData.getAllMethodsSortedInList();
        } else {
            mthds = optnToMethodMap.values().stream().collect(Collectors.toList());
        }
        if (mthds.size() < ModelDataValidation.MINIMUM_METHOD_NUMBER) {
            if (isValidatingCurrentLoadedData) {
                notifier.firePropertyChange("curinvld", null, "The number of loaded GAP methods does not meet the minimum threshold: " + ModelDataValidation.MINIMUM_METHOD_NUMBER + ".");
            } else {
                notifier.firePropertyChange("fileinvld", null, "The number of GAP methods in the dump file does not meet the minimum threshold: " + ModelDataValidation.MINIMUM_METHOD_NUMBER + ".");
            }
            return false;
        }
        Set<String> names = mthds.stream().map(Method::getName).collect(Collectors.toSet());
        for (String name : ModelDataValidation.FUNDAMENTAL_METHOD_NAMES) {
            if (!names.contains(name)) {
                if (isValidatingCurrentLoadedData) {
                    notifier.firePropertyChange("curinvld", null, "GAP method " + name + " is not found.");
                } else {
                    notifier.firePropertyChange("fileinvld", null, "GAP method " + name + " is not found in the dump file.");
                }
                return false;
            }
        }
        Set<String> filePaths = mthds.stream().map(Method::getFilePath).collect(Collectors.toSet());
        for (String filePath : ModelDataValidation.FUNDAMENTAL_METHOD_FILE_PATHS) {
            if (!filePaths.contains(filePath)) {
                if (isValidatingCurrentLoadedData) {
                    if (filePath.startsWith(".")) {
                        filePath = filePath.replaceFirst("./", ModelData.getGapRootDir());
                    }
                    notifier.firePropertyChange("curinvld", null, "GAP file " + filePath + " is not found.");
                } else {
                    if (filePath.startsWith(".")) {
                        filePath = filePath.replaceFirst("./", GAP_ROOT_DIRECTORY);
                    }
                    notifier.firePropertyChange("fileinvld", null, "GAP file " + filePath + " is not found in the dump file.");
                }
                return false;
            }
        }
        return true;
    }
    
    private boolean checkFundamentalFilters(boolean isValidatingCurrentLoadedData) {
        Set<String> filters;
        if (isValidatingCurrentLoadedData) {
            filters = modelData.getAllFiltersInSet();
        } else {
            filters = filterSet;
        }
        if (filters.size() < ModelDataValidation.MINIMUM_FILTER_NUMBER) {
            if (isValidatingCurrentLoadedData) {
                notifier.firePropertyChange("curinvld", null, "The number of loaded GAP filters does not meet the minimum threshold: " + ModelDataValidation.MINIMUM_FILTER_NUMBER + ".");
            } else {
                notifier.firePropertyChange("fileinvld", null, "The number of GAP filters in the dump file does not meet the minimum threshold: " + ModelDataValidation.MINIMUM_FILTER_NUMBER + ".");
            }
            return false;
        }
        for (String filter : ModelDataValidation.FUNDAMENTAL_FILTERS) {
            if (!filters.contains(filter)) {
                if (isValidatingCurrentLoadedData) {
                    notifier.firePropertyChange("curinvld", null, "GAP filter " + filter + " is not found.");
                } else {
                    notifier.firePropertyChange("fileinvld", null, "GAP filter " + filter + " is not found in the dump file.");
                }
                return false;
            }
        }
        return true;
    }
    
    private boolean readFromDumpFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        try {
            JSONObject rootDirInfo = new JSONObject(line);
            String rootDirPath = rootDirInfo.getString("GAP root directory");
            File rootDir = new File(rootDirPath);
            if (!rootDir.exists() || !rootDir.isDirectory()) {
                br.close();
                notifier.firePropertyChange("fileinvld", null, "GAP root directory " + rootDirPath + " not found or illegal");
                return false;
            }
            GAP_ROOT_DIRECTORY = rootDirPath;
            while ((line = br.readLine()) != null) {
                readLineFromDumpFile(line);
            }
            br.close();
            return true;
        } catch (IndexOutOfBoundsException | JSONException | NumberFormatException e) {
            br.close();
            notifier.firePropertyChange("fileinvld", null,
                    "Illegal content format in the dump file at " + file.getCanonicalPath());
            return false;
        }
    }
    
    private void readLineFromDumpFile(String line) throws JSONException, NumberFormatException {
        JSONObject obj = new JSONObject(line);
        String optName;
        Set<String> fields = obj.keySet();
        if (fields.contains("opt_name")) {
            optName = obj.getString("opt_name");
        } else if (fields.contains("atr_name")) {
            optName = obj.getString("atr_name");
        } else if (fields.contains("prp_name")) {
            optName = obj.getString("prp_name");
        } else {
            throw new JSONException("");
        }

        JSONArray methods = obj.getJSONArray("methods");
        String rootDirPattern = Pattern.quote(GAP_ROOT_DIRECTORY);

        for (int i = 0; i < methods.length(); i++) {
            JSONObject method = (JSONObject) methods.get(i);
            String mthdName = method.getString("mthd_name").trim();
            JSONObject property = (JSONObject) method.get("property");

            JSONArray JSONArgFilter = property.getJSONArray("filters");
            int numArgs = JSONArgFilter.length();
            String[][] argFilters = new String[numArgs][];
            for (int j = 0; j < numArgs; j++) {
                String[] filters = modelData.JSONArrayToStringArray((JSONArray) JSONArgFilter.get(j));
                argFilters[j] = filters;
                filterSet.addAll(Arrays.asList(filters));
            }

            // Using type Object here to handle unexpected fraction number for rank
            Object rank = property.get("rank");
            JSONObject src = (JSONObject) method.get("src");
            String filePath = src.getString("file_path").replaceFirst(rootDirPattern, "./").trim();
            int lineNumStart = src.getInt("line_num_start");
            int lineNumEnd = src.getInt("line_num_end");

            // use Method to keep methods of duplicate name.
            Method mthd = new Method(mthdName, argFilters, modelData.parseFractionToInteger(rank.toString()), filePath,
                    lineNumStart, lineNumEnd);
            optnToMethodMap.put(optName.trim(), mthd);
        }
    }

}
