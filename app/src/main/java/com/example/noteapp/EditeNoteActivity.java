package com.example.noteapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noteapp.db.NotesDB;
import com.example.noteapp.db.NotesDao;
import com.example.noteapp.model.Note;

import java.util.Date;

public class EditeNoteActivity extends AppCompatActivity {
    private EditText inputNote;
    private NotesDao dao;
    private Note temp;
    public static final String NOTE_EXTRA_Key = "note_id";

    private SensorManager ss;
    private float acelVal;
    private float acelLast;
    private float shake;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_note);

        //sensor
        ss = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;


        inputNote = findViewById(R.id.input_note);
        dao = NotesDB.getInstance(this).notesDao();
        if (getIntent().getExtras() != null) {
            int id = getIntent().getExtras().getInt(NOTE_EXTRA_Key, 0);
            temp = dao.getNoteById(id);
            inputNote.setText(temp.getNoteText());
        } else inputNote.setFocusable(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edite_note_menu, menu);
        // while editing/adding new note, screen doesn't turn off
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_note)
            onSaveNote();
        return super.onOptionsItemSelected(item);
    }


    private void onSaveNote() {
        // TODO: 20/06/2018 Save Note
        String text = inputNote.getText().toString();
        if (!text.isEmpty()) {
            long date = new Date().getTime(); // get  system time
            // if  exist update els crete new
            if (temp == null) {
                temp = new Note(text, date);
                dao.insertNote(temp); // create new note and insert to database
            } else {
                temp.setNoteText(text);
                temp.setNoteDate(date);
                dao.updateNote(temp); // change text and date and update note on database
            }

            // always screen on mode is off
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            finish(); // return to the MainActivity
        }
    }
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            EditText editNote1 = findViewById(R.id.input_note);
            String editNote = editNote1.getText().toString();

            acelLast = acelVal;
            acelVal = (float) Math.sqrt((double) (x * x + y * y + z * z));
            Float delta = acelVal - acelLast;
            shake = shake * 0.9f + delta;

            if (shake > 30) {
                if (TextUtils.isEmpty(editNote)){
                    Toast.makeText(EditeNoteActivity.this," Please Take Note!!", Toast.LENGTH_SHORT).show();}
                else { openDialog(); }
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };

    @Override
    protected void onResume() {
        super.onResume();
        ss.registerListener(sensorListener, ss.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        ss.unregisterListener(sensorListener);
        super.onPause();
    }

    public void openDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        TextView title = new TextView(this);
        title.setText("Clear All");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);

        // Set Message
        TextView msg = new TextView(this);
        // Message Properties
        msg.setText("\n Are you sure you want to clear all the text?");
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        msg.setTextColor(Color.BLACK);
        alertDialog.setView(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button
                inputNote.setText("");

            }
        });
        alertDialog.show();

    }


}