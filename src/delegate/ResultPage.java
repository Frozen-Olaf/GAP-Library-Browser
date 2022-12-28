package delegate;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import model.Method;
import model.Model;

public class ResultPage extends Page {
	
	private List<Method> methods;
	private int type;
    private int tableColNum;
    
    private JPanel header;
    
    private JPanel headerTop;
	private JLabel info;

    private JPanel filterPanel;
    private JLabel filter;
    private JTextField filterText;
    private JCheckBox cbHideTrivial;

	private JTable table;
    private JScrollPane sp;

    private TableRowSorter<TableModel> sorter;
    private RowFilter<Object, Object> prevRowFilter = null;
    private final static RowFilter<Object, Object> nontrivialFilter = createNonTrivialFilter();

    private JLabel full;
    private JTextArea fullText;
    private JScrollPane fullTextScroll;
    private JPanel fullTextPanel;

	private int cursor = Cursor.DEFAULT_CURSOR;

    public ResultPage(JFrame frame, Model model, String name, List<Method> methodList, int type) {
        super(frame, model);

    	this.frame = frame;
    	this.model = model;
		this.setName(name);
		this.type = type;
		methods = methodList;
    	
    	init();
        
        Delegate.multiPages.addPage(name, this);
    }

    private static RowFilter<Object, Object> createNonTrivialFilter() {
        List<RowFilter<Object,Object>> fl = new ArrayList<RowFilter<Object,Object>>(2);
        RowFilter<Object, Object> gsetterFilter = RowFilter.regexFilter("system(\\smutable)?\\s[gs]etter");
        RowFilter<Object, Object> trivialFilter = RowFilter.regexFilter("default\\smethod,\\sdoes\\snothing");

        fl.add(gsetterFilter);
        fl.add(trivialFilter);
        return RowFilter.notFilter(RowFilter.orFilter(fl));
    }
    
    private void init() {
        initHeader();
        
    	initScrollableTable();
		
        initFullTextDisplayer();
        
        setupSplitContent();
    }
    
    private void initHeader() {
        header = new JPanel();
        
        headerTop = new JPanel(new BorderLayout());
		headerTop.add(btnPanel, BorderLayout.WEST);

		info = new JLabel();
		info.setHorizontalAlignment(JLabel.CENTER);
		info.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		String words = "Search results for: ";
		if (type == Model.STATE_SEARCH_OPERATION)
			info.setText(words+ "<Operation " + this.getName() + ">");
		if (type == Model.STATE_SEARCH_FILTER)
			info.setText(words + "<Filter " + this.getName() + ">");
		if (type == Model.STATE_SEARCH_METHOD)
			info.setText(words + "<Method " + this.getName() + ">");
		info.setToolTipText(info.getText());
		
		headerTop.add(info, BorderLayout.CENTER);
		initFilter();
		setupHeaderLayout();
    }
    
    private void initFilter() {
        filterPanel = new JPanel(new BorderLayout());
        filter = new JLabel("Table Filtering:");
        filter.setToolTipText("Filter the table by your input text");
        filterText = new JTextField();
        filterText.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        newTableFilter();
                    }
                    public void insertUpdate(DocumentEvent e) {
                        newTableFilter();
                    }
                    public void removeUpdate(DocumentEvent e) {
                        newTableFilter();
                    }
                });
        filter.setLabelFor(filterText);
        Dimension d = filter.getPreferredSize();
        d.width+=80;
        d.height+=15;
        filterText.setMinimumSize(d);
        filterText.setPreferredSize(d);
        
        cbHideTrivial = new JCheckBox("Hide Trivials");
        cbHideTrivial.setToolTipText("Hide all trivial methods");
        cbHideTrivial.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) { 
                if (cbHideTrivial.isSelected()) {
                    if (prevRowFilter != null) {
                        List<RowFilter<Object,Object>> fl = new ArrayList<RowFilter<Object,Object>>(2);
                        fl.add(nontrivialFilter);
                        fl.add(prevRowFilter);
                        sorter.setRowFilter(RowFilter.andFilter(fl));
                    } else {
                        sorter.setRowFilter(nontrivialFilter);
                    }
                }
                else {
                    sorter.setRowFilter(prevRowFilter);
                }
            }  
        });
        
        filterPanel.add(filter, BorderLayout.WEST);
        filterPanel.add(filterText, BorderLayout.CENTER);
        filterPanel.add(cbHideTrivial, BorderLayout.EAST);
        
        constructSearchBar(false);
    }

    private void newTableFilter() {
        RowFilter<Object, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(Pattern.quote(filterText.getText()));
            prevRowFilter = rf;
            if (cbHideTrivial.isSelected()) {
                List<RowFilter<Object,Object>> fl = new ArrayList<>(2);
                fl.add(nontrivialFilter);
                fl.add(rf);
                rf = RowFilter.andFilter(fl);
            }
            sorter.setRowFilter(rf);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
    }

    private void setupHeaderLayout() {
        header.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.weightx = 1.0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(0,5,0,0);
        header.add(headerTop, c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.weightx = 0.55;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0,5,0,0);
        header.add(filterPanel, c);
        
        c.weightx = 0.45;
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0,10,0,0);
        c.gridwidth = 1;
        header.add(searchBar, c);
    }
    
	private void initScrollableTable() {
		int maxArgNum = maxMethodArgNumber(methods);
		String[] colNames = new String[maxArgNum+4];
		colNames[0] = "Method";
		for (int i=1; i<=maxArgNum; i++)
			colNames[i] = "Argument " + i;
		colNames[colNames.length-3] = "Rank";
		colNames[colNames.length-2] = "File Path";
		colNames[colNames.length-1] = "Code Range";

		int mthdNum = methods.size();
		Object[][] data = new Object[mthdNum][colNames.length];
		
		for (int i=0; i<mthdNum; i++) {
			Method method = methods.get(i);
			data[i][0] = method.getName();

			String[][] argFilters = method.getArgFilters();
			int methodArgNum = method.getArgNumber();
			if (methodArgNum < maxArgNum) {
				for (int j=1; j<=methodArgNum; j++) {
					data[i][j] = Method.getFiltersInOneLine(argFilters[j-1]);
				}
				for (int j=methodArgNum+1; j<=maxArgNum; j++) {
					data[i][j] = "n/a";
				}
			}	
			else { 	
				for (int j=1; j<=maxArgNum; j++) {
					data[i][j] = Method.getFiltersInOneLine(argFilters[j-1]);
				}
			}
			
			data[i][colNames.length-3] = method.getRank();
			data[i][colNames.length-2] = method.getFilePath();
			data[i][colNames.length-1] = method.getLineNumStart()+ "-"+ method.getLineNumEnd();
		}
		CustomTableModel tableModel = new CustomTableModel(data, colNames);
		table = new JTable(tableModel);
		tableColNum = table.getColumnCount();
		sorter = new TableRowSorter<TableModel>(tableModel);
		table.setRowSorter(sorter);
		renderTable();
		initTableMouseEvent(this);

        sp = new JScrollPane(table);
	}
	
	private int maxMethodArgNumber(List<Method> methods) {
		
		List<Integer> argNums = new ArrayList<Integer>();
		methods.forEach(m -> argNums.add(m.getArgNumber()));
		return Collections.max(argNums);
	}
    
    private void renderTable() {
        
        CustomTableCellRenderer renderer = new CustomTableCellRenderer(false);
		
		for (int i=0; i<tableColNum; i++) {
			TableColumn tc = table.getColumnModel().getColumn(i);
			if (i == tableColNum-2) {
			    tc.setPreferredWidth(50);
		        tc.setCellRenderer(new CustomTableCellRenderer(true));
		        continue;
			}
			tc.setCellRenderer(renderer);
		}
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		// Disable any content editing from the table.
    	table.setDefaultEditor(Object.class, null);
    }
	
	private void initTableMouseEvent(ResultPage page) {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
			    int selectedRow = table.getSelectedRow();
			    int selectedCol = table.getSelectedColumn();
                Object val = table.getValueAt(selectedRow, selectedCol);
				if (selectedCol==tableColNum-2) {
					String path = val.toString();
					if (path.startsWith(".")) {
						path = Model.getGapRootDir() + path.substring(2);
					}
					try {
                        if (e.isControlDown()) {
                            Desktop.getDesktop().open(new File(path));
                        }
                        else if (e.isShiftDown()) {
					        Desktop.getDesktop().browseFileDirectory(new File(path));
					    }
					} catch (UnsupportedOperationException uoe) {
					    SwingUtilities.invokeLater(new Runnable() {
			                public void run() {
		                        JOptionPane.showMessageDialog(frame, "Not supported, cannot open the file or the driectory of it from this browser.");
			                }
			            });
					} catch (IllegalArgumentException iae) {
					    SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                JOptionPane.showMessageDialog(frame, "This file is not found on this path.");
                            }
                        });
                    } catch (IOException ioe) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                JOptionPane.showMessageDialog(frame, ioe.getMessage());
                            }
                        });
                    }
				}
				

				updateFullTextDisplayer(val);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				if (cursor==Cursor.HAND_CURSOR) {
					page.setCursor(Cursor.getDefaultCursor());
					cursor=Cursor.DEFAULT_CURSOR;
				}
			}
		});
		
		table.addMouseMotionListener(new MouseMotionAdapter() {
			@Override 
			public void mouseMoved(MouseEvent e) {
				Point pt = e.getPoint();
				if(cursor==Cursor.DEFAULT_CURSOR && table.columnAtPoint(pt)==tableColNum-2) {
					page.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					cursor=Cursor.HAND_CURSOR;
				} 
				else if (cursor==Cursor.HAND_CURSOR && table.columnAtPoint(pt)!=tableColNum-2) {
					page.setCursor(Cursor.getDefaultCursor());
					cursor=Cursor.DEFAULT_CURSOR;
				}
			}
		});
	}
	
    private void initFullTextDisplayer() {
        fullTextPanel = new JPanel(new BorderLayout());
        fullTextPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        full = new JLabel("Full Text:");
        full.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));
        fullText = new JTextArea();
        fullTextScroll = new JScrollPane(fullText);
        fullTextScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        full.setLabelFor(fullTextScroll);
        
        Dimension x = full.getPreferredSize();
        x.height+=60;
        fullTextScroll.setPreferredSize(x);
        
        fullTextPanel.add(full, BorderLayout.WEST);
        fullTextPanel.add(fullTextScroll, BorderLayout.CENTER);
        fullTextPanel.setAlignmentX(LEFT_ALIGNMENT);
    }
    
    private void updateFullTextDisplayer(Object value) {
        fullText.setText("");
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            String str = (String) value;
            
            String[] temp = str.split(", ");
            try {
                for (int i=0; i<temp.length; i+=4) {
                    fullText.append(temp[i+0]+", ");
                    fullText.append(temp[i+1]+", ");
                    fullText.append(temp[i+2]+", ");
                    fullText.append(temp[i+3]);
                    if (i+4 != temp.length) {
                        fullText.append(",\n");
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                String s = fullText.getText();
                s = s.substring(0, s.length()-2);
                fullText.setText(s);
            }
        }
        else if (value instanceof Integer) {
            fullText.setText(value.toString());
        }
    }
    
    private void setupSplitContent() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(header, BorderLayout.NORTH);
        contentPanel.add(sp, BorderLayout.CENTER);
        
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                contentPanel, fullTextPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(0.8);
        
        add(splitPane, BorderLayout.CENTER);
    }
    

    private static final long serialVersionUID = 1L;
    
}
