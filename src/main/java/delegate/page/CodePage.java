package delegate.page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.LineNumberList;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import delegate.UserInterface;
import model.Model;

public class CodePage extends Page implements SearchListener {

    private static final Color LIGHT_LINE_HIGHLIGHT_COLOR = Color.decode("#f0f4fc");
    private static final Color DARK_LINE_HIGHLIGHT_COLOR = Color.decode("#38343c");

    private static int newEmptyCodePageCount = 0;

    private final boolean isNewEmptyCodePage;

    private final String filePath;
    private final Integer[] codeRange;
    private JPanel headerTop;
    private JPanel infoPanel;
    private JTextField info;
    private JCheckBox cbSearchBar;
    private RSyntaxTextArea codeText;
    private LineNumberList lineNumberBar;
    private RTextScrollPane sp;

    private CustomFindDialog findDialog;
    private ReplaceDialog replaceDialog;

    private boolean highlightOn = true;
    private boolean isEdited;

    public CodePage(UserInterface userInterface, Model model, String name) {
        super(userInterface, model);
        isNewEmptyCodePage = false;
        String[] info = name.split("@");
        filePath = info[0];
        codeRange = Arrays.stream(info[1].split("-")).map(Integer::parseInt).toArray(Integer[]::new);
        init();
        setName(name.substring(name.lastIndexOf("/") + 1));
    }

    public CodePage(UserInterface userInterface, Model model) {
        super(userInterface, model);
        isNewEmptyCodePage = true;
        filePath = "Untitled" + ++newEmptyCodePageCount;
        codeRange = null;
        init();
        setName(filePath);
    }

    public RSyntaxTextArea getCodeTextArea() {
        return codeText;
    }

    public static int getNewEmptyCodePageCount() {
        return newEmptyCodePageCount;
    }

    public static void decreaseNewEmptyCodePageCount() {
        newEmptyCodePageCount--;
    }

    public String getCodeTextContent() {
        return (codeText != null) ? codeText.getText() : null;
    }

    public void updateFileInfoDisplay(String filePath) {
        setName(filePath.substring(filePath.lastIndexOf("/") + 1));
        info.setText(filePath);
        info.setToolTipText(filePath);
        codeText.discardAllEdits();
    }

    public void showFindDialog() {
        replaceDialog.setVisible(false);
        findDialog.setVisible(true);
    }

    public void showReplaceDialog() {
        findDialog.setVisible(false);
        replaceDialog.setVisible(true);
    }

    public void showGoToLineDialog() {
        findDialog.setVisible(false);
        replaceDialog.setVisible(false);

        CustomGoToDialog dialog = new CustomGoToDialog(frame);
        dialog.setMaxLineNumberAllowed(codeText.getLineCount());
        dialog.setVisible(true);
        int line = dialog.getLineNumber();
        if (line > 0) {
            try {
                codeText.setCaretPosition(codeText.getLineStartOffset(line - 1));
            } catch (BadLocationException ble) { // Never happens
                UIManager.getLookAndFeel().provideErrorFeedback(codeText);
                ble.printStackTrace();
            }
        }
    }

    private void init() {
        contentPanel = new JPanel(new BorderLayout());
        initHeader();
        contentPanel.add(header, BorderLayout.NORTH);

        initCodeContentDisplayer();
        contentPanel.add(sp, BorderLayout.CENTER);

        JPanel invisible = new JPanel();
        invisible.setVisible(false);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, contentPanel, invisible);
        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerSize(0);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(1.0);
        add(splitPane, BorderLayout.CENTER);

        findDialog = new CustomFindDialog(frame, this);
        replaceDialog = new ReplaceDialog(frame, this);
        // This ties the properties of the two dialogs together (match case,
        // regex, etc.).
        SearchContext context = findDialog.getSearchContext();
        replaceDialog.setSearchContext(context);
        
        WindowFocusListener wfl = new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    userInterface.setMenuBarEnabled(false);
                });
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    userInterface.setMenuBarEnabled(true);
                });
            }
        };
        findDialog.addWindowFocusListener(wfl);
        replaceDialog.addWindowFocusListener(wfl);
        findDialog.setVisible(false);
        replaceDialog.setVisible(false);
    }

    private void initHeader() {
        header = new JPanel(new BorderLayout());
        if (UserInterface.getIsInDarkTheme())
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#363636")));
        else
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#d4d4d4")));

        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        headerTop = new JPanel(new BorderLayout());
        headerTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        btnPanel.setMinimumSize(btnPanel.getPreferredSize());
        headerTop.add(btnPanel, BorderLayout.WEST);

        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 5));

        info = new JTextField("File: " + filePath);
        info.setToolTipText(info.getText());
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

        constructSearchBar(false);
        searchBar.setVisible(false);
        searchBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        cbSearchBar = new JCheckBox("Search bar");
        cbSearchBar.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (cbSearchBar.isSelected()) {
                    searchBar.setVisible(true);
                    headerTop.setBorder(BorderFactory.createEmptyBorder());
                } else {
                    searchBar.setVisible(false);
                    headerTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
                }

            }
        });
        infoPanel.add(cbSearchBar, BorderLayout.EAST);
        headerTop.add(infoPanel, BorderLayout.CENTER);
        header.add(headerTop, BorderLayout.NORTH);
        header.add(searchBar, BorderLayout.SOUTH);
    }

    private void initCodeContentDisplayer() {
        codeText = new RSyntaxTextArea();
        codeText.setForeground(UIManager.getColor("TextArea.foreground"));
        codeText.setBackground(UIManager.getColor("TextArea.background"));
        codeText.setSelectionColor(UIManager.getColor("TextArea.selectionBackground"));
        codeText.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        codeText.setCodeFoldingEnabled(false);
        codeText.setMarkOccurrences(true);
        codeText.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

        if (UserInterface.getIsInDarkTheme()) {
            codeText.setCurrentLineHighlightColor(DARK_LINE_HIGHLIGHT_COLOR);
            codeText.setMarkAllHighlightColor(Color.decode("#0f6614"));
        } else {
            codeText.setCurrentLineHighlightColor(LIGHT_LINE_HIGHLIGHT_COLOR);
            codeText.setMarkAllHighlightColor(Color.decode("#fcc82b"));
        }

        if (!isNewEmptyCodePage) {
            initCodeContentAndHighlight();
        }

        codeText.getDocument().addDocumentListener(new DocumentListener() {
            private int initialRowCount = codeText.getRows();

            @Override
            public void insertUpdate(DocumentEvent e) {
                fileEditStateUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fileEditStateUpdate();
                if (checkRowCountChange())
                    lineNumberBar.repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            private void fileEditStateUpdate() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        isEdited = codeText.canUndo();
                        if (isEdited && !info.getText().endsWith("*"))
                            info.setText(info.getText() + "*");
                        else if (!isEdited && info.getText().endsWith("*"))
                            info.setText(info.getText().substring(0, info.getText().length() - 1));
                    }
                });
            }

            private boolean checkRowCountChange() {
                int newRowCount = codeText.getLineCount();
                if (newRowCount != initialRowCount) {
                    initialRowCount = newRowCount;
                    return true;
                }
                return false;
            }
        });

        lineNumberBar = new LineNumberList(codeText);
        if (UserInterface.getIsInDarkTheme())
            lineNumberBar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.decode("#363636")));
        else
            lineNumberBar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.decode("#d4d4d4")));

        sp = new RTextScrollPane(codeText);
        sp.setRowHeaderView(lineNumberBar);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    }

    private void initCodeContentAndHighlight() {
        String content;
        try {
            content = model.getModelData().codeContentOf(filePath);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(frame, ioe.getMessage() + " ;(");
            return;
        }
        codeText.setText(content);
        codeText.discardAllEdits();

        int startPos = 0;
        for (int i = 0; i < codeRange[1]; i++) {
            int lineSepIndex = content.indexOf(System.getProperty("line.separator"), startPos);
            if (lineSepIndex < 0 && startPos > 0) {
                // handling the case where the end of code range reaches EOF.
                startPos += content.substring(startPos).length();
                break;
            }
            startPos = lineSepIndex + 1;
        }
        if (startPos > 0)
            startPos--;
        codeText.setCaretPosition(startPos);

        Highlighter hl = codeText.getHighlighter();
        try {
            hl.addHighlight(codeText.getLineStartOffset(codeRange[0] - 1), codeText.getLineEndOffset(codeRange[1] - 1),
                    new DefaultHighlighter.DefaultHighlightPainter(UIManager.getColor("TextArea.selectionBackground")));
        } catch (BadLocationException e) {
        }

        codeText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!isEdited && highlightOn) {
                    hl.removeAllHighlights();
                    highlightOn = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    if (!isEdited && !highlightOn) {
                        hl.addHighlight(codeText.getLineStartOffset(codeRange[0] - 1),
                                codeText.getLineEndOffset(codeRange[1] - 1),
                                new DefaultHighlighter.DefaultHighlightPainter(
                                        UIManager.getColor("TextArea.selectionBackground")));
                        highlightOn = true;
                    }
                } catch (BadLocationException ble) {
                }
            }
        });

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() != userInterface)
            return;
        String propertyName = evt.getPropertyName();
        if (propertyName == "light" || propertyName == "dark") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    findDialog.updateUI();
                    replaceDialog.updateUI();

                    if (propertyName == "light") {
                        Color light = Color.decode("#d4d4d4");
                        lineNumberBar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, light));
                        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, light));
                        codeText.setCurrentLineHighlightColor(LIGHT_LINE_HIGHLIGHT_COLOR);
                        codeText.setMarkAllHighlightColor(Color.decode("#fcc82b"));
                    } else if (propertyName == "dark") {
                        Color dark = Color.decode("#363636");
                        lineNumberBar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, dark));
                        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, dark));
                        codeText.setCurrentLineHighlightColor(DARK_LINE_HIGHLIGHT_COLOR);
                        codeText.setMarkAllHighlightColor(Color.decode("#0f6614"));
                    }
                    if (!isNewEmptyCodePage) {
                        if (!isEdited && highlightOn) {
                            Highlighter hl = codeText.getHighlighter();
                            hl.removeAllHighlights();
                            try {
                                hl.addHighlight(codeText.getLineStartOffset(codeRange[0] - 1),
                                        codeText.getLineEndOffset(codeRange[1] - 1),
                                        new DefaultHighlighter.DefaultHighlightPainter(
                                                UIManager.getColor("TextArea.selectionBackground")));
                            } catch (BadLocationException e) {
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void searchEvent(SearchEvent e) {
        SearchEvent.Type type = e.getType();
        SearchContext context = e.getSearchContext();
        SearchResult result;

        switch (type) {
        default: // Prevent FindBugs warning later
        case MARK_ALL:
            result = SearchEngine.markAll(codeText, context);
            break;
        case FIND:
            result = SearchEngine.find(codeText, context);
            if (!result.wasFound() || result.isWrapped()) {
                UIManager.getLookAndFeel().provideErrorFeedback(codeText);
            }
            break;
        case REPLACE:
            result = SearchEngine.replace(codeText, context);
            if (!result.wasFound() || result.isWrapped()) {
                UIManager.getLookAndFeel().provideErrorFeedback(codeText);
            }
            break;
        case REPLACE_ALL:
            result = SearchEngine.replaceAll(codeText, context);
            JOptionPane.showMessageDialog(frame, result.getCount() + " occurrences replaced.");
            break;
        }
    }

    @Override
    public String getSelectedText() {
        return codeText.getSelectedText();
    }

    private static final long serialVersionUID = 1L;
}
