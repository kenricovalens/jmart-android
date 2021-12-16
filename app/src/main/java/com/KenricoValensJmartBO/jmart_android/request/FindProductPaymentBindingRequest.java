package com.KenricoValensJmartBO.jmart_android.request;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * FindProductPaymentBindingRequest digunakan untuk mengambil Product sesuai dengan ID pada Payment.
 */
public class FindProductPaymentBindingRequest extends StringRequest {
    private static final String URL = "http://10.0.2.2:8080/product/%d";
    private final Map<String, String> params;

    public FindProductPaymentBindingRequest(int productId,
                                            Response.Listener<String> listener,
                                            @Nullable Response.ErrorListener errorListener) {
        super(Request.Method.GET, String.format(URL, productId), listener, errorListener);
        params = new HashMap<>();
        /*
           Tidak diperlukan untuk menggunakan params.
           Hal ini disebabkan Volley StringRequest untuk Method.GET, parameter tidak bisa dipassing
           dan tidak dimasukkan ke dalam URL (berbeda dari Postman yang dari body bisa langsung menambahkan
           parameter ke URL) sehingga harus dilakukan formatting URL agar sesuai yang diinginkan.
         */
    }

    public Map<String, String> getParams() {
        return params;
    }
}
