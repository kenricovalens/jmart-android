package com.KenricoValensJmartBO.jmart_android;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.KenricoValensJmartBO.jmart_android.model.Product;

import java.math.BigDecimal;

/**
 * ItemDetailsActivity adalah activity untuk melihat detail produk saat diklik yang ada di ListView.
 */
public class ItemDetailsActivity extends AppCompatActivity {

    // Inisiasi komponen yang ingin digunakan
    TextView productName, productWeight, productPrice, productDiscount, productConditionUsed, productCategory, productShipmentPlans;
    Button proceedToBuy;
    int productId, accountId;
    byte shipmentPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Cari ID tiap komponen sesuai .xml
        productName = findViewById(R.id.productDetailName);
        productWeight = findViewById(R.id.productDetailWeight);
        productPrice = findViewById(R.id.productDetailPrice);
        productDiscount = findViewById(R.id.productDetailDiscount);
        productConditionUsed = findViewById(R.id.productDetailConditionUsed);
        productCategory = findViewById(R.id.productDetailCategory);
        productShipmentPlans = findViewById(R.id.productDetailShipmentPlans);

        proceedToBuy = findViewById(R.id.toPaymentActivity);

        // Dapatkan extras dari Intent berisi semua key value pair tentang produk yang diclick.
        if(getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            productId = bundle.getInt("productId");
            shipmentPlan = bundle.getByte("shipmentPlans");
            accountId = bundle.getInt("accountId");

            productName.setText(bundle.getString("name"));
            productWeight.setText(String.valueOf(bundle.getInt("weight")));

            BigDecimal prodPrice = BigDecimal.valueOf(bundle.getDouble("price"));
            productPrice.setText(prodPrice.toPlainString());
            productDiscount.setText(String.valueOf(bundle.getDouble("discount")));
            productCategory.setText(String.valueOf(bundle.getString("category")));

            switch(String.valueOf(bundle.getBoolean("conditionUsed"))) {
                case "true":
                    productConditionUsed.setText("Used");
                    break;
                case "false":
                    productConditionUsed.setText("New");
                    break;
            }


            switch(String.valueOf(bundle.getByte("shipmentPlans"))) {
                case "1":
                    productShipmentPlans.setText("INSTANT");
                    break;
                case "2":
                    productShipmentPlans.setText("SAME DAY");
                    break;
                case "4":
                    productShipmentPlans.setText("NEXT DAY");
                    break;
                case "8":
                    productShipmentPlans.setText("REGULER");
                    break;
                case "16":
                    productShipmentPlans.setText("KARGO");
                    break;
            }
        }

        /**
         * Button ini digunakan untuk membeli produk yang sudah diclick untuk dilihat detailnya.
         * Untuk melakukan konfirmasi pembelian, setelah user click button ini maka akan dibuat bundle
         * berisi informasi yang dibutuhkan, lalu Intent ke PaymentActivity.
         */
        proceedToBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();

                bundle.putString("name", productName.getText().toString());
                bundle.putInt("productId", productId);
                bundle.putDouble("price", Double.parseDouble(productPrice.getText().toString()));
                bundle.putDouble("discount", Double.parseDouble(productDiscount.getText().toString()));
                bundle.putByte("shipmentPlan", shipmentPlan);

                // User tidak bisa membeli produknya sendiri
                if(getLoggedAccount().id == accountId) {
                    Toast.makeText(getApplicationContext(), "Anda tidak bisa membeli produk Anda sendiri.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent toPayment = new Intent(ItemDetailsActivity.this, PaymentActivity.class);
                    toPayment.putExtras(bundle);
                    startActivity(toPayment);
                }
            }
        });

    }
}