package com.das.notepad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class DescriptionNote extends AppCompatActivity {
    int ID;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Aplicar tema
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.APP_PREFERENCES,
                Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt(MainActivity.THEME_Key, R.style.AppTheme);
        setTheme(theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        ID = i.getIntExtra("ID", 0);
        dbHelper = new DbHelper(this);
        Note note = dbHelper.getNote(ID);

        Objects.requireNonNull(getSupportActionBar()).setTitle(note.getTitle());
        TextView details = findViewById(R.id.tvDescripcion);
        details.setText(note.getDescription());
        details.setMovementMethod(new ScrollingMovementMethod());


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuEdit) {
            Intent i = new Intent(this, EditNote.class);
            i.putExtra("ID", ID);
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Crea un dialog personalizado, con el cual el usuario puede confirmar el borrado de la nota.
     */
    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro?").setCancelable(true)
                .setPositiveButton("Sí",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHelper.deleteNote(ID);
                                Toast.makeText(getApplicationContext(), "Nota borrada",
                                        Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }


    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

}