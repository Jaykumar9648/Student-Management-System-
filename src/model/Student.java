package model;

/**
 * Represents a Student entity.
 * Encapsulates all student data with getters/setters (OOP - Encapsulation).
 */
public class Student {

    private int id;
    private String name;
    private int age;
    private String course;
    private String email;
    private String phone;

    public Student(int id, String name, int age, String course, String email, String phone) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
        this.email = email;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Converts this student into a single CSV line for file storage.
     * Commas inside fields are not supported (kept simple by design).
     */
    public String toCSV() {
        return id + "," + name + "," + age + "," + course + "," + email + "," + phone;
    }

    /**
     * Builds a Student object from a single CSV line read from file.
     */
    public static Student fromCSV(String line) {
        String[] parts = line.split(",", -1);
        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        int age = Integer.parseInt(parts[2].trim());
        String course = parts[3].trim();
        String email = parts[4].trim();
        String phone = parts[5].trim();
        return new Student(id, name, age, course, email, phone);
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', age=" + age +
                ", course='" + course + "', email='" + email + "', phone='" + phone + "'}";
    }
}
