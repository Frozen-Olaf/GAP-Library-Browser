package delegate.searchsuggestion;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import delegate.UserInterface;

public class SuggestionDropDownDecorator<C extends JComponent> implements PropertyChangeListener {

    private final UserInterface userInterface;
    private final C invoker;
    private final boolean isInMethodArgumentInputMode;
    private final SuggestionClient<C> suggestionClient;
    private JPopupMenu popupMenu;
    private DefaultListModel<SuggestionEntry> listModel;
    private JList<SuggestionEntry> listComp;
    private JScrollPane scrollList;

    private boolean disableTextEvent;

    private static int searchSuggestionResponseTime = DeferredDocumentListener.FASTER_RESPONSE_TIME;
    private final DeferredDocumentListener ddl = new DeferredDocumentListener(
            DeferredDocumentListener.FASTER_RESPONSE_TIME, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    update(ddl.getIsRemovingText());
                }
            }, false);

    public SuggestionDropDownDecorator(UserInterface userInterface, C invoker, boolean isInMethodArgumentInputMode,
            SuggestionClient<C> suggestionClient) {
        this.userInterface = userInterface;
        this.invoker = invoker;
        this.isInMethodArgumentInputMode = isInMethodArgumentInputMode;
        this.suggestionClient = suggestionClient;

        userInterface.addObserver(this);
    }

    public static <C extends JComponent> void decorate(UserInterface userInterface, C component,
            boolean isInMethodArgumentInputMode, SuggestionClient<C> suggestionClient) {
        SuggestionDropDownDecorator<C> d = new SuggestionDropDownDecorator<>(userInterface, component,
                isInMethodArgumentInputMode, suggestionClient);
        d.init();
    }

    public static int getSearchSuggestionResponseTime() {
        return searchSuggestionResponseTime;
    }

    public static void setSearchSuggestionResponseTime(int newResponseTime) {
        searchSuggestionResponseTime = newResponseTime;
    }

    private void init() {
        initPopup();
        initSuggestionCompListener();
        initInvokerListeners();
    }

    private void initPopup() {
        popupMenu = new JPopupMenu();
        listModel = new DefaultListModel<>();
        listComp = new JList<>(listModel);
        listComp.setLayoutOrientation(JList.VERTICAL);
        listComp.setFocusable(false);
        listComp.setCellRenderer(new CustomListCellRenderer());
        scrollList = new JScrollPane(listComp);

        popupMenu.setFocusable(false);
        popupMenu.setBorder(null);
        popupMenu.add(scrollList);
    }

    private void initSuggestionCompListener() {
        if (invoker instanceof JTextComponent) {
            JTextComponent tc = (JTextComponent) invoker;
            tc.getDocument().addDocumentListener(ddl);
            tc.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (tc.getText().isEmpty())
                        update(false);
                }
            });
        }
    }

    private void update(boolean isTextRemoved) {
        if (disableTextEvent)
            return;
        SwingUtilities.invokeLater(() -> {
            List<SuggestionEntry> suggestions = suggestionClient.getSuggestions(invoker, isInMethodArgumentInputMode,
                    isTextRemoved);
            if (suggestions != null && !suggestions.isEmpty()) {
                showPopup(suggestions);
            } else {
                popupMenu.setVisible(false);
            }
        });
    }

    private void showPopup(List<SuggestionEntry> suggestions) {
        listModel.clear();
        suggestions.forEach(listModel::addElement);
        if (listModel.size() > 20)
            listComp.setVisibleRowCount(20);
        else
            listComp.setVisibleRowCount(listModel.size());
        Point p = suggestionClient.getPopupLocation(invoker);
        if (p == null) {
            return;
        }
        popupMenu.pack();
        listComp.setSelectedIndex(0);
        popupMenu.show(invoker, p.x, p.y);
    }

    private void initInvokerListeners() {
        // not using key inputMap cause that would override the original handling
        invoker.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    selectFromList(e);
                    break;
                case KeyEvent.VK_UP:
                    moveUp(e);
                    break;
                case KeyEvent.VK_DOWN:
                    moveDown(e);
                    break;
                case KeyEvent.VK_LEFT:
                    listModel.clear();
                    popupMenu.setVisible(false);
                    break;
                case KeyEvent.VK_RIGHT:
                    listModel.clear();
                    popupMenu.setVisible(false);
                    break;
                case KeyEvent.VK_ESCAPE:
                    listModel.clear();
                    popupMenu.setVisible(false);
                    break;
                }
            }
        });

        listComp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                selectFromList(e);
            }
        });

        listComp.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (popupMenu.isVisible() && listModel.getSize() > 0) {
                    Point p = e.getPoint();
                    listComp.setSelectedIndex(listComp.locationToIndex(p));
                    e.consume();
                }
            }
        });
    }

    private void selectFromList(InputEvent e) {
        if (popupMenu.isVisible()) {
            int selectedIndex = listComp.getSelectedIndex();
            if (selectedIndex != -1) {
                popupMenu.setVisible(false);
                String selectedValue = listComp.getSelectedValue().getText();
                disableTextEvent = true;
                ddl.setDisableTextEvent(disableTextEvent);
                suggestionClient.setSelectedText(invoker, selectedValue);
                disableTextEvent = false;
                ddl.setDisableTextEvent(disableTextEvent);
                listModel.clear();
                e.consume();
            }
        }
    }

    private void moveDown(KeyEvent keyEvent) {
        if (popupMenu.isVisible() && listModel.getSize() > 0) {
            int selectedIndex = listComp.getSelectedIndex();
            if (selectedIndex < listModel.getSize()) {
                int newSelectedIndex = selectedIndex + 1;
                listComp.setSelectedIndex(newSelectedIndex);
                // Make the selected search suggestion always visible
                listComp.ensureIndexIsVisible(newSelectedIndex);
                keyEvent.consume();
            }
        }
    }

    private void moveUp(KeyEvent keyEvent) {
        if (popupMenu.isVisible() && listModel.getSize() > 0) {
            int selectedIndex = listComp.getSelectedIndex();
            if (selectedIndex > 0) {
                int newSelectedIndex = selectedIndex - 1;
                listComp.setSelectedIndex(newSelectedIndex);
                // Make the selected search suggestion always visible
                listComp.ensureIndexIsVisible(newSelectedIndex);
                keyEvent.consume();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() != userInterface)
            return;
        String propertyName = evt.getPropertyName();
        if (propertyName == "dark") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (popupMenu != null & scrollList != null) {
                        popupMenu.updateUI();
                        scrollList.getVerticalScrollBar().updateUI();
                    }
                }
            });
        } else if (propertyName == "light") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (popupMenu != null & scrollList != null) {
                        popupMenu.updateUI();
                        scrollList.getVerticalScrollBar().updateUI();
                    }
                }
            });
        } else if (propertyName == "response") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    int newResponseTime = (int) evt.getNewValue();
                    ddl.updateDelayTime(newResponseTime);
                }
            });
        }
    }

    private class CustomListCellRenderer extends DefaultListCellRenderer {

        private final Color ltPurple = Color.decode("#6F1AB6");
        private final Color ltSelectedColor = Color.decode("#0063e1");
        private final Color dtTextFontColor = Color.decode("#dedede");
        private final Color dtSelectedBgColor = Color.decode("#0058d0");
        private final Color dtBgColor = Color.decode("#1e1e1e");
        private final Color dtPurple = Color.decode("#8e57f7");

        public CustomListCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            SuggestionEntry entry = (SuggestionEntry) value;
            value = entry.getText();
            Color fg, bg;
            if (UserInterface.getIsInDarkTheme()) {
                fg = isSelected ? Color.WHITE : (entry.getIsSearchHistory() ? dtPurple : dtTextFontColor);
                bg = isSelected ? dtSelectedBgColor : dtBgColor;
            } else {
                fg = isSelected ? Color.WHITE : (entry.getIsSearchHistory() ? ltPurple : Color.BLACK);
                bg = isSelected ? ltSelectedColor : Color.WHITE;
            }
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            c.setForeground(fg);
            c.setBackground(bg);
            return c;
        }

        private static final long serialVersionUID = 1L;
    }

}
