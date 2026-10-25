package com.example.librarymanagement;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class IssueBookActivity extends AppCompatActivity {

    Spinner studentSpinner;
    Spinner bookSpinner;

    TextView issueDateText;
    Button issueBookButton;

    DatabaseHelper databaseHelper;

    ArrayList<String> studentList;
    ArrayList<Integer> studentIds;

    ArrayList<String> bookList;
    ArrayList<Integer> bookIds;

    ArrayAdapter<String> studentAdapter;
    ArrayAdapter<String> bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_issue_book);

        studentSpinner =
                findViewById(R.id.studentSpinner);

        bookSpinner =
                findViewById(R.id.bookSpinner);

        issueDateText =
                findViewById(R.id.issueDateText);

        issueBookButton =
                findViewById(R.id.issueBookButton);

        databaseHelper =
                new DatabaseHelper(this);


        // Lists
        studentList = new ArrayList<>();
        studentIds = new ArrayList<>();

        bookList = new ArrayList<>();
        bookIds = new ArrayList<>();


        // Student Adapter
        studentAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        studentList
                );

        studentAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        studentSpinner.setAdapter(studentAdapter);


        // Book Adapter
        bookAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        bookList
                );

        bookAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        bookSpinner.setAdapter(bookAdapter);


        // Current Date
        String currentDate =
                new SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                ).format(new Date());

        issueDateText.setText(
                "Issue Date: " + currentDate
        );


        // Load students and books
        loadStudents();
        loadBooks();


        // ISSUE BOOK
        issueBookButton.setOnClickListener(v -> {

            if (studentIds.isEmpty()) {

                Toast.makeText(
                        this,
                        "No students available",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (bookIds.isEmpty()) {

                Toast.makeText(
                        this,
                        "No books available",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            int selectedStudentId =
                    studentIds.get(
                            studentSpinner.getSelectedItemPosition()
                    );

            int selectedBookId =
                    bookIds.get(
                            bookSpinner.getSelectedItemPosition()
                    );


            boolean result =
                    databaseHelper.issueBook(
                            selectedStudentId,
                            selectedBookId,
                            currentDate
                    );


            if (result) {

                Toast.makeText(
                        this,
                        "Book Issued Successfully",
                        Toast.LENGTH_SHORT
                ).show();

                // Refresh available books
                loadBooks();

            } else {

                Toast.makeText(
                        this,
                        "Book cannot be issued. Student already has this book or no quantity is available.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }


    // LOAD STUDENTS
    private void loadStudents() {

        studentList.clear();
        studentIds.clear();

        Cursor cursor =
                databaseHelper.getStudentsForIssue();

        while (cursor.moveToNext()) {

            int id =
                    cursor.getInt(0);

            String studentId =
                    cursor.getString(1);

            String name =
                    cursor.getString(2);

            studentList.add(
                    studentId + " - " + name
            );

            studentIds.add(id);
        }

        cursor.close();

        studentAdapter.notifyDataSetChanged();
    }


    // LOAD AVAILABLE BOOKS
    private void loadBooks() {

        bookList.clear();
        bookIds.clear();

        Cursor cursor =
                databaseHelper.getAvailableBooksForIssue();

        while (cursor.moveToNext()) {

            int id =
                    cursor.getInt(0);

            String name =
                    cursor.getString(1);

            String author =
                    cursor.getString(2);

            int quantity =
                    cursor.getInt(3);

            bookList.add(
                    name +
                            " - " +
                            author +
                            " (" +
                            quantity +
                            " available)"
            );

            bookIds.add(id);
        }

        cursor.close();

        bookAdapter.notifyDataSetChanged();
    }
}