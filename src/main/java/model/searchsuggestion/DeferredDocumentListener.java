package model.searchsuggestion;

import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DeferredDocumentListener implements DocumentListener {

    public static final int FASTER_RESPONSE_TIME = 250;
    public static final int SLOWER_RESPONSE_TIME = 750;
    public static final int RESPONSE_TIM_LOWER_LIMIT = FASTER_RESPONSE_TIME;
    public static final int RESPONSE_TIME_UPPER_LIMIT = 1500;

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

    public void setDisableTextEvent(boolean disableTextEvent) {
        this.disableTextEvent = disableTextEvent;
    }

    public boolean getIsRemovingText() {
        return isRemovingText;
    }

    public void setUpdateType(boolean isRemovingText) {
        this.isRemovingText = isRemovingText;
    }

    public void updateDelayTime(int delay) {
        timer.setDelay(delay);
        timer.setInitialDelay(delay);
    }

}
