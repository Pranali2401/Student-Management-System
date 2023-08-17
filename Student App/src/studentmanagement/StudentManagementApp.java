package studentmanagement;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class StudentManagementApp extends Application {

    private StudentManagementSystem sms = new StudentManagementSystem();
    private TableView<Student> studentTable;
    private TextField searchField;

    private Label nameLabel;
    private Label rollNumberLabel;
    private Label gradeLabel;
    private TextField nameField;
    private TextField rollNumberField;
    private TextField gradeField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        studentTable = createStudentTable();
        searchField = new TextField();

        searchField.setPromptText("Search by Name or Roll Number");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearchButton(newValue));

        Button addButton = new Button("Add Student");
        addButton.setOnAction(e -> handleAddButton());

        Button removeButton = new Button("Remove Student");
        removeButton.setOnAction(e -> handleRemoveButton());

        Button displayAllButton = new Button("Display All Students");
        displayAllButton.setOnAction(e -> handleDisplayAllButton());

        VBox root = new VBox(createTitleLabel(), searchField, studentTable, createButtonBar());
        root.setSpacing(10);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setTitle("Student Management App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createTitleLabel() {
        Label titleLabel = new Label("Student Management System");
        titleLabel.getStyleClass().add("title-label");
        return titleLabel;
    }

    private TableView<Student> createStudentTable() {
        TableView<Student> table = new TableView<>();
        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, Integer> rollNumberCol = new TableColumn<>("Roll Number");
        rollNumberCol.setCellValueFactory(new PropertyValueFactory<>("rollNumber"));

        TableColumn<Student, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));

        table.getColumns().add(nameCol);
        table.getColumns().add(rollNumberCol);
        table.getColumns().add(gradeCol);

        // Convert ArrayList<Student> to ObservableList<Student>
        ObservableList<Student> studentData = FXCollections.observableArrayList(getStudentData());
        table.setItems(studentData);

        return table;
    }

    private HBox createButtonBar() {
        Button addButton = new Button("Add Student");
        addButton.getStyleClass().add("action-button");
        addButton.setOnAction(e -> handleAddButton());

        Button displayAllButton = new Button("Display All Students");
        displayAllButton.getStyleClass().addAll("action-button", "green-button");
        displayAllButton.setOnAction(e -> handleDisplayAllButton());

        Button removeButton = new Button("Remove Student");
        removeButton.getStyleClass().add("action-button");
        removeButton.setOnAction(e -> handleRemoveButton());

        HBox buttonBar = new HBox(addButton, displayAllButton, removeButton);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setSpacing(20);
        return buttonBar;
    }

    private void handleSearchButton(String newValue) {
        String searchTerm = searchField.getText();
        List<Student> filteredStudents = sms.searchStudents(searchTerm);
        studentTable.setItems(FXCollections.observableArrayList(filteredStudents));
    }

    private void handleAddButton() {
        Dialog<Student> dialog = createStudentDialog("Add Student", "Enter student details:");
        Optional<Student> result = dialog.showAndWait();
        result.ifPresent(student -> {
            sms.addStudent(student);
            refreshTable();

            showAlert("Student Added", "Student added successfully!", AlertType.INFORMATION);
        });
    }

    private void handleRemoveButton() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirm Removal");
            alert.setHeaderText("Remove Student");
            alert.setContentText("Are you sure you want to remove the selected student?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                sms.removeStudent(selectedStudent.getRollNumber());
                refreshTable();
            }
        } else {
            showAlert("No Student Selected", "Please select a student to remove.", AlertType.WARNING);
        }
    }

    private Dialog<Student> createStudentDialog(String title, String headerText) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);

        ButtonType addButton = new ButtonType("Add", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        nameLabel = new Label("Name:");
        nameField = new TextField();
        rollNumberLabel = new Label("Roll Number:");
        rollNumberField = new TextField();
        gradeLabel = new Label("Grade:");
        gradeField = new TextField();

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(rollNumberLabel, 0, 1);
        grid.add(rollNumberField, 1, 1);
        grid.add(gradeLabel, 0, 2);
        grid.add(gradeField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                String name = nameField.getText();
                String rollNumberText = rollNumberField.getText();
                String grade = gradeField.getText();

                if (name.isEmpty() || rollNumberText.isEmpty() || grade.isEmpty()) {
                    showAlert("Validation Error", "All fields are required.", AlertType.ERROR);
                    return null;
                }

                try {
                    int rollNumber = Integer.parseInt(rollNumberText);
                    return new Student(name, rollNumber, grade);
                } catch (NumberFormatException e) {
                    showAlert("Validation Error", "Invalid Roll Number. Please enter a valid integer.",
                            AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private void showAlert(String title, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleDisplayAllButton() {
        List<Student> allStudents = sms.getAllStudents();
        studentTable.setItems(FXCollections.observableArrayList(allStudents));
        StringBuilder studentListText = new StringBuilder("List of students:\n");
        for (Student student : allStudents) {
            studentListText.append(student.getName()).append(" - ").append(student.getRollNumber()).append("\n");
        }
        showAlert("All Students", studentListText.toString(), AlertType.INFORMATION);

    }

    private void refreshTable() {
        studentTable.setItems((ObservableList<Student>) getStudentData());
    }

    private List<Student> getStudentData() {
        return sms.getAllStudents();
    }
}
