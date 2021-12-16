package com.KenricoValensJmartBO.jmart_android.request;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * TopUpRequest digunakan untuk melakukan TopUp balance pada akun yang sedang log in
 */
public class TopUpRequest extends StringRequest {
    private final static String URL = "http://10.0.2.2:8080/account/" + getLoggedAccount().id + "/topUp";
    private final Map<String, String> params;

    public TopUpRequest(Double amount, Response.Listener<String> listener,
                                Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        params = new HashMap<>();
        params.put("balance", String.valueOf(amount));
    }

    public Map<String, String> getParams() {
        return params;
    }

}
