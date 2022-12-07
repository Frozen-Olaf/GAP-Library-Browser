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
    private JCheckBox cbCategory;
    private JCheckBox[] cbs = new JCheckBox[3];
    
    private final JFrame frame;
    private final Model model;
    
    public SearchBar(JFrame frame, Model model) {
    	super();
    	this.frame = frame;
    	this.model = model;
    	
    	init();
    	
    	setupCheckBoxLayout();
    	setupLayout();
    }
    
    private void init() {
    	search = new JLabel("Search:");
    	searchInput = new JTextField("Please enter here to search...");

    	SuggestionDropDownDecorator.decorate(searchInput,
                new TextComponentWordSuggestionClient(SearchBar::newSearchSuggestion));
    	
    	Dimension st = search.getPreferredSize();
    	st.height += 20;
    	searchInput.setPreferredSize(st);

    	initSearchButton();
    	initCheckBoxes();
    }
    
    private static List<String> newSearchSuggestion(String input) {
    	
        List<String> searchHistories = Model.getNonDuplicateSearchHisotryInList(Model.getModelState());
    	
    	Set<String> suggestions = searchHistories.stream()
    			.filter(s -> s.startsWith(input))
    			.limit(5)
    			.collect(Collectors.toSet());

    	if (Model.getModelState() == Model.SEARCH_OPERATION) {
    		List<String> optns = Model.getAllOperationsInList();
    		List<String> filtered = optns.stream()
    				.filter(c -> c.startsWith(input))
    				.limit(20)
    				.collect(Collectors.toList());
    		suggestions.addAll(filtered);
    	} 
    	else if (Model.getModelState() == Model.SEARCH_CATEGORY) {
    		List<String> ctgrys = Model.getAllCategoriesInList();
    		List<String> filtered = ctgrys.stream()
    				.filter(c -> c.startsWith(input))
    				.limit(20)
    				.collect(Collectors.toList());
    		suggestions.addAll(filtered);
    	} 
    	else if (Model.getModelState() == Model.SEARCH_METHOD) {
    		List<Method> methods = Model.getAllMethodsInList();
    		List<Method> filtered = methods.stream()
    				.filter(m -> m.getName().startsWith(input))
    				.limit(20)
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
    		Delegate.multiPages.changeToPage(toSearch);
    		return;
    	}

    	if (modelState == Model.STATE_IDLE) {
    		SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	JOptionPane.showMessageDialog(frame, "Please tick one checkbox to\n"
                			+ "speficy your intended searching type.");;
                }
            });
    	}
    	else {
        	Page p = null;
        	List<Method> res = null;
			if (modelState == Model.SEARCH_OPERATION) {
	        	if ((res = model.searchOpt(toSearch)) != null)
	        		p = new Page(toSearch, res, Model.SEARCH_OPERATION);
			} 
			else if (modelState == Model.SEARCH_CATEGORY) {
	        	if ((res = model.searchCategory(toSearch)) != null)
	        		p = new Page(toSearch, res, Model.SEARCH_CATEGORY);
			}
			else if (modelState == Model.SEARCH_METHOD) {
	        	if ((res = model.searchMethodWithCategory(toSearch)) != null)
	        		p = new Page(toSearch, res, Model.SEARCH_METHOD);
			} 
			else return;
			if (res!=null && p!=null) {
			    Delegate.multiPages.changeToPage(p.getName());
                Model.getSearchHisotryInSet().add(new ImmutablePair<String, Integer>(toSearch, modelState));
			}
    	}

    }
    
    private void initCheckBoxes() {
    	cbPanel = new JPanel();
    	cbOpt = new JCheckBox("Operation");
    	cbCategory = new JCheckBox("Category"); 
    	cbMethod = new JCheckBox("Method");
    	cbs[0] = cbOpt;
    	cbs[1] = cbOpt;
    	cbs[2] = cbMethod;
    	
    	ItemListener il = new ItemListener() {
    		public void itemStateChanged(ItemEvent e) { 
    			if (e.getSource().equals(cbOpt) && cbOpt.isSelected()) {
    				cbCategory.setSelected(false);
    				cbMethod.setSelected(false);
    				Model.setModelState(Model.SEARCH_OPERATION);
    			}
    			else if (e.getSource().equals(cbCategory) && cbCategory.isSelected()) {
    				cbOpt.setSelected(false);
    				cbMethod.setSelected(false);
    				Model.setModelState(Model.SEARCH_CATEGORY);
    			}
    			else if (e.getSource().equals(cbMethod) && cbMethod.isSelected()) {
    				cbOpt.setSelected(false);
    				cbCategory.setSelected(false);
    				Model.setModelState(Model.SEARCH_METHOD);
    			}
    			else {
    				Model.setModelState(Model.STATE_IDLE);
    			}
    		}  
    	};   
    	cbOpt.addItemListener(il);
    	cbCategory.addItemListener(il); 
    	cbMethod.addItemListener(il);    
    }

    
    private void setupLayout() {
    	GroupLayout layout = new GroupLayout(this);

    	layout.setAutoCreateGaps(true);
    	layout.setAutoCreateContainerGaps(true);

    	layout.setHorizontalGroup(
    			layout.createSequentialGroup()
    			.addComponent(search)
    			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
    					.addComponent(searchInput, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    					.addComponent(cbPanel))
    			.addComponent(searchBtn)
    			);
    	layout.setVerticalGroup(
    			layout.createSequentialGroup()
    			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    					.addComponent(search)
    					.addComponent(searchInput)
    					.addComponent(searchBtn))
    			.addComponent(cbPanel)
    			);
    	
    	this.setLayout(layout);
    }
    
    private void setupCheckBoxLayout() {
    	FlowLayout cbLayout = new FlowLayout(FlowLayout.LEADING);
    	
    	cbPanel.setLayout(cbLayout);
    	
    	cbPanel.add(cbMethod);
    	cbPanel.add(cbOpt);
    	cbPanel.add(cbCategory);
    	cbPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }
    

    private static final long serialVersionUID = 1L;
}
