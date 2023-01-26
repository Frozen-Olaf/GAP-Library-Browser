package delegate.page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.LineNumberList;
import org.fife.ui.rtextarea.RTextScrollPane;

import delegate.UserInterface;
import model.Model;

public class CodePage extends Page {

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
    private RTextScrollPane sp;

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
        codeText.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        Color lineHighlightColor = UserInterface.getIsInDarkTheme() ? DARK_LINE_HIGHLIGHT_COLOR
                : LIGHT_LINE_HIGHLIGHT_COLOR;
        codeText.setCurrentLineHighlightColor(lineHighlightColor);
        if (!isNewEmptyCodePage) {
            initCodeContentAndHighlight();
        }

        codeText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fileEditStateUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fileEditStateUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fileEditStateUpdate();
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
        });

        LineNumberList lineNumberBar = new LineNumberList(codeText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D graphics = (Graphics2D) g;
                graphics.setColor(UIManager.getColor("TextArea.background"));
                graphics.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }

            private static final long serialVersionUID = 1L;
        };

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
                    codeText.setBackground(UIManager.getColor("TextArea.background"));
                    codeText.setForeground(UIManager.getColor("TextArea.foreground"));
                    codeText.setSelectionColor(UIManager.getColor("TextArea.selectionBackground"));

                    if (propertyName == "light") {
                        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#d4d4d4")));
                        codeText.setCurrentLineHighlightColor(LIGHT_LINE_HIGHLIGHT_COLOR);
                    } else if (propertyName == "dark") {
                        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#363636")));
                        codeText.setCurrentLineHighlightColor(DARK_LINE_HIGHLIGHT_COLOR);
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

    private static final long serialVersionUID = 1L;
}
