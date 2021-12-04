package com.KenricoValensJmartBO.jmart_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.KenricoValensJmartBO.jmart_android.model.Account;
import com.KenricoValensJmartBO.jmart_android.request.LoginRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static final Gson gson = new Gson();
    private static Account loggedAccount;

    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private TextView toRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        toRegister = findViewById(R.id.toRegisterPage);

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if(object != null) {
                                loggedAccount = gson.fromJson(object.toString(), Account.class);

                                Toast.makeText(LoginActivity.this, "Login berhasil",
                                        Toast.LENGTH_SHORT).show();

                                Intent successfulLogin = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(successfulLogin);
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Anda belum terdaftar, silahkan register.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Terdapat kesalahan sistem, coba ulang login",
                                Toast.LENGTH_SHORT).show();
                    }
                };
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();
                LoginRequest newLogin = new LoginRequest(email, password, listener, errorListener);
                queue.add(newLogin);
            }
        });

        toRegister.setOnClickListener(v -> {
            Intent toRegisterPage = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(toRegisterPage);
        });
    }

    public static Account getLoggedAccount() {
        return loggedAccount;
    }
}