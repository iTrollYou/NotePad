package com.das.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


import javax.net.ssl.HttpsURLConnection;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View view) {
//Obtener los campos introducidos por el usuario
        EditText etUserEmail = findViewById(R.id.etUserEmail);
        String email = etUserEmail.getText().toString();
        System.out.println(email);
        EditText etUserName = findViewById(R.id.etUserName);
        String username = etUserName.getText().toString();
        EditText etUserPassword = findViewById(R.id.etUserPassword);
        String pass = etUserPassword.getText().toString();

        //Comprobar que el usuario ha introducido todos los campos
        if (email.isEmpty() || username.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show();
        } else {
            HttpsURLConnection urlConnection =
                    GeneradorConexionesSeguras.getInstance().
                            crearConexionSegura(this, "https://134.209.235.115/pguerrero002/WEB/init.php");

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
                Log.i("MY-APP", "STATUS: " + statusCode); //genera mensajes de tipo informacion

                if (statusCode == 200) {
                    BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                        System.out.println(line);
                    }
                    inputStream.close();
                    Log.i("MY-APP", "DATA: " + result); //genera mensajes de tipo informacion

                    if (result.toString().contains("Ha habido algún error")) {
                        Toast.makeText(this, "Username used", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(this, "SignUp succesfull", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, Login.class);
                        this.startActivity(i);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
