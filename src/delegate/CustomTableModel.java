package delegate;

import javax.swing.table.DefaultTableModel;

class CustomTableModel extends DefaultTableModel {

    private int colNum;
    
    public CustomTableModel(Object rowData[][], Object columnNames[]) {
         super(rowData, columnNames);
         colNum = columnNames.length;
      }
    
    @Override
    public Class<?> getColumnClass(int col) {
        if (col == colNum-3)       //column 'rank' accepts only Integer values
            return Integer.class;
        else return String.class;  //other columns accept String values
    }


    private static final long serialVersionUID = 1L;
}
