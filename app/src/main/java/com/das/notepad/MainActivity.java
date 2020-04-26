package com.das.notepad;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener {
    private RecyclerView recyclerView;
    // gdWt0BjweH
    private DbHelper dbHelper;
    TextView noItemText;

    // Preferencias
    private SharedPreferences settings;
    public static final String THEME_Key = "app_theme";
    public static final String APP_PREFERENCES = "notepad_settings";
    private int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        theme = settings.getInt(THEME_Key, R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        noItemText = findViewById(R.id.noItemText);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupNavigation(savedInstanceState, toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddNote();
            }
        });

        //db
        dbHelper = new DbHelper(this);

        // List<Note> lNote = dbHelper.getAllNotes();

        // listview personalizado
        recyclerView = findViewById(R.id.rvMain);

        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");


    }

    private void setupNavigation(Bundle savedInstanceState, Toolbar toolbar) {

        // Item del IDrawerItem
        List<IDrawerItem> iDrawerItems = new ArrayList<>();
        iDrawerItems.add(new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.ic_home_black_24dp));

        // sticky DrawItems ; items de la parte de abajo
        List<IDrawerItem> stockyItems = new ArrayList<>();
        SwitchDrawerItem switchDrawerItem = new SwitchDrawerItem()
                .withName("Tema Oscuro")
                .withChecked(theme == R.style.AppTheme_Dark)
                .withIcon(R.drawable.ic_dark_theme)
                .withOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            settings.edit().putInt(THEME_Key, R.style.AppTheme_Dark).apply();
                        } else {
                            settings.edit().putInt(THEME_Key, R.style.AppTheme).apply();
                        }
                        // cerrramos y volvemos a abrir la aplicación para aplicar el tema
                        TaskStackBuilder.create(MainActivity.this)
                                .addNextIntent(new Intent(MainActivity.this, MainActivity.class))
                                .addNextIntent(getIntent()).startActivities();
                    }
                });
        stockyItems.add(switchDrawerItem);

        // Navigation drawer
        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar) // toolbar
                .withSavedInstance(savedInstanceState)
                .withDrawerItems(iDrawerItems) // menu items
                .withTranslucentNavigationBar(true)
                .withStickyDrawerItems(stockyItems) // footer items
                .withOnDrawerItemClickListener(this) // listener for menu items click
                .build();
    }


    private void goToAddNote() {
        Intent i = new Intent(this, AddNote.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Note> lNote = dbHelper.getAllNotes();
        if (lNote.isEmpty()) {
            noItemText.setVisibility(View.VISIBLE);
        } else {
            noItemText.setVisibility(View.GONE);
            displayList(lNote);
        }
    }

    private void displayList(List<Note> allNotes) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdapterRecycler adapterRecycler = new AdapterRecycler(this, allNotes);
        recyclerView.setAdapter(adapterRecycler);
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        return false;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }


}
