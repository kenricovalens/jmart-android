package com.KenricoValensJmartBO.jmart_android.request;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * SubmitOrderRequest digunakan Store untuk melakukan submit untuk diproses delivery.
 */
public class SubmitOrderRequest extends StringRequest {
    public static final String URL = "http://10.0.2.2:8080/payment/%d/submit";
    private final Map<String, String> params;

    public SubmitOrderRequest(int paymentId,
                              String receipt,
                              Response.Listener<String> listener,
                              @Nullable Response.ErrorListener errorListener) {
        super(Method.POST, String.format(URL, paymentId), listener, errorListener);
        params = new HashMap<>();
        params.put("receipt", receipt);
    }

    public Map<String, String> getParams() {
        return params;
    }

}
