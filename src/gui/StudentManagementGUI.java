package gui;

import dao.StudentDAO;
import exception.InvalidStudentDataException;
import exception.StudentNotFoundException;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Swing-based desktop GUI for the Student Management System.
 * Provides Add / Update / Delete / Search / Display functionality
 * backed by StudentDAO (CSV file storage).
 */
public class StudentManagementGUI extends JFrame {

    private final StudentDAO dao;

    // Form fields
    private JTextField idField, nameField, ageField, courseField, emailField, phoneField;
    private JTextField searchField;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public StudentManagementGUI() {
        dao = new StudentDAO("data/students.csv");

        setTitle("Student Management System");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    // ---------- UI BUILDERS ----------

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(6);
        idField.setEditable(false); // auto-generated
        nameField = new JTextField(15);
        ageField = new JTextField(5);
        courseField = new JTextField(12);
        emailField = new JTextField(15);
        phoneField = new JTextField(12);

        int row = 0;
        addFormRow(panel, gbc, row++, "Student ID:", idField, "Age:", ageField);
        addFormRow(panel, gbc, row++, "Name:", nameField, "Course:", courseField);
        addFormRow(panel, gbc, row++, "Email:", emailField, "Phone:", phoneField);

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row,
                             String label1, JComponent field1, String label2, JComponent field2) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label1), gbc);
        gbc.gridx = 1;
        panel.add(field1, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel(label2), gbc);
        gbc.gridx = 3;
        panel.add(field2, gbc);
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        searchPanel.add(new JLabel("Search (ID / Name / Course):"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);

        searchBtn.addActionListener(this::onSearch);
        showAllBtn.addActionListener(e -> refreshTable());

        // Table
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Age", "Course", "Email", "Phone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton addBtn = new JButton("Add Student");
        JButton updateBtn = new JButton("Update Student");
        JButton deleteBtn = new JButton("Delete Student");
        JButton clearBtn = new JButton("Clear Form");

        addBtn.addActionListener(this::onAdd);
        updateBtn.addActionListener(this::onUpdate);
        deleteBtn.addActionListener(this::onDelete);
        clearBtn.addActionListener(e -> clearForm());

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        return panel;
    }

    // ---------- EVENT HANDLERS ----------

    private void onAdd(ActionEvent e) {
        try {
            Student student = buildStudentFromForm(true);
            dao.addStudent(student);
            JOptionPane.showMessageDialog(this, "Student added successfully.");
            clearForm();
            refreshTable();
        } catch (InvalidStudentDataException ex) {
            showError("Validation Error", ex.getMessage());
        } catch (IOException ex) {
            showError("File Error", "Could not save student data: " + ex.getMessage());
        }
    }

    private void onUpdate(ActionEvent e) {
        if (idField.getText().trim().isEmpty()) {
            showError("Selection Required", "Select a student from the table first.");
            return;
        }
        try {
            Student student = buildStudentFromForm(false);
            dao.updateStudent(student);
            JOptionPane.showMessageDialog(this, "Student updated successfully.");
            clearForm();
            refreshTable();
        } catch (InvalidStudentDataException ex) {
            showError("Validation Error", ex.getMessage());
        } catch (StudentNotFoundException ex) {
            showError("Not Found", ex.getMessage());
        } catch (IOException ex) {
            showError("File Error", "Could not update student data: " + ex.getMessage());
        }
    }

    private void onDelete(ActionEvent e) {
        if (idField.getText().trim().isEmpty()) {
            showError("Selection Required", "Select a student from the table first.");
            return;
        }
        int id = Integer.parseInt(idField.getText().trim());
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete student ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dao.deleteStudent(id);
            JOptionPane.showMessageDialog(this, "Student deleted successfully.");
            clearForm();
            refreshTable();
        } catch (StudentNotFoundException ex) {
            showError("Not Found", ex.getMessage());
        } catch (IOException ex) {
            showError("File Error", "Could not delete student data: " + ex.getMessage());
        }
    }

    private void onSearch(ActionEvent e) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }
        try {
            List<Student> results = dao.search(keyword);
            populateTable(results);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No matching students found.");
            }
        } catch (IOException ex) {
            showError("File Error", "Could not search student data: " + ex.getMessage());
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        ageField.setText(tableModel.getValueAt(row, 2).toString());
        courseField.setText(tableModel.getValueAt(row, 3).toString());
        emailField.setText(tableModel.getValueAt(row, 4).toString());
        phoneField.setText(tableModel.getValueAt(row, 5).toString());
    }

    // ---------- HELPERS ----------

    /** Builds and validates a Student object from the current form fields. */
    private Student buildStudentFromForm(boolean isNew) throws InvalidStudentDataException, IOException {
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String course = courseField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty()) {
            throw new InvalidStudentDataException("Name cannot be empty.");
        }
        if (course.isEmpty()) {
            throw new InvalidStudentDataException("Course cannot be empty.");
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException nfe) {
            throw new InvalidStudentDataException("Age must be a valid number.");
        }
        if (age <= 0 || age > 120) {
            throw new InvalidStudentDataException("Age must be between 1 and 120.");
        }

        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidStudentDataException("Email format is invalid.");
        }
        if (!phone.isEmpty() && !phone.matches("\\d{7,15}")) {
            throw new InvalidStudentDataException("Phone must contain 7-15 digits only.");
        }
        if (name.contains(",") || course.contains(",") || email.contains(",") || phone.contains(",")) {
            throw new InvalidStudentDataException("Fields cannot contain commas (CSV storage limitation).");
        }

        int id = isNew ? dao.getNextId() : Integer.parseInt(idField.getText().trim());
        return new Student(id, name, age, course, email, phone);
    }

    private void refreshTable() {
        try {
            populateTable(dao.loadStudents());
        } catch (IOException ex) {
            showError("File Error", "Could not load student data: " + ex.getMessage());
        }
    }

    private void populateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getId(), s.getName(), s.getAge(), s.getCourse(), s.getEmail(), s.getPhone()
            });
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        courseField.setText("");
        emailField.setText("");
        phoneField.setText("");
        table.clearSelection();
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
