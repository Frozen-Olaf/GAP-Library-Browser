package delegate;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
	private boolean isFilePath;
	public CustomTableCellRenderer(boolean isFilePath) {
		super();
		this.isFilePath = isFilePath;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
        setHorizontalAlignment(JLabel.LEFT);
		if (value!=null) {
		    setToolTipText(value.toString());
			if (isFilePath)
				setText("<html><u><font color='blue'>"+value.toString()+"</u></html>");
		}
		else setToolTipText("N/A");
		return this;
	}

    private static final long serialVersionUID = 1L;
}
