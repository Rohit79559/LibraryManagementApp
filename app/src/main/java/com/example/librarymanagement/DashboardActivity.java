package com.example.librarymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    Button booksButton;
    Button studentsButton;
    Button issueButton;
    Button returnButton;
    Button historyButton;
    TextView totalBooksText;
    TextView totalStudentsText;
    TextView issuedBooksText;
    TextView availableBooksText;

    DatabaseHelper databaseHelper;
    Button logoutButton;

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadDashboardCounts();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        databaseHelper = new DatabaseHelper(this);

        totalBooksText = findViewById(R.id.totalBooksText);
        totalStudentsText = findViewById(R.id.totalStudentsText);
        issuedBooksText = findViewById(R.id.issuedBooksText);
        availableBooksText = findViewById(R.id.availableBooksText);

        loadDashboardCounts();

        booksButton = findViewById(R.id.booksButton);
        studentsButton = findViewById(R.id.studentsButton);
        issueButton = findViewById(R.id.issueButton);
        returnButton = findViewById(R.id.returnButton);
        historyButton = findViewById(R.id.historyButton);
        logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();
        });

        booksButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    BooksActivity.class
            );

            startActivity(intent);
        });

        studentsButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    StudentsActivity.class
            );

            startActivity(intent);
        });

        issueButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    IssueBookActivity.class
            );

            startActivity(intent);
        });

        returnButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    ReturnBookActivity.class
            );

            startActivity(intent);
        });

        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    HistoryActivity.class
            );
            startActivity(intent);
        });
    }
    private void loadDashboardCounts() {

        totalBooksText.setText(
                String.valueOf(databaseHelper.getTotalBooks())
        );

        totalStudentsText.setText(
                String.valueOf(databaseHelper.getTotalStudents())
        );

        issuedBooksText.setText(
                String.valueOf(databaseHelper.getIssuedBooksCount())
        );

        availableBooksText.setText(
                String.valueOf(databaseHelper.getAvailableBooks())
        );
    }
}