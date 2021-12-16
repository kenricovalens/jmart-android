package com.KenricoValensJmartBO.jmart_android.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Class LoginRequest
 * <p>
 *     Class ini digunakan untuk melakukan request terhadap kredensial log in
 *     yang sudah diberikan pada EditText. Login Request ini menggunakan Volley
 *     dengan Method POST ke URL yang sudah ditentukan RestController pada
 *     jmart back end.
 * </p>
 */
public class LoginRequest extends StringRequest {
    private static final String URL = "http://10.0.2.2:8080/account/login";
    private final Map<String, String> params;

    /**
     * Constructor untuk LoginRequest. Method ini akan membuat object LoginRequest baru.
     * @param email Email dari user.
     * @param password Password user.
     * @param listener Response.Listener saat request Volley berhasil.
     * @param errorListener Response.ErrorListener saat request Volley gagal.
     */
    public LoginRequest(String email, String password, Response.Listener<String> listener,
                        Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);

        /**
         * Params digunakan untuk menempatkan paramater apa yang ingin dikirimkan ke backend
         * dengan menentukan key dan value dari masing-masing parameter.
         */
        params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
    }

    public Map<String, String> getParams() {
        return params;
    }
}
