package com.KenricoValensJmartBO.jmart_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.KenricoValensJmartBO.jmart_android.model.Account;
import com.KenricoValensJmartBO.jmart_android.model.Store;
import com.KenricoValensJmartBO.jmart_android.request.LoginRequest;
import com.KenricoValensJmartBO.jmart_android.session.SessionManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * LoginActivity digunakan untuk melakukan login. Halaman ini akan diperlihatkan ke user untuk pertama
 * kalinya. Pada activity ini sudah diimplementasikan session sehingga saat device masih menyala dan
 * sudah melakukan login sebelumnya, user bisa langsung masuk ke MainActivity.
 */
public class LoginActivity extends AppCompatActivity {
    private static final Gson gson = new Gson();
    // loggedAccount adalah global variabel untuk akun yang sedang log in sekarang.
    private static Account loggedAccount;

    // Inisiasi komponen yang akan digunakan
    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private TextView toRegister;

    public static SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Buat session baru
        session = new SessionManager(getApplicationContext());

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        toRegister = findViewById(R.id.toRegisterPage);

        // Jika user pernah log in sebelumnya, langsung cari Account terkait dengan ID, masukkan ke
        // loggedAccount, lalu Intent ke MainActivity
        if(session.isLoggedIn()) {
            Map<String, Integer> userId = session.getUserDetails();
            int loggedAccountId = userId.get("accountId");

            final String URL = "http://10.0.2.2:8080/account/" + loggedAccountId;

            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object = new JSONObject(response);
                        loggedAccount = gson.fromJson(object.toString(), Account.class);

                        Toast.makeText(getApplicationContext(), "Session berhasil dipulihkan",
                                Toast.LENGTH_SHORT).show();

                        Intent acquiredSessionToMain = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(acquiredSessionToMain);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            };

            StringRequest getAccountSession = new StringRequest(Request.Method.GET, URL, listener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(getAccountSession);
        }
        else { // Jika session false, maka user harus login pada activity ini.
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

            /**
             * Button login digunakan untuk melakukan login menggunakan email dan password yang
             * dimasukkan user
             */
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

                                    // Create Session here
                                    session.createLoginSession(loggedAccount.id);

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
                    // Ambil email dan password, kirimkan melalui request.
                    String email = loginEmail.getText().toString();
                    String password = loginPassword.getText().toString();
                    LoginRequest newLogin = new LoginRequest(email, password, listener, errorListener);
                    queue.add(newLogin);
                }
            });

            // TextView ditambahkan method onClick untuk Intent ke RegisterActivity
            toRegister.setOnClickListener(v -> {
                Intent toRegisterPage = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(toRegisterPage);
            });
        }
        }



    public static Account getLoggedAccount() {
        return loggedAccount;
    }
}