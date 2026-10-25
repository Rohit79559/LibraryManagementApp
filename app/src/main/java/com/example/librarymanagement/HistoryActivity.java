package com.example.librarymanagement;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView historyList;
    DatabaseHelper databaseHelper;

    ArrayList<String> history = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        historyList = findViewById(R.id.historyList);

        databaseHelper = new DatabaseHelper(this);

        loadHistory();
    }

    private void loadHistory() {

        history.clear();

        Cursor cursor = databaseHelper.getHistory();

        while (cursor.moveToNext()) {

            String studentName = cursor.getString(
                    cursor.getColumnIndexOrThrow("student_name")
            );

            String bookName = cursor.getString(
                    cursor.getColumnIndexOrThrow("book_name")
            );

            String issueDate = cursor.getString(
                    cursor.getColumnIndexOrThrow("issue_date")
            );

            String returnDate = cursor.getString(
                    cursor.getColumnIndexOrThrow("return_date")
            );

            String status = cursor.getString(
                    cursor.getColumnIndexOrThrow("status")
            );

            String record =
                    "Student: " + studentName +
                            "\nBook: " + bookName +
                            "\nIssue Date: " + issueDate +
                            "\nReturn Date: " + returnDate +
                            "\nStatus: " + status;

            history.add(record);
        }

        cursor.close();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                history
        );

        historyList.setAdapter(adapter);
    }
}