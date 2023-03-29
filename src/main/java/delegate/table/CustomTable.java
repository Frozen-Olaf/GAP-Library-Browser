package delegate.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import delegate.UserInterface;
import delegate.page.CodePage;
import delegate.page.Page;
import model.Model;
import model.data.ModelData;

public class CustomTable extends JTable {

    private final UserInterface userInterface;
    private final JFrame frame;
    private final Model model;
    private final Page page;

    private int filePathColumnIndex;
    private int cursor = Cursor.DEFAULT_CURSOR;

    public CustomTable(UserInterface userInterface, Model model, Page page, CustomTableModel tableModel) {
        super(tableModel);
        this.userInterface = userInterface;
        frame = userInterface.getFrame();
        this.model = model;
        this.page = page;

        filePathColumnIndex = tableModel.getColumnCount() - 2;

        init();
    }

    public static CustomTable create(UserInterface userInterface, Model model, Page page, CustomTableModel tableModel) {
        return new CustomTable(userInterface, model, page, tableModel);
    }

    public int getFilePathColumnIndex() {
        return filePathColumnIndex;
    }

    private void init() {
        renderTable();
        initTableMouseListener();
    }

    private void renderTable() {
        CustomTableCellRenderer renderer = new CustomTableCellRenderer();
        int tableColNum = getColumnCount();
        for (int i = 0; i < tableColNum; i++) {
            TableColumn tc = getColumnModel().getColumn(i);
            if (i == filePathColumnIndex - 1) {
                tc.setPreferredWidth(40);
            } else if (i == filePathColumnIndex + 1) {
                tc.setPreferredWidth(50);
            }
            tc.setCellRenderer(renderer);
        }

        setShowHorizontalLines(true);
        setShowVerticalLines(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellSelectionEnabled(true);
    }

    private void initTableMouseListener() {
        addMouseListener(new MouseAdapter() {
            boolean hasAlreadyOneClick;

            @Override
            public void mouseClicked(MouseEvent e) {
                String path = getFullFilePathIfSelected();
                if (path != null) {
                    if (hasAlreadyOneClick) {
                        openFileFromPath(e, path, true);
                        hasAlreadyOneClick = false;
                    } else {
                        hasAlreadyOneClick = true;
                        if (openFileFromPath(e, path, false)) {
                            hasAlreadyOneClick = false;
                            return;
                        } else {
                            Object limit = Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
                            int timerLimit = (limit instanceof Integer) ? (Integer) limit : 500;
                            Timer t = new Timer("doubleclickTimer", false);
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    hasAlreadyOneClick = false;
                                }
                            }, timerLimit);
                        }
                    }
                }
                e.consume();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (cursor == Cursor.HAND_CURSOR) {
                    setCursor(Cursor.getDefaultCursor());
                    cursor = Cursor.DEFAULT_CURSOR;
                }
            }

            /**
             * 
             * @return the full path to the file in selected cell, or null if the selected
             *         value is not a file path.
             */
            private String getFullFilePathIfSelected() {
                int selectedRow = getSelectedRow();
                int selectedCol = getSelectedColumn();
                // Avoid invalid index exception
                if (selectedRow == -1 || selectedCol == -1)
                    return null;
                if (selectedCol == filePathColumnIndex) {
                    Object val = getValueAt(selectedRow, selectedCol);
                    if (val != null) {
                        String path = val.toString();
                        if (path.startsWith(".")) {
                            path = ModelData.getGapRootDir() + path.substring(2);
                        }
                        return path;
                    }
                }
                return null;
            }

            private boolean openFileFromPath(MouseEvent e, String path, boolean openInNewPage) {
                if (openInNewPage) {
                    String codeRange = (String) getValueAt(getSelectedRow(), getSelectedColumn() + 1);
                    String name = path + "@" + codeRange;
                    if (!UserInterface.getMultiPage().changeToPage(name, false)) {
                        CodePage p = new CodePage(userInterface, model, name);
                        UserInterface.getMultiPage().addPage(p.getName(), p);
                        UserInterface.getMultiPage().changeToPage(p, false);
                        page.setNext(p);
                        p.setPrev(page);
                        return true;
                    }
                } else {
                    try {
                        if (e.isShiftDown()) {
                            Desktop.getDesktop().browseFileDirectory(new File(path));
                            return true;
                        } else if (e.isControlDown()) {
                            Desktop.getDesktop().open(new File(path));
                            return true;
                        }

                    } catch (UnsupportedOperationException uoe) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                JOptionPane.showMessageDialog(frame,
                                        "Opening the file or driectory directly from this browser is NOT supported by your OS.");
                            }
                        });
                    } catch (IllegalArgumentException iae) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                JOptionPane.showMessageDialog(frame, "This file is not found on this path.");
                            }
                        });
                    } catch (IOException ioe) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                JOptionPane.showMessageDialog(frame, ioe.getMessage());
                            }
                        });
                    }
                }
                return false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point pt = e.getPoint();
                if (cursor == Cursor.DEFAULT_CURSOR && columnAtPoint(pt) == filePathColumnIndex) {
                    if (getValueAt(rowAtPoint(pt), columnAtPoint(pt)) != null) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        cursor = Cursor.HAND_CURSOR;
                    }
                } else if (cursor == Cursor.HAND_CURSOR && columnAtPoint(pt) != filePathColumnIndex) {
                    setCursor(Cursor.getDefaultCursor());
                    cursor = Cursor.DEFAULT_CURSOR;
                }
            }
        });
    }

    private class CustomTableCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            if (row == table.getSelectedRow()) {
                if (UserInterface.getIsInDarkTheme())
                    setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#c29500")));
                else
                    setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#FFC300")));
            }
            if (column == filePathColumnIndex - 1 || column == filePathColumnIndex + 1)
                setHorizontalAlignment(SwingConstants.CENTER);
            else
                setHorizontalAlignment(SwingConstants.LEFT);

            if (value != null) {
                if (column == filePathColumnIndex) {
                    boolean selected = (row == table.getSelectedRow() && column == table.getSelectedColumn());
                    String color = selected ? "#ffffff" : (UserInterface.getIsInDarkTheme() ? "#419cff" : "#0068da");
                    String path = value.toString();
                    setText("<html><u><font color='" + color + "'>" + path + "</u></html>");
                    if (path.startsWith(".")) {
                        path = ModelData.getGapRootDir() + path.substring(2);
                    }
                    setToolTipText(path);
                } else
                    setToolTipText(value.toString());
            }
            return this;
        }

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}