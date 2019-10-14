package com.example.noteapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.GridLayoutManager;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.noteapp.adapters.NotesAdapter;
import com.example.noteapp.callbacks.MultiActionModeCallback;
import com.example.noteapp.callbacks.NoteEventListener;
import com.example.noteapp.db.NotesDB;
import com.example.noteapp.db.NotesDao;
import com.example.noteapp.model.Note;
import com.example.noteapp.utils.NoteUtils;
import static com.example.noteapp.EditeNoteActivity.NOTE_EXTRA_Key;


import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements NoteEventListener {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private NotesAdapter adapter;
    private NotesDao dao;
    private MultiActionModeCallback actionModeCallback;

    private FloatingActionButton fab;

    private int checkedCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // init recyclerView
        int currentScreenOrientation = this.getResources().getConfiguration().orientation;
        recyclerView = findViewById(R.id.notes_list);
        recyclerView.setHasFixedSize(true);

        // portrait mode vertical list
        if (currentScreenOrientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        // landscape mode horizontal list
        if (currentScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
         recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        }


        // init fab Button
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 13/05/2018  add new note
                onAddNewNote();
            }
        });

        dao = NotesDB.getInstance(this).notesDao();
    }


    private void loadNotes() {
        this.notes = new ArrayList<>();
        List<Note> list = dao.getNotes();// get All notes from DataBase
        this.notes.addAll(list);
        this.adapter = new NotesAdapter(this, this.notes);
        // set listener to adapter
        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);
        showEmptyView();


    }

    // if empty note, showing message "empty note"

    private void showEmptyView() {
        if (notes.size() == 0) {
            this.recyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_notes_view).setVisibility(View.VISIBLE);

        } else {
            this.recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_notes_view).setVisibility(View.GONE);
        }
    }

    // Start EditNoteActivity.class for Create New Note

    private void onAddNewNote() {
        startActivity(new Intent(this, EditeNoteActivity.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    @Override
    public void onNoteClick(Note note) {
        // TODO: 22/07/2018  note clicked : edit note
        Intent edit = new Intent(this, EditeNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_Key, note.getId());
        startActivity(edit);

    }

    @Override
    public void onNoteLongClick(final Note note) {
        // TODO: 2019-10-07 note long clicked : delete, share
        note.setChecked(true);
        checkedCount = 1;
        adapter.setMultiCheckMode(true);

        adapter.setListener(new NoteEventListener() {
            @Override
            public void onNoteClick(Note note) {
                note.setChecked((!note.isChecked()));
                if (note.isChecked())
                    checkedCount++;
                else checkedCount--;

                if (checkedCount > 1) {
                    actionModeCallback.changeShareItemVisible(false);
                } else actionModeCallback.changeShareItemVisible(true);

                if (checkedCount == 0) {
                    actionModeCallback.getAction().finish();
                }
                actionModeCallback.setCount(checkedCount + "/" + notes.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNoteLongClick(Note note) {

            }
        });

        actionModeCallback = new MultiActionModeCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete_notes)
                    onDeleteMultiNotes();
                else if (menuItem.getItemId() == R.id.action_share_note)
                    onShareNote();

                actionMode.finish();
                return false;
            }
        };

        startActionMode(actionModeCallback);

        fab.setVisibility(View.GONE);
        actionModeCallback.setCount(checkedCount + "/" + notes.size());
    }

    private void onDeleteMultiNotes() {
        // TODO: 22/07/2018 delete multi notes

        List<Note> checkedNotes = adapter.getCheckedNotes();
        if (checkedNotes.size() != 0) {
            for (Note note : checkedNotes) {
                dao.deleteNote(note);
            }
            // refresh Notes
            loadNotes();
            Toast.makeText(this, checkedNotes.size() + " Note Deleted successfully !", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "No Note selected", Toast.LENGTH_SHORT).show();

        //adapter.setMultiCheckMode(false);
    }

    private void onShareNote() {
        // TODO: 22/07/2018  we need share just one Note not multi

        Note note = adapter.getCheckedNotes().get(0);
        // TODO: 22/07/2018 do your logic here to share note ; on social or something else
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String notetext = note.getNoteText() + "\n\n Create on : " +
                NoteUtils.dateFromLong(note.getNoteDate()) + "\n  By :" +
                getString(R.string.app_name);
        share.putExtra(Intent.EXTRA_TEXT, notetext);
        startActivity(share);


    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);

        adapter.setMultiCheckMode(false); // uncheck the notes
        adapter.setListener(this); // set back the old listener
        fab.setVisibility(View.VISIBLE);
    }

}



