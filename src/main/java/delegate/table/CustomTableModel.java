package delegate.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import model.data.Method;

public class CustomTableModel extends DefaultTableModel {

    public CustomTableModel(Object rowData[][], Object columnNames[]) {
        super(rowData, columnNames);
    }

    @Override
    public Class<?> getColumnClass(int col) {
        if (col == getColumnCount() - 3) // column 'rank' accepts only Integer values
            return Integer.class;
        else
            return String.class; // other columns accept String values
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // Disable any editing in the table.
        return false;
    }

    public static CustomTableModel create(List<Method> methods) {
        int maxArgNum = maxMethodArgNumber(methods);
        String[] colNames = new String[maxArgNum + 4];
        colNames[0] = "Method";
        for (int i = 1; i <= maxArgNum; i++)
            colNames[i] = "Argument " + i;
        colNames[colNames.length - 3] = "Rank";
        colNames[colNames.length - 2] = "File Path";
        colNames[colNames.length - 1] = "Code Range";

        int mthdNum = methods.size();
        Object[][] data = new Object[mthdNum][colNames.length];

        for (int i = 0; i < mthdNum; i++) {
            Method method = methods.get(i);
            data[i][0] = method.getName();

            String[][] argFilters = method.getArgFilters();
            int methodArgNum = method.getArgNumber();
            if (methodArgNum < maxArgNum) {
                for (int j = 1; j <= methodArgNum; j++) {
                    data[i][j] = Method.getFiltersInOneLine(argFilters[j - 1]);
                }
                for (int j = methodArgNum + 1; j <= maxArgNum; j++) {
                    data[i][j] = "n/a";
                }
            } else {
                for (int j = 1; j <= maxArgNum; j++) {
                    data[i][j] = Method.getFiltersInOneLine(argFilters[j - 1]);
                }
            }

            data[i][colNames.length - 3] = method.getRank();
            data[i][colNames.length - 2] = method.getFilePath();
            if (method.getLineNumStart() == null && method.getLineNumEnd() == null)
                data[i][colNames.length - 1] = null;
            else
                data[i][colNames.length - 1] = method.getLineNumStart() + "-" + method.getLineNumEnd();
        }
        return new CustomTableModel(data, colNames);
    }

    private static int maxMethodArgNumber(List<Method> methods) {
        List<Integer> argNums = new ArrayList<Integer>();
        methods.forEach(m -> argNums.add(m.getArgNumber()));
        return Collections.max(argNums);
    }

    private static final long serialVersionUID = 1L;
}
