<<<<<<< HEAD
# Student Management System (Java Swing)

A desktop application built in Java (Swing) for managing student records — part of the
Java Development Internship (Task 1) at Kinetrexa Software Pvt. Ltd.

## Features

- **Add, Update, Delete, Search** student records (full CRUD)
- **Object-Oriented Design** — separate `model`, `dao`, `exception`, and `gui` packages
- **File Handling** — student data persisted to a CSV file (`data/students.csv`)
- **Exception Handling** — custom exceptions (`InvalidStudentDataException`,
  `StudentNotFoundException`) plus validation of name, age, email, and phone
- **Search & Filter** — search by ID, name, or course (partial, case-insensitive match)
- **Modular Code Structure** — clean separation of concerns across packages

## Project Structure

```
StudentManagementSystem/
├── src/
│   ├── Main.java                          # Application entry point
│   ├── model/
│   │   └── Student.java                   # Student data model
│   ├── dao/
│   │   └── StudentDAO.java                # CRUD logic + CSV file I/O
│   ├── exception/
│   │   ├── InvalidStudentDataException.java
│   │   └── StudentNotFoundException.java
│   └── gui/
│       └── StudentManagementGUI.java      # Swing GUI (JFrame)
├── data/
│   └── students.csv                       # Auto-created on first run
└── README.md
```

## How to Run

Requires Java JDK 8 or later.

```bash
# From the project root:
mkdir -p bin
javac -d bin $(find src -name "*.java")
java -cp bin Main
```

On Windows (PowerShell):

```powershell
mkdir bin
javac -d bin (Get-ChildItem -Recurse -Filter *.java -Path src).FullName
java -cp bin Main
```

The app window will open. Student data is stored in `data/students.csv`, created
automatically on first run.

## Usage

1. **Add** — fill in Name, Age, Course, Email, Phone (ID is auto-generated) → click
   "Add Student".
2. **Update** — select a row in the table (fields auto-fill) → edit values → click
   "Update Student".
3. **Delete** — select a row → click "Delete Student" → confirm.
4. **Search** — type an ID, name, or course keyword into the search box → click
   "Search". Click "Show All" to reset the view.

## Data Storage Format

Each line in `data/students.csv` is a comma-separated record:

```
id,name,age,course,email,phone
```

Note: field values should not contain commas, since the delimiter is a comma
(the app validates and rejects this on input).

## Validation Rules

- Name and Course: required, cannot be empty
- Age: required, must be a number between 1 and 120
- Email: optional, but if provided must match a standard email format
- Phone: optional, but if provided must be 7–15 digits

## Author

Jay Kumar — Java Development Internship, Kinetrexa Software Pvt. Ltd.
Application ID: KTS020260715548
=======
# Student-Management-System-
A Java Swing-based Student Management System that enables efficient student record management with CRUD operations, CSV-based data storage, search and filter functionality, input validation, custom exception handling, and a clean modular OOP architecture, ensuring maintainability, reliability, and a user-friendly desktop experience.
>>>>>>> 9245b9fed35a404f7245a4b2f039af763b30d389
