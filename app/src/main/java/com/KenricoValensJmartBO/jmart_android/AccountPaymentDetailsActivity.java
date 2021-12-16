package com.KenricoValensJmartBO.jmart_android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.KenricoValensJmartBO.jmart_android.request.CancelOrderRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class AccountPaymentDetailsActivity extends AppCompatActivity {

    TextView boughtProductName, boughtProductPrice, boughtProductDate, boughtProductMessage, boughtProductShipmentPlan;
    Button cancelAccountOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_payment_details);

        boughtProductName = findViewById(R.id.accPaymentProdName);
        boughtProductPrice = findViewById(R.id.accPaymentProdPrice);
        boughtProductDate = findViewById(R.id.accPaymentProdDate);
        boughtProductMessage = findViewById(R.id.accPaymentProdMessage);
        boughtProductShipmentPlan = findViewById(R.id.accPaymentProdShipmentPlan);

        cancelAccountOrder = findViewById(R.id.accCancelPayment);

        Bundle bundle =  getIntent().getExtras();

        boughtProductName.setText(bundle.getString("name"));
        boughtProductPrice.setText(String.valueOf(bundle.getDouble("price")));
        boughtProductDate.setText(bundle.getString("date"));
        boughtProductMessage.setText(bundle.getString("message"));

        // TODO: Pakai Switch Case nanti
        boughtProductShipmentPlan.setText(String.valueOf(bundle.getByte("shipmentPlans")));

        if(bundle.getString("status").equals("CANCELLED")) {
            cancelAccountOrder.setVisibility(View.GONE);
        }
        else {
            cancelAccountOrder.setVisibility(View.VISIBLE);
        }

        cancelAccountOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountPaymentDetailsActivity.this);

                builder.setTitle("Konfirmasi Cancel Pembelian");
                builder.setMessage("Apakah Anda yakin ingin cancel pembelian Anda?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Response.Listener<String> listener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.equals("true")) {
                                    Toast.makeText(getApplicationContext(),
                                            "Pesanan Anda sudah dicancel.", Toast.LENGTH_SHORT).show();
                                    Intent toAccountPaymentList = new Intent(AccountPaymentDetailsActivity.this, AccountOrderActivity.class);
                                    startActivity(toAccountPaymentList);
                                }
                            }
                        };

                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),
                                        "Terdapat kesalahan sistem.", Toast.LENGTH_SHORT).show();
                            }
                        };

                        int paymentId = bundle.getInt("paymentId");
                        CancelOrderRequest newCancelOrder = new CancelOrderRequest(paymentId, listener, errorListener);
                        RequestQueue queue = Volley.newRequestQueue(AccountPaymentDetailsActivity.this);
                        queue.add(newCancelOrder);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do nothing
                    }
                });

                builder.show();
            }
        });
    }
}