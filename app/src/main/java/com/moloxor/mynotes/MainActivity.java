package com.moloxor.mynotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesAdapter.NoteViewHolder.NoteViewCallBack, NotesAdapter.DeleteAllNotesViewHolder.btnDeleteAllCallBack {

    public static final int ADD_NOTE_REQUEST_ID = 1001;
    public static final int EDIT_NOTE_REQUEST_ID = 1002;
    public static final String EXTRA_KEY_TITLE = "title";
    public static final String EXTRA_KEY_CONTENT = "content";
    private int pendingEditNotePosition = -1;
    private String pendingTitle = null;
    private NotesAdapter notesAdapter;
    private NotesDB notesDB;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews();
        init();
        getNotesFromDB();

    }

    private void init() {
        notesDB = new NotesDB(this);
    }

    private void setUpViews() {
        setUpToolbar();
        setUpRecyclerView();
        FloatingActionButton fat = findViewById(R.id.fab_main_addNote);
        coordinatorLayout = findViewById(R.id.coordinator);

        fat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST_ID);
            }
        });

    }

    private void setUpRecyclerView() {
        notesAdapter = new NotesAdapter(this, this, this);
        RecyclerView rvNotes = findViewById(R.id.rv_main_notes);
        rvNotes.setAdapter(notesAdapter);
        rvNotes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
    }

    public void getNotesFromDB() {
        List<Note> notes = notesDB.getNotes();

        if (notes.size() > 0) {
            for (int i = 0; i < notes.size(); i++) {
                notesAdapter.addNote(notes.get(i));
            }
        }
    }

    public boolean isTitleExist(Note note) {
        List<Note> notes = notesDB.getNotes();
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            titles.add(notes.get(i).getTitle());
        }

        return !titles.contains(note.getTitle());
    }


    @Override
    public void onDeleteBtnClicked(int position, Note note) {
        notesAdapter.removeNote(position);
        notesDB.deleteNote(note.getTitle());
    }

    @Override
    public void onEditBtnClicked(int position, Note note) {

        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EXTRA_KEY_TITLE, note.getTitle());
        intent.putExtra(EXTRA_KEY_CONTENT, note.getContent());
        startActivityForResult(intent, EDIT_NOTE_REQUEST_ID);
        pendingEditNotePosition = position;
        pendingTitle = note.getTitle();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST_ID && resultCode == RESULT_OK) {
            String title = data.getStringExtra(EXTRA_KEY_TITLE);
            String content = data.getStringExtra(EXTRA_KEY_CONTENT);
            Note note = new Note();
            note.setTitle(title);
            note.setContent(content);

            if (isTitleExist(note)) {
                notesAdapter.addNote(note);
                notesDB.addNote(note);
            } else {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.snack_main_exist), Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }


        } else if (requestCode == EDIT_NOTE_REQUEST_ID && resultCode == RESULT_OK) {
            String title = data.getStringExtra(EXTRA_KEY_TITLE);
            String content = data.getStringExtra(EXTRA_KEY_CONTENT);
            Note note = new Note();
            note.setTitle(title);
            note.setContent(content);

            if (isTitleExist(note)) {
                notesAdapter.updateNote(pendingEditNotePosition, title, content);
                notesDB.updateNote(pendingTitle, title, content);
            } else {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.snack_main_exist), Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }


        }
    }


    @Override
    public void onDeleteAllClicked() {
        notesDB.deleteAll();
    }
}
