package com.example.librarymanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class BookAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> books;
    ArrayList<Integer> bookIds;

    OnUpdateClickListener updateListener;
    OnDeleteClickListener deleteListener;

    public interface OnUpdateClickListener {
        void onUpdateClick(int bookId);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int bookId);
    }

    public BookAdapter(
            Context context,
            ArrayList<String> books,
            ArrayList<Integer> bookIds,
            OnUpdateClickListener updateListener,
            OnDeleteClickListener deleteListener) {

        this.context = context;
        this.books = books;
        this.bookIds = bookIds;
        this.updateListener = updateListener;
        this.deleteListener = deleteListener;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return bookIds.get(position);
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_book, parent, false);
        }

        TextView bookInfo =
                convertView.findViewById(R.id.bookInfo);

        Button updateButton =
                convertView.findViewById(R.id.updateButton);

        Button deleteButton =
                convertView.findViewById(R.id.deleteButton);

        bookInfo.setText(books.get(position));

        int bookId = bookIds.get(position);

        updateButton.setOnClickListener(v -> {
            updateListener.onUpdateClick(bookId);
        });

        deleteButton.setOnClickListener(v -> {
            deleteListener.onDeleteClick(bookId);
        });

        return convertView;
    }
}