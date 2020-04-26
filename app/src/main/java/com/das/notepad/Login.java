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

import javax.net.ssl.HttpsURLConnection;

public class Login extends AppCompatActivity {

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void goToRegister(View view) {
    }

    public void login(View view) {
        //Obtener los campos introducidos por el usuario
        EditText i_username = findViewById(R.id.etUserEmail);
        String username = i_username.getText().toString();
        System.out.println(username);
        EditText i_pass = findViewById(R.id.etUserPassword);
        String pass = i_pass.getText().toString();
        System.out.println(pass);
        //Comprobar que el usuario ha introducido todos los campos
        if (username.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show();
        } else {
            HttpsURLConnection urlConnection = GeneradorConexionesSeguras.getInstance().
                    crearConexionSegura(this,
                            "https://134.209.235.115/pguerrero002/WEB/init.php");

            try {
                JSONObject parametrosJSON = new JSONObject();
                parametrosJSON.put("action", "login");
                parametrosJSON.put("email", username);
                parametrosJSON.put("password", pass);

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                Log.i("MY-APP", "JSON: " + parametrosJSON); //genera mensajes de tipo informacion

                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(parametrosJSON.toString()); //
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


                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(result.toString());
                    String usuario = (String) json.get("user");

                    Log.i("MY-APP", "DATA: " + result); //genera mensajes de tipo informacion

                    if (usuario == null) {
                        Toast.makeText(this, "Login incorrect, please try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Login correct", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, MainActivity.class);
                        i.putExtra("usuario", usuario);
                        this.startActivity(i);
                    }
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
