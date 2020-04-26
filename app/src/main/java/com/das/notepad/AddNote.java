package com.das.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Objects;

public class AddNote extends AppCompatActivity {

    private EditText etNoteTitle, etNoteDescription;
    private ImageView ivPicture;

    private String todayDate;
    private String currentTime;
    private DbHelper dbHelper;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    // id notificación
    private static final int idNotification = (int) (Math.random() * 999999) + 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // set theme
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.APP_PREFERENCES,
                Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt(MainActivity.THEME_Key, R.style.AppTheme);
        setTheme(theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // toolbar personalizable
        Toolbar toolbar = findViewById(R.id.toolbar);
        // en 'colors.xml' se ha anadido un nuevo color 'colorWhite'
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Nota");

        // habilitar flecha de retroceso en el toolbar.
        // se necesita modificar el 'AndroidManifest.xml', sino no retrocede
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteDescription = findViewById(R.id.etNoteDescription);

        ivPicture = findViewById(R.id.ivPicture);

        // modificar el titulo del toolbar al modificar 'etNoteTitle'
        etNoteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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


        // datos del calendario
        Calendar calendar = Calendar.getInstance();
        todayDate = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1)
                + "/" + calendar.get(Calendar.YEAR);
        currentTime = fillTime(calendar.get(Calendar.HOUR)) + ":"
                + fillTime(calendar.get(Calendar.MINUTE));


        // Conexión de base de datos persistente
        // https://developer.android.com/training/data-storage/sqlite?hl=es#PersistingDbConnection
        dbHelper = new DbHelper(this);


        // Camara
        FloatingActionButton fab = findViewById(R.id.fbCamera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

    }

    // anadir opciones al toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_note_activity, menu);
        return true;
    }


    // listener a la opciones del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // menu_add: identificador de la opcion '+' anadida en la toolbar
        switch (menuItem.getItemId()) {
            case R.id.menu_delete:
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
            case R.id.menu_save:
                Note note = new Note(etNoteTitle.getText().toString(),
                        etNoteDescription.getText().toString(),
                        todayDate, currentTime);

                if (dbHelper.addNote(note)) {
                    sendNotification();
                    onBackPressed();  // volver al anterior intent
                } else {
                    Toast.makeText(this, "Nota no guardada", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }


    private String fillTime(int time) {
        if (time < 10)
            return "0" + time;
        return time + "";

    }

    private void sendNotification() {
        NotificationManager elManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder elBuilder =
                new NotificationCompat.Builder(this, "IdCanalNotepad");
        elBuilder.setSmallIcon(android.R.drawable.ic_menu_save)
                .setContentTitle("Note guardada")
                .setSubText("Información")
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel elCanal = new NotificationChannel("IdCanalNotepad",
                    "NotePadAdd", NotificationManager.IMPORTANCE_DEFAULT);
            //elCanal.setDescription("Note saved");
            elCanal.enableLights(true);
            elCanal.setLightColor(R.color.colorWhite);
            elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            elCanal.enableVibration(true);

            Objects.requireNonNull(elManager).createNotificationChannel(elCanal);

            elManager.notify(idNotification, elBuilder.build());
        }
    }


    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivPicture.setImageBitmap(imageBitmap);
        }
    }


    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

}
