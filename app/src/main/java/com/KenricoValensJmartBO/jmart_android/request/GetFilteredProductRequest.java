package com.KenricoValensJmartBO.jmart_android.request;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;

import androidx.annotation.Nullable;

import com.KenricoValensJmartBO.jmart_android.model.ProductCategory;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * GetFilteredProductRequest adalah request untuk mendapatkan produk dengan filter.
 */
public class GetFilteredProductRequest extends StringRequest {

    private static final String URL = "http://10.0.2.2:8080/product/getFiltered?page=%d&pageSize=%d&accountId=%d&search=%s&minPrice=%d&maxPrice=%d&category=%s";
    private final Map<String, String> params;

    public GetFilteredProductRequest(int page,
                                     int accountId,
                                     String search,
                                     int minPrice,
                                     int maxPrice,
                                     ProductCategory category,
                                     Response.Listener<String> listener,
                                     @Nullable Response.ErrorListener errorListener) {
        super(Request.Method.GET, String.format(URL, page, 9, accountId, search, minPrice, maxPrice, String.valueOf(category)), listener, errorListener);
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