package com.KenricoValensJmartBO.jmart_android.request;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Class GetProductRequest digunakan untuk melakukan pengambilan produk dari product.json.
 * Terdapat perbedaan, yaitu saat menggunakan Volley dengan method GET, Volley tidak bisa
 * memasukkan parameter langsung ke dalam URL. Maka dari itu, URL harus diformat secara manual.
 */
public class GetProductRequest extends StringRequest {
    private static final String URL = "http://10.0.2.2:8080/product/" + getLoggedAccount().id + "/store?page=%d&pageSize=%d";
    private final Map<String, String> params;

    public GetProductRequest(int page,
                             Response.Listener<String> listener,
                             @Nullable Response.ErrorListener errorListener) {
        super(Request.Method.GET, String.format(URL, page, 9), listener, errorListener);
        params = new HashMap<>();
        /*
           Tidak diperlukan untuk menggunakan params.
           Hal ini disebabkan Volley StringRequest untuk Method.GET, parameter tidak bisa
           dipassing dan tidak dimasukkan ke dalam URL (berbeda dari Postman yang dari body bisa langsung menambahkan
           parameter ke URL).
         */
    }

    public Map<String, String> getParams() {
        return params;
    }
}
