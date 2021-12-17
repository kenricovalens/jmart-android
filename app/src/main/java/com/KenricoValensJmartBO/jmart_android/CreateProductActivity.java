package com.KenricoValensJmartBO.jmart_android;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.KenricoValensJmartBO.jmart_android.model.ProductCategory;
import com.KenricoValensJmartBO.jmart_android.request.CreateProductRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * CreateProductActivity digunakan untuk membuat produk baru.
 */
public class CreateProductActivity extends AppCompatActivity {

    // Inisiasi semua komponen yang ingin digunakan
    EditText edProductName, edProductWeight, edProductPrice, edProductDiscount;
    RadioGroup radioConditionUsed;
    Spinner spinnerProductCategory, spinnerProductShipmentPlan;
    Button createProductBtn, cancelCreateProductBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        RequestQueue queue = Volley.newRequestQueue(CreateProductActivity.this);

        // Cari ID dari layout sesuai komponen yang ada .xml
        edProductName = findViewById(R.id.createProductName);
        edProductWeight = findViewById(R.id.createProductWeight);
        edProductPrice = findViewById(R.id.createProductPrice);
        edProductDiscount = findViewById(R.id.createProductDiscount);

        radioConditionUsed = findViewById(R.id.radioGroupCondition);

        spinnerProductCategory = findViewById(R.id.createProductCategory);
        spinnerProductShipmentPlan = findViewById(R.id.createProductShipmentPlan);

        createProductBtn = findViewById(R.id.createProductButton);
        cancelCreateProductBtn = findViewById(R.id.cancelCreateProductButton);

        /**
         * Button Create digunakan untuk membuat produk dengan mengirikan request. Tidak bisa buat
         * produk jika terdapat field yang kosong.
         */
        createProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // Setelah dibuat, clear semua field agar mencegah user membuat produk
                            // dengan spesifikasi yang sama
                            if(jsonObject != null) {
                                Toast.makeText(getApplicationContext(),
                                        "Produk berhasil dibuat.", Toast.LENGTH_SHORT).show();
                                edProductName.setText("");
                                edProductWeight.setText("");
                                edProductPrice.setText("");
                                edProductDiscount.setText("");
                                radioConditionUsed.clearCheck();
                                spinnerProductCategory.setSelection(0);
                                spinnerProductShipmentPlan.setSelection(0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Produk gagal dibuat", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                "Terdapat kesalahan sistem, silahkan ulangi buat produk.", Toast.LENGTH_SHORT).show();
                    }
                };

                // Error handling jika ada field yang kosong
                if(edProductName.getText().toString().isEmpty() || edProductWeight.getText().toString().isEmpty()
                        || edProductPrice.getText().toString().isEmpty()
                        || edProductDiscount.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Semua field harus diisi.", Toast.LENGTH_SHORT).show();
                }
                else {
                    String productName = edProductName.getText().toString();
                    int productWeight = Integer.parseInt(edProductWeight.getText().toString());
                    Double productPrice = Double.parseDouble(edProductPrice.getText().toString());
                    Double productDiscount = Double.parseDouble(edProductDiscount.getText().toString());

                    boolean conditionUsed = false;

                    ProductCategory productCategory = ProductCategory.valueOf(spinnerProductCategory.getSelectedItem().toString());
                    String shipmentPlan = spinnerProductShipmentPlan.getSelectedItem().toString();

                    switch(radioConditionUsed.getCheckedRadioButtonId()) {
                        case R.id.newCondition:
                            conditionUsed = false;
                            break;
                        case R.id.usedCondition:
                            conditionUsed = true;
                            break;
                        default:
                            Toast.makeText(getApplicationContext(),
                                    "Harap pilih kondisi produk.", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    if(productDiscount > 100) {
                        edProductDiscount.setText("100");
                        productDiscount = 100.0;
                    }
                    else if(productDiscount < 0) {
                        edProductDiscount.setText("0");
                        productDiscount = 0.0;
                    }
                    byte shipmentBytes;

                    switch(shipmentPlan) {
                        case "INSTANT":
                            shipmentBytes = 1 << 0;
                            break;
                        case "SAME DAY":
                            shipmentBytes = 1 << 1;
                            break;
                        case "NEXT DAY":
                            shipmentBytes = 1 << 2;
                            break;
                        case "REGULER":
                            shipmentBytes = 1 << 3;
                            break;
                        case "KARGO":
                            shipmentBytes = 1 << 4;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + shipmentPlan);

                    }
                    // Request untuk membuat produk baru dengan parameter yang dibutuhkan backend
                    CreateProductRequest newCreateProduct = new CreateProductRequest(getLoggedAccount().id, productName,
                            productWeight, productPrice, productDiscount, conditionUsed,
                            productCategory, shipmentBytes, listener, errorListener);

                    queue.add(newCreateProduct);
                }
            }
        });

        /**
         * Jika cancel, maka Intent ke MainActivity.
         */
        cancelCreateProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToMainActivity = new Intent(CreateProductActivity.this, MainActivity.class);
                startActivity(backToMainActivity);
            }
        });
    }
}