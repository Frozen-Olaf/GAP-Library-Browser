package delegate.page;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import delegate.CornerRoundedTextPane;
import delegate.UserInterface;
import delegate.button.ButtonDecorator;
import delegate.table.CustomTable;
import delegate.table.CustomTableModel;
import model.Model;
import model.data.Method;
import model.data.ModelData;
import model.icon.IconVault;
import model.search.SearchModule;

public class SearchResultPage extends Page {

    private List<Method> methods;
    private final String name;
    private final int type;

    private JPanel headerTop;
    private JPanel infoPanel;
    private JTextField info;

    private JPanel filterPanel;
    private JButton filterBtn;
    private CornerRoundedTextPane filterTextPane;
    private JTextField filterText;
    private JCheckBox cbHideTrivial;

    private CustomTable table;
    private JScrollPane sp;
    private TableRowSorter<CustomTableModel> sorter;
    private RowFilter<Object, Object> prevRowFilter = null;
    private final static RowFilter<Object, Object> nontrivialFilter = createNonTrivialFilter();

    private JLabel full;
    private JTextArea fullText;
    private JScrollPane fullTextScroll;
    private JPanel fullTextPanel;

    public SearchResultPage(UserInterface userInterface, Model model, String name, List<Method> methodList, int type) {
        super(userInterface, model);
        this.name = name;
        this.type = type;
        methods = methodList;

        init();
        setName(name);
    }

    private static RowFilter<Object, Object> createNonTrivialFilter() {
        List<RowFilter<Object, Object>> fl = new ArrayList<RowFilter<Object, Object>>(2);
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
        btnPanel.setMinimumSize(btnPanel.getPreferredSize());
        headerTop.add(btnPanel, BorderLayout.WEST);

        infoPanel = new JPanel(new BorderLayout());
        String words = "Search results for: ";
        if (type == SearchModule.STATE_SEARCH_OPERATION) {
            String optnType = model.getModelData().getOperationType(name);
            if (optnType != null && !optnType.equals("Else"))
                words += "<" + optnType + " " + name + ">";
            else
                words += "<Operation " + name + ">";
        } else if (type == SearchModule.STATE_SEARCH_FILTER) {
            words += "<Filter " + name + ">";
        } else if (type == SearchModule.STATE_SEARCH_METHOD) {
            words += "<Method " + name + ">";
        }

        info = new JTextField(words);
        info.setToolTipText(words);
        info.setBackground(UIManager.getColor("Label.background"));
        info.setEditable(false);
        info.setBorder(BorderFactory.createEmptyBorder());
        Dimension infoSize = info.getPreferredSize();
        infoSize.width += 8;
        info.setMinimumSize(infoSize);
        info.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                info.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                info.setCursor(Cursor.getDefaultCursor());
            }
        });
        infoPanel.add(info, BorderLayout.CENTER);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));

        headerTop.add(infoPanel, BorderLayout.CENTER);
        initFilter();
        constructSearchBar(false);
        setupHeaderLayout();
    }

    private void initFilter() {
        filterPanel = new JPanel(new BorderLayout());

        filterTextPane = new CornerRoundedTextPane(new BorderLayout(), true);
        filterTextPane.setBorder(BorderFactory.createEmptyBorder());
        filterTextPane.setFixedTextFieldSize(120, 28);
        filterText = filterTextPane.getTextField();
        filterText.getDocument().addDocumentListener(new DocumentListener() {
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

        filterBtn = new JButton(IconVault.getFilterIcon());
        filterBtn.setToolTipText("Filter the table by your input text");
        filterBtn.setMargin(new Insets(5, 5, 5, 5));
        filterBtn.setFocusable(false);
        if (!UserInterface.getIsInDarkTheme()) {
            filterBtn.setBorderPainted(false);
            filterBtn.setBackground(getBackground());
        }
        filterBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterText.requestFocusInWindow();
                filterText.setSelectionStart(0);
            }
        });
        ButtonDecorator.addDefaultMouseListener(filterBtn);

        cbHideTrivial = new JCheckBox("Hide Trivials");
        cbHideTrivial.setToolTipText("Hide all trivial methods");
        cbHideTrivial.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (cbHideTrivial.isSelected()) {
                    if (prevRowFilter != null) {
                        List<RowFilter<Object, Object>> fl = new ArrayList<RowFilter<Object, Object>>(2);
                        fl.add(nontrivialFilter);
                        fl.add(prevRowFilter);
                        sorter.setRowFilter(RowFilter.andFilter(fl));
                    } else {
                        sorter.setRowFilter(nontrivialFilter);
                    }
                } else {
                    sorter.setRowFilter(prevRowFilter);
                }
            }
        });

        filterPanel.add(filterBtn, BorderLayout.WEST);
        filterPanel.add(filterTextPane, BorderLayout.CENTER);
        filterPanel.add(cbHideTrivial, BorderLayout.EAST);
    }

    private void newTableFilter() {
        RowFilter<Object, Object> rf = null;
        // If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(Pattern.quote(filterText.getText()));
            prevRowFilter = rf;
            if (cbHideTrivial.isSelected()) {
                List<RowFilter<Object, Object>> fl = new ArrayList<>(2);
                fl.add(nontrivialFilter);
                fl.add(rf);
                rf = RowFilter.andFilter(fl);
            }
            sorter.setRowFilter(rf);
        } catch (PatternSyntaxException e) {
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
        c.insets = new Insets(0, 5, 0, 0);
        header.add(headerTop, c);

        c.fill = GridBagConstraints.HORIZONTAL;

        c.weightx = 0.2;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        header.add(filterPanel, c);

        c.weightx = 0.8;
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0, 10, 0, 0);
        c.gridwidth = 1;
        header.add(searchBar, c);
    }

    private void initScrollableTable() {
        CustomTableModel tableModel = CustomTableModel.create(methods);
        table = CustomTable.create(userInterface, model, this, tableModel);
        sorter = new TableRowSorter<CustomTableModel>(tableModel);
        table.setRowSorter(sorter);
        initTableCellSelectionListener();

        sp = new JScrollPane(table);
    }

    private void initTableCellSelectionListener() {
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectionUpdate();
            }
        });
        table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectionUpdate();
            }
        });
    }

    private void selectionUpdate() {
        int selectedRow = table.getSelectedRow();
        int selectedCol = table.getSelectedColumn();
        // Avoid invalid index exception
        if (selectedRow == -1 || selectedCol == -1)
            return;
        Object val = table.getValueAt(selectedRow, selectedCol);
        if (selectedCol == table.getFilePathColumnIndex()) {
            if (val != null) {
                String path = val.toString();
                if (path.startsWith(".")) {
                    path = ModelData.getGapRootDir() + path.substring(2);
                }
                updateFullTextDisplayer(path);
            }
        } else
            updateFullTextDisplayer(val);
    }

    private void initFullTextDisplayer() {
        fullTextPanel = new JPanel(new BorderLayout());
        fullTextPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        full = new JLabel("Full Text:");
        full.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));

        fullText = new JTextArea();
        fullText.setLineWrap(true);
        fullText.setWrapStyleWord(true);

        fullTextScroll = new JScrollPane(fullText);
        fullTextScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        full.setLabelFor(fullTextScroll);

        Dimension x = full.getPreferredSize();
        x.height += 60;
        fullTextScroll.setPreferredSize(x);

        fullTextPanel.add(full, BorderLayout.WEST);
        fullTextPanel.add(fullTextScroll, BorderLayout.CENTER);
        fullTextPanel.setAlignmentX(LEFT_ALIGNMENT);
    }

    private void updateFullTextDisplayer(Object value) {
        if (value == null)
            return;
        fullText.setText(value.toString());
    }

    private void setupSplitContent() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(header, BorderLayout.NORTH);
        contentPanel.add(sp, BorderLayout.CENTER);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, contentPanel, fullTextPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(0.8);

        add(splitPane, BorderLayout.CENTER);
    }

    private static final long serialVersionUID = 1L;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() != userInterface)
            return;
        String propertyName = evt.getPropertyName();
        if (propertyName == "dark" || propertyName == "light") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (propertyName == "light")
                        filterBtn.setBackground(getBackground());
                    info.setBackground(UIManager.getColor("Label.background"));
                    table.setShowHorizontalLines(true);
                    table.setShowVerticalLines(true);
                }
            });
        }
    }
}
