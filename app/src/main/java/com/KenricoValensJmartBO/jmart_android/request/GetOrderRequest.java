package com.KenricoValensJmartBO.jmart_android.request;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * GetOrderRequest adalah request untuk mendapatkan semua Order Request pada paymentTable. pageSize
 * diset menjadi 100 untuk mencegah ketidakkonsisten hasil untuk user.
 */
public class GetOrderRequest extends StringRequest {
    public static final String URL = "http://10.0.2.2:8080/payment/page?page=%d&pageSize=%d";
    private final Map<String, String> params;

    public GetOrderRequest(int page, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(Method.GET, String.format(URL, page, 100), listener, errorListener);
        params = new HashMap<>();
    }

    public Map<String, String> getParams() {
        return params;
    }
}
