import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class Main {

    static final String APP_TITLE = "Expense Manager";
    static final String DATA_FILE = "transactions.txt";
    static final String REPORT_FILE = "expense_report.txt";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseManagerFrame().setVisible(true));
    }

    // Interface
    interface Reportable {
        String generateReport();
    }

    // Abstract class
    static abstract class Transaction implements Reportable {
        private String id;
        private String description;
        private double amount;
        private String category;
        private LocalDate date;
        static int objectsCreated = 0;

        Transaction(String id, String description, double amount,
                String category, LocalDate date) {
            this.id = id;
            this.description = description;
            this.amount = amount;
            this.category = category;
            this.date = date;
            objectsCreated++;
        }

        public String getId() { return id; }
        public String getDescription() { return description; }
        public double getAmount() { return amount; }
        public String getCategory() { return category; }
        public LocalDate getDate() { return date; }

        public abstract String getType();

        public static int getObjectsCreated() {
            return objectsCreated;
        }

        @Override
        public String generateReport() {
            return id + " | " + getType() + " | " + description
                    + " | " + category + " | ₹" + String.format("%.2f", amount)
                    + " | " + date;
        }
    }

    // Single inheritance
    static class Expense extends Transaction {
        Expense(String id, String description, double amount,
                String category, LocalDate date) {
            super(id, description, amount, category, date);
        }

        @Override
        public String getType() { return "Expense"; }
    }

    // Multilevel inheritance: Transaction -> Expense -> FoodExpense
    static class FoodExpense extends Expense {
        FoodExpense(String id, String description, double amount,
                String category, LocalDate date) {
            super(id, description, amount, category, date);
        }

        @Override
        public String getType() { return "Food Expense"; }
    }

    static class Income extends Transaction {
        Income(String id, String description, double amount,
                String category, LocalDate date) {
            super(id, description, amount, category, date);
        }

        @Override
        public String getType() { return "Income"; }
    }

    // User-defined exception
    static class InvalidAmountException extends Exception {
        private static final long serialVersionUID = 1L;

        InvalidAmountException(String message) {
            super(message);
        }
    }

    // Business logic and collections
    static class ExpenseService {

        private final ArrayList<Transaction> transactions = new ArrayList<>();
        private final HashMap<String, Transaction> transactionMap = new HashMap<>();
        private int nextId = 1;

        public List<Transaction> getAll() {
            return new ArrayList<>(transactions);
        }

        public Transaction findById(String id) {
            return transactionMap.get(id);
        }

        // Overloaded method 1
        public void addTransaction(String description, double amount,
                String category, String type, LocalDate date)
                throws InvalidAmountException {

            if (description == null || description.trim().isEmpty()) {
                throw new IllegalArgumentException("Enter a description.");
            }
            if (!Double.isFinite(amount) || amount <= 0) {
                throw new InvalidAmountException(
                        "Amount must be a finite number greater than zero.");
            }
            if (category == null || category.trim().isEmpty()) {
                throw new IllegalArgumentException("Select a category.");
            }

            // Type / category consistency checks
            if ("Food Expense".equals(type) && !"Food".equals(category)) {
                throw new IllegalArgumentException(
                        "A Food Expense must use the Food category.");
            }
            if ("Income".equals(type)
                    && !("Salary".equals(category) || "Other".equals(category))) {
                throw new IllegalArgumentException(
                        "Income must use the Salary or Other category.");
            }

            String id = "TXN" + String.format("%03d", nextId++);
            Transaction transaction;

            if ("Income".equals(type)) {
                transaction = new Income(id, description, amount, category, date);
            } else if ("Food Expense".equals(type)) {
                transaction = new FoodExpense(id, description, amount, category, date);
            } else {
                transaction = new Expense(id, description, amount, category, date);
            }
            addTransaction(transaction);
        }

        // Overloaded method 2
        public void addTransaction(Transaction transaction) {
            Objects.requireNonNull(transaction, "Transaction cannot be null.");
            if (transactionMap.containsKey(transaction.getId())) {
                throw new IllegalArgumentException(
                        "Duplicate transaction ID: " + transaction.getId());
            }
            transactions.add(transaction);
            transactionMap.put(transaction.getId(), transaction);
        }

        public boolean deleteById(String id) {
            Iterator<Transaction> iterator = transactions.iterator();
            while (iterator.hasNext()) {
                Transaction transaction = iterator.next();
                if (transaction.getId().equals(id)) {
                    iterator.remove();
                    transactionMap.remove(id);
                    return true;
                }
            }
            return false;
        }

        public double getTotalIncome() {
            double total = 0;
            for (Transaction t : transactions) {
                if (t instanceof Income) {
                    total += t.getAmount();
                }
            }
            return total;
        }

        public double getTotalExpenses() {
            double total = 0;
            for (Transaction t : transactions) {
                if (!(t instanceof Income)) {
                    total += t.getAmount();
                }
            }
            return total;
        }

        public double getBalance() {
            return getTotalIncome() - getTotalExpenses();
        }

        public HashMap<String, Double> getCategoryTotals() {
            HashMap<String, Double> totals = new HashMap<>();
            for (Transaction t : transactions) {
                if (!(t instanceof Income)) {
                    totals.put(t.getCategory(),
                            totals.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
                }
            }
            return totals;
        }

        public String generateReport() {
            StringBuilder report = new StringBuilder();
            report.append("EXPENSE MANAGER REPORT\n");
            report.append("======================\n\n");
            for (Transaction t : transactions) {
                report.append(t.generateReport()).append("\n");
            }
            report.append("\nTotal Income: ₹").append(String.format("%.2f", getTotalIncome()));
            report.append("\nTotal Expenses: ₹").append(String.format("%.2f", getTotalExpenses()));
            report.append("\nBalance: ₹").append(String.format("%.2f", getBalance()));
            return report.toString();
        }

        // File input (no GUI code here - the caller shows any error)
        public void loadFromFile() throws IOException {
            Path path = Paths.get(DATA_FILE);
            if (!Files.exists(path)) {
                return;
            }

            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

            for (String line : lines) {
                try {
                    String[] p = line.split("\\|", -1);
                    if (p.length != 6) {
                        continue;
                    }

                    String id = p[0];
                    String type = p[1];
                    String description = decode(p[2]);
                    double amount = Double.parseDouble(p[3]);
                    String category = decode(p[4]);
                    LocalDate date = LocalDate.parse(p[5]);

                    Transaction t;
                    if ("Income".equals(type)) {
                        t = new Income(id, description, amount, category, date);
                    } else if ("Food Expense".equals(type)) {
                        t = new FoodExpense(id, description, amount, category, date);
                    } else {
                        t = new Expense(id, description, amount, category, date);
                    }
                    addTransaction(t);

                    try {
                        int number = Integer.parseInt(id.replace("TXN", ""));
                        nextId = Math.max(nextId, number + 1);
                    } catch (NumberFormatException ignored) {
                    }
                } catch (Exception ignored) {
                    // Skip malformed records
                }
            }
        }

        // File output
        public void saveToFile() throws IOException {
            List<String> lines = new ArrayList<>();
            for (Transaction t : transactions) {
                lines.add(t.getId() + "|" + t.getType() + "|"
                        + encode(t.getDescription()) + "|"
                        + t.getAmount() + "|"
                        + encode(t.getCategory()) + "|"
                        + t.getDate());
            }
            Files.write(Paths.get(DATA_FILE), lines, StandardCharsets.UTF_8);
        }

        private String encode(String value) {
            return Base64.getEncoder().encodeToString(
                    value.getBytes(StandardCharsets.UTF_8));
        }

        private String decode(String value) {
            return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
        }
    }

    // Swing GUI, ActionListener and MouseListener
    static class ExpenseManagerFrame extends JFrame
            implements ActionListener, MouseListener {

        private static final long serialVersionUID = 1L;

        private final transient ExpenseService service = new ExpenseService();

        private final JTextField descriptionField = new JTextField();
        private final JTextField amountField = new JTextField();
        private final JTextField dateField = new JTextField(LocalDate.now().toString());
        private final JTextField searchField = new JTextField();

        private final JComboBox<String> categoryBox = new JComboBox<>(
                new String[] { "Food", "Travel", "Shopping", "Bills",
                        "Education", "Health", "Salary", "Other" });

        private final JComboBox<String> typeBox = new JComboBox<>(
                new String[] { "Expense", "Food Expense", "Income" });

        private final DefaultTableModel tableModel = new DefaultTableModel(
                new String[] { "ID", "Type", "Description", "Category",
                        "Amount (₹)", "Date" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        private final JTable table = new JTable(tableModel);

        private final JLabel incomeLabel = new JLabel("₹0.00");
        private final JLabel expenseLabel = new JLabel("₹0.00");
        private final JLabel balanceLabel = new JLabel("₹0.00");
        private final JLabel countLabel = new JLabel("0");
        private final JLabel statusLabel = new JLabel("Welcome!");

        ExpenseManagerFrame() {
            setTitle(APP_TITLE);
            setSize(1050, 700);
            setMinimumSize(new Dimension(850, 600));
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setLocationRelativeTo(null);

            buildUI();

            try {
                service.loadFromFile();
            } catch (IOException ex) {
                showError("Could not load saved transactions: " + ex.getMessage());
            }
            refreshTable();

            // Ask to save when the window is closed
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    int choice = JOptionPane.showConfirmDialog(
                            ExpenseManagerFrame.this,
                            "Save before exiting?", "Exit",
                            JOptionPane.YES_NO_CANCEL_OPTION);

                    if (choice == JOptionPane.CANCEL_OPTION
                            || choice == JOptionPane.CLOSED_OPTION) {
                        return;
                    }
                    if (choice == JOptionPane.YES_OPTION) {
                        try {
                            service.saveToFile();
                        } catch (IOException ex) {
                            showError("Could not save: " + ex.getMessage());
                            return;
                        }
                    }
                    dispose();
                    System.exit(0);
                }
            });
        }

        private void buildUI() {
            setLayout(new BorderLayout(12, 12));
            getContentPane().setBackground(new Color(245, 247, 251));

            JLabel heading = new JLabel("  Expense Manager");
            heading.setFont(new Font("SansSerif", Font.BOLD, 27));
            heading.setForeground(new Color(30, 50, 85));
            heading.setBorder(BorderFactory.createEmptyBorder(15, 12, 5, 5));
            add(heading, BorderLayout.NORTH);

            JPanel center = new JPanel(new BorderLayout(10, 10));
            center.setOpaque(false);
            center.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

            JPanel stats = new JPanel(new GridLayout(1, 4, 10, 10));
            stats.setOpaque(false);
            stats.add(statCard("Total Income", incomeLabel));
            stats.add(statCard("Total Expenses", expenseLabel));
            stats.add(statCard("Balance", balanceLabel));
            stats.add(statCard("Transactions", countLabel));

            JPanel form = buildFormPanel();
            JPanel tablePanel = buildTablePanel();

            JPanel upper = new JPanel(new BorderLayout(10, 10));
            upper.setOpaque(false);
            upper.add(stats, BorderLayout.NORTH);
            upper.add(form, BorderLayout.CENTER);

            center.add(upper, BorderLayout.NORTH);
            center.add(tablePanel, BorderLayout.CENTER);
            add(center, BorderLayout.CENTER);

            statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
            add(statusLabel, BorderLayout.SOUTH);
        }

        private JPanel statCard(String title, JLabel value) {
            JPanel panel = new JPanel(new GridLayout(2, 1, 4, 4));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 225, 235)),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));

            JLabel label = new JLabel(title);
            label.setForeground(new Color(95, 105, 120));
            label.setFont(new Font("SansSerif", Font.PLAIN, 13));

            value.setFont(new Font("SansSerif", Font.BOLD, 20));
            value.setForeground(new Color(35, 75, 130));

            panel.add(label);
            panel.add(value);
            return panel;
        }

        private JPanel buildFormPanel() {
            JPanel panel = new JPanel(new GridLayout(3, 4, 8, 8));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createTitledBorder("Transaction Details"));

            panel.add(new JLabel("Description"));
            panel.add(descriptionField);
            panel.add(new JLabel("Amount"));
            panel.add(amountField);
            panel.add(new JLabel("Category"));
            panel.add(categoryBox);
            panel.add(new JLabel("Type"));
            panel.add(typeBox);
            panel.add(new JLabel("Date (YYYY-MM-DD)"));
            panel.add(dateField);

            JButton addButton = new JButton("Add");
            addButton.setActionCommand("ADD");
            addButton.addActionListener(this);
            panel.add(addButton);

            JButton clearButton = new JButton("Clear");
            clearButton.setActionCommand("CLEAR");
            clearButton.addActionListener(this);
            panel.add(clearButton);

            return panel;
        }

        private JPanel buildTablePanel() {
            JPanel panel = new JPanel(new BorderLayout(8, 8));
            panel.setOpaque(false);

            JPanel tools = new JPanel(new BorderLayout(8, 8));
            tools.setOpaque(false);
            tools.add(new JLabel("Search:"), BorderLayout.WEST);
            tools.add(searchField, BorderLayout.CENTER);

            // Pressing Enter in the search box also searches
            searchField.setActionCommand("SEARCH");
            searchField.addActionListener(this);

            JButton searchButton = new JButton("Search");
            searchButton.setActionCommand("SEARCH");
            searchButton.addActionListener(this);
            tools.add(searchButton, BorderLayout.EAST);

            table.setRowHeight(25);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.addMouseListener(this);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(800, 260));

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttons.setOpaque(false);
            addButton(buttons, "Delete Selected", "DELETE");
            addButton(buttons, "Save", "SAVE");
            addButton(buttons, "Category Summary", "SUMMARY");
            addButton(buttons, "Generate Report", "REPORT");

            panel.add(tools, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(buttons, BorderLayout.SOUTH);
            return panel;
        }

        private void addButton(JPanel panel, String text, String command) {
            JButton button = new JButton(text);
            button.setActionCommand(command);
            button.addActionListener(this);
            panel.add(button);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch (command) {
                case "ADD":
                    addTransactionFromForm();
                    break;
                case "DELETE":
                    deleteSelected();
                    break;
                case "SAVE":
                    saveTransactions();
                    break;
                case "SUMMARY":
                    showCategorySummary();
                    break;
                case "REPORT":
                    generateReportInBackground();
                    break;
                case "CLEAR":
                    clearForm();
                    break;
                case "SEARCH":
                    refreshTable();
                    break;
                default:
                    statusLabel.setText("Unknown action.");
            }
        }

        private void addTransactionFromForm() {
            try {
                String description = descriptionField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                String category = (String) categoryBox.getSelectedItem();
                String type = (String) typeBox.getSelectedItem();
                LocalDate date = LocalDate.parse(dateField.getText().trim());

                service.addTransaction(description, amount, category, type, date);

                refreshTable();
                clearForm();
                statusLabel.setText("Transaction added successfully.");

            } catch (NumberFormatException ex) {
                showError("Enter a valid numeric amount.");
            } catch (InvalidAmountException | IllegalArgumentException ex) {
                showError(ex.getMessage());
            } catch (java.time.format.DateTimeParseException ex) {
                showError("Enter the date in YYYY-MM-DD format.");
            }
        }

        private void deleteSelected() {
            int row = table.getSelectedRow();
            if (row < 0) {
                showError("Please select a transaction to delete.");
                return;
            }

            String id = tableModel.getValueAt(row, 0).toString();
            int choice = JOptionPane.showConfirmDialog(this,
                    "Delete transaction " + id + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION && service.deleteById(id)) {
                refreshTable();
                statusLabel.setText("Transaction deleted.");
            }
        }

        private void saveTransactions() {
            try {
                service.saveToFile();
                statusLabel.setText("Transactions saved to " + DATA_FILE);
                JOptionPane.showMessageDialog(this,
                        "Transactions saved successfully!",
                        "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError("Could not save data: " + ex.getMessage());
            }
        }

        private void showCategorySummary() {
            HashMap<String, Double> totals = service.getCategoryTotals();
            StringBuilder summary = new StringBuilder("EXPENSES BY CATEGORY\n\n");

            if (totals.isEmpty()) {
                summary.append("No expenses recorded yet.");
            } else {
                for (Map.Entry<String, Double> entry : totals.entrySet()) {
                    summary.append(entry.getKey())
                            .append(": ₹")
                            .append(String.format("%.2f", entry.getValue()))
                            .append("\n");
                }
            }

            JOptionPane.showMessageDialog(this, summary.toString(),
                    "Category Summary", JOptionPane.INFORMATION_MESSAGE);
        }

        // Multithreading: write the report file in a background thread
        private void generateReportInBackground() {
            statusLabel.setText("Generating report...");

            // Build the report on the GUI thread so the data is consistent
            final String report = service.generateReport();

            Thread reportThread = new Thread(() -> {
                try {
                    Files.write(Paths.get(REPORT_FILE),
                            report.getBytes(StandardCharsets.UTF_8));

                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Report saved to " + REPORT_FILE);

                        JTextArea textArea = new JTextArea(report, 20, 60);
                        textArea.setEditable(false);
                        textArea.setCaretPosition(0);

                        JOptionPane.showMessageDialog(this,
                                new JScrollPane(textArea),
                                "Expense Report",
                                JOptionPane.INFORMATION_MESSAGE);
                    });
                } catch (IOException ex) {
                    SwingUtilities.invokeLater(() ->
                            showError("Could not write report: " + ex.getMessage()));
                }
            });
            reportThread.start();
        }

        // Double-click a table row to copy it into the form
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    descriptionField.setText(tableModel.getValueAt(row, 2).toString());
                    categoryBox.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                    amountField.setText(tableModel.getValueAt(row, 4).toString());
                    dateField.setText(tableModel.getValueAt(row, 5).toString());
                    typeBox.setSelectedItem(tableModel.getValueAt(row, 1).toString());

                    statusLabel.setText("Copied " + tableModel.getValueAt(row, 0)
                            + " into the form. Click Add to save it as a new entry "
                            + "(this does not edit the original).");
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) { }

        @Override
        public void mouseReleased(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }

        private void clearForm() {
            descriptionField.setText("");
            amountField.setText("");
            dateField.setText(LocalDate.now().toString());
            categoryBox.setSelectedIndex(0);
            typeBox.setSelectedIndex(0);
            table.clearSelection();
        }

        private void refreshTable() {
            tableModel.setRowCount(0);
            String search = searchField.getText().trim().toLowerCase(Locale.ROOT);

            for (Transaction t : service.getAll()) {
                String searchable = (t.getId() + " " + t.getType() + " "
                        + t.getDescription() + " " + t.getCategory() + " "
                        + t.getDate()).toLowerCase(Locale.ROOT);

                if (!search.isEmpty() && !searchable.contains(search)) {
                    continue;
                }

                tableModel.addRow(new Object[] {
                        t.getId(),
                        t.getType(),
                        t.getDescription(),
                        t.getCategory(),
                        String.format("%.2f", t.getAmount()),
                        t.getDate()
                });
            }

            incomeLabel.setText("₹" + String.format("%.2f", service.getTotalIncome()));
            expenseLabel.setText("₹" + String.format("%.2f", service.getTotalExpenses()));
            balanceLabel.setText("₹" + String.format("%.2f", service.getBalance()));
            countLabel.setText(String.valueOf(service.getAll().size()));
        }

        private void showError(String message) {
            JOptionPane.showMessageDialog(this, message,
                    "Expense Manager", JOptionPane.ERROR_MESSAGE);
        }
    }
}