package dao;

import exception.StudentNotFoundException;
import model.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object - handles all file I/O and CRUD operations
 * for Student records. Uses a plain text CSV file for storage.
 */
public class StudentDAO {

    private final String filePath;

    public StudentDAO(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    /** Creates the data file (and parent folder) if it doesn't already exist. */
    private void ensureFileExists() {
        File file = new File(filePath);
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Could not initialize data file: " + e.getMessage());
        }
    }

    /** Reads every student record from the CSV file. */
    public List<Student> loadStudents() throws IOException {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    students.add(Student.fromCSV(line));
                } catch (Exception parseError) {
                    // Skip corrupted lines instead of crashing the whole load.
                    System.err.println("Skipping corrupted record: " + line);
                }
            }
        }
        return students;
    }

    /** Overwrites the CSV file with the given list of students. */
    private void saveStudents(List<Student> students) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Student s : students) {
                writer.write(s.toCSV());
                writer.newLine();
            }
        }
    }

    /** Adds a new student and persists it. */
    public void addStudent(Student student) throws IOException {
        List<Student> students = loadStudents();
        students.add(student);
        saveStudents(students);
    }

    /** Updates an existing student (matched by ID). */
    public void updateStudent(Student updated) throws IOException, StudentNotFoundException {
        List<Student> students = loadStudents();
        boolean found = false;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == updated.getId()) {
                students.set(i, updated);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new StudentNotFoundException("No student found with ID " + updated.getId());
        }
        saveStudents(students);
    }

    /** Deletes a student by ID. */
    public void deleteStudent(int id) throws IOException, StudentNotFoundException {
        List<Student> students = loadStudents();
        boolean removed = students.removeIf(s -> s.getId() == id);
        if (!removed) {
            throw new StudentNotFoundException("No student found with ID " + id);
        }
        saveStudents(students);
    }

    /** Returns the next available auto-increment ID. */
    public int getNextId() throws IOException {
        List<Student> students = loadStudents();
        int max = 0;
        for (Student s : students) {
            if (s.getId() > max) max = s.getId();
        }
        return max + 1;
    }

    /** Searches/filters students by name, course, or ID (case-insensitive, partial match). */
    public List<Student> search(String keyword) throws IOException {
        List<Student> results = new ArrayList<>();
        String key = keyword.trim().toLowerCase();
        for (Student s : loadStudents()) {
            if (String.valueOf(s.getId()).equals(key)
                    || s.getName().toLowerCase().contains(key)
                    || s.getCourse().toLowerCase().contains(key)) {
                results.add(s);
            }
        }
        return results;
    }
}
