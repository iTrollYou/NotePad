package com.das.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        etEmail = findViewById(R.id.etUserEmail);
        etPassword = findViewById(R.id.etUserPassword);
    }

    public void goToRegister(View view) {
        Intent i = new Intent(this, Register.class);
        this.startActivity(i);
    }

    public void login(View view) {
        String username = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        // Campos no vacios
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Algún campo vacío", Toast.LENGTH_SHORT).show();
        } else {
            HttpsURLConnection urlConnection = GeneradorConexionesSeguras.getInstance().
                    crearConexionSegura(this,
                            "https://134.209.235.115/pguerrero002/WEB/conn.php");

            try {
                JSONObject parametrosJSON = new JSONObject();
                parametrosJSON.put("action", "login");
                parametrosJSON.put("email", username);
                parametrosJSON.put("password", password);

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


                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(result.toString());


                    if (json.get("user") == null) {
                        Toast.makeText(this, "Fallo en la identificación", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Identificación correcta", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, MainActivity.class);
                        this.startActivity(i);
                    }
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
