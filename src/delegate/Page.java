package delegate;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import model.Method;
import model.Model;

public class Page extends JPanel {
	
	private List<Method> methods;
	private int type;
    private int tableColNum;
    
    private JPanel header;
    private JButton homeBtn;
	private JLabel info;

    private JPanel filterPanel;
    private JLabel filter;
    private JTextField filterText;
    
	private JTable table;
    private JScrollPane sp;
    
    private TableRowSorter<TableModel> sorter;
    
    private JLabel full;
    private JTextArea fullText;
    private JScrollPane fullTextScroll;
    private JPanel fullTextPanel;
    
    private JSplitPane splitPane;
    private JPanel top;
    
	private int cursor = Cursor.DEFAULT_CURSOR;
	
    public Page(String name, List<Method> methodList, int type) {
    	super(new BorderLayout());

		this.setName(name);
		this.type = type;
		methods = methodList;
    	
    	init();
        
        Delegate.multiPages.addPage(this, this.getName());
    }
    
    public List<Method> getMethods() {
		return methods;
	}
    
    private void init() {
        initHeader();
        
    	initScrollableTable();
		
        initFullTextDisplayer();
        
        setupSplitContent();
    }
    
    private void initHeader() {
    	homeBtn = new JButton("home");
		homeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Delegate.multiPages.changeToPage("home");
            }
        });
		homeBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getDefaultCursor());
			}
		});
		
		header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.PAGE_AXIS));
		
		header.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		homeBtn.setAlignmentX(LEFT_ALIGNMENT);
		header.add(homeBtn);
		
		info = new JLabel();
		info.setHorizontalAlignment(JLabel.CENTER);
		info.setAlignmentX(LEFT_ALIGNMENT);
		String words = "Search results for: ";
		if (type == Model.SEARCH_OPERATION)
			info.setText(words+ "<Operation " + this.getName() + ">");
		if (type == Model.SEARCH_CATEGORY)
			info.setText(words + "<Category " + this.getName() + ">");
		if (type == Model.SEARCH_METHOD)
			info.setText(words + "<Method " + this.getName() + ">");
		header.add(info);

		initFilter();
    }
    
    private void initFilter() {
        filterPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        filterPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        filter = new JLabel("Filter Text:");
        filterText = new JTextField();
        filterText.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        newFilter();
                    }
                    public void insertUpdate(DocumentEvent e) {
                        newFilter();
                    }
                    public void removeUpdate(DocumentEvent e) {
                        newFilter();
                    }
                });
        filter.setLabelFor(filterText);
        Dimension x = filter.getPreferredSize();
        x.width+=100;
        x.height+=15;
        filterText.setPreferredSize(x);
        filterPanel.add(filter);
        filterPanel.add(filterText);
        filterPanel.setAlignmentX(LEFT_ALIGNMENT);
        
        header.add(filterPanel);
    }
    
    private void newFilter() {
        RowFilter<Object, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(filterText.getText());
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
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

			String[][] argCtgrys = method.getArgCategories();
			int methodArgNum = method.getArgNumber();
			if (methodArgNum < maxArgNum) {
				for (int j=1; j<=methodArgNum; j++) {
					data[i][j] = Method.getCategoriesInOneLine(argCtgrys[j-1]);
				}
				for (int j=methodArgNum+1; j<=maxArgNum; j++) {
					data[i][j] = "N/A";
				}
			}	
			else { 	
				for (int j=1; j<=maxArgNum; j++) {
					data[i][j] = Method.getCategoriesInOneLine(argCtgrys[j-1]);
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
    	table.setDefaultEditor(Object.class, null);
    }
	
	private void initTableMouseEvent(Page page) {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isShiftDown()&& table.getSelectedColumn()==tableColNum-2) {
					String path = methods.get(table.getSelectedRow()).getFilePath();
					if (path.startsWith(".")) {
						path = model.Model.getGapRootDir() + path.substring(1);
					}
					Desktop.getDesktop().browseFileDirectory(new File(path));
				}
				
				Object str = table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
				updateFullTextDisplayer(str);
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
        fullTextPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        full = new JLabel("Full Text:  ");
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
        
        top = new JPanel(new BorderLayout());
        top.add(header, BorderLayout.NORTH);
        top.add(sp, BorderLayout.CENTER);
        
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                top, fullTextPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(0.8);
        
        this.add(splitPane, BorderLayout.CENTER);
    }
    

    private static final long serialVersionUID = 1L;
    
}
