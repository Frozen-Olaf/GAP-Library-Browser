package model.search;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.SetMultimap;
import com.google.common.primitives.Booleans;

import model.data.Method;
import model.data.ModelData;

public class ModelSearchClient implements SearchClient {

    private final ModelData modelData;
    private final PropertyChangeSupport notifier;
    private final SetMultimap<String, Method> optnToMethodMap;
    private final SetMultimap<Set<String>, Method> filterToMethodMap;
    private final Set<String> filterSet;
    private final SetMultimap<Integer, String> searchHistoyMap;

    public ModelSearchClient(ModelData modelData, PropertyChangeSupport notifier) {
        this.modelData = modelData;
        this.notifier = notifier;
        optnToMethodMap = modelData.getOperationToMethodMap();
        filterToMethodMap = modelData.getFilterToMethodMap();
        filterSet = modelData.getAllFiltersInSet();
        searchHistoyMap = modelData.getSearchHistoyMap();
    }

    @Override
    public List<String> getNonDuplicateSearchHisotryInList(int modelState) {
        return searchHistoyMap.get(modelState).stream().collect(Collectors.toList());
    }

    @Override
    public boolean addSearchHistory(int modelState, String toSearch) {
        return searchHistoyMap.put(modelState, toSearch);
    }

    @Override
    @SuppressWarnings("null")
    public String hasEquivalentSearchHistory(int modelState, String toSearch) {
        if (modelState == STATE_SEARCH_FILTER) {
            Set<String> filtersSet = splitStringAndStoreElementsInSet(toSearch, ",");
            return getNonDuplicateSearchHisotryInList(modelState).stream()
                    .filter(s -> splitStringAndStoreElementsInSet(s, ",").equals(filtersSet)).findFirst().orElse(null);
        } else if (modelState == STATE_SEARCH_METHOD) {
            String mthdName = StringUtils.substringBeforeLast(toSearch, "(").trim();
            String filters = StringUtils.substring(toSearch, toSearch.lastIndexOf('(') + 1, toSearch.lastIndexOf(')'))
                    .trim();
            boolean isOfArgOrderFlag = checkArgOrderFlag(filters);
            Set<String> filterSet = splitStringAndStoreElementsInSet(filters, ",");

            Boolean isArgNumSubsetFlag = null;
            String[] argFilters = null;
            List<Set<String>> argFiltersSetList = null;
            if (isOfArgOrderFlag) {
                isArgNumSubsetFlag = checkSubsetFlag(filters, isOfArgOrderFlag);
                argFilters = StringUtils.substringsBetween(filters, "[", "]");
                argFiltersSetList = Arrays.stream(argFilters).map(s -> splitStringAndStoreElementsInSet(s, ","))
                        .collect(Collectors.toList());
            }

            for (String sh : getNonDuplicateSearchHisotryInList(modelState)) {
                String mthdNameHistory = StringUtils.substringBeforeLast(sh, "(").trim();
                if (mthdNameHistory.equals(mthdName)) {
                    String filtersHistory = StringUtils.substring(sh, sh.lastIndexOf('(') + 1, sh.lastIndexOf(')'))
                            .trim();
                    boolean isOfArgOrderFlagHistory = checkArgOrderFlag(filtersHistory);
                    if (isOfArgOrderFlag && isOfArgOrderFlagHistory) {
                        boolean argNumSubsetCheck = isArgNumSubsetFlag
                                ^ checkSubsetFlag(filtersHistory, isOfArgOrderFlagHistory);
                        // two search inputs to compare need to be of the same value for
                        // argNumSubsetFlag.
                        if (argNumSubsetCheck)
                            continue;
                        String[] argFiltersHistory = StringUtils.substringsBetween(filtersHistory, "[", "]");
                        if (argFilters.length == argFiltersHistory.length) {
                            List<Set<String>> argFiltersHistorySetList = Arrays.stream(argFiltersHistory)
                                    .map(s -> splitStringAndStoreElementsInSet(s, ",")).collect(Collectors.toList());
                            if (argFiltersSetList.equals(argFiltersHistorySetList))
                                return sh;
                        }

                    } else if (!(isOfArgOrderFlag || isOfArgOrderFlagHistory)) {
                        if (filterSet.equals(splitStringAndStoreElementsInSet(filtersHistory, ",")))
                            return sh;
                    }
                }
            }
            return null;
        } else if (searchHistoyMap.containsEntry(modelState, toSearch))
            return toSearch;

        return null;
    }

    private Set<String> splitStringAndStoreElementsInSet(String input, String splitter) {
        return Arrays.stream(input.split(splitter)).map(String::trim).collect(Collectors.toSet());
    }

    @Override
    public List<Method> searchOpt(String toSearch) {
        Set<Method> matches = optnToMethodMap.get(toSearch.trim());
        if (matches.size() != 0)
            return matches.stream().collect(Collectors.toList());
        notifier.firePropertyChange("404", null, "Operation");
        return null;
    }

    @Override
    public List<Method> searchFilter(String toSearch) {
        return searchByFilter(toSearch, checkSubsetFlag(toSearch, false));
    }

    private List<Method> searchByFilter(String input, boolean isSubsetFlag) {
        String[] filters = Arrays.stream(input.split(",")).map(String::trim).toArray(String[]::new);
        if (filters.length == 1) {
            if (isSubsetFlag)
                return modelData.getAllMethodsSortedInList();
            else if (filters[filters.length - 1].equals("\\void")) {
                // search input '\void' to search methods that take no arguments.
                List<Method> res = (filterToMethodMap.get(ModelData.emptyFilterSet)).stream()
                        .collect(Collectors.toList());
                if (!res.isEmpty())
                    return res;
                notifier.firePropertyChange("404", null, "Methods under the specified filter");
                return null;
            }
        }

        Set<String> filtersFound = new HashSet<String>();
        Set<String> filtersNotFound = new HashSet<String>();
        if (!findMatchingFilters(filters, isSubsetFlag, filtersFound, filtersNotFound)) {
            notifier.firePropertyChange("404", null, "Entered filters all");
            return null;
        }
        displayFiltersNotFound(filtersFound, filtersNotFound);
        // this allows user not to have to enter "IsObejct" every time if they would
        // like to search for methods under the specific filters they enter.
        if (!isSubsetFlag)
            filtersFound.add("IsObject");

        return findMatchingMethodsFromFilters(filtersFound, isSubsetFlag);
    }

    /**
     * Method search input format 1: method_name(filter1, filter2, ...) Method
     * search input format 2: method_name([arg1_filter1, arg1_filter2, ...],
     * [arg2_filter1, arg2_filter2, ...], ...)
     * 
     * @param toSearch
     * @return
     */
    @Override
    public List<Method> searchMethodByNameAndFilters(String toSearch) {
        String tempMthdName = StringUtils.substringBeforeLast(toSearch, "{").trim();
        boolean anyMethodNameFlag = tempMthdName.equals("...");
        boolean methodNamePrefixFlag = true;
        if (tempMthdName.startsWith("~")) {
            methodNamePrefixFlag = false;
            tempMthdName = tempMthdName.substring(1);
        }
        // using tempMthdName to avoid defining the actual variable mthdName in an
        // enclosing scope, in which case it requires to must be final or effectively
        // final.
        String mthdName = tempMthdName;

        int indexFore = toSearch.lastIndexOf('{');
        int indexBack = toSearch.lastIndexOf('}');
        // illegal input format, if method name is not recognised, ( or ) is not found
        // or found to be misplaced, or ) is not the last non-whitespace element of
        // input.
        if (mthdName.length() == 0 || indexFore == -1 || indexBack == -1 || indexFore > indexBack
                || indexBack < toSearch.length() - 1) {
            notifier.firePropertyChange("illf", null,
                    "Unsupported input format for searching the method " + toSearch + "\n"
                            + "try: method_name{filter1, filter2, ...}\n"
                            + "or, method_name{[arg1_flt1, arg1_flt2, ...], [arg2_flt1, ...], ...}");
            return null;
        }
        String filters = toSearch.substring(indexFore + 1, indexBack).trim();

        List<Method> res;
        boolean isOfArgOrderFlag = checkArgOrderFlag(filters);
        boolean isSubsetFlag = checkSubsetFlag(filters, isOfArgOrderFlag);
        if (isOfArgOrderFlag) {
            return searchMethodByNameAndArgumentOrder(mthdName, filters, anyMethodNameFlag, methodNamePrefixFlag,
                    isSubsetFlag);
        } else {
            List<Method> ml = searchByFilter(filters, isSubsetFlag);
            if (ml == null)
                return null;
            if (anyMethodNameFlag) {
                if (!ml.isEmpty())
                    return ml;
            } else {
                if (methodNamePrefixFlag) {
                    res = ml.stream().filter(mthd -> mthd.getName().startsWith(mthdName)).collect(Collectors.toList());
                } else {
                    res = ml.stream().filter(mthd -> mthd.getName().contains(mthdName)).collect(Collectors.toList());
                }
                if (!res.isEmpty())
                    return res;
            }
            notifier.firePropertyChange("404", null, "Method with specified filters");
            return null;
        }
    }

    private List<Method> searchMethodByNameAndArgumentOrder(String mthdName, String filtersToSearch,
            boolean anyMethodNameFlag, boolean methodNamePrefixFlag, boolean isArgNumSubsetFlag) {

        String[] argFilters = StringUtils.substringsBetween(filtersToSearch, "[", "]");
        int argNum = argFilters.length;
        List<Set<String>> filtersOfArg = new ArrayList<Set<String>>();

        Set<String> filtersFound = new HashSet<String>();
        Set<String> filtersNotFound = new HashSet<String>();

        Set<String> argFiltersFound = new HashSet<String>();
        Set<String> argFiltersNotFound = new HashSet<String>();
        boolean[] isSubsetFlags = new boolean[argNum];
        for (int i = 0; i < argNum; i++) {
            isSubsetFlags[i] = checkSubsetFlag(argFilters[i], false);
            String[] filters = Arrays.stream(argFilters[i].split(",")).map(String::trim).toArray(String[]::new);

            if (isSubsetFlags[i]) {
                if (filters.length == 1) {
                    // if [...] is specified for an argument, then the argument is treated in the
                    // way that any set of filters is applicable.
                    filtersOfArg.add(ModelData.emptyFilterSet);
                    continue;
                }
                // ignore the subset symbol at the end
                filtersOfArg.add(
                        Arrays.asList(filters).subList(0, filters.length - 1).stream().collect(Collectors.toSet()));
            } else {
                filtersOfArg.add(Arrays.stream(filters).collect(Collectors.toSet()));
            }
            if (!findMatchingFilters(filters, isSubsetFlags[i], argFiltersFound, argFiltersNotFound)) {
                notifier.firePropertyChange("404", null, "Entered filters at arg" + (i + 1) + " all");
                return null;
            }
            if (!isSubsetFlags[i]) {
                filtersOfArg.get(i).add("IsObject");
                argFiltersFound.add("IsObject");
            }
            filtersFound.addAll(argFiltersFound);
            filtersNotFound.addAll(argFiltersNotFound);
            argFiltersFound.clear();
            argFiltersNotFound.clear();
        }
        displayFiltersNotFound(filtersFound, filtersNotFound);

        boolean isArgFilterSubsetFlag = Booleans.contains(isSubsetFlags, true);
        // When searching a method with subset flag,
        // no matter if the flag is indicating a subset of argument number or a subset
        // of argument filters at an argument,
        // we obtain all the methods that are supersets of the input first, and then
        // filter according to the actual type of subset flag later.
        List<Method> methodsMatchedFilters = findMatchingMethodsFromFilters(filtersFound,
                (isArgNumSubsetFlag || isArgFilterSubsetFlag));
        if (methodsMatchedFilters == null)
            return null;

        List<Method> methodsMatchedArgNumAndNames = new ArrayList<Method>();
        for (Method mthd : methodsMatchedFilters) {
            if (mthd.getArgNumber() == argNum || (isArgNumSubsetFlag && mthd.getArgNumber() > argNum)) {
                if (anyMethodNameFlag) {
                    methodsMatchedArgNumAndNames.add(mthd);
                } else {
                    if (methodNamePrefixFlag) {
                        if (mthd.getName().startsWith(mthdName))
                            methodsMatchedArgNumAndNames.add(mthd);
                    } else {
                        if (mthd.getName().contains(mthdName))
                            methodsMatchedArgNumAndNames.add(mthd);
                    }
                }
            }
        }

        List<Method> res = new ArrayList<Method>();
        boolean isArgNameAndOrderMatch;
        if (!methodsMatchedArgNumAndNames.isEmpty()) {
            for (Method mthd : methodsMatchedArgNumAndNames) {
                isArgNameAndOrderMatch = true;
                String[][] mthdArgFilters = mthd.getArgFilters();
                for (int i = 0; i < argNum; i++) {
                    Set<String> argFilterSet = Arrays.stream(mthdArgFilters[i]).collect(Collectors.toSet());
                    if (isSubsetFlags[i]) {
                        if (!argFilterSet.containsAll(filtersOfArg.get(i))) {
                            isArgNameAndOrderMatch = false;
                            break;
                        }
                    } else {
                        if (!argFilterSet.equals(filtersOfArg.get(i))) {
                            isArgNameAndOrderMatch = false;
                            break;
                        }
                    }
                }
                if (isArgNameAndOrderMatch)
                    res.add(mthd);
            }
            if (!res.isEmpty())
                return res;
        }
        notifier.firePropertyChange("404", null, "Method with specified filters");
        return null;
    }

    private boolean checkArgOrderFlag(String input) {
        int index = input.indexOf('[');
        if (index == -1)
            return false;
        return (index < input.indexOf(']'));
    }

    private boolean checkSubsetFlag(String input, boolean isOfArgOrderFlag) {
        if (isOfArgOrderFlag) {
            String lastPart = input.substring(input.lastIndexOf(']') + 1);
            int index = lastPart.indexOf(',');
            if (index == -1)
                return false;
            return lastPart.substring(index + 1).trim().equals("...");
        } else {
            int lastIndex = input.lastIndexOf(',');
            if (lastIndex == -1) {
                return input.trim().equals("...");
            }
            return input.substring(lastIndex + 1).trim().equals("...");
        }
    }

    private boolean findMatchingFilters(String[] filters, boolean isSubsetFlag, Set<String> filtersFound,
            Set<String> filtersNotFound) {
        for (int i = 0; i < filters.length; i++) {
            // if subset symbol is entered at the end in the input, ignore it as a filter.
            if (isSubsetFlag && i == filters.length - 1)
                break;
            if (filterSet.contains(filters[i])) {
                filtersFound.add(filters[i]);
            } else {
                filtersNotFound.add(filters[i]);
            }
        }
        return !filtersFound.isEmpty();
    }

    private void displayFiltersNotFound(Set<String> filtersFound, Set<String> filtersNotFound) {
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
            List<Set<String>> matches = filterToMethodMap.keySet().stream().filter(c -> c.containsAll(filtersFound))
                    .collect(Collectors.toList());
            matches.forEach(k -> filterToMethodMap.get(k).forEach(m -> res.add(m)));
        } else {
            res = filterToMethodMap.get(filtersFound).stream().collect(Collectors.toList());
        }

        if (!res.isEmpty())
            return res;
        notifier.firePropertyChange("404", null, "Methods under the specified filter");
        return null;
    }

}
