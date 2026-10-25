package com.example.librarymanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Library.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE Books (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "book_name TEXT," +
                        "author TEXT," +
                        "category TEXT," +
                        "quantity INTEGER)"
        );

        db.execSQL(
                "CREATE TABLE Students (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "student_id TEXT UNIQUE," +
                        "student_name TEXT," +
                        "course TEXT," +
                        "phone TEXT)"
        );

        db.execSQL(
                "CREATE TABLE IssuedBooks (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "student_id INTEGER," +
                        "book_id INTEGER," +
                        "issue_date TEXT," +
                        "return_date TEXT," +
                        "status TEXT)"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        if (oldVersion < 2) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS Students (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "student_id TEXT UNIQUE," +
                            "student_name TEXT," +
                            "course TEXT," +
                            "phone TEXT)"
            );
        }

        if (oldVersion < 3) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS IssuedBooks (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "student_id INTEGER," +
                            "book_id INTEGER," +
                            "issue_date TEXT," +
                            "return_date TEXT," +
                            "status TEXT)"
            );
        }
    }

    public boolean addBook(
            String bookName,
            String author,
            String category,
            int quantity) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("book_name", bookName);
        values.put("author", author);
        values.put("category", category);
        values.put("quantity", quantity);

        long result = db.insert("Books", null, values);

        return result != -1;
    }

    public boolean addStudent(
            String studentId,
            String studentName,
            String course,
            String phone) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("student_id", studentId);
        values.put("student_name", studentName);
        values.put("course", course);
        values.put("phone", phone);

        long result = db.insert(
                "Students",
                null,
                values
        );

        return result != -1;
    }

    public Cursor getAllStudents() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM Students",
                null
        );
    }

    public boolean deleteStudent(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "Students",
                "id=?",
                new String[]{
                        String.valueOf(id)
                }
        );

        return result > 0;
    }

    public boolean updateStudent(
            int id,
            String studentId,
            String studentName,
            String course,
            String phone) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("student_id", studentId);
        values.put("student_name", studentName);
        values.put("course", course);
        values.put("phone", phone);

        int result = db.update(
                "Students",
                values,
                "id=?",
                new String[]{
                        String.valueOf(id)
                }
        );

        return result > 0;
    }

    public boolean issueBook(int studentId, int bookId, String issueDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Check if this student already has this book
        Cursor existingCursor = db.rawQuery(
                "SELECT id FROM IssuedBooks " +
                        "WHERE student_id = ? " +
                        "AND book_id = ? " +
                        "AND status = 'ISSUED'",
                new String[]{
                        String.valueOf(studentId),
                        String.valueOf(bookId)
                }
        );

        if (existingCursor.moveToFirst()) {
            existingCursor.close();
            return false;
        }

        existingCursor.close();

        // Check available quantity
        Cursor cursor = db.rawQuery(
                "SELECT quantity FROM Books WHERE id = ?",
                new String[]{String.valueOf(bookId)}
        );

        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        int quantity = cursor.getInt(0);
        cursor.close();

        if (quantity <= 0) {
            return false;
        }

        db.beginTransaction();

        try {

            ContentValues values = new ContentValues();

            values.put("student_id", studentId);
            values.put("book_id", bookId);
            values.put("issue_date", issueDate);
            values.put("return_date", "");
            values.put("status", "ISSUED");

            long result = db.insert(
                    "IssuedBooks",
                    null,
                    values
            );

            if (result == -1) {
                return false;
            }

            ContentValues updateValues = new ContentValues();
            updateValues.put("quantity", quantity - 1);

            db.update(
                    "Books",
                    updateValues,
                    "id = ?",
                    new String[]{String.valueOf(bookId)}
            );

            db.setTransactionSuccessful();

            return true;

        } finally {
            db.endTransaction();
        }
    }
    public Cursor getAllBooks() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM Books",
                null
        );
    }

    public boolean deleteBook(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "Books",
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    public boolean updateBook(
            int id,
            String bookName,
            String author,
            String category,
            int quantity) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("book_name", bookName);
        values.put("author", author);
        values.put("category", category);
        values.put("quantity", quantity);

        int result = db.update(
                "Books",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    public Cursor getStudentsForIssue() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, student_id, student_name " +
                        "FROM Students",
                null
        );
    }

    public Cursor getAvailableBooksForIssue() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, book_name, author, quantity " +
                        "FROM Books " +
                        "WHERE quantity > 0",
                null
        );
    }

    public Cursor getIssuedBooks() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT IssuedBooks.id, " +
                        "Students.student_name, " +
                        "Books.book_name, " +
                        "IssuedBooks.issue_date " +

                        "FROM IssuedBooks " +

                        "INNER JOIN Students " +
                        "ON IssuedBooks.student_id = Students.id " +

                        "INNER JOIN Books " +
                        "ON IssuedBooks.book_id = Books.id " +

                        "WHERE IssuedBooks.status = 'ISSUED'",

                null
        );
    }

    public boolean returnBook(int issuedBookId, String returnDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        try {

            Cursor cursor = db.rawQuery(
                    "SELECT book_id FROM IssuedBooks " +
                            "WHERE id=? AND status='ISSUED'",

                    new String[]{
                            String.valueOf(issuedBookId)
                    }
            );

            if (!cursor.moveToFirst()) {
                cursor.close();
                return false;
            }

            int bookId = cursor.getInt(0);

            cursor.close();


            // Update issued record
            ContentValues issueValues = new ContentValues();

            issueValues.put(
                    "return_date",
                    returnDate
            );

            issueValues.put(
                    "status",
                    "RETURNED"
            );

            int issueUpdated = db.update(
                    "IssuedBooks",
                    issueValues,
                    "id=?",
                    new String[]{
                            String.valueOf(issuedBookId)
                    }
            );

            if (issueUpdated <= 0) {
                return false;
            }


            // Increase book quantity
            Cursor bookCursor = db.rawQuery(
                    "SELECT quantity FROM Books WHERE id=?",
                    new String[]{
                            String.valueOf(bookId)
                    }
            );

            if (!bookCursor.moveToFirst()) {
                bookCursor.close();
                return false;
            }

            int quantity = bookCursor.getInt(0);

            bookCursor.close();


            ContentValues bookValues = new ContentValues();

            bookValues.put(
                    "quantity",
                    quantity + 1
            );

            int bookUpdated = db.update(
                    "Books",
                    bookValues,
                    "id=?",
                    new String[]{
                            String.valueOf(bookId)
                    }
            );

            if (bookUpdated <= 0) {
                return false;
            }


            db.setTransactionSuccessful();

            return true;

        } finally {

            db.endTransaction();
        }
    }

    public Cursor getHistory() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT Students.student_name, " +
                        "Books.book_name, " +
                        "IssuedBooks.issue_date, " +
                        "IssuedBooks.return_date, " +
                        "IssuedBooks.status " +
                        "FROM IssuedBooks " +
                        "INNER JOIN Students " +
                        "ON IssuedBooks.student_id = Students.id " +
                        "INNER JOIN Books " +
                        "ON IssuedBooks.book_id = Books.id " +
                        "ORDER BY IssuedBooks.id DESC",
                null
        );
    }

    public int getTotalBooks() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Books",
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public int getTotalStudents() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Students",
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public int getIssuedBooksCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM IssuedBooks WHERE status = 'ISSUED'",
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public int getAvailableBooks() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COALESCE(SUM(quantity), 0) FROM Books",
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }
}