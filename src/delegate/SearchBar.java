package delegate;

import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.tuple.ImmutablePair;

import model.Method;
import model.Model;
import searchsuggestion.SuggestionDropDownDecorator;
import searchsuggestion.TextComponentWordSuggestionClient;

public class SearchBar extends JPanel{
	private JLabel search;
    private JTextField searchInput;
    private JButton searchBtn;
    private JPanel cbPanel;
    private JCheckBox cbMethod;
    private JCheckBox cbOpt;
    private JCheckBox cbFilter;
    
    private boolean isPrimary;
    
    private Page page;
    
    private final JFrame frame;
    private final Model model;
    
    public SearchBar(JFrame frame, Model model, Page page, boolean isPrimary) {
    	super();
    	this.frame = frame;
    	this.model = model;
    	this.page = page;
    	
    	this.isPrimary = isPrimary;
    	
    	init();
    }
    
    public void setDefaultSearch() {
        frame.getRootPane().setDefaultButton(searchBtn);
        setSearchState();
    }
    
    private void setSearchState() {
        if (cbMethod.isSelected()) {
            Model.setModelState(Model.STATE_SEARCH_METHOD);
        }
        else if (cbOpt.isSelected()){
            Model.setModelState(Model.STATE_SEARCH_OPERATION);
        }
        else if (cbFilter.isSelected()) {
            Model.setModelState(Model.STATE_SEARCH_FILTER);
        }
        else Model.setModelState(Model.STATE_IDLE);
    }

    private void init() {
    	search = new JLabel("Search:");
    	searchInput = new JTextField("Please enter here to search...");
    	SuggestionDropDownDecorator.decorate(searchInput,
                new TextComponentWordSuggestionClient(SearchBar::newSearchSuggestion));

    	Dimension st = search.getPreferredSize();
    	st.height += 20;
    	searchInput.setPreferredSize(st);
    	searchInput.setMinimumSize(new Dimension(0, st.height));

    	initSearchButton();
    	initCheckBoxes();
    	
    	setupPrimaryLayout();
    }

    private static List<String> newSearchSuggestion(String input) {

        List<String> searchHistories = Model.getNonDuplicateSearchHisotryInList(Model.getModelState());

    	Set<String> suggestions = searchHistories.stream()
    			.filter(s -> s.startsWith(input))
    			.limit(5)
    			.collect(Collectors.toSet());

    	if (Model.getModelState() == Model.STATE_SEARCH_OPERATION) {
    		List<String> optns = Model.getAllOperationsInList();
    		List<String> filtered = optns.stream()
    				.filter(c -> c.startsWith(input))
    				.collect(Collectors.toList());
    		suggestions.addAll(filtered);
    	}
    	else if (Model.getModelState() == Model.STATE_SEARCH_FILTER) {
    		List<String> ctgrys = Model.getAllFiltersInList();
    		List<String> filtered = ctgrys.stream()
    				.filter(c -> c.startsWith(input))
    				.collect(Collectors.toList());
    		suggestions.addAll(filtered);
    	}
    	else if (Model.getModelState() == Model.STATE_SEARCH_METHOD) {
    		List<Method> methods = Model.getAllMethodsInList();
    		List<Method> filtered = methods.stream()
    				.filter(m -> m.getName().startsWith(input))
    				.collect(Collectors.toList());
    		filtered.forEach(m -> suggestions.add(m.getName()));
    	}

    	return suggestions.stream().collect(Collectors.toList());
    }

    private void initSearchButton() {
    	searchBtn = new JButton("Search");
    	searchBtn.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
            	performSearch(Model.getModelState());
    		}
    	});
    	searchBtn.addMouseListener(new MouseAdapter() {
    		@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getDefaultCursor());
			}
		});
    	searchBtn.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					performSearch(Model.getModelState());
				}
			}
    	});
    	frame.getRootPane().setDefaultButton(searchBtn);
    }

    private void performSearch(int modelState) {
    	String toSearch = searchInput.getText().trim();
    	
    	if (Model.getSearchHisotryInSet().contains(new ImmutablePair<String, Integer>(toSearch, modelState))) {
    		if (Delegate.multiPages.changeToPage(toSearch))
    		    return;
    	}

    	if (modelState == Model.STATE_IDLE) {
    		SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	JOptionPane.showMessageDialog(frame, "Please tick one checkbox to\n"
                			+ "speficy your intended searching type.");
                }
            });
    	}
    	else {
        	ResultPage p = null;
        	List<Method> res = null;
			if (modelState == Model.STATE_SEARCH_OPERATION) {
	        	if ((res = model.searchOpt(toSearch)) != null)
	        		p = new ResultPage(frame, model, toSearch, res, Model.STATE_SEARCH_OPERATION);
			} 
			else if (modelState == Model.STATE_SEARCH_FILTER) {
	        	if ((res = model.searchFilter(toSearch)) != null)
	        		p = new ResultPage(frame, model, toSearch, res, Model.STATE_SEARCH_FILTER);
			}
			else if (modelState == Model.STATE_SEARCH_METHOD) {
	        	if ((res = model.searchMethodWithFilter(toSearch)) != null)
	        		p = new ResultPage(frame, model, toSearch, res, Model.STATE_SEARCH_METHOD);
			} 
			else return;
			if (res!=null && p!=null) {
			    Delegate.multiPages.changeToPage(p);
			    page.next = p;
			    p.prev = page;
                Model.getSearchHisotryInSet().add(new ImmutablePair<String, Integer>(toSearch, modelState));
			}
    	}
    }
    
    private void initCheckBoxes() {
    	cbPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        cbPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        cbMethod = new JCheckBox("Method");
    	cbOpt = new JCheckBox("Operation");
    	cbFilter = new JCheckBox("Filter");

        cbPanel.add(cbMethod);
        cbPanel.add(cbOpt);
        cbPanel.add(cbFilter);
    	
    	ItemListener il = new ItemListener() {
    		public void itemStateChanged(ItemEvent e) { 
    			if (e.getSource().equals(cbOpt) && cbOpt.isSelected()) {
    			    cbFilter.setSelected(false);
    				cbMethod.setSelected(false);
    				Model.setModelState(Model.STATE_SEARCH_OPERATION);
    			}
    			else if (e.getSource().equals(cbFilter) && cbFilter.isSelected()) {
    				cbOpt.setSelected(false);
    				cbMethod.setSelected(false);
    				Model.setModelState(Model.STATE_SEARCH_FILTER);
    			}
    			else if (e.getSource().equals(cbMethod) && cbMethod.isSelected()) {
    				cbOpt.setSelected(false);
    				cbFilter.setSelected(false);
    				Model.setModelState(Model.STATE_SEARCH_METHOD);
    			}
    			else {
    				Model.setModelState(Model.STATE_IDLE);
    			}
    		}  
    	};   
    	cbOpt.addItemListener(il);
    	cbFilter.addItemListener(il); 
    	cbMethod.addItemListener(il);    
    }
    
    private void setupPrimaryLayout() {
    	GroupLayout layout = new GroupLayout(this);
    	layout.setAutoCreateGaps(isPrimary);
    	layout.setAutoCreateContainerGaps(isPrimary);

    	layout.setHorizontalGroup(
    			layout.createSequentialGroup()
    			.addComponent(search)
    			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
    					.addComponent(searchInput, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    					.addComponent(cbPanel))
    			.addComponent(searchBtn)
    			);

        ParallelGroup pg = layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(search)
                .addComponent(searchInput)
                .addComponent(searchBtn);

        SequentialGroup sg = layout.createSequentialGroup()
                .addGroup(pg);
    	
    	if (isPrimary) {
    	    sg.addComponent(cbPanel);
    	} 
    	else {
    	    sg.addGap(0).addComponent(cbPanel);
    	}
    	layout.setVerticalGroup(sg);
	
    	this.setLayout(layout);
    }

    private static final long serialVersionUID = 1L;
}
