package com.example.librarymanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class StudentAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> students;
    ArrayList<Integer> studentIds;

    OnUpdateClickListener updateListener;
    OnDeleteClickListener deleteListener;

    public interface OnUpdateClickListener {
        void onUpdateClick(int id);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int id);
    }

    public StudentAdapter(
            Context context,
            ArrayList<String> students,
            ArrayList<Integer> studentIds,
            OnUpdateClickListener updateListener,
            OnDeleteClickListener deleteListener) {

        this.context = context;
        this.students = students;
        this.studentIds = studentIds;
        this.updateListener = updateListener;
        this.deleteListener = deleteListener;
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return studentIds.get(position);
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_student, parent, false);
        }

        TextView studentInfo =
                convertView.findViewById(R.id.studentInfo);

        Button updateButton =
                convertView.findViewById(R.id.updateStudentButton);

        Button deleteButton =
                convertView.findViewById(R.id.deleteStudentButton);

        studentInfo.setText(students.get(position));

        int id = studentIds.get(position);

        updateButton.setOnClickListener(v ->
                updateListener.onUpdateClick(id));

        deleteButton.setOnClickListener(v ->
                deleteListener.onDeleteClick(id));

        return convertView;
    }
}