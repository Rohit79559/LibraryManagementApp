package com.example.librarymanagement;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class StudentsActivity extends AppCompatActivity {

    EditText studentId;
    EditText studentName;
    EditText course;
    EditText phone;
    EditText searchStudent;

    Button addStudentButton;
    Button searchStudentButton;

    ListView studentList;

    DatabaseHelper databaseHelper;

    ArrayList<String> students;
    ArrayList<Integer> studentIds;

    StudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_students);

        studentId = findViewById(R.id.studentId);
        studentName = findViewById(R.id.studentName);
        course = findViewById(R.id.course);
        phone = findViewById(R.id.phone);

        searchStudent = findViewById(R.id.searchStudent);
        searchStudentButton = findViewById(R.id.searchStudentButton);

        addStudentButton = findViewById(R.id.addStudentButton);
        studentList = findViewById(R.id.studentList);

        databaseHelper = new DatabaseHelper(this);

        students = new ArrayList<>();
        studentIds = new ArrayList<>();

        adapter = new StudentAdapter(
                this,
                students,
                studentIds,

                id -> showUpdateDialog(id),

                id -> showDeleteConfirmation(id)
        );

        studentList.setAdapter(adapter);

        loadStudents();

        // ADD STUDENT
        addStudentButton.setOnClickListener(v -> {

            String id =
                    studentId.getText().toString().trim();

            String name =
                    studentName.getText().toString().trim();

            String studentCourse =
                    course.getText().toString().trim();

            String studentPhone =
                    phone.getText().toString().trim();

            if (id.isEmpty()
                    || name.isEmpty()
                    || studentCourse.isEmpty()
                    || studentPhone.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            boolean result =
                    databaseHelper.addStudent(
                            id,
                            name,
                            studentCourse,
                            studentPhone
                    );

            if (result) {

                Toast.makeText(
                        this,
                        "Student Added Successfully",
                        Toast.LENGTH_SHORT
                ).show();

                studentId.setText("");
                studentName.setText("");
                course.setText("");
                phone.setText("");

                loadStudents();

            } else {

                Toast.makeText(
                        this,
                        "Student ID already exists",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // SEARCH
        searchStudentButton.setOnClickListener(v -> {

            String search =
                    searchStudent.getText().toString().trim();

            if (search.isEmpty()) {
                loadStudents();
            } else {
                searchStudents(search);
            }
        });
    }

    // LOAD STUDENTS
    private void loadStudents() {

        students.clear();
        studentIds.clear();

        Cursor cursor =
                databaseHelper.getAllStudents();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);

            String studentIdValue =
                    cursor.getString(1);

            String name =
                    cursor.getString(2);

            String studentCourse =
                    cursor.getString(3);

            String studentPhone =
                    cursor.getString(4);

            String info =
                    "👨‍🎓 " + name +
                            "\nStudent ID: " + studentIdValue +
                            "\nCourse: " + studentCourse +
                            "\nPhone: " + studentPhone;

            students.add(info);
            studentIds.add(id);
        }

        cursor.close();

        adapter.notifyDataSetChanged();
    }

    // SEARCH
    private void searchStudents(String search) {

        students.clear();
        studentIds.clear();

        Cursor cursor =
                databaseHelper.getReadableDatabase()
                        .rawQuery(
                                "SELECT * FROM Students " +
                                        "WHERE student_name LIKE ? " +
                                        "OR student_id LIKE ?",
                                new String[]{
                                        "%" + search + "%",
                                        "%" + search + "%"
                                }
                        );

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);

            String studentIdValue =
                    cursor.getString(1);

            String name =
                    cursor.getString(2);

            String studentCourse =
                    cursor.getString(3);

            String studentPhone =
                    cursor.getString(4);

            String info =
                    "👨‍🎓 " + name +
                            "\nStudent ID: " + studentIdValue +
                            "\nCourse: " + studentCourse +
                            "\nPhone: " + studentPhone;

            students.add(info);
            studentIds.add(id);
        }

        cursor.close();

        adapter.notifyDataSetChanged();
    }

    // UPDATE
    private void showUpdateDialog(int id) {

        Cursor cursor =
                databaseHelper.getReadableDatabase()
                        .rawQuery(
                                "SELECT * FROM Students WHERE id=?",
                                new String[]{
                                        String.valueOf(id)
                                }
                        );

        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        String oldStudentId = cursor.getString(1);
        String oldName = cursor.getString(2);
        String oldCourse = cursor.getString(3);
        String oldPhone = cursor.getString(4);

        cursor.close();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 10, 40, 10);

        EditText idInput = new EditText(this);
        idInput.setHint("Student ID");
        idInput.setText(oldStudentId);
        layout.addView(idInput);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Student Name");
        nameInput.setText(oldName);
        layout.addView(nameInput);

        EditText courseInput = new EditText(this);
        courseInput.setHint("Course");
        courseInput.setText(oldCourse);
        layout.addView(courseInput);

        EditText phoneInput = new EditText(this);
        phoneInput.setHint("Phone");
        phoneInput.setText(oldPhone);
        phoneInput.setInputType(
                InputType.TYPE_CLASS_PHONE
        );
        layout.addView(phoneInput);

        new AlertDialog.Builder(this)
                .setTitle("Update Student")
                .setView(layout)

                .setPositiveButton(
                        "UPDATE",
                        (dialog, which) -> {

                            String newId =
                                    idInput.getText()
                                            .toString().trim();

                            String newName =
                                    nameInput.getText()
                                            .toString().trim();

                            String newCourse =
                                    courseInput.getText()
                                            .toString().trim();

                            String newPhone =
                                    phoneInput.getText()
                                            .toString().trim();

                            if (newId.isEmpty()
                                    || newName.isEmpty()
                                    || newCourse.isEmpty()
                                    || newPhone.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "Please fill all fields",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            boolean result =
                                    databaseHelper.updateStudent(
                                            id,
                                            newId,
                                            newName,
                                            newCourse,
                                            newPhone
                                    );

                            if (result) {

                                Toast.makeText(
                                        this,
                                        "Student Updated",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadStudents();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Update Failed",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )

                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .show();
    }

    // DELETE
    private void showDeleteConfirmation(int id) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage(
                        "Are you sure you want to delete this student?"
                )
                .setPositiveButton(
                        "DELETE",
                        (dialog, which) -> {

                            boolean result =
                                    databaseHelper.deleteStudent(id);

                            if (result) {

                                Toast.makeText(
                                        this,
                                        "Student Deleted",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadStudents();
                            }
                        }
                )
                .setNegativeButton(
                        "CANCEL",
                        null)
                .show();
    }
}