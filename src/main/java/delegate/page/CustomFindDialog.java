package delegate.page;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.AssistanceIconPanel;
import org.fife.rsta.ui.UIUtil;
import org.fife.rsta.ui.search.AbstractFindReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rtextarea.SearchContext;

public class CustomFindDialog extends AbstractFindReplaceDialog {

    /**
     * Our search listener, cached, so we can grab its selected text easily.
     */
    protected SearchListener searchListener;

    /**
     * Creates a new <code>FindDialog</code>.
     *
     * @param owner    The parent dialog.
     * @param listener The component that listens for {@link SearchEvent}s.
     */
    public CustomFindDialog(Dialog owner, SearchListener listener) {
        super(owner);
        init(listener);
    }

    /**
     * Creates a new <code>FindDialog</code>.
     *
     * @param owner    The main window that owns this dialog.
     * @param listener The component that listens for {@link SearchEvent}s.
     */
    public CustomFindDialog(Frame owner, SearchListener listener) {
        super(owner);
        init(listener);
    }

    /**
     * Initializes find dialog-specific initialization stuff.
     *
     * @param listener The component that listens for {@link SearchEvent}s.
     */
    private void init(SearchListener listener) {

        this.searchListener = listener;

        ComponentOrientation orientation = ComponentOrientation.getOrientation(getLocale());

        // Make a panel containing the "Find" edit box.
        JPanel enterTextPane = new JPanel(new SpringLayout());
        enterTextPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        JTextComponent textField = UIUtil.getTextComponent(findTextCombo);
        textField.addFocusListener(new FindFocusAdapter());
        textField.getDocument().addDocumentListener(new FindDocumentListener());
        JPanel temp = new JPanel(new BorderLayout());
        temp.add(findTextCombo);
        AssistanceIconPanel aip = new AssistanceIconPanel(findTextCombo);
        temp.add(aip, BorderLayout.LINE_START);
        if (orientation.isLeftToRight()) {
            enterTextPane.add(findFieldLabel);
            enterTextPane.add(temp);
        } else {
            enterTextPane.add(temp);
            enterTextPane.add(findFieldLabel);
        }

        UIUtil.makeSpringCompactGrid(enterTextPane, 1, 2, // rows, cols
                0, 0, // initX, initY
                6, 6); // xPad, yPad

        // Make a panel containing the inherited search direction radio
        // buttons and the inherited search options.
        JPanel bottomPanel = new JPanel(new BorderLayout());
        temp = new JPanel(new BorderLayout());
        bottomPanel.setBorder(UIUtil.getEmpty5Border());
        temp.add(searchConditionsPanel, BorderLayout.LINE_START);
        JPanel temp2 = new JPanel(new BorderLayout());
        temp2.add(dirPanel, BorderLayout.NORTH);
        temp.add(temp2);
        bottomPanel.add(temp, BorderLayout.LINE_START);

        // Now, make a panel containing all the above stuff.
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(enterTextPane);
        leftPanel.add(bottomPanel);

        // Make a panel containing the action buttons.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 5, 5));
        buttonPanel.add(findNextButton);
        buttonPanel.add(cancelButton);
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(buttonPanel, BorderLayout.NORTH);

        // Put everything into a neat little package.
        JPanel contentPane = new JPanel(new BorderLayout());
        if (orientation.isLeftToRight()) {
            contentPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 5));
        } else {
            contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        }
        contentPane.add(leftPanel);
        contentPane.add(rightPanel, BorderLayout.LINE_END);
        temp = new JPanel(new BorderLayout());
        temp.add(contentPane, BorderLayout.NORTH);
        setContentPane(temp);
        getRootPane().setDefaultButton(findNextButton);
        setTitle(getString("FindDialogTitle"));
        setResizable(true);
        pack();
        setLocationRelativeTo(getParent());

        setSearchContext(new SearchContext());
        addSearchListener(listener);

        applyComponentOrientation(orientation);

    }

    /**
     * Overrides <code>JDialog</code>'s <code>setVisible</code> method; decides
     * whether buttons are enabled.
     *
     * @param visible Whether the dialog should be visible.
     */
    @Override
    public void setVisible(boolean visible) {

        if (visible) {

            // Select text entered into the UI
            String text = searchListener.getSelectedText();
            if (text != null) {
                findTextCombo.addItem(text);
            }

            String selectedItem = findTextCombo.getSelectedString();
            boolean nonEmpty = selectedItem != null && selectedItem.length() > 0;
            findNextButton.setEnabled(nonEmpty);
            super.setVisible(true);
            focusFindTextField();

        }

        else {
            super.setVisible(false);
        }

    }

    /**
     * This method should be called whenever the <code>LookAndFeel</code> of the
     * application changes. This calls
     * <code>SwingUtilities.updateComponentTreeUI(this)</code> and does other
     * necessary things.
     * <p>
     * Note that this is <em>not</em> an override, as JDialogs don't have an
     * <code>updateUI()</code> method.
     */
    public void updateUI() {
        SwingUtilities.updateComponentTreeUI(this);
        pack();
        JTextComponent textField = UIUtil.getTextComponent(findTextCombo);
        textField.addFocusListener(new FindFocusAdapter());
        textField.getDocument().addDocumentListener(new FindDocumentListener());
    }

    private static final long serialVersionUID = 1L;

    /**
     * Listens for changes in the text field (find search field).
     */
    private class FindDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            handleToggleButtons();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            JTextComponent comp = UIUtil.getTextComponent(findTextCombo);
            if (comp.getDocument().getLength() == 0) {
                findNextButton.setEnabled(false);
            } else {
                handleToggleButtons();
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

    }

    /**
     * Listens for the text field gaining focus. All it does is select all text in
     * the combo box's text area.
     */
    private class FindFocusAdapter extends FocusAdapter {

        @Override
        public void focusGained(FocusEvent e) {
            UIUtil.getTextComponent(findTextCombo).selectAll();
        }

    }
}
