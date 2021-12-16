package com.KenricoValensJmartBO.jmart_android;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;
import static com.KenricoValensJmartBO.jmart_android.LoginActivity.session;

import static java.lang.Double.valueOf;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.KenricoValensJmartBO.jmart_android.model.Account;
import com.KenricoValensJmartBO.jmart_android.model.Store;
import com.KenricoValensJmartBO.jmart_android.request.BuyProductRequest;
import com.KenricoValensJmartBO.jmart_android.request.RegisterStoreRequest;
import com.KenricoValensJmartBO.jmart_android.request.RequestFactory;
import com.KenricoValensJmartBO.jmart_android.request.TopUpRequest;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * AboutMeActivity berfungsi untuk melihat informasi akun yang sedang login (nama, balance, dll.) dan
 * aktivitas untuk melakukan Top Up, daftar store dan informasi store, history pembelian account dan
 * history store yang produknya dibeli oleh akun lain, dan pilihan untuk log out.
 */
public class AboutMeActivity extends AppCompatActivity {

    // Inisiasikan setiap komponen layout yang ingin digunakan
    private static final Gson gson = new Gson();
    private TextView showAccountName, showAccountEmail, showAccountBalance, storeName, storeAddress, storePhoneNumber;
    private EditText topUp, registerStoreName, registerStoreAddress, registerStorePhoneNumber;
    private Button topUpBtn, registerStoreBtn, doRegisterBtn, cancelRegisterBtn;
    private CardView registerStoreForm, storeInformation;
    private MenuItem storeOrder, accountHistory;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_me_menu, menu);

        // Jika tidak memiliki store, menu Add Product tidak kelihatan
        invalidateOptionsMenu();
        storeOrder = menu.findItem(R.id.storeOrderRequest);
        if (getLoggedAccount().store != null) {
            storeOrder.setVisible(true);
        }
        else {
            storeOrder.setVisible(false);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        RequestQueue queue = Volley.newRequestQueue(AboutMeActivity.this);

        // Inisiasi semua component pada XML
        topUp = findViewById(R.id.topUpAmount);
        topUpBtn = findViewById(R.id.topUpButton);

        registerStoreBtn = findViewById(R.id.registerStoreButton);
        storeInformation = findViewById(R.id.storeInfo);

        storeName = findViewById(R.id.storeName);
        storeAddress = findViewById(R.id.storeAddress);
        storePhoneNumber = findViewById(R.id.storePhoneNumber);

        registerStoreName = findViewById(R.id.registerStoreName);
        registerStoreAddress = findViewById(R.id.registerStoreAddress);
        registerStorePhoneNumber = findViewById(R.id.registerStorePhoneNumber);

        cancelRegisterBtn = findViewById(R.id.doCancel);
        doRegisterBtn = findViewById(R.id.doRegister);

        registerStoreForm = findViewById(R.id.registerStoreCardView);

        // Cari ID untuk field Account
        showAccountName = findViewById(R.id.accountName);
        showAccountEmail = findViewById(R.id.accountEmail);
        showAccountBalance = findViewById(R.id.accountBalance);

        // Set field Account ke TextView
        showAccountName.setText(getLoggedAccount().name);
        showAccountEmail.setText(getLoggedAccount().email);

        showAccountBalance.setText(String.valueOf(getLoggedAccount().balance));

        if(getLoggedAccount().store != null) {
            // Jika Account PUNYA store, maka tampilkan card informasi store
            registerStoreBtn.setVisibility(View.GONE);
            registerStoreForm.setVisibility(View.GONE);
            storeInformation.setVisibility(View.VISIBLE);

            storeName.setText(getLoggedAccount().store.name);
            storeAddress.setText(getLoggedAccount().store.address);
            storePhoneNumber.setText(getLoggedAccount().store.phoneNumber);
        }
        else {
            // Jika Account TIDAK PUNYA store, tampilkan Layout CardView dengan id registerStoreCardView
            registerStoreBtn.setVisibility(View.VISIBLE);
            registerStoreForm.setVisibility(View.GONE);
            storeInformation.setVisibility(View.GONE);
        }

        /**
         * setOnClickListener untuk button melakukan top up.
         */
        topUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { // Jika request berhasil, response akan ada disini
                        if(response.equals("true")) { // Jika backend mengembalikan true, tambahkan balance ke getLoggedAccount().balance
                            Double totalBalance = Double.parseDouble(showAccountBalance.getText().toString())
                                    + Double.parseDouble(topUp.getText().toString());
                            showAccountBalance.setText(String.valueOf(totalBalance));
                            getLoggedAccount().balance = totalBalance;

                            // Clear field amount top up
                            topUp.setText("");

                            Toast.makeText(getApplicationContext(),
                                    "Top up berhasil. Selamat berbelanja!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    "Top up gagal.", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                "Maaf, terdapat kesalahan sistem.", Toast.LENGTH_SHORT).show();
                    }
                };

                // Error handling saat mencegah field null
                if(topUp.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Silahkan masukkan jumlah uang yang ingin top up.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Double amount = Double.valueOf(topUp.getText().toString());

                    if(amount < 20000) { // Jika semua terisi, lakukan besarnya. (minimal 20000)
                        Toast.makeText(getApplicationContext(), "Minimal top up senilai 20000",
                                Toast.LENGTH_SHORT).show();
                    }
                    else { // Jika banyaknya top-up lebih dari 20000, jalankan request
                        TopUpRequest newTopUp = new TopUpRequest(amount, listener, errorListener);

                        RequestQueue queue = Volley.newRequestQueue(AboutMeActivity.this);
                        queue.add(newTopUp);
                    }
                }
            }
        });

        /**
         * Untuk register, pengguna bisa klik button ini dan akan menghide button ini dan show
         * CardView berisi form daftar store.
         */
        registerStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerStoreBtn.setVisibility(View.GONE);
                registerStoreForm.setVisibility(View.VISIBLE);
            }
        });

        /**
         * Untuk melakukan pendaftaran, user mengklik button ini.
         */
        doRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try { // Jika response berhasil, ubah response ke JSONObject.
                            JSONObject object = new JSONObject(response);
                            if(object != null) {
                                // Ubah JSONObject ke bentuk Store.class, lalu masukkan ke getLoggedAccount().store.
                                getLoggedAccount().store = gson.fromJson(object.toString(), Store.class);

                                storeName.setText(getLoggedAccount().store.name);
                                storeAddress.setText(getLoggedAccount().store.address);
                                storePhoneNumber.setText(getLoggedAccount().store.phoneNumber);
                                Toast.makeText(getApplicationContext(), "Pendaftaran Store berhasil.", Toast.LENGTH_SHORT).show();

                                registerStoreForm.setVisibility(View.GONE);
                                storeInformation.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Pendaftaran store gagal.", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan sistem.", Toast.LENGTH_SHORT).show();
                    }
                };

                // Dapatkan String yang dibutuhkan untuk pendaftaran Store
                String newStoreName = registerStoreName.getText().toString();
                String newStoreAddress = registerStoreAddress.getText().toString();
                String newStorePhoneNumber = registerStorePhoneNumber.getText().toString();

                // Error handling jika null
                if(newStoreName.isEmpty() || newStoreAddress.isEmpty() || newStorePhoneNumber.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Tidak boleh ada field yang kosong!", Toast.LENGTH_SHORT).show();
                }
                else { // Jika tidak null, maka lanjutkan untuk melakukan request.
                    RegisterStoreRequest newRegisterStore = new RegisterStoreRequest(newStoreName, newStoreAddress, newStorePhoneNumber,
                            listener, errorListener);

                    queue.add(newRegisterStore);
                }
            }
        });

        /**
         * Cancel register button untuk menutup form CardView pendaftaran store. Button RegisterStore
         * akan dimunculkan.
         */
        cancelRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerStoreBtn.setVisibility(View.VISIBLE);
                registerStoreForm.setVisibility(View.GONE);
            }
        });
    }

    /**
     * method onOptionsItemSelected digunakan untuk set onMenuItemClickListener. Terdapat tiga buah
     * menu, yaitu Store History, Account history, dan Account logout.
     * @param item Item yang ter-click
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.storeOrderRequest:
                Intent toStoreOrderRequest = new Intent(AboutMeActivity.this, StoreOrderActivity.class);
                startActivity(toStoreOrderRequest);
                return true;
            case R.id.accountBuyHistory:
                Intent toAccountBuyHistory = new Intent(AboutMeActivity.this, AccountOrderActivity.class);
                startActivity(toAccountBuyHistory);
                return true;
            case R.id.accountLogOut:
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutMeActivity.this);

                builder.setTitle("Konfirmasi Log Out");
                builder.setMessage("Apakah Anda ingin Log Out?");
                builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        session.logoutUser();
                        Toast.makeText(getApplicationContext(), "Berhasil log out", Toast.LENGTH_SHORT).show();
                        Intent toLoginPage = new Intent(AboutMeActivity.this, LoginActivity.class);
                        startActivity(toLoginPage);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}