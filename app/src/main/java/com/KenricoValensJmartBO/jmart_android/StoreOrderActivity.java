package com.KenricoValensJmartBO.jmart_android;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.KenricoValensJmartBO.jmart_android.model.Payment;
import com.KenricoValensJmartBO.jmart_android.model.Product;
import com.KenricoValensJmartBO.jmart_android.model.ProductPaymentBinding;
import com.KenricoValensJmartBO.jmart_android.request.FindProductPaymentBindingRequest;
import com.KenricoValensJmartBO.jmart_android.request.GetOrderRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * StoreOrderActivity pada dasarnya sama seperti AccountOrderActivity, yang membedakan adalah isi ListView
 * dengan filter yang berbeda. Selain itu, Store bisa melakukan Accept dan Submit order.
 */
public class StoreOrderActivity extends AppCompatActivity {

    ListView orderListView;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_order);

        orderListView = findViewById(R.id.accOrderListView);

        // Setelah activity ini aktif, langsung request semua order.
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Ambil semua Payment
                    JSONArray jsonArray = new JSONArray(response);
                    List<ProductPaymentBinding> filteredPaymentList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        // Pisahkan Payment per object
                        JSONObject newObj = jsonArray.getJSONObject(i);
                        ProductPaymentBinding productPaymentBinding = new ProductPaymentBinding();
                        Payment payment = gson.fromJson(newObj.toString(), Payment.class);
                        productPaymentBinding.payment = payment;

                        Response.Listener<String> listener1 = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response1) {
                                try {
                                    JSONObject object = new JSONObject(response1);
                                    if (object != null) {
                                        Product productReturned = gson.fromJson(object.toString(), Product.class);
                                        productPaymentBinding.product = productReturned;

                                        // Filter jika pembuat produk tersebut sama seperti akun yang sedang log in, maka
                                        // order dituju untuk store akun itu.
                                        if (productPaymentBinding.product.accountId == getLoggedAccount().id) {
                                            filteredPaymentList.add(productPaymentBinding);
                                        }

                                        ArrayAdapter<ProductPaymentBinding> allItemsAdapter = new ArrayAdapter<ProductPaymentBinding>(StoreOrderActivity.this,
                                                android.R.layout.simple_list_item_1,
                                                filteredPaymentList);
                                        orderListView.setAdapter(allItemsAdapter);

                                        // Set agar setiap item dapat diclick
                                        orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                ProductPaymentBinding paymentClicked = filteredPaymentList.get(i);

                                                Bundle bundle = new Bundle();
                                                // Put all the product and payment fields
                                                // Product
                                                bundle.putInt("accountId", paymentClicked.product.accountId);
                                                bundle.putString("category", paymentClicked.product.category.toString());
                                                bundle.putBoolean("conditionUsed", paymentClicked.product.conditionUsed);
                                                bundle.putDouble("discount", paymentClicked.product.discount);
                                                bundle.putString("name", paymentClicked.product.name);
                                                bundle.putDouble("price", paymentClicked.product.price);
                                                bundle.putByte("shipmentPlans", paymentClicked.product.shipmentPlans);
                                                bundle.putInt("weight", paymentClicked.product.weight);

                                                // Payment
                                                bundle.putInt("paymentId", paymentClicked.payment.id);
                                                bundle.putString("date", String.valueOf(paymentClicked.payment.history.get(paymentClicked.payment.history.size() - 1).date));
                                                bundle.putString("message", paymentClicked.payment.history.get(paymentClicked.payment.history.size() - 1).message);
                                                bundle.putString("status", String.valueOf(paymentClicked.payment.history.get(paymentClicked.payment.history.size() - 1).status));
                                                bundle.putString("address", paymentClicked.payment.shipment.address);
                                                bundle.putInt("cost", paymentClicked.payment.shipment.cost);
                                                bundle.putByte("plan", paymentClicked.payment.shipment.plan);
                                                bundle.putString("receipt", paymentClicked.payment.shipment.receipt);
                                                bundle.putInt("productCount", paymentClicked.payment.productCount);

                                                Intent toStorePaymentDetails = new Intent(StoreOrderActivity.this, StorePaymentDetailsActivity.class);
                                                toStorePaymentDetails.putExtras(bundle);
                                                startActivity(toStorePaymentDetails);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        Response.ErrorListener errorListener1 = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Terdapat kesalahan sistem."
                                        , Toast.LENGTH_SHORT).show();
                            }
                        };

                        // Lakukan request mencari product terkait payment
                        FindProductPaymentBindingRequest newFindProductPaymentBinding = new FindProductPaymentBindingRequest(productPaymentBinding.payment.productId, listener1, errorListener1);
                        RequestQueue queue = Volley.newRequestQueue(StoreOrderActivity.this);
                        queue.add(newFindProductPaymentBinding);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Terjadi gangguan pengambilan data.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Terdapat kesalahan sistem.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        // Lakukan request mencari Payment.
        GetOrderRequest newAccountOrder = new GetOrderRequest(0, listener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(StoreOrderActivity.this);
        queue.add(newAccountOrder);
    }
}