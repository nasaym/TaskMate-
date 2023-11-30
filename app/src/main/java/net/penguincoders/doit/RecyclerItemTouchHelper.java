package net.penguincoders.doit;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import net.penguincoders.doit.Adapters.ToDoAdapter;


public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ToDoAdapter adapter;

    public RecyclerItemTouchHelper(ToDoAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            showDeleteConfirmationDialog(position);
        } else {
            adapter.editItem(position);
        }
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this Task?");
        builder.setPositiveButton("Confirm", (dialog, which) -> adapter.deleteItem(position));
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> adapter.notifyItemChanged(position));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        drawBackgroundAndIcon(viewHolder, c, dX);
    }

    private void drawBackgroundAndIcon(@NonNull RecyclerView.ViewHolder viewHolder, Canvas c, float dX) {
        View itemView = viewHolder.itemView;
        Drawable icon = getIconForDirection(dX);
        ColorDrawable background = getBackgroundForDirection(dX);

        setBoundsForBackgroundAndIcon(viewHolder, itemView, dX, icon, background);
        background.draw(c);
        icon.draw(c);
    }

    private Drawable getIconForDirection(float dX) {
        return (dX > 0) ? ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_edit) :
                ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_delete);
    }

    private ColorDrawable getBackgroundForDirection(float dX) {
        return (dX > 0) ? new ColorDrawable(ContextCompat.getColor(adapter.getContext(), R.color.colorPrimaryDark)) :
                new ColorDrawable(Color.RED);
    }

    private void setBoundsForBackgroundAndIcon(@NonNull RecyclerView.ViewHolder viewHolder, View itemView, float dX, Drawable icon, ColorDrawable background) {
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(), (int) (itemView.getLeft() + dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + (int) dX - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }
    }
}