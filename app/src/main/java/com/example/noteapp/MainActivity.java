package com.example.noteapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.GridLayoutManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.noteapp.adapters.NotesAdapter;
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

    private FloatingActionButton fab;

    private int currentScreenOrientation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        fab = (FloatingActionButton) findViewById(R.id.fab);
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
       // showEmptyView();


    }

    /**
     * when no notes show msg in main_layout
     */
    //private void showEmptyView() {
    //    if (notes.size() == 0) {
    //        this.recyclerView.setVisibility(View.GONE);
    //        findViewById(R.id.empty_notes_view).setVisibility(View.VISIBLE);

    //    } else {
    //        this.recyclerView.setVisibility(View.VISIBLE);
    //        findViewById(R.id.empty_notes_view).setVisibility(View.GONE);
    //    }
   // }

    /**
     * Start EditNoteActivity.class for Create New Note
     */
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
