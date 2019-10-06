package com.example.noteapp;

import android.arch.persistence.room.Insert;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.noteapp.adapters.NotesAdapter;
import com.example.noteapp.callbacks.NoteEventListener;
import com.example.noteapp.db.NotesDB;
import com.example.noteapp.db.NotesDao;
import com.example.noteapp.model.Note;
import com.example.noteapp.utils.NoteUtils;


import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.noteapp.EditeNoteActivity.NOTE_EXTRA_Key;

public class MainActivity extends AppCompatActivity implements NoteEventListener {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private NotesAdapter adapter;
    private NotesDao dao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView=findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // initiate recyclerView

        // + button to add new note
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2019-10-04 add new note
                onAddNewNote();

            }
        });

        dao = NotesDB.getInstance(this).notesDao();
    }

    private void loadNotes() {
        this.notes = new ArrayList<>();
        List<Note> list = dao.getNotes();
        this.notes.addAll(list);
        this.adapter = new NotesAdapter(this, this.notes);
        this.recyclerView.setAdapter(adapter);
        this.adapter.setListener(this);

        // showEmptyView();
        // adapter.notifyDataSetChanged();
    }

    //private void showEmptyView() {
    //    if (notes.size() == 0) {
    //       this.recyclerView.setVisibility(View.GONE);
    //        findViewById(R.id.empty_notes_view).setVisibility(View.VISIBLE);
    //    } else {
    //        this.recyclerView.setVisibility(View.VISIBLE);
    //        findViewById(R.id.empty_notes_view).setVisibility(View.GONE);
    //    }
    // }

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
        // TODO: 2019-10-06 note clicked : edit note

        Intent edit = new Intent(this,EditeNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_Key, note.getId());
        startActivity(edit);

    }

    @Override
    public void onNoteLongClick(final Note note) {
        // TODO: 2019-10-06 note long clicked : delete, share..

        new AlertDialog.Builder(this)

                .setTitle(R.string.app_name)

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 2019-10-06 delete Note from database and refresh
                        dao.deleteNote(note);
                        loadNotes();
                    }

                })

                .setNegativeButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 2019-10-06 share note text
                        Intent share = new Intent(Intent.ACTION_SEND);
                        String text = note.getNoteText() + "\n create on :"
                                + NoteUtils.dateFromLong(note.getNoteDate()) +
                                "by :" + getString(R.string.app_name);

                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, text);
                        startActivity(share);

                    }
                })
                .create()
                .show();

                }
        }



