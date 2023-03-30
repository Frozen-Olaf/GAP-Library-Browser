package delegate.ui.searchbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

import delegate.ui.button.ButtonDecorator;
import delegate.ui.icon.IconVault;
import delegate.ui.page.HomePage;
import delegate.ui.page.Page;
import delegate.ui.page.SearchResultPage;
import delegate.ui.searchsuggestion.SuggestionClient;
import delegate.ui.searchsuggestion.SuggestionDropDownDecorator;
import delegate.ui.searchsuggestion.SuggestionEntry;
import delegate.ui.searchsuggestion.TextComponentWordSuggestionClient;
import delegate.ui.CornerRoundedTextPane;
import delegate.ui.UserInterface;
import model.Model;
import model.data.Method;
import model.data.ModelData;
import model.search.SearchModule;

public class SearchBar extends JPanel implements PropertyChangeListener {

    private final UserInterface userInterface;
    private final JFrame frame;
    private final Page page;
    private final Model model;
    private final ModelData modelData;
    private final SearchModule searchModule;
    private final SuggestionClient<JTextComponent> suggestionClient;

    private JButton searchInputBtn;
    private CornerRoundedTextPane searchInputBar;
    private JTextField searchInput;
    private JButton eraseBtn;

    private JButton searchBtn;
    private JPanel cbPanel;
    private JCheckBox cbMethod;
    private JCheckBox cbOpt;
    private JCheckBox cbFilter;
    private MethodSearchInputPane methodSearchInputPane;

    private boolean isPrimary;
    private boolean isInitialInputInfoSet;

    public SearchBar(UserInterface userInterface, Model model, Page page, boolean isPrimary) {
        super();
        this.userInterface = userInterface;
        frame = userInterface.getFrame();
        this.model = model;
        modelData = model.getModelData();
        searchModule = model.getSearchModule();
        suggestionClient = new TextComponentWordSuggestionClient(model, this::newSearchSuggestion);

        this.page = page;
        this.isPrimary = isPrimary;

        init();

        userInterface.addObserver(this);
    }

    public void setDefaultSearch() {
        frame.getRootPane().setDefaultButton(searchBtn);
        setSearchState();
    }

    private void setSearchState() {
        if (cbMethod.isSelected()) {
            model.setSearchState(SearchModule.STATE_SEARCH_METHOD);
        } else if (cbOpt.isSelected()) {
            model.setSearchState(SearchModule.STATE_SEARCH_OPERATION);
        } else if (cbFilter.isSelected()) {
            model.setSearchState(SearchModule.STATE_SEARCH_FILTER);
        } else
            model.setSearchState(SearchModule.STATE_IDLE);
    }

    private List<SuggestionEntry> newSearchSuggestion(String input, Boolean isInMethodArgumentInputMode,
            Boolean hasSearchStateChanged, List<SuggestionEntry> previousResult) {
        if (previousResult.isEmpty() || hasSearchStateChanged) {
            int searchState = isInMethodArgumentInputMode ? SearchModule.STATE_SEARCH_FILTER : model.getSearchState();

            Set<SuggestionEntry> orderedSuggestions = new LinkedHashSet<SuggestionEntry>();
            // don't display search histories when user is typing in a method argument input
            // bar.
            if (!isInMethodArgumentInputMode) {
                List<String> searchHistories = searchModule.getNonDuplicateSearchHistoryInList(searchState);
                ListIterator<String> iterator = searchHistories.listIterator(searchHistories.size());
                int searchHistoryDisplayNumLimit = 5;
                while (iterator.hasPrevious() && orderedSuggestions.size() < searchHistoryDisplayNumLimit) {
                    String sh = iterator.previous();
                    if (sh.startsWith(input))
                        orderedSuggestions.add(new SuggestionEntry(true, sh));
                }
            }

            if (searchState == SearchModule.STATE_SEARCH_OPERATION) {
                List<String> optns = modelData.getAllOperationsSortedInList();
                if (optns != null) {
                    List<SuggestionEntry> filtered = optns.stream().filter(o -> o.startsWith(input))
                            .map(e -> new SuggestionEntry(false, e)).collect(Collectors.toList());
                    orderedSuggestions.addAll(filtered);
                }
            } else if (searchState == SearchModule.STATE_SEARCH_FILTER) {
                List<String> filters = modelData.getAllFiltersSortedInList();
                if (filters != null) {
                    List<SuggestionEntry> filtered = filters.stream().filter(f -> f.startsWith(input))
                            .map(e -> new SuggestionEntry(false, e)).collect(Collectors.toList());
                    orderedSuggestions.addAll(filtered);
                }
            } else if (searchState == SearchModule.STATE_SEARCH_METHOD) {
                List<Method> methods = modelData.getAllMethodsSortedInList();
                if (methods != null) {
                    List<SuggestionEntry> filtered = methods.stream().map(Method::getName)
                            .filter(m -> m.startsWith(input)).map(e -> new SuggestionEntry(false, e))
                            .collect(Collectors.toList());
                    orderedSuggestions.addAll(filtered);
                }
            }

            return orderedSuggestions.stream().collect(Collectors.toList());
        } else {
            return previousResult.stream().filter(s -> (s.getText().startsWith(input))).collect(Collectors.toList());
        }
    }

    private void init() {
        initSearchInputBar();
        initInputButton();
        initSearchButton();

        if (isPrimary)
            initMethodSearchInputPanel();

        initCheckBoxes();
        setupLayout();
    }

    private void initSearchInputBar() {
        searchInputBar = new CornerRoundedTextPane(new BorderLayout(), true);
        searchInput = searchInputBar.getTextField();
        String initInfo = "Please enter here to search...";
        if (isPrimary) {
            isInitialInputInfoSet = true;
            searchInput.setForeground(Color.decode("#bfbfbf"));
            searchInput.setText(initInfo);
        }
        searchInputBar.replaceFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isInitialInputInfoSet) {
                    if (searchInput.getText().equals(initInfo)) {
                        searchInput.setText("");
                        searchInput.setForeground(UIManager.getColor("TextField.foreground"));
                        isInitialInputInfoSet = false;
                    }
                }
                searchInputBar.setPaintBorder(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                searchInputBar.setPaintBorder(false);
            }

        });
        searchInputBar.setTextFieldSize(50, 36);
        SuggestionDropDownDecorator.decorate(userInterface, searchInput, false, suggestionClient);

        eraseBtn = new JButton(IconVault.getEraseIcon());
        eraseBtn.setToolTipText("Erase the input");
        eraseBtn.setOpaque(false);
        eraseBtn.setBorderPainted(false);
        eraseBtn.setMargin(new Insets(0, 2, 0, 2));
        eraseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        searchInput.setText("");
                        searchInput.requestFocusInWindow();
                    }
                });
            }
        });
        ButtonDecorator.addDefaultMouseListener(eraseBtn);
        searchInputBar.add(eraseBtn, BorderLayout.EAST);
    }

    private void initInputButton() {
        searchInputBtn = new JButton(IconVault.getInputIcon());
        searchInputBtn.setToolTipText("Enter the input");
        searchInputBtn.setMargin(new Insets(5, 5, 5, 5));
        searchInputBtn.setFocusable(false);
        if (!UserInterface.getIsInDarkTheme()) {
            searchInputBtn.setBorderPainted(false);
            searchInputBtn.setBackground(getBackground());
        }
        searchInputBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchInput.requestFocusInWindow();
                searchInput.setSelectionStart(0);
            }
        });
        ButtonDecorator.addDefaultMouseListener(searchInputBtn);
    }

    private void initSearchButton() {
        searchBtn = new JButton(IconVault.getSearchIcon());
        searchBtn.setPreferredSize(eraseBtn.getPreferredSize());
        searchBtn.setMargin(new Insets(4, 4, 4, 4));
        if (!UserInterface.getIsInDarkTheme()) {
            searchBtn.setBackground(getBackground());
            searchBtn.setBorderPainted(false);
        }
        initSearchButtonListeners(searchBtn);
        frame.getRootPane().setDefaultButton(searchBtn);
    }

    protected void initSearchButtonListeners(JButton searchBtn) {
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch(model.getSearchState());
            }
        });
        ButtonDecorator.addDefaultMouseListener(searchBtn);
        searchBtn.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch(model.getSearchState());
                }
            }
        });
    }

    private void performSearch(int searchState) {
        String toSearch = searchInput.getText().trim();

        boolean isInMethodSearchArgInputMode = (methodSearchInputPane != null && !methodSearchInputPane.isEmpty());
        if (searchState == SearchModule.STATE_SEARCH_METHOD && isInMethodSearchArgInputMode) {
            toSearch += methodSearchInputPane.getAllArgumentsInStandardInputFormat();
        }

        if (UserInterface.getMultiPage().changeToPage(toSearch, true)) {
            searchModule.addSearchHistory(searchState, toSearch);
            return;
        }
        String history;
        if ((history = searchModule.hasEquivalentSearchHistory(searchState, toSearch)) != null) {
            if (UserInterface.getMultiPage().changeToPage(history, true))
                return;
        }

        if (searchState == SearchModule.STATE_IDLE) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(frame,
                            "Please tick one checkbox to indicate your intended category for searching.");
                }
            });
        } else {
            SearchResultPage p = null;
            List<Method> res = null;
            if (searchState == SearchModule.STATE_SEARCH_OPERATION) {
                if ((res = searchModule.searchOpt(toSearch)) != null)
                    p = new SearchResultPage(userInterface, model, toSearch, res, SearchModule.STATE_SEARCH_OPERATION);
            } else if (searchState == SearchModule.STATE_SEARCH_FILTER) {
                if ((res = searchModule.searchFilter(toSearch)) != null)
                    p = new SearchResultPage(userInterface, model, toSearch, res, SearchModule.STATE_SEARCH_FILTER);
            } else if (searchState == SearchModule.STATE_SEARCH_METHOD) {
                if ((res = searchModule.searchMethod(toSearch)) != null)
                    p = new SearchResultPage(userInterface, model, toSearch, res, SearchModule.STATE_SEARCH_METHOD);
            } else
                return;

            if (p != null) {
                UserInterface.getMultiPage().addPage(toSearch, p);
                UserInterface.getMultiPage().changeToPage(p, true);
                page.setNext(p);
                p.setPrev(page);
                searchModule.addSearchHistory(searchState, toSearch);
            }
        }
    }

    private void initMethodSearchInputPanel() {
        methodSearchInputPane = new MethodSearchInputPane(userInterface, suggestionClient);
        initSearchButtonListeners(methodSearchInputPane.getSearchButton());
    }

    private void initCheckBoxes() {
        cbPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        cbPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        cbMethod = new JCheckBox("Method");
        cbOpt = new JCheckBox("Operation");
        cbOpt.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        cbFilter = new JCheckBox("Filter");

        cbPanel.add(cbMethod);
        cbPanel.add(cbOpt);
        cbPanel.add(cbFilter);

        ItemListener il = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getSource().equals(cbOpt) && cbOpt.isSelected()) {
                    cbFilter.setSelected(false);
                    cbMethod.setSelected(false);
                    model.setSearchState(SearchModule.STATE_SEARCH_OPERATION);
                    updateGuiDisplay(false);
                } else if (e.getSource().equals(cbFilter) && cbFilter.isSelected()) {
                    cbOpt.setSelected(false);
                    cbMethod.setSelected(false);
                    model.setSearchState(SearchModule.STATE_SEARCH_FILTER);
                    updateGuiDisplay(false);
                } else if (e.getSource().equals(cbMethod)) {
                    if (cbMethod.isSelected()) {
                        cbOpt.setSelected(false);
                        cbFilter.setSelected(false);
                        model.setSearchState(SearchModule.STATE_SEARCH_METHOD);
                        updateGuiDisplay(true);
                    } else {
                        model.setSearchState(SearchModule.STATE_IDLE);
                        updateGuiDisplay(false);
                    }
                } else {
                    model.setSearchState(SearchModule.STATE_IDLE);
                }
            }
        };
        cbOpt.addItemListener(il);
        cbFilter.addItemListener(il);
        cbMethod.addItemListener(il);
    }

    private void updateGuiDisplay(boolean isInMethodSearchArgInputMode) {
        if (page instanceof HomePage) {
            // if we are in HomePage, then the searchBar is primary and so the
            // methodSearchInputPanel is initialised.
            methodSearchInputPane.setVisible(isInMethodSearchArgInputMode);
            ((HomePage) page).updateGuiDisplay(isInMethodSearchArgInputMode);
        }
    }

    private void setupLayout() {
        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(false);
        layout.setAutoCreateContainerGaps(false);

        ParallelGroup hpg = layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(searchInputBar, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(cbPanel);
        if (isPrimary)
            hpg.addComponent(methodSearchInputPane);
        SequentialGroup hsg = layout.createSequentialGroup();
        if (isPrimary)
            hsg.addGap(20);
        hsg.addComponent(searchInputBtn);
        if (isPrimary)
            hsg.addGap(6);
        else
            hsg.addGap(3);
        hsg.addGroup(hpg);
        if (isPrimary)
            hsg.addGap(5);
        else
            hsg.addGap(2);
        hsg.addComponent(searchBtn);
        if (isPrimary)
            hsg.addGap(20);
        else
            hsg.addGap(3);
        layout.setHorizontalGroup(hsg);

        ParallelGroup vpg = layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(searchInputBtn)
                .addComponent(searchInputBar).addComponent(searchBtn);
        SequentialGroup vsg = layout.createSequentialGroup().addGap(10).addGroup(vpg);
        if (isPrimary)
            vsg.addGap(6);
        vsg.addComponent(cbPanel);
        if (isPrimary)
            vsg.addGap(6).addComponent(methodSearchInputPane);
        layout.setVerticalGroup(vsg);

        setLayout(layout);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() != userInterface)
            return;
        String propertyName = evt.getPropertyName();
        if (propertyName == "light") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    searchBtn.setBackground(getBackground());
                    searchInputBtn.setBackground(getBackground());
                }
            });
        }
    }

    private static final long serialVersionUID = 1L;
}
