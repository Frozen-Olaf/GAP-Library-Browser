package delegate.searchbar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.tuple.Pair;

import delegate.UserInterface;
import delegate.button.ButtonDecorator;
import model.icon.IconVault;
import model.searchsuggestion.SuggestionClient;
import model.searchsuggestion.SuggestionDropDownDecorator;

public class MethodSearchInputPane extends JPanel {

    private final UserInterface userInterface;
    private final SuggestionClient<JTextComponent> suggestionClient;

    private final MethodSearchArgumentInputBar[] argInputBars = new MethodSearchArgumentInputBar[6];
    private JButton searchBtn;
    private JButton clearBtn;

    public MethodSearchInputPane(UserInterface userInterface, SuggestionClient<JTextComponent> suggestionClient) {
        super(new GridBagLayout());
        this.userInterface = userInterface;
        this.suggestionClient = suggestionClient;
        init();
    }

    public JButton getSearchButton() {
        return searchBtn;
    }

    private void init() {
        for (int i = 0; i < argInputBars.length; i++) {
            MethodSearchArgumentInputBar mc = new MethodSearchArgumentInputBar(i + 1);
            argInputBars[i] = mc;
        }

        clearBtn = new JButton(IconVault.getClearIcon());
        clearBtn.setToolTipText("Clear all input");
        clearBtn.setMargin(new Insets(4, 0, 4, 0));
        Dimension d = clearBtn.getPreferredSize();
        d.setSize(130, d.getHeight());
        clearBtn.setPreferredSize(d);
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAllInputs();
            }
        });
        ButtonDecorator.addDefaultMouseListener(clearBtn);

        searchBtn = new JButton(IconVault.getSearchIcon());
        searchBtn.setToolTipText("Search");
        searchBtn.setMargin(new Insets(4, 0, 4, 0));
        searchBtn.setPreferredSize(d);

        setVisible(false);
        setupLayout();
    }

    private void clearAllInputs() {
        for (MethodSearchArgumentInputBar mb : argInputBars) {
            mb.clear();
        }
    }

    public boolean isEmpty() {
        return getAllArgumentInfo().isEmpty();
    }

    public String getAllArgumentsInStandardInputFormat() {
        String stdFormat = "(";
        List<Pair<String, Boolean>> argInfos = getAllArgumentInfo();
        int validArgCount = argInfos.size();
        for (int i = 0; i < validArgCount; i++) {
            stdFormat += "[";
            Pair<String, Boolean> argInfo = argInfos.get(i);
            String argFilters = argInfo.getKey();
            stdFormat += argFilters;
            if (!argFilters.endsWith("...")) {
                if (argInfo.getValue()) {
                    if (argFilters.isEmpty()) {
                        stdFormat += "...";
                    } else {
                        if (!argFilters.endsWith(",")) {
                            stdFormat += ",";
                        }
                        stdFormat += " ...";
                    }
                }
            }
            stdFormat += "]";
            if (i == validArgCount - 1)
                break;
            stdFormat += ", ";
        }
        stdFormat += ")";
        return stdFormat;
    }

    private List<Pair<String, Boolean>> getAllArgumentInfo() {
        List<Pair<String, Boolean>> args = new ArrayList<>();
        for (MethodSearchArgumentInputBar mb : argInputBars) {
            Pair<String, Boolean> argInfo = mb.getInputInfo();
            if (argInfo == null)
                break;
            else
                args.add(argInfo);
        }
        return args;
    }

    private void setupLayout() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 5, 0, 5);
        for (int i = 0; i < argInputBars.length; i++) {
            add(argInputBars[i], c);
            c.gridy++;
        }

        c.gridwidth = 1;
        c.insets = new Insets(12, 0, 5, 0);
        c.fill = GridBagConstraints.NONE;
        add(clearBtn, c);

        c.gridx = 1;
        add(searchBtn, c);
    }

    private class MethodSearchArgumentInputBar extends JPanel {

        private int argNum;
        private JLabel argumentLabel;
        private CornerRoundedPanel textPanel;
        private JTextField argumentInput;
        private JCheckBox cbSearchForSuperset;

        public MethodSearchArgumentInputBar(int num) {
            super(new BorderLayout());
            argNum = num;
            init();
        }

        public String getText() {
            return argumentInput.getText().trim();
        }

        public boolean isSearchingForSuperset() {
            return cbSearchForSuperset.isSelected();
        }

        public Pair<String, Boolean> getInputInfo() {
            String text = argumentInput.getText();
            return (text.isBlank() && !isSearchingForSuperset()) ? null : Pair.of(getText(), isSearchingForSuperset());
        }

        public void clear() {
            argumentInput.setText("");
            cbSearchForSuperset.setSelected(false);
        }

        private void init() {
            argumentLabel = new JLabel("Argument " + argNum + ":");
            argumentLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            argumentLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 3));

            textPanel = new CornerRoundedPanel(new BorderLayout(), false);
            argumentInput = new JTextField();
            argumentLabel.setLabelFor(argumentInput);
            Dimension s = argumentLabel.getPreferredSize();
            s.setSize(s.width, 32);
            argumentInput.setPreferredSize(s);
            argumentInput.setMinimumSize(new Dimension(0, s.height));
            argumentInput.setOpaque(false);
            argumentInput.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            argumentInput.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    textPanel.setPaintBorder(true);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    textPanel.setPaintBorder(false);
                }
            });
            textPanel.add(argumentInput, BorderLayout.CENTER);

            cbSearchForSuperset = new JCheckBox("Superset");
            cbSearchForSuperset.setToolTipText("Search for superset of these filters");

            add(argumentLabel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
            add(cbSearchForSuperset, BorderLayout.EAST);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

            SuggestionDropDownDecorator.decorate(userInterface, argumentInput, true, suggestionClient);
        }

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
