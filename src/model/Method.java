package model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Method {

	private String name;
	private int rank;
	
	private String[][] argFilters;
	
	private String filePath;
	private int lineNumStart;
	private int lineNumEnd;
	
	public Method(String name, String[][] argFilters, int rank, String filePath, int lineNumStart, int lineNumEnd) {
		this.name = name;
		this.rank = rank;
		
		this.argFilters = argFilters;
		
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
	
	public int getRank() {
		return rank;
	}

	public String getFilePath() {
		return filePath;
	}

	public int getLineNumStart() {
		return lineNumStart;
	}

	public int getLineNumEnd() {
		return lineNumEnd;
	}
	
	public static Set<String> getUniqueFilters(String[][] argFilters){
	    return Arrays.stream(argFilters)
	            .flatMap(Arrays::stream)
	            .collect(Collectors.toSet());
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

	/**
     * Overridden equals();
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj != null && obj.getClass() == this.getClass()) {
        	Method mthd = (Method) obj;
            return (this.name.equals(mthd.getName()) && Arrays.deepEquals(this.argFilters, mthd.getArgFilters())
            		&& this.rank==mthd.getRank() && this.filePath.equals(mthd.getFilePath())
            		&& this.lineNumStart == mthd.lineNumStart && this.lineNumEnd == mthd.lineNumEnd);
        }
        return false;
    }
}
