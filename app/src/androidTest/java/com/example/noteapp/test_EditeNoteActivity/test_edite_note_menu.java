package com.example.noteapp;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class test_edite_note_menu {

    @Rule
    public ActivityTestRule<EditeNoteActivity> mAddTestRule1= new ActivityTestRule<EditeNoteActivity>(EditeNoteActivity.class);

    private EditeNoteActivity mAddTest = null;

    @Before
    public void setUp() throws Exception {
        mAddTest = mAddTestRule1.getActivity();
    }

    @Test
    public void testLaunch(){
        View view = mAddTest.findViewById(R.menu.edite_note_menu);
        assertNotNull(view);
    }
    @After
    public void tearDown() throws Exception {
        mAddTest = null;
    }

}