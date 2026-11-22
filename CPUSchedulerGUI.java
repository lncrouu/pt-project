import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * BATANGAS STATE UNIVERSITY - CICS ALANGILAN
 * PT 101: PLATFORM TECHNOLOGIES FINAL PROJECT
 * TOPIC 2: CPU SCHEDULING SIMULATOR GUI
 */
public class CPUSchedulerGUI extends JFrame {

    private JComboBox<String> algoSelector;
    private PlaceholderTextField tqField;
    private JLabel tqLabel;

    private PlaceholderTextField rowCountField;
    private JTable inputTable;
    private DefaultTableModel inputTableModel;
    private JScrollPane inputTableScroll;
    private JPanel div2Panel;

    private JPanel rightResultsPanel;
    private CardLayout resultsCardLayout;

    // Green Series Colors for Gantt Chart Blocks
    private static final Map<String, Color> GREEN_SERIES = new HashMap<>();
    static {
        GREEN_SERIES.put("P1", new Color(34, 197, 94));    // Emerald Green
        GREEN_SERIES.put("P2", new Color(16, 185, 129));   // Mint Teal
        GREEN_SERIES.put("P3", new Color(22, 163, 74));    // Medium Forest
        GREEN_SERIES.put("P4", new Color(5, 150, 105));    // Deep Jade
        GREEN_SERIES.put("P5", new Color(74, 222, 128));   // Bright Lime
        GREEN_SERIES.put("P6", new Color(21, 128, 61));    // Dark Pine
        GREEN_SERIES.put("P7", new Color(13, 148, 136));   // Teal Green
        GREEN_SERIES.put("P8", new Color(101, 163, 13));   // Olive Lime
        GREEN_SERIES.put("P9", new Color(52, 211, 153));   // Soft Mint
        GREEN_SERIES.put("P10", new Color(20, 83, 45));    // Deep Forest
        GREEN_SERIES.put("Idle", new Color(226, 232, 240)); // Slate Gray
    }

    /**
     * Builds the main window and initializes the simulator interface.
     */
    public CPUSchedulerGUI() {
        setTitle("PT101 Final Project - Topic 2: CPU Scheduling Simulator");
        setSize(1300, 670);
        setMinimumSize(new Dimension(1100, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Root container panel with clean padding
        JPanel rootPanel = new JPanel(new BorderLayout(16, 16));
        rootPanel.setBackground(new Color(248, 250, 252)); // Slate 50 background
        rootPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        // 1. TOP HEADER BAR
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. MAIN SPLIT BODY (Left = Controls & Divisions, Right = Results)
        JPanel mainBodyPanel = new JPanel(new BorderLayout(16, 0));
        mainBodyPanel.setOpaque(false);

        // LEFT COLUMN (Divisions 1, 2, 3)
        JPanel leftColumnPanel = createLeftColumnPanel();
        leftColumnPanel.setPreferredSize(new Dimension(410, 0));
        mainBodyPanel.add(leftColumnPanel, BorderLayout.WEST);

        // RIGHT COLUMN (Placeholder Box -> Results View)
        rightResultsPanel = new JPanel();
        resultsCardLayout = new CardLayout();
        rightResultsPanel.setLayout(resultsCardLayout);
        rightResultsPanel.setOpaque(false);

        // Card 1: Dashed Rectangular Placeholder Box
        JPanel placeholderCard = createPlaceholderCard();
        rightResultsPanel.add(placeholderCard, "PLACEHOLDER");

        // Card 2: Simulated Results Container
        JPanel resultsCard = new JPanel(new BorderLayout(0, 12));
        resultsCard.setOpaque(false);
        rightResultsPanel.add(resultsCard, "RESULTS");

        resultsCardLayout.show(rightResultsPanel, "PLACEHOLDER");
        mainBodyPanel.add(rightResultsPanel, BorderLayout.CENTER);

        rootPanel.add(mainBodyPanel, BorderLayout.CENTER);

        // Wrap root in JScrollPane with VERTICAL_SCROLLBAR_ALWAYS on far right
        JScrollPane mainScrollPane = new JScrollPane(rootPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        setContentPane(mainScrollPane);

        // Initialize state
        updateTQVisibility();
    }

    /**
     * Creates the title and project-member header at the top of the window.
     */
    private JPanel createHeaderPanel() {
        JPanel p = createRoundedPanel(new Color(22, 101, 52), 8); // Forest Green Header
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel titleLabel = new JLabel("• CPU Scheduling Algorithm Simulator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(Color.WHITE);

        JLabel membersLabel = new JLabel("Group Members: Bulanhagui, Leosala, Calingasan, Aberin  |  Course: PT101");
        membersLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        membersLabel.setForeground(new Color(220, 252, 231));

        p.add(titleLabel, BorderLayout.WEST);
        p.add(membersLabel, BorderLayout.EAST);
        return p;
    }

    /**
     * Creates the left-side panel containing all simulator input controls.
     */
    private JPanel createLeftColumnPanel() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        // DIVISION 1: Algorithm Selection
        JPanel div1 = createDivisionPanel("1. Select Scheduling Algorithm");
        div1.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel algoLabel = new JLabel("Algorithm:");
        algoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String[] algos = {
            "First-Come, First-Served (FCFS)",
            "Shortest Job First (SJF)",
            "Shortest Remaining Time First (SRTF)",
            "Round Robin (RR)",
            "★ Compare All Algorithms"
        };
        algoSelector = new JComboBox<>(algos);
        algoSelector.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        algoSelector.addActionListener(e -> updateTQVisibility());

        tqLabel = new JLabel("Time Quantum (TQ):");
        tqLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tqField = new PlaceholderTextField("Enter TQ (e.g., 3)", 10);
        tqField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        gbc.gridx = 0; gbc.gridy = 0; div1.add(algoLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; div1.add(algoSelector, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; div1.add(tqLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; div1.add(tqField, gbc);

        div1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        // DIVISION 2: Process Table Configuration
        div2Panel = createDivisionPanel("2. Configure Process Table");
        div2Panel.setLayout(new BorderLayout(0, 6));

        // Top Row: Left = Rows + Set Rows | Right = Green (+) and Red (-)
        JPanel topControlBar = new JPanel(new BorderLayout());
        topControlBar.setOpaque(false);

        JPanel leftRowControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftRowControls.setOpaque(false);
        JLabel numRowsLabel = new JLabel("Rows:");
        numRowsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rowCountField = new PlaceholderTextField("e.g., 5", 5);
        rowCountField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JButton setRowsBtn = createStyledButton("Set Rows", new Color(241, 245, 249), Color.BLACK);
        setRowsBtn.setBorder(new LineBorder(new Color(203, 213, 225), 1, true));
        setRowsBtn.addActionListener(e -> setRowCountFromField());
        leftRowControls.add(numRowsLabel);
        leftRowControls.add(rowCountField);
        leftRowControls.add(setRowsBtn);

        JPanel rightRowControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        rightRowControls.setOpaque(false);
        JButton addRowBtn = createStyledButton("+", new Color(22, 163, 74), Color.WHITE); // Green
        addRowBtn.setPreferredSize(new Dimension(32, 24));
        addRowBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addRowBtn.setToolTipText("Add Process Row");
        addRowBtn.addActionListener(e -> addSingleRow());

        JButton delRowBtn = createStyledButton("-", new Color(220, 38, 38), Color.WHITE); // Red
        delRowBtn.setPreferredSize(new Dimension(32, 24));
        delRowBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        delRowBtn.setToolTipText("Delete Selected or Last Row");
        delRowBtn.addActionListener(e -> deleteSelectedRow());

        rightRowControls.add(addRowBtn);
        rightRowControls.add(delRowBtn);

        topControlBar.add(leftRowControls, BorderLayout.WEST);
        topControlBar.add(rightRowControls, BorderLayout.EAST);

        // Input Table (Process ID is read-only, AT & BT start blank with placeholders)
        String[] cols = {"Process", "Arrival Time (AT)", "Burst Time (BT)"};
        inputTableModel = new DefaultTableModel(cols, 0) {
            /**
             * Keeps the process ID column read-only.
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Column 0 (Process) is strictly uneditable
            }
        };
        inputTable = new JTable(inputTableModel);
        inputTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        inputTable.setRowHeight(24);
        inputTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Custom Renderer to display muted placeholders when AT/BT cells are blank
        inputTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            /**
             * Formats table values and displays placeholder text for blank timing fields.
             */
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String text = (value == null) ? "" : value.toString().trim();

                if (column == 0) {
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    setForeground(new Color(22, 101, 52));
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else if (text.isEmpty()) {
                    setText(column == 1 ? "Enter AT" : "Enter BT");
                    setFont(new Font("Segoe UI", Font.ITALIC, 11));
                    setForeground(new Color(148, 163, 184)); // Muted slate placeholder
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.CENTER);
                }

                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                return c;
            }
        });

        // Forest Green Table Header
        inputTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        inputTable.getTableHeader().setBackground(new Color(22, 101, 52));
        inputTable.getTableHeader().setForeground(Color.WHITE);

        // Initial clean row with blank AT and BT (shows placeholders)
        inputTableModel.addRow(new Object[]{"P1", "", ""});

        inputTableScroll = new JScrollPane(inputTable);
        inputTableScroll.getViewport().setBackground(Color.WHITE);

        // Bottom Action Buttons: Sample & Reset
        JPanel actionBtns = new JPanel(new GridLayout(1, 2, 6, 0));
        actionBtns.setOpaque(false);

        JButton sampleBtn = createStyledButton("Sample", new Color(30, 41, 59), Color.WHITE);
        sampleBtn.addActionListener(e -> loadSampleData());

        JButton resetBtn = createStyledButton("Reset", new Color(30, 41, 59), Color.WHITE);
        resetBtn.addActionListener(e -> resetToInitialState());

        actionBtns.add(sampleBtn);
        actionBtns.add(resetBtn);

        div2Panel.add(topControlBar, BorderLayout.NORTH);
        div2Panel.add(inputTableScroll, BorderLayout.CENTER);
        div2Panel.add(actionBtns, BorderLayout.SOUTH);

        updateInputTableSize();

        // DIVISION 3: Execution Control (Consistent Flat Rectangular Button)
        JPanel div3 = createDivisionPanel("3. Execution Control");
        div3.setLayout(new BorderLayout());

        JButton runBtn = new JButton("Run Simulation");
        runBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        runBtn.setBackground(new Color(22, 101, 52)); // Forest Green
        runBtn.setForeground(Color.WHITE);
        runBtn.setOpaque(true);
        runBtn.setContentAreaFilled(true);
        runBtn.setBorderPainted(true);
        runBtn.setBorder(new LineBorder(new Color(20, 83, 45), 1, true));
        runBtn.setFocusPainted(false);
        runBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        runBtn.setPreferredSize(new Dimension(380, 40));
        runBtn.addActionListener(e -> executeSimulation());

        div3.add(runBtn, BorderLayout.CENTER);
        div3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 82));

        left.add(div1);
        left.add(Box.createVerticalStrut(10));
        left.add(div2Panel);
        left.add(Box.createVerticalStrut(10));
        left.add(div3);
        left.add(Box.createVerticalGlue());

        return left;
    }

    /**
     * Adjusts the process table height to match the number of rows.
     */
    private void updateInputTableSize() {
        int rows = inputTableModel.getRowCount();
        int tableBodyH = Math.min(200, Math.max(24, rows * 24));
        int scrollPaneH = tableBodyH + 30; // Exact space for header + borders

        inputTable.setPreferredScrollableViewportSize(new Dimension(380, tableBodyH));
        inputTableScroll.setPreferredSize(new Dimension(380, scrollPaneH));
        inputTableScroll.setMinimumSize(new Dimension(380, scrollPaneH));
        inputTableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, scrollPaneH));

        if (div2Panel != null) {
            int totalCardH = scrollPaneH + 86;
            div2Panel.setPreferredSize(new Dimension(410, totalCardH));
            div2Panel.setMinimumSize(new Dimension(410, totalCardH));
            div2Panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, totalCardH));
            div2Panel.revalidate();
            div2Panel.repaint();
        }
    }


    /**
     * Creates the placeholder shown before a simulation is run.
     */
    private JPanel createPlaceholderCard() {
        JPanel placeholder = new JPanel(new BorderLayout());
        placeholder.setOpaque(false);

        JPanel innerBox = new JPanel(new GridBagLayout()) {
            /**
             * Draws the rounded dashed border around the results placeholder.
             */
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 252, 231)); // Soft green background
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Dashed rectangular green border
                Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{8, 6}, 0);
                g2.setStroke(dashed);
                g2.setColor(new Color(22, 101, 52));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 12, 12);
            }
        };
        innerBox.setOpaque(false);

        JLabel msgLabel = new JLabel("Simulation results will appear here after clicking Run Simulation.");
        msgLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        msgLabel.setForeground(new Color(22, 101, 52));

        innerBox.add(msgLabel);
        placeholder.add(innerBox, BorderLayout.CENTER);
        return placeholder;
    }

    /**
     * Enables the time quantum field only for Round Robin or the comparison mode.
     */
    private void updateTQVisibility() {
        int idx = algoSelector.getSelectedIndex();
        boolean needsTQ = (idx == 3 || idx == 4); // RR or Compare All
        tqLabel.setEnabled(needsTQ);
        tqField.setEnabled(needsTQ);
    }

    /**
     * Adds one blank process row to the input table.
     */
    private void addSingleRow() {
        int currentRows = inputTableModel.getRowCount();
        // Warn once when crossing from 10 to 11 rows
        if (currentRows == 10) {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Adding more than 10 processes may make the Gantt chart timeline look crowded.\nDo you want to continue adding rows?",
                "Gantt Chart Crowding Notice",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        int nextNum = currentRows + 1;
        inputTableModel.addRow(new Object[]{"P" + nextNum, "", ""});
        rowCountField.setText(String.valueOf(inputTableModel.getRowCount()));
        updateInputTableSize();
    }

    /**
     * Deletes the selected process row, or the last row when none is selected.
     */
    private void deleteSelectedRow() {
        if (inputTable.isEditing()) {
            inputTable.getCellEditor().stopCellEditing();
        }
        int sel = inputTable.getSelectedRow();
        if (sel != -1 && inputTableModel.getRowCount() > 1) {
            inputTableModel.removeRow(sel);
        } else if (inputTableModel.getRowCount() > 1) {
            inputTableModel.removeRow(inputTableModel.getRowCount() - 1);
        } else {
            JOptionPane.showMessageDialog(this, "At least 1 process row is required.", "Minimum Row Limit", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (int i = 0; i < inputTableModel.getRowCount(); i++) {
            inputTableModel.setValueAt("P" + (i + 1), i, 0);
        }
        rowCountField.setText(String.valueOf(inputTableModel.getRowCount()));
        updateInputTableSize();
    }

    /**
     * Rebuilds the process table using the number entered by the user.
     */
    private void setRowCountFromField() {
        try {
            String raw = rowCountField.getText().trim();
            if (raw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the number of rows first.", "Missing Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int count = Integer.parseInt(raw);
            if (count < 1) {
                JOptionPane.showMessageDialog(this, "Number of rows must be at least 1.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (count > 10) {
                int choice = JOptionPane.showConfirmDialog(
                    this,
                    "You entered " + count + " processes. Having more than 10 processes may cause the Gantt chart timeline to look crowded.\nDo you want to proceed?",
                    "Gantt Chart Crowding Notice",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            if (inputTable.isEditing()) {
                inputTable.getCellEditor().stopCellEditing();
            }
            inputTableModel.setRowCount(0);
            for (int i = 1; i <= count; i++) {
                inputTableModel.addRow(new Object[]{"P" + i, "", ""});
            }
            rowCountField.setText(String.valueOf(count));
            updateInputTableSize();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive integer for rows.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads example process values into the input table.
     */
    private void loadSampleData() {
        if (inputTable.isEditing()) {
            inputTable.getCellEditor().stopCellEditing();
        }
        inputTableModel.setRowCount(0);
        inputTableModel.addRow(new Object[]{"P1", 0, 8});
        inputTableModel.addRow(new Object[]{"P2", 1, 4});
        inputTableModel.addRow(new Object[]{"P3", 2, 2});
        inputTableModel.addRow(new Object[]{"P4", 3, 1});
        inputTableModel.addRow(new Object[]{"P5", 4, 3});
        rowCountField.setText("5");
        if (tqField.getText().trim().isEmpty()) {
            tqField.setText("3");
        }
        updateInputTableSize();
    }

    /**
     * Restores the form and results area to their starting state.
     */
    private void resetToInitialState() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reset all inputs and clear the simulation results?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (inputTable.isEditing()) {
            inputTable.getCellEditor().stopCellEditing();
        }
        inputTableModel.setRowCount(0);
        inputTableModel.addRow(new Object[]{"P1", "", ""});
        rowCountField.setText("");
        algoSelector.setSelectedIndex(0);
        tqField.setText("");
        updateInputTableSize();
        resultsCardLayout.show(rightResultsPanel, "PLACEHOLDER");
    }

    /**
     * Reads the table and validates the process arrival and burst times.
     */
    private List<CPUSchedulerBackend.Process> getProcessesFromTable() throws IllegalArgumentException {
        if (inputTable.isEditing()) {
            inputTable.getCellEditor().stopCellEditing();
        }
        List<CPUSchedulerBackend.Process> list = new ArrayList<>();
        for (int i = 0; i < inputTableModel.getRowCount(); i++) {
            String id = inputTableModel.getValueAt(i, 0).toString().trim();
            Object atObj = inputTableModel.getValueAt(i, 1);
            Object btObj = inputTableModel.getValueAt(i, 2);

            String atStr = (atObj == null) ? "" : atObj.toString().trim();
            String btStr = (btObj == null) ? "" : btObj.toString().trim();

            if (atStr.isEmpty() || btStr.isEmpty()) {
                throw new IllegalArgumentException("Please fill in both Arrival Time (AT) and Burst Time (BT) for " + id + ".");
            }

            int at, bt;
            try {
                at = Integer.parseInt(atStr);
                bt = Integer.parseInt(btStr);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Arrival Time and Burst Time for " + id + " must be valid integers.");
            }

            if (at < 0) throw new IllegalArgumentException("Arrival Time for " + id + " must be 0 or greater.");
            if (bt < 1) throw new IllegalArgumentException("Burst Time for " + id + " must be at least 1.");

            list.add(new CPUSchedulerBackend.Process(id, at, bt, i + 1));
        }
        return list;
    }

    /**
     * Converts a short algorithm code into its full display name.
     */
    private String getFullAlgorithmName(String shortCode) {
        switch (shortCode) {
            case "FCFS": return "First-Come, First-Served (FCFS)";
            case "SJF":  return "Shortest Job First (SJF)";
            case "SRTF": return "Shortest Remaining Time First (SRTF)";
            case "RR":   return "Round Robin (RR)";
            default:     return shortCode;
        }
    }

    /**
     * Runs all four scheduling algorithms and returns their results for comparison.
     */
    private List<CPUSchedulerBackend.SimulationResult> runCompareAllAlgorithms(List<CPUSchedulerBackend.Process> procs, int tq) {
        List<CPUSchedulerBackend.SimulationResult> results = new ArrayList<>();
        results.add(CPUSchedulerBackend.runFCFS(procs));
        results.add(CPUSchedulerBackend.runSJF(procs));
        results.add(CPUSchedulerBackend.runSRTF(procs));
        results.add(CPUSchedulerBackend.runRoundRobin(procs, tq));
        return results;
    }

    /**
     * Validates inputs, runs the selected simulation, and displays the results.
     */
    private void executeSimulation() {
        try {
            // 1. Validate table inputs first
            List<CPUSchedulerBackend.Process> procs = getProcessesFromTable();
            if (procs.isEmpty()) return;

            int algoIdx = algoSelector.getSelectedIndex();
            int tq = 3;

            // 2. Validate Time Quantum only when RR or Compare All is selected
            if (algoIdx == 3 || algoIdx == 4) {
                String tqRaw = tqField.getText().trim();
                if (tqRaw.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a Time Quantum (TQ) value.", "Missing Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    tq = Integer.parseInt(tqRaw);
                    if (tq < 1) throw new NumberFormatException();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Time Quantum (TQ) must be a positive integer (>= 1).", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // 3. Confirmation prompt to prevent accidental misclicks
            String selectedAlgo = algoSelector.getSelectedItem().toString();
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Do you want to run the simulation using " + selectedAlgo + "?",
                "Confirm Simulation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            JPanel resultsCard = (JPanel) rightResultsPanel.getComponent(1);
            resultsCard.removeAll();

            // Main Results Stack Panel
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setOpaque(false);

            if (algoIdx == 4) { // COMPARE ALL
                List<CPUSchedulerBackend.SimulationResult> results = runCompareAllAlgorithms(procs, tq);

                // CARD BOX 1: Gantt Timelines
                JPanel ganttSectionCard = createSectionCard("• Gantt Chart Timeline(s)");

                JPanel chartsContainer = new JPanel();
                chartsContainer.setLayout(new BoxLayout(chartsContainer, BoxLayout.Y_AXIS));
                chartsContainer.setOpaque(false);

                for (CPUSchedulerBackend.SimulationResult res : results) {
                    String fullName = getFullAlgorithmName(res.algorithmName);
                    JLabel nameLbl = new JLabel("• " + fullName + (res.timeQuantum > 0 ? " (TQ=" + res.timeQuantum + ")" : ""));
                    nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    nameLbl.setForeground(new Color(22, 101, 52));

                    GanttChartPanel chart = new GanttChartPanel(res.ganttChart);

                    JPanel p = new JPanel(new BorderLayout(0, 2));
                    p.setOpaque(false);
                    p.add(nameLbl, BorderLayout.NORTH);
                    p.add(chart, BorderLayout.CENTER);
                    p.setPreferredSize(new Dimension(480, 62));

                    chartsContainer.add(p);
                    chartsContainer.add(Box.createVerticalStrut(6));
                }

                ganttSectionCard.add(chartsContainer, BorderLayout.CENTER);
                int dynamicHeight = Math.max(120, (results.size() * 72) + 42);
                ganttSectionCard.setPreferredSize(new Dimension(500, dynamicHeight));
                ganttSectionCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, dynamicHeight));

                contentPanel.add(ganttSectionCard);
                contentPanel.add(Box.createVerticalStrut(10));

                // CARD BOX 2: Comparison Table
                JPanel tableSectionCard = createSectionCard("• Computed Algorithm Table");

                String[] cols = {"Algorithm", "TQ", "Avg WT", "Avg TAT", "Avg RT"};
                DefaultTableModel model = new DefaultTableModel(cols, 0);

                double sumWT = 0, sumTAT = 0, sumRT = 0;
                for (CPUSchedulerBackend.SimulationResult r : results) {
                    model.addRow(new Object[]{
                        getFullAlgorithmName(r.algorithmName),
                        r.timeQuantum > 0 ? r.timeQuantum : "-",
                        String.format("%.2f", r.avgWaitingTime),
                        String.format("%.2f", r.avgTurnaroundTime),
                        String.format("%.2f", r.avgResponseTime)
                    });
                    sumWT += r.avgWaitingTime;
                    sumTAT += r.avgTurnaroundTime;
                    sumRT += r.avgResponseTime;
                }

                int n = results.size();
                model.addRow(new Object[]{
                    "AVERAGE",
                    "-",
                    String.format("%.2f", sumWT / n),
                    String.format("%.2f", sumTAT / n),
                    String.format("%.2f", sumRT / n)
                });

                JTable compTable = createStyledTable(model);
                compTable.getColumnModel().getColumn(0).setPreferredWidth(230);

                JScrollPane compScroll = new JScrollPane(compTable);
                compScroll.getViewport().setBackground(Color.WHITE);
                compScroll.setPreferredSize(new Dimension(500, 160));
                tableSectionCard.add(compScroll, BorderLayout.CENTER);

                tableSectionCard.setPreferredSize(new Dimension(500, 195));
                tableSectionCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 195));
                contentPanel.add(tableSectionCard);
                contentPanel.add(Box.createVerticalStrut(10));

                // CARD BOX 3: Performance Metrics Summary
                JPanel metricsSectionCard = createSectionCard("• Performance Metrics Summary");
                metricsSectionCard.add(createHighlightedMetricBoxes(sumTAT / n, sumWT / n, sumRT / n), BorderLayout.CENTER);
                metricsSectionCard.setPreferredSize(new Dimension(500, 115));
                metricsSectionCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

                contentPanel.add(metricsSectionCard);

            } else { // SINGLE ALGORITHM
                CPUSchedulerBackend.SimulationResult res;
                switch (algoIdx) {
                    case 0: res = CPUSchedulerBackend.runFCFS(procs); break;
                    case 1: res = CPUSchedulerBackend.runSJF(procs); break;
                    case 2: res = CPUSchedulerBackend.runSRTF(procs); break;
                    default: res = CPUSchedulerBackend.runRoundRobin(procs, tq); break;
                }

                // CARD BOX 1: Gantt Timeline
                String titleSuffix = res.timeQuantum > 0 ? " (TQ=" + res.timeQuantum + ")" : "";
                JPanel ganttSectionCard = createSectionCard("• Gantt Chart Timeline (" + getFullAlgorithmName(res.algorithmName) + titleSuffix + ")");
                ganttSectionCard.add(new GanttChartPanel(res.ganttChart), BorderLayout.CENTER);
                ganttSectionCard.setPreferredSize(new Dimension(500, 90));
                ganttSectionCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

                contentPanel.add(ganttSectionCard);
                contentPanel.add(Box.createVerticalStrut(10));

                // CARD BOX 2: Computed Algorithm Table
                JPanel tableSectionCard = createSectionCard("• Computed Algorithm Table");

                String[] cols = {"Process", "AT", "BT", "CT", "TAT", "WT", "RT"};
                DefaultTableModel model = new DefaultTableModel(cols, 0);

                for (CPUSchedulerBackend.Process p : res.processes) {
                    model.addRow(new Object[]{
                        p.id, p.arrivalTime, p.burstTime, p.completionTime,
                        p.turnaroundTime, p.waitingTime, p.responseTime
                    });
                }
                model.addRow(new Object[]{
                    "AVERAGE", "-", "-",
                    String.format("%.2f", res.avgCompletionTime),
                    String.format("%.2f", res.avgTurnaroundTime),
                    String.format("%.2f", res.avgWaitingTime),
                    String.format("%.2f", res.avgResponseTime)
                });

                JTable resTable = createStyledTable(model);
                JScrollPane tableScroll = new JScrollPane(resTable);
                tableScroll.getViewport().setBackground(Color.WHITE);

                int calcH = (res.processes.size() + 1) * 26 + 28;
                tableScroll.setPreferredSize(new Dimension(500, Math.min(200, calcH)));
                tableSectionCard.add(tableScroll, BorderLayout.CENTER);

                int cardH = Math.min(238, calcH + 38);
                tableSectionCard.setPreferredSize(new Dimension(500, cardH));
                tableSectionCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, cardH));

                contentPanel.add(tableSectionCard);
                contentPanel.add(Box.createVerticalStrut(10));

                // CARD BOX 3: Performance Metrics Summary
                JPanel metricsSectionCard = createSectionCard("• Performance Metrics Summary");
                metricsSectionCard.add(createHighlightedMetricBoxes(res.avgTurnaroundTime, res.avgWaitingTime, res.avgResponseTime), BorderLayout.CENTER);
                metricsSectionCard.setPreferredSize(new Dimension(500, 115));
                metricsSectionCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

                contentPanel.add(metricsSectionCard);
            }

            contentPanel.add(Box.createVerticalGlue());

            JScrollPane resultsScroll = new JScrollPane(contentPanel);
            resultsScroll.setBorder(null);
            resultsScroll.setOpaque(false);
            resultsScroll.getViewport().setOpaque(false);

            resultsCard.add(resultsScroll, BorderLayout.CENTER);
            resultsCard.revalidate();
            resultsCard.repaint();

            resultsCardLayout.show(rightResultsPanel, "RESULTS");

        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, iae.getMessage(), "Invalid or Missing Input", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error executing simulation: " + ex.getMessage(), "Simulation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates a bordered section card with a title for the results area.
     */
    private JPanel createSectionCard(String title) {
        JPanel p = createRoundedPanel(Color.WHITE, 8);
        p.setLayout(new BorderLayout(0, 6));
        p.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(22, 101, 52));

        p.add(lbl, BorderLayout.NORTH);
        return p;
    }

    /**
     * Creates the three highlighted average performance metric cards.
     */
    private JPanel createHighlightedMetricBoxes(double avgTAT, double avgWT, double avgRT) {
        JPanel container = new JPanel(new GridLayout(1, 3, 10, 0));
        container.setOpaque(false);

        container.add(createSingleMetricCard("Average Turnaround Time", String.format("%.2f", avgTAT)));
        container.add(createSingleMetricCard("Average Waiting Time", String.format("%.2f", avgWT)));
        container.add(createSingleMetricCard("Average Response Time", String.format("%.2f", avgRT)));

        return container;
    }

    /**
     * Creates one labeled card for a performance metric value.
     */
    private JPanel createSingleMetricCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setOpaque(false);

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLbl.setForeground(new Color(71, 85, 105));

        JPanel highlightBox = new JPanel(new GridBagLayout());
        highlightBox.setBackground(new Color(220, 252, 231)); // Light Green Highlight
        highlightBox.setBorder(new LineBorder(new Color(134, 239, 172), 1, true));

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valLbl.setForeground(new Color(22, 101, 52));

        highlightBox.add(valLbl);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(highlightBox, BorderLayout.CENTER);
        return card;
    }

    /**
     * Creates a formatted table with a green header and alternating row colors.
     */
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(26);

        // Forest Green Table Header
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(22, 101, 52));
        table.getTableHeader().setForeground(Color.WHITE);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            /**
             * Centers table values and highlights the average row.
             */
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row == table.getRowCount() - 1) { // AVERAGE row
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    c.setBackground(new Color(220, 252, 231));
                    c.setForeground(new Color(22, 101, 52));
                } else {
                    c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(Color.BLACK);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        return table;
    }

    /**
     * Creates a titled, rounded panel used by the left-side controls.
     */
    private JPanel createDivisionPanel(String title) {
        JPanel p = createRoundedPanel(Color.WHITE, 8);
        p.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(22, 101, 52)
            ),
            new EmptyBorder(8, 8, 8, 8)
        ));
        return p;
    }

    // Custom JTextField with built-in placeholder support
    private static class PlaceholderTextField extends JTextField {
        private final String placeholder;

        /**
         * Creates a text field that displays helper text while empty.
         */
        public PlaceholderTextField(String placeholder, int columns) {
            super(columns);
            this.placeholder = placeholder;
        }

        /**
         * Draws the placeholder text over the text field when it is empty.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(148, 163, 184));
                g2.setFont(getFont().deriveFont(Font.ITALIC, 11f));
                Insets ins = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, ins.left + 2, y);
                g2.dispose();
            }
        }
    }

    /**
     * Returns the green or teal chart color assigned to a process.
     */
    private static Color getProcessColor(String processId) {
        if (GREEN_SERIES.containsKey(processId)) {
            return GREEN_SERIES.get(processId);
        }
        try {
            int num = Integer.parseInt(processId.replaceAll("[^0-9]", ""));
            float hue = 0.35f + ((num * 0.047f) % 0.14f); // Distinct emerald-to-teal shades
            return Color.getHSBColor(hue, 0.72f, 0.70f);
        } catch (Exception e) {
            return new Color(34, 197, 94);
        }
    }

    private static class GanttChartPanel extends JPanel {
        private final List<CPUSchedulerBackend.GanttBlock> blocks;

        /**
         * Creates a Gantt chart panel from a list of time blocks.
         */
        public GanttChartPanel(List<CPUSchedulerBackend.GanttBlock> blocks) {
            this.blocks = blocks;
            setOpaque(false);
            setPreferredSize(new Dimension(480, 42));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        }

        /**
         * Draws the Gantt chart blocks and their starting and ending times.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (blocks == null || blocks.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth() - 24;
            int blockH = 24;
            int startY = 2;

            int totalDuration = blocks.get(blocks.size() - 1).endTime;
            if (totalDuration == 0) return;

            double scale = (double) width / totalDuration;

            for (CPUSchedulerBackend.GanttBlock b : blocks) {
                int x = 12 + (int) (b.startTime * scale);
                int w = Math.max(1, (int) ((b.endTime - b.startTime) * scale));

                Color bg = getProcessColor(b.processId);
                g2.setColor(bg);
                g2.fillRoundRect(x, startY, w, blockH, 6, 6);

                g2.setColor(new Color(22, 101, 52));
                g2.drawRoundRect(x, startY, w, blockH, 6, 6);

                // Label inside block
                g2.setColor(b.processId.equals("Idle") ? Color.DARK_GRAY : Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(b.processId);
                if (w > textW + 2) {
                    g2.drawString(b.processId, x + (w - textW) / 2, startY + (blockH + fm.getAscent()) / 2 - 2);
                }

                // Start tick number
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(new Color(71, 85, 105));
                g2.drawString(String.valueOf(b.startTime), x - 4, startY + blockH + 12);
            }

            // Final tick number
            CPUSchedulerBackend.GanttBlock last = blocks.get(blocks.size() - 1);
            int lastX = 12 + (int) (last.endTime * scale);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(71, 85, 105));
            g2.drawString(String.valueOf(last.endTime), lastX - 4, startY + blockH + 12);
        }
    }

    /**
     * Creates a panel with a softly rounded background and border.
     */
    private static JPanel createRoundedPanel(Color bg, int cornerRadius) {
        JPanel p = new JPanel() {
            /**
             * Draws the rounded panel background and border.
             */
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            }
        };
        p.setOpaque(false);
        return p;
    }

    /**
     * Creates a consistently styled button for the interface.
     */
    private static JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(4, 8, 4, 8));
        return btn;
    }

    /**
     * Starts the CPU scheduling simulator application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CPUSchedulerGUI().setVisible(true));
    }
}
