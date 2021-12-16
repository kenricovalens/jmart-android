package com.KenricoValensJmartBO.jmart_android;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.KenricoValensJmartBO.jmart_android.request.BuyProductRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class PaymentActivity extends AppCompatActivity {

    TextView buyProductName, buyProductPrice, buyProductDiscount, loggedAccountBalance, buyTotalPrice;
    EditText buyProductPcs, buyProductAddress;
    Button buyProductBtn, cancelBuyProductBtn, incrementProductPcs, decrementProductPcs;
    int productId;
    byte shipmentPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        buyProductName = findViewById(R.id.buyProductName);
        buyProductPrice = findViewById(R.id.buyProductPrice);
        buyProductDiscount = findViewById(R.id.buyProductDiscount);
        loggedAccountBalance = findViewById(R.id.loggedAccountBalance);
        buyTotalPrice = findViewById(R.id.buyProductTotalPrice);

        buyProductPcs = findViewById(R.id.buyProductPcs);
        buyProductAddress = findViewById(R.id.buyProductAddress);

        buyProductBtn = findViewById(R.id.buyProductBtn);
        cancelBuyProductBtn = findViewById(R.id.cancelBuyProductBtn);

        incrementProductPcs = findViewById(R.id.incrementPcs);
        decrementProductPcs = findViewById(R.id.decrementPcs);

        if(getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            productId = bundle.getInt("productId");
            shipmentPlan = bundle.getByte("shipmentPlan");

            buyProductName.setText(bundle.getString("name"));

            BigDecimal prodPrice = BigDecimal.valueOf(bundle.getDouble("price"));
            buyProductPrice.setText(prodPrice.toPlainString());
            buyProductDiscount.setText(String.valueOf(bundle.getDouble("discount")));
            loggedAccountBalance.setText(String.valueOf(getLoggedAccount().balance));
            buyTotalPrice.setText(buyProductPrice.getText().toString());
        }

        buyProductPcs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!buyProductPcs.getText().toString().isEmpty()) {
                    int productPcs = Integer.parseInt(buyProductPcs.getText().toString());
                    buyTotalPrice.setText(String.valueOf(Double.parseDouble(String.valueOf(productPcs * Double.parseDouble(buyProductPrice.getText().toString())))));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        incrementProductPcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyProductPcs.setText(String.valueOf(Integer.parseInt(buyProductPcs.getText().toString()) + 1));
            }
        });

        decrementProductPcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(buyProductPcs.getText().toString()) == 1 || buyProductPcs.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Silahkan isi jumlah produk.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    buyProductPcs.setText(String.valueOf(Integer.parseInt(buyProductPcs.getText().toString()) - 1));
                }
            }
        });

        buyProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getLoggedAccount().balance < Double.parseDouble(buyTotalPrice.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Saldo Anda tidak cukup, silahkan top up terlebih dahulu.",
                            Toast.LENGTH_SHORT).show();
                }
                else if(buyProductAddress.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Alamat tidak boleh kosong.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);

                    builder.setTitle("Konfirmasi Pembelian Produk");
                    builder.setMessage("Apakah Anda sudah yakin?");
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Response.Listener<String> listener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject object = new JSONObject(response);
                                        if(object != null) {
                                            getLoggedAccount().balance -= Integer.parseInt(buyProductPcs.getText().toString()) * Double.parseDouble(buyProductPrice.getText().toString());

                                            Toast.makeText(getApplicationContext(), "Pembelian berhasil! Silahkan cek history Anda.",
                                                    Toast.LENGTH_SHORT).show();

                                            Intent toMainPage = new Intent(PaymentActivity.this, MainActivity.class);
                                            startActivity(toMainPage);
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), "Transaksi gagal, silahkan ulangi.",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            Response.ErrorListener errorListener = new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Terdapat kesalahan sistem.", Toast.LENGTH_SHORT).show();
                                }
                            };

                            String address = buyProductAddress.getText().toString();
                            int totalPcs = Integer.parseInt(buyProductPcs.getText().toString());

                            BuyProductRequest newBuyProductRequest = new BuyProductRequest(getLoggedAccount().id,
                                    productId, totalPcs, address, shipmentPlan, listener, errorListener);

                            RequestQueue queue = Volley.newRequestQueue(PaymentActivity.this);
                            queue.add(newBuyProductRequest);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }
            }
        });

        cancelBuyProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toItemDetails = new Intent(PaymentActivity.this, ItemDetailsActivity.class);
                startActivity(toItemDetails);
            }
        });

    }
}