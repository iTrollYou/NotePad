package com.das.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Objects;

public class EditNote extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText etNoteTitle, etNoteDescription;

    private Calendar calendar;
    private String todaysDate;
    private String currentTime;
    private int ID;

    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Aplicar tema
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.APP_PREFERENCES,
                Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt(MainActivity.THEME_Key, R.style.AppTheme);
        setTheme(theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        // toolbar personalizable
        toolbar = findViewById(R.id.toolbar);
        // en 'colors.xml' se ha anadido un nuevo color 'colorWhite'
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Nota");

        // habilitar flecha de retroceso en el toolbar.
        // se necesita modificar el 'AndroidManifest.xml', sino no retrocede
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        Intent i = getIntent();
        ID = i.getIntExtra("ID", 0);
        dbHelper = new DbHelper(this);
        Note note = dbHelper.getNote(ID);

        final String title = note.getTitle();
        String content = note.getDescription();

        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteDescription = findViewById(R.id.etNoteDescription);

        // modificar el titulo del toolbar al modificar 'etNoteTitle'
        etNoteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etNoteTitle.setText(title);
        etNoteDescription.setText(content);

        // datos del calendario
        calendar = Calendar.getInstance();
        todaysDate = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1)
                + "/" + calendar.get(Calendar.YEAR);
        currentTime = fillTime(calendar.get(Calendar.HOUR)) + ":"
                + fillTime(calendar.get(Calendar.MINUTE));

    }

    /**
     * @param time Minuto u hora
     * @return Se añade un 0, si el timpo es < 10.
     */
    private String fillTime(int time) {
        if (time < 10)
            return "0" + time;
        return time + "";

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_note_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {
            Note note = new Note(ID, etNoteTitle.getText().toString(),
                    etNoteDescription.getText().toString(), todaysDate, currentTime);
            if (dbHelper.editNote(note)) {
                goToMain();
                Toast.makeText(this, "Nota editada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Nota no editada", Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.menu_delete) {
            Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Ir hacia el MainActivity.
     */
    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

}
