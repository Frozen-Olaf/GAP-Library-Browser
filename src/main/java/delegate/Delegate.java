package delegate;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import model.Model;

public class Delegate implements PropertyChangeListener {

    private final UserInterface userInterface;
    private final JFrame frame;
    private final Model model;

    public Delegate(Model model, boolean initWithDarkTheme) {
        this.model = model;
        userInterface = UserInterface.createUI(model, initWithDarkTheme);
        frame = userInterface.getFrame();

        this.model.addObserver(this);
    }

    /**
     * This receives and reacts to property change event from model.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() != model)
            return;
        String propertyName = evt.getPropertyName();
        if (propertyName == "illf") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(frame, evt.getNewValue());
                }
            });
        } else if (propertyName == "404") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(frame, evt.getNewValue() + " not found.");
                }
            });
        } else if (propertyName == "rtdir") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(frame,
                            "GAP root directory not found or illegal:\n" + evt.getNewValue());
                }
            });
        } else if (propertyName == "newrtdir") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(frame,
                            "GAP root directory from this dump file: "+evt.getNewValue()+"\nis different from the previous: " + evt.getOldValue());
                }
            });
        } else if (propertyName == "illflt") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(frame, evt.getNewValue());
                }
            });
        } else if (propertyName == "success") {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(frame,
                            "Successfully loaded the dump file from:\n" + evt.getNewValue());
                }
            });
        }

    }
}