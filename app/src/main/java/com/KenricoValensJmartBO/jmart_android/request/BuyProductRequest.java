package com.KenricoValensJmartBO.jmart_android.request;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request yang digunakan bagi pengguna untuk membeli produk yang tersedia. Pembelian
 * produk akan membuat bukti Payment baru.
 */
public class BuyProductRequest extends StringRequest {
    public static final String URL = "http://10.0.2.2:8080/payment/create";
    private final Map<String, String> params;

    public BuyProductRequest(int buyerId,
                             int productId,
                             int productCount,
                             String shipmentAddress,
                             byte shipmentPlan,
                             Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        params = new HashMap<>();
        params.put("buyerId", String.valueOf(buyerId));
        params.put("productId", String.valueOf(productId));
        params.put("productCount", String.valueOf(productCount));
        params.put("shipmentAddress", shipmentAddress);
        params.put("shipmentPlan", String.valueOf(shipmentPlan));
    }

    public Map<String, String> getParams() {
        return params;
    }
}
