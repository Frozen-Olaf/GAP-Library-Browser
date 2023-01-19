package model.data;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Method implements Comparable<Method> {

    private String name;
    private String[][] argFilters;
    private Integer rank;
    private String filePath;
    private Integer lineNumStart;
    private Integer lineNumEnd;

    public Method(String name, String[][] argFilters, Integer rank, String filePath, Integer lineNumStart,
            Integer lineNumEnd) {
        this.name = name;
        this.argFilters = argFilters;
        this.rank = rank;
        this.filePath = filePath;
        this.lineNumStart = lineNumStart;
        this.lineNumEnd = lineNumEnd;
    }

    public String getName() {
        return name;
    }

    public String[][] getArgFilters() {
        return argFilters;
    }

    public int getArgNumber() {
        return argFilters.length;
    }

    public Integer getRank() {
        return rank;
    }

    public String getFilePath() {
        return filePath;
    }

    public Integer getLineNumStart() {
        return lineNumStart;
    }

    public Integer getLineNumEnd() {
        return lineNumEnd;
    }

    public static Set<String> getUniqueFilters(String[][] argFilters) {
        return Arrays.stream(argFilters).flatMap(Arrays::stream).collect(Collectors.toSet());
    }

    public static String getFiltersInOneLine(String[] argFilters) {
        String res = "";
        for (String elem : argFilters) {
            res += elem + ", ";
        }
        if (!res.isEmpty())
            return res.substring(0, res.lastIndexOf(','));
        return res;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rank, filePath, lineNumStart, lineNumEnd) + Arrays.deepHashCode(argFilters);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj != null && obj.getClass() == getClass()) {
            Method mthd = (Method) obj;
            return (name.equals(mthd.getName()) && Arrays.deepEquals(argFilters, mthd.getArgFilters())
                    && rank.equals(mthd.getRank()) && filePath.equals(mthd.getFilePath())
                    && lineNumStart.equals(mthd.lineNumStart) && lineNumEnd.equals(mthd.lineNumEnd));
        }
        return false;
    }

    @Override
    public int compareTo(Method o) {
        if (o == null)
            return 1;
        String on = o.getName();
        if (on == null)
            return 1;
        return name.compareTo(on);
    }

}
