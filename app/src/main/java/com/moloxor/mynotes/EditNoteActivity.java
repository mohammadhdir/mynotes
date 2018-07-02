package com.moloxor.mynotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class EditNoteActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        setUpViews();
    }

    private void setUpViews() {
        setUpToolbar();


        final EditText etTitle = findViewById(R.id.et_editNote_title);
        final EditText etContent = findViewById(R.id.et_editNote_content);

        final String title = getIntent().getStringExtra(MainActivity.EXTRA_KEY_TITLE);
        final String content = getIntent().getStringExtra(MainActivity.EXTRA_KEY_CONTENT);

        if (title != null && content != null) {
            etTitle.setText(title);
            etContent.setText(content);
        }

        FloatingActionButton fab = findViewById(R.id.fab_editNote_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTitle.length() > 0) {
                    if (etContent.length() > 0) {

                        Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
                        intent.putExtra(MainActivity.EXTRA_KEY_TITLE, etTitle.getText().toString());
                        intent.putExtra(MainActivity.EXTRA_KEY_CONTENT, etContent.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        etContent.setError(getResources().getString(R.string.et_editNote_content_error));
                    }
                } else {
                    etTitle.setError(getResources().getString(R.string.et_editNote_title_error));
                }
            }
        });

    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_editNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
