package com.example.noteapp;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.example.noteapp.adapters.NotesAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class test_note_date {

    @Rule
    public ActivityTestRule<MainActivity> mAddTestRule1= new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mAddTest = null;

    @Before
    public void setUp() throws Exception {
        mAddTest = mAddTestRule1.getActivity();
    }

    @Test
    public void testLaunch(){
        View view = mAddTest.findViewById(R.id.note_date);
        assertNotNull(view);
    }
    @After
    public void tearDown() throws Exception {
        mAddTest = null;
    }

}