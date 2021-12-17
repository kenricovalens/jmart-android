package com.KenricoValensJmartBO.jmart_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.KenricoValensJmartBO.jmart_android.model.Account;
import com.KenricoValensJmartBO.jmart_android.request.RegisterRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * RegisterActivity digunakan jika user tidak memiliki akun, maka user bisa mendaftarkan akun baru.
 */
public class RegisterActivity extends AppCompatActivity {

    // Inisiasi komponen yang ingin digunakan
    private EditText registerName;
    private EditText registerEmail;
    private EditText registerPassword;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Cari ID setiap komponen
        registerName = findViewById(R.id.registerName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerButton = findViewById(R.id.registerButton);

        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);

        /**
         * Setelah mengclick button ini, maka ambil string dari field name, email, dan passsword untuk
         * dikirim ke request membuat akun baru. Jika berhasil, maka user akan diredirect ke LoginActivity.
         */
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if(object != null) {
                                Toast.makeText(getApplicationContext(), "Register berhasil, silahkan login.",
                                        Toast.LENGTH_SHORT).show();

                                Intent successfulRegister = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(successfulRegister);
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Register gagal, silahkan ulangi.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                Response.ErrorListener errorListener =  new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Terdapat kesalahan sistem, coba ulangi register.",
                                Toast.LENGTH_SHORT).show();
                    }
                };

                String name = registerName.getText().toString();
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();
                RegisterRequest newRegister = new RegisterRequest(name, email, password, listener, errorListener);
                queue.add(newRegister);
            }
        });
    }
}