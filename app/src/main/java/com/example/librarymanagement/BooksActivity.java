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

public class BooksActivity extends AppCompatActivity {

    EditText bookName;
    EditText author;
    EditText category;
    EditText quantity;
    EditText searchBook;

    Button addBookButton;
    Button searchButton;

    ListView bookList;

    DatabaseHelper databaseHelper;

    ArrayList<String> books;
    ArrayList<Integer> bookIds;

    BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_books);

        bookName = findViewById(R.id.bookName);
        author = findViewById(R.id.author);
        category = findViewById(R.id.category);
        quantity = findViewById(R.id.quantity);

        searchBook = findViewById(R.id.searchBook);
        searchButton = findViewById(R.id.searchButton);

        addBookButton = findViewById(R.id.addBookButton);

        bookList = findViewById(R.id.bookList);

        databaseHelper = new DatabaseHelper(this);

        books = new ArrayList<>();
        bookIds = new ArrayList<>();

        adapter = new BookAdapter(
                this,
                books,
                bookIds,

                // UPDATE
                bookId -> showUpdateDialog(bookId),

                // DELETE
                bookId -> showDeleteConfirmation(bookId)
        );

        bookList.setAdapter(adapter);

        loadBooks();

        // ADD BOOK
        addBookButton.setOnClickListener(v -> {

            String name = bookName.getText().toString().trim();
            String bookAuthor = author.getText().toString().trim();
            String bookCategory = category.getText().toString().trim();
            String qtyText = quantity.getText().toString().trim();

            // Empty field validation
            if (name.isEmpty()) {
                bookName.setError("Enter book name");
                bookName.requestFocus();
                return;
            }

            if (bookAuthor.isEmpty()) {
                author.setError("Enter author name");
                author.requestFocus();
                return;
            }

            if (bookCategory.isEmpty()) {
                category.setError("Enter category");
                category.requestFocus();
                return;
            }

            if (qtyText.isEmpty()) {
                quantity.setError("Enter quantity");
                quantity.requestFocus();
                return;
            }

            // Quantity validation
            int qty;

            try {
                qty = Integer.parseInt(qtyText);
            } catch (NumberFormatException e) {
                quantity.setError("Enter a valid number");
                quantity.requestFocus();
                return;
            }

            if (qty <= 0) {
                quantity.setError("Quantity must be greater than 0");
                quantity.requestFocus();
                return;
            }

            // Add book
            databaseHelper.addBook(
                    name,
                    bookAuthor,
                    bookCategory,
                    qty
            );

            Toast.makeText(
                    BooksActivity.this,
                    "Book Added Successfully",
                    Toast.LENGTH_SHORT
            ).show();

            bookName.setText("");
            author.setText("");
            category.setText("");
            quantity.setText("");

            loadBooks();
        });
        // SEARCH BOOK
        searchButton.setOnClickListener(v -> {

            String search =
                    searchBook.getText().toString().trim();

            if (search.isEmpty()) {

                loadBooks();

            } else {

                searchBooks(search);
            }
        });
    }


    // LOAD ALL BOOKS
    private void loadBooks() {

        books.clear();
        bookIds.clear();

        Cursor cursor =
                databaseHelper.getAllBooks();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);

            String name =
                    cursor.getString(1);

            String authorName =
                    cursor.getString(2);

            String categoryName =
                    cursor.getString(3);

            int qty =
                    cursor.getInt(4);

            String bookInfo =
                    "📖 " + name +
                            "\nAuthor: " + authorName +
                            "\nCategory: " + categoryName +
                            "\nQuantity: " + qty;

            books.add(bookInfo);
            bookIds.add(id);
        }

        cursor.close();

        adapter.notifyDataSetChanged();
    }


    // SEARCH BOOKS
    private void searchBooks(String search) {

        books.clear();
        bookIds.clear();

        Cursor cursor =
                databaseHelper.getReadableDatabase()
                        .rawQuery(
                                "SELECT * FROM Books " +
                                        "WHERE book_name LIKE ?",
                                new String[]{
                                        "%" + search + "%"
                                }
                        );

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);

            String name =
                    cursor.getString(1);

            String authorName =
                    cursor.getString(2);

            String categoryName =
                    cursor.getString(3);

            int qty =
                    cursor.getInt(4);

            String bookInfo =
                    "📖 " + name +
                            "\nAuthor: " + authorName +
                            "\nCategory: " + categoryName +
                            "\nQuantity: " + qty;

            books.add(bookInfo);
            bookIds.add(id);
        }

        cursor.close();

        adapter.notifyDataSetChanged();
    }


    // UPDATE BOOK
    private void showUpdateDialog(int bookId) {

        Cursor cursor =
                databaseHelper.getReadableDatabase()
                        .rawQuery(
                                "SELECT * FROM Books WHERE id=?",
                                new String[]{
                                        String.valueOf(bookId)
                                }
                        );

        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        String oldName = cursor.getString(1);
        String oldAuthor = cursor.getString(2);
        String oldCategory = cursor.getString(3);
        String oldQuantity = cursor.getString(4);

        cursor.close();


        // Create layout
        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = 40;

        layout.setPadding(
                padding,
                10,
                padding,
                10
        );


        // Book Name
        EditText nameInput = new EditText(this);

        nameInput.setHint("Book Name");
        nameInput.setText(oldName);

        layout.addView(nameInput);


        // Author
        EditText authorInput = new EditText(this);

        authorInput.setHint("Author");
        authorInput.setText(oldAuthor);

        layout.addView(authorInput);


        // Category
        EditText categoryInput = new EditText(this);

        categoryInput.setHint("Category");
        categoryInput.setText(oldCategory);

        layout.addView(categoryInput);


        // Quantity
        EditText quantityInput = new EditText(this);

        quantityInput.setHint("Quantity");
        quantityInput.setText(oldQuantity);

        quantityInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        layout.addView(quantityInput);


        // Dialog
        new AlertDialog.Builder(this)

                .setTitle("Update Book")

                .setView(layout)

                .setPositiveButton(
                        "UPDATE",
                        (dialog, which) -> {

                            String newName =
                                    nameInput.getText()
                                            .toString()
                                            .trim();

                            String newAuthor =
                                    authorInput.getText()
                                            .toString()
                                            .trim();

                            String newCategory =
                                    categoryInput.getText()
                                            .toString()
                                            .trim();

                            String newQuantityText =
                                    quantityInput.getText()
                                            .toString()
                                            .trim();


                            if (newName.isEmpty()
                                    || newAuthor.isEmpty()
                                    || newCategory.isEmpty()
                                    || newQuantityText.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "Please fill all fields",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            int newQuantity =
                                    Integer.parseInt(
                                            newQuantityText
                                    );


                            boolean result =
                                    databaseHelper.updateBook(
                                            bookId,
                                            newName,
                                            newAuthor,
                                            newCategory,
                                            newQuantity
                                    );


                            if (result) {

                                Toast.makeText(
                                        this,
                                        "Book Updated Successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadBooks();

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


    // DELETE CONFIRMATION
    private void showDeleteConfirmation(int bookId) {

        new AlertDialog.Builder(this)

                .setTitle("Delete Book")

                .setMessage(
                        "Are you sure you want to delete this book?"
                )

                .setPositiveButton(
                        "DELETE",
                        (dialog, which) -> {

                            boolean result =
                                    databaseHelper.deleteBook(
                                            bookId
                                    );

                            if (result) {

                                Toast.makeText(
                                        this,
                                        "Book Deleted",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadBooks();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Delete Failed",
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
}