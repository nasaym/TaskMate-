package net.penguincoders.doit;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.penguincoders.doit.Adapters.ToDoAdapter;
import net.penguincoders.doit.Model.ToDoModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.Objects;


public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";
    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DatabaseHandler db;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
        db = new DatabaseHandler(requireActivity()); // Initialize database in onCreate
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        setTaskUpdateLogic();
    }

    private void initViews() {
        newTaskText = requireView().findViewById(R.id.newTaskText);
        newTaskSaveButton = requireView().findViewById(R.id.newTaskButton);
    }

    private void setTaskUpdateLogic() {
        Bundle bundle = getArguments();
        boolean isUpdate = bundle != null;
        if (isUpdate) {
            handleTaskUpdate(bundle);
        } else {
            handleNewTaskSave();
        }
        setTextChangedListener();
    }

    private void handleTaskUpdate(Bundle bundle) {
        String task = bundle.getString("task", "");
        if (!task.isEmpty()) {
            newTaskText.setText(task);
            newTaskSaveButton.setTextColor(requireContext().getColor(R.color.colorPrimaryDark));
        }
    }

    private void handleNewTaskSave() {
        newTaskSaveButton.setOnClickListener(v -> {
            String text = newTaskText.getText().toString();
            if (!text.trim().isEmpty()) {
                insertOrUpdateTask(text);
                dismiss();
            } else {
                // Show an error message or perform appropriate validation
            }
        });
    }

    private void insertOrUpdateTask(String text) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            int taskId = bundle.getInt("id");
            db.updateTask(taskId, text);
        } else {
            ToDoModel task = new ToDoModel();
            task.setTask(text);
            task.setStatus(0);
            db.insertTask(task);
        }
        notifyAndClose();
    }

    private void notifyAndClose() {
        Activity activity = requireActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(requireDialog());
        }
    }

    private void setTextChangedListener() {
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonState(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateSaveButtonState(String text) {
        if (text.trim().isEmpty()) {
            newTaskSaveButton.setEnabled(false);
            newTaskSaveButton.setTextColor(Color.GRAY);
        } else {
            newTaskSaveButton.setEnabled(true);
            newTaskSaveButton.setTextColor(requireContext().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        notifyAndClose();
    }
}