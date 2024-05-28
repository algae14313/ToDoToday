package com.example.todoapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todoapp.Model.ToDoModel;
import com.example.todoapp.Utils.DataBaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    // Widgets
    private EditText mEditText;
    private Button mSaveButton;

    private DataBaseHelper myDb;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_newtask, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditText = view.findViewById(R.id.edittext);
        mSaveButton = view.findViewById(R.id.button_save);

        myDb = new DataBaseHelper(getActivity());

        final Bundle bundle = getArguments();
        if (bundle != null) {
            handleUpdateTask(bundle);
        } else {
            mSaveButton.setEnabled(false);
            mSaveButton.setBackgroundColor(Color.GRAY);
        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                toggleSaveButton(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask(bundle);
            }
        });
    }

    private void handleUpdateTask(Bundle bundle) {
        String task = bundle.getString("task");
        mEditText.setText(task);
        mSaveButton.setEnabled(task != null && !task.isEmpty());
        mSaveButton.setBackgroundColor(task != null && !task.isEmpty() ? getResources().getColor(R.color.colorPrimary) : Color.GRAY);
    }

    private void toggleSaveButton(String text) {
        if (text.isEmpty()) {
            mSaveButton.setEnabled(false);
            mSaveButton.setBackgroundColor(Color.GRAY);
        } else {
            mSaveButton.setEnabled(true);
            mSaveButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void saveTask(Bundle bundle) {
        String text = mEditText.getText().toString();

        if (bundle != null) {
            myDb.updateTask(bundle.getInt("id"), text);
        } else {
            ToDoModel item = new ToDoModel();
            item.setTask(text);
            item.setStatus(0);
            myDb.insertTask(item);
        }
        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListner) {
            ((OnDialogCloseListner) activity).onDialogClose(dialogInterface);
        }
    }
}
