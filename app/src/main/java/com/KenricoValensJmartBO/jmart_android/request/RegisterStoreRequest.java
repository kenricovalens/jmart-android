package com.KenricoValensJmartBO.jmart_android.request;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Request ini digunakan untuk mendaftarkan store.
 */
public class RegisterStoreRequest extends StringRequest {
    private static final String URL = "http://10.0.2.2:8080/account/" + getLoggedAccount().id + "/registerStore";
    private final Map<String, String> params;

    public RegisterStoreRequest(String name, String address, String phoneNumber, Response.Listener<String> listener,
                        Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        params = new HashMap<>();
        params.put("name", name);
        params.put("address", address);
        params.put("phoneNumber", phoneNumber);
    }

    public Map<String, String> getParams() {
        return params;
    }
}
