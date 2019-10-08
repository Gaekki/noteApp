package com.example.noteapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.noteapp.MainActivity;
import com.example.noteapp.R;
import com.example.noteapp.callbacks.NoteEventListener;
import com.example.noteapp.model.Note;
import com.example.noteapp.utils.NoteUtils;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder> {
    private Context context;
    private ArrayList<Note> notes;
    private NoteEventListener listener;

    public NotesAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.note_layout, parent, false);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder( @NonNull NoteHolder holder, int position) {
        final Note note = getNote(position);
        if (note != null){
            holder.noteText.setText(note.getNoteText());
            holder.noteDate.setText(NoteUtils.dateFromLong(note.getNoteDate()));
            // ini note click even
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onNoteClick(note);
                }
            });

            // init note long click
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onNoteLongClick(note);
                    return false;
                }
            });

        }
    }
    @Override
    public int getItemCount() {
        return  notes.size();
    }

    private Note getNote(int position) {
        return notes.get(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        TextView noteText, noteDate;

        public NoteHolder(View itemView){
            super(itemView);
            noteDate=itemView.findViewById(R.id.note_date);
            noteText=itemView.findViewById(R.id.note_text);
        }



    }

    public void setListener(NoteEventListener listener) {
        this.listener = listener;
    }




}
