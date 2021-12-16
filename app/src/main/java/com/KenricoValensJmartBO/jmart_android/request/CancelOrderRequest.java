package com.KenricoValensJmartBO.jmart_android.request;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * CancelOrderRequest digunakan untuk mengcancel pembelian. Operasi ini bisa dilakukan oleh
 * user dan store.
 */
public class CancelOrderRequest extends StringRequest {
    public static final String URL = "http://10.0.2.2:8080/payment/%d/cancel";
    private final Map<String, String> params;

    public CancelOrderRequest(int paymentId,
                              Response.Listener<String> listener,
                              @Nullable Response.ErrorListener errorListener) {
        super(Method.POST, String.format(URL, paymentId), listener, errorListener);
        params = new HashMap<>();
    }

    public Map<String, String> getParams() {
        return params;
    }
}
