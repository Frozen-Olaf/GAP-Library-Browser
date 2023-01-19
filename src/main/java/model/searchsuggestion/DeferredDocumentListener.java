package model.searchsuggestion;

import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DeferredDocumentListener implements DocumentListener {

    private final Timer timer;
    private boolean isRemovingText;

    private boolean disableTextEvent;

    public DeferredDocumentListener(int timeOut, ActionListener listener, boolean repeats) {
        timer = new Timer(timeOut, listener);
        timer.setRepeats(repeats);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        isRemovingText = false;
        if (!disableTextEvent) {
            timer.restart();
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        isRemovingText = true;
        timer.restart();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        timer.restart();
    }

    public void setDisableTextEvent(boolean val) {
        disableTextEvent = val;
    }

    public boolean getIsRemovingText() {
        return isRemovingText;
    }

    public void setUpdateType(boolean isRemovingText) {
        this.isRemovingText = isRemovingText;
    }

}
