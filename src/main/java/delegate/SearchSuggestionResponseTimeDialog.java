package delegate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fife.rsta.ui.EscapableDialog;

import delegate.button.ButtonDecorator;
import model.searchsuggestion.DeferredDocumentListener;
import model.searchsuggestion.SuggestionDropDownDecorator;

public class SearchSuggestionResponseTimeDialog extends EscapableDialog {

    private JSlider slider;
    private JButton okButton;
    private JButton cancelButton;
    private PropertyChangeSupport UIChangeNotifier;

    /**
     * Creates a new <code>GoToDialog</code>.
     *
     * @param owner The parent window.
     */
    public SearchSuggestionResponseTimeDialog(Frame owner, PropertyChangeSupport notifier, String name) {
        super(owner, name);
        UIChangeNotifier = notifier;
        init();
    }

    public JSlider getSlider() {
        return slider;
    }

    private void init() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel infoLabel = new JLabel("Please drag the slide bar to specify the response time");
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JLabel valueLabel = new JLabel("Response Time");
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        slider = new JSlider(DeferredDocumentListener.RESPONSE_TIM_LOWER_LIMIT,
                DeferredDocumentListener.RESPONSE_TIME_UPPER_LIMIT,
                SuggestionDropDownDecorator.getSearchSuggestionResponseTime());
        slider.setPaintTrack(true);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(125);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int delayTime = slider.getValue();
                valueLabel.setText("Response Time: " + delayTime);
                if (!slider.getValueIsAdjusting()) {
                    UIChangeNotifier.firePropertyChange("response", 0, delayTime);
                    SuggestionDropDownDecorator.setSearchSuggestionResponseTime(delayTime);
                }
            }
        });
        slider.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchSuggestionResponseTimeDialog.this.setVisible(false);
            }
        };
        okButton.addActionListener(al);
        cancelButton.addActionListener(al);
        ButtonDecorator.addDefaultMouseListenerWithBorderAlwaysDrawn(okButton);
        ButtonDecorator.addDefaultMouseListenerWithBorderAlwaysDrawn(cancelButton);

        JPanel bottomPanel = createButtonPanel(okButton, cancelButton);
        bottomPanel.add(valueLabel, BorderLayout.PAGE_START);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(slider, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        getRootPane().setDefaultButton(okButton);
        setVisible(false);
        Dimension d = new Dimension(400, 200);
        setSize(d);
        setMinimumSize(d);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }

    /**
     * Returns a panel containing the OK and Cancel buttons. This panel is added to
     * the bottom of this dialog. Applications that don't like these buttons
     * right-aligned in the dialog can override this method to change that
     * behaviour.
     *
     * @param ok     The OK button.
     * @param cancel The Cancel button.
     * @return A panel containing the two buttons.
     */
    private JPanel createButtonPanel(JButton ok, JButton cancel) {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.LINE_END);
        return bottomPanel;
    }

    private static final long serialVersionUID = 1L;
}
