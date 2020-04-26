package com.das.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;


import javax.net.ssl.HttpsURLConnection;

public class Register extends AppCompatActivity {
    private EditText etEmail, etPassword, etUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etUserEmail);
        etPassword = findViewById(R.id.etUserPassword);
        etUserName = findViewById(R.id.etUserName);
    }

    public void register(View view) {
        String email = etEmail.getText().toString();
        String username = etUserName.getText().toString();
        String pass = etPassword.getText().toString();

        // Campos no vacios
        if (email.isEmpty() || username.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Algún campo vacío", Toast.LENGTH_SHORT).show();
        } else {
            HttpsURLConnection urlConnection = GeneradorConexionesSeguras.getInstance().
                    crearConexionSegura(this,
                            "https://134.209.235.115/pguerrero002/WEB/conn.php");

            try {
                JSONObject parametrosJSON = new JSONObject();
                parametrosJSON.put("action", "signup");
                parametrosJSON.put("email", email);
                parametrosJSON.put("name", username);
                parametrosJSON.put("password", pass);


                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(parametrosJSON.toString());
                out.close();

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    BufferedInputStream inputStream =
                            new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader =
                            new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    inputStream.close();

                    if (result.toString().contains("Error")) {
                        Toast.makeText(this, "Email en uso", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(this, "Registro con éxito", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, Login.class);
                        this.startActivity(i);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void goToLogin(View view) {
        Intent i = new Intent(this, Login.class);
        this.startActivity(i);
    }
}
