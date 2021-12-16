package com.KenricoValensJmartBO.jmart_android.request;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class StoreOrderRequest extends StringRequest {
    public static final String URL = "http://10.0.2.2:8080/payment/page?page=%d&pageSize=%d";
    private final Map<String, String> params;

    public StoreOrderRequest(int page, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(Method.GET, String.format(URL, page, 9), listener, errorListener);
        params = new HashMap<>();
    }

    public Map<String, String> getParams() {
        return params;
    }
}
