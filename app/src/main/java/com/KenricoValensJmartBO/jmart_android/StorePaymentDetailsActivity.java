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

import com.KenricoValensJmartBO.jmart_android.request.AcceptOrderRequest;
import com.KenricoValensJmartBO.jmart_android.request.CancelOrderRequest;
import com.KenricoValensJmartBO.jmart_android.request.SubmitOrderRequest;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

public class StorePaymentDetailsActivity extends AppCompatActivity {

    TextView orderedProductName, orderedProductPrice, orderedProductDate, orderedProductMessage, orderedProductShipmentPlan;
    Button cancelStoreOrder, acceptStoreOrder, submitStoreOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_payment_details);

        orderedProductName = findViewById(R.id.storePaymentProdName);
        orderedProductPrice = findViewById(R.id.storePaymentProdPrice);
        orderedProductDate = findViewById(R.id.storePaymentProdDate);
        orderedProductMessage = findViewById(R.id.storePaymentProdMessage);
        orderedProductShipmentPlan = findViewById(R.id.storePaymentProdShipmentPlan);

        cancelStoreOrder = findViewById(R.id.storeCancelPayment);
        acceptStoreOrder = findViewById(R.id.storeAcceptPayment);
        submitStoreOrder = findViewById(R.id.storeSubmitPayment);

        Bundle bundle = getIntent().getExtras();

        orderedProductName.setText(bundle.getString("name"));
        orderedProductPrice.setText(String.valueOf(bundle.getDouble("price")));
        orderedProductDate.setText(bundle.getString("date"));
        orderedProductMessage.setText(bundle.getString("message"));

        // TODO: Pakai Switch Case nanti
        orderedProductShipmentPlan.setText(String.valueOf(bundle.getByte("shipmentPlans")));

        // set button OnClickListener
        // Cancel button, cant click if status CANCELLED or status already accepted and on_delivery
        cancelStoreOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bundle.getString("status").equals("WAITING_CONFIRMATION")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StorePaymentDetailsActivity.this);

                    builder.setTitle("Konfirmasi Cancel Order");
                    builder.setMessage("Apakah Anda yakin ingin cancel order Anda?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Response.Listener<String> listener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if(response.equals("true")) {
                                        Toast.makeText(getApplicationContext(),
                                                "Order Anda sudah dicancel.", Toast.LENGTH_SHORT).show();
                                        Intent toStorePaymentList = new Intent(StorePaymentDetailsActivity.this, StoreOrderActivity.class);
                                        startActivity(toStorePaymentList);
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
                            RequestQueue queue = Volley.newRequestQueue(StorePaymentDetailsActivity.this);
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
                else {
                    Toast.makeText(getApplicationContext(), "Order tidak bisa mengcancelnya."
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Accept button, cant click if already accepted or status is CANCELLED
        acceptStoreOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bundle.getString("status").equals("WAITING_CONFIRMATION")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StorePaymentDetailsActivity.this);

                    builder.setTitle("Konfirmasi Accept Order");
                    builder.setMessage("Apakah Anda yakin ingin accept order Anda?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Response.Listener<String> listener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if(response.equals("true")) {
                                        Toast.makeText(getApplicationContext(),
                                                "Order telah di-accept.", Toast.LENGTH_SHORT).show();
                                        Intent toStorePaymentList = new Intent(StorePaymentDetailsActivity.this, StoreOrderActivity.class);
                                        startActivity(toStorePaymentList);
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
                            AcceptOrderRequest newAcceptOrder = new AcceptOrderRequest(paymentId, listener, errorListener);
                            RequestQueue queue = Volley.newRequestQueue(StorePaymentDetailsActivity.this);
                            queue.add(newAcceptOrder);
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
                else {
                    Toast.makeText(getApplicationContext(), "Order tidak bisa di-accept."
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Submit button, cant click until accepted or status is CANCELLED
        submitStoreOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bundle.getString("status").equals("ON_PROGRESS")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StorePaymentDetailsActivity.this);

                    builder.setTitle("Konfirmasi Submit Order");
                    builder.setMessage("Apakah Anda yakin ingin mengirim order Anda?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Response.Listener<String> listener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if(response.equals("true")) {
                                        Toast.makeText(getApplicationContext(),
                                                "Order Anda sedang dalam pengiriman.", Toast.LENGTH_SHORT).show();
                                        Intent toStorePaymentList = new Intent(StorePaymentDetailsActivity.this, StoreOrderActivity.class);
                                        startActivity(toStorePaymentList);
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

                            SubmitOrderRequest newSubmitOrder = new SubmitOrderRequest(paymentId, "ThanksForShopping", listener, errorListener);
                            RequestQueue queue = Volley.newRequestQueue(StorePaymentDetailsActivity.this);
                            queue.add(newSubmitOrder);
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
                else {
                    Toast.makeText(getApplicationContext(), "Order tidak bisa dikirim (belum di-accept atau sudah tercancel)."
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}