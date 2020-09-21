package com.maxrzhe.simplenotesapp.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.maxrzhe.simplenotesapp.R;
import com.maxrzhe.simplenotesapp.pojo.Note;



public class NoteFirestoreRecyclerAdapter extends FirestoreRecyclerAdapter<Note, NoteFirestoreRecyclerAdapter.NoteViewHolder> {


    public NoteFirestoreRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.noteTextView.setText(note.getText());
        holder.completedCheckBox.setChecked(note.getIsCompleted());
        CharSequence dateCharSequence = DateFormat.format("EEEE, MMM d, yyyy, HH:mm:ss", note.getCreated().toDate());
        holder.createdTextView.setText(dateCharSequence);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView noteTextView;
        private TextView createdTextView;
        private CheckBox completedCheckBox;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTextView = itemView.findViewById(R.id.tv_note);
            createdTextView = itemView.findViewById(R.id.tv_created);
            completedCheckBox = itemView.findViewById(R.id.chb_note_check);
        }
    }

}
