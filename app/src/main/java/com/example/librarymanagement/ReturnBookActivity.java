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

public class ReturnBookActivity extends AppCompatActivity {

    Spinner issuedBookSpinner;
    TextView returnDateText;
    Button returnBookButton;

    DatabaseHelper databaseHelper;

    ArrayList<String> issuedBooks = new ArrayList<>();
    ArrayList<Integer> issuedBookIds = new ArrayList<>();

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_return_book);

        issuedBookSpinner = findViewById(R.id.issuedBookSpinner);
        returnDateText = findViewById(R.id.returnDateText);
        returnBookButton = findViewById(R.id.returnBookButton);

        databaseHelper = new DatabaseHelper(this);

        String currentDate = new SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
        ).format(new Date());

        returnDateText.setText("Return Date: " + currentDate);

        loadIssuedBooks();

        returnBookButton.setOnClickListener(v -> {

            if (issuedBookIds.isEmpty()) {
                Toast.makeText(
                        ReturnBookActivity.this,
                        "No issued books available",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            int position = issuedBookSpinner.getSelectedItemPosition();

            int issuedBookId = issuedBookIds.get(position);

            boolean result = databaseHelper.returnBook(
                    issuedBookId,
                    currentDate
            );

            if (result) {

                Toast.makeText(
                        ReturnBookActivity.this,
                        "Book Returned Successfully",
                        Toast.LENGTH_SHORT
                ).show();

                loadIssuedBooks();

            } else {

                Toast.makeText(
                        ReturnBookActivity.this,
                        "Return Failed",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void loadIssuedBooks() {

        issuedBooks.clear();
        issuedBookIds.clear();

        Cursor cursor = databaseHelper.getIssuedBooks();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
            );

            String studentName = cursor.getString(
                    cursor.getColumnIndexOrThrow("student_name")
            );

            String bookName = cursor.getString(
                    cursor.getColumnIndexOrThrow("book_name")
            );

            String issueDate = cursor.getString(
                    cursor.getColumnIndexOrThrow("issue_date")
            );

            issuedBookIds.add(id);

            issuedBooks.add(
                    bookName + " -> " +
                            studentName +
                            " | Issued: " +
                            issueDate
            );
        }

        cursor.close();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                issuedBooks
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        issuedBookSpinner.setAdapter(adapter);
    }
}