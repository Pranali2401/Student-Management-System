package studentmanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;

public class StudentManagementSystem {
    private ObservableList<Student> students = FXCollections.observableArrayList();

    public void addStudent(Student student) {
        students.add(student);
    }

    public void removeStudent(int rollNumber) {
        students.removeIf(student -> student.getRollNumber() == rollNumber);
    }

    public List<Student> searchStudents(String searchTerm) {
        List<Student> searchResults = new ArrayList<>();
        for (Student student : students) {
            if (student.getName().toLowerCase().contains(searchTerm.toLowerCase())
                    || String.valueOf(student.getRollNumber()).contains(searchTerm)) {
                searchResults.add(student);
            }
        }
        return searchResults;
    }

    public ObservableList<Student> getAllStudents() {
        return students;
    }

}
