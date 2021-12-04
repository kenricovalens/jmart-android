package com.KenricoValensJmartBO.jmart_android.request;

import androidx.annotation.Nullable;

import com.KenricoValensJmartBO.jmart_android.model.ProductCategory;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CreateProductRequest extends StringRequest {
    public static final String URL = "http://10.0.2.2:8080/product/create";
    private final Map<String, String> params;

    public CreateProductRequest(int accountId,
                                String name,
                                int weight,
                                double price,
                                double discount,
                                boolean conditionUsed,
                                ProductCategory category,
                                byte shipmentPlans,
                                Response.Listener<String> listener,
                                @Nullable Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);
        params = new HashMap<>();
        params.put("accountId", String.valueOf(accountId));
        params.put("name", name);
        params.put("weight", String.valueOf(weight));
        params.put("price", String.valueOf(price));
        params.put("discount", String.valueOf(discount));
        params.put("conditionUsed", String.valueOf(conditionUsed));
        params.put("category", String.valueOf(category));
        params.put("shipmentPlans", String.valueOf(shipmentPlans));
    }

    public Map<String, String> getParams() {
        return params;
    }
}
