package com.KenricoValensJmartBO.jmart_android.fragment;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;
import static com.KenricoValensJmartBO.jmart_android.fragment.FilterFragment.filterCategory;
import static com.KenricoValensJmartBO.jmart_android.fragment.FilterFragment.filterHighestPrice;
import static com.KenricoValensJmartBO.jmart_android.fragment.FilterFragment.filterLowestPrice;
import static com.KenricoValensJmartBO.jmart_android.fragment.FilterFragment.filterName;
import static com.KenricoValensJmartBO.jmart_android.fragment.FilterFragment.isFiltered;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.KenricoValensJmartBO.jmart_android.MainActivity;
import com.KenricoValensJmartBO.jmart_android.R;
import com.KenricoValensJmartBO.jmart_android.model.Account;
import com.KenricoValensJmartBO.jmart_android.model.Product;
import com.KenricoValensJmartBO.jmart_android.model.ProductCategory;
import com.KenricoValensJmartBO.jmart_android.request.GetFilteredProductRequest;
import com.KenricoValensJmartBO.jmart_android.request.GetProductRequest;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Button prevPageBtn, nextPageBtn, goFindProductBtn;
    private EditText pageNum;

    public ProductFragment() {
        // Required empty public constructor
    }

    public static ProductFragment newInstance(String param1) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product, container, false);
        ListView lstItems = v.findViewById(R.id.listView);
        Gson gson = new Gson();

        prevPageBtn = v.findViewById(R.id.productPrevPage);
        nextPageBtn = v.findViewById(R.id.productNextPage);
        goFindProductBtn = v.findViewById(R.id.productGoPage);

        pageNum = v.findViewById(R.id.productPageNumber);

        // Saat dibuat, lakukan request terhadap GetProductRequest.
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<Product> productReturned = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject newObj = jsonArray.getJSONObject(i);
                        Product product = gson.fromJson(newObj.toString(), Product.class);
                        productReturned.add(product);
                    }

                    ArrayAdapter<Product> allItemsAdapter = new ArrayAdapter<Product>(getActivity().getBaseContext(),
                            android.R.layout.simple_list_item_1,
                            productReturned);
                    lstItems.setAdapter(allItemsAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Terdapat kesalahan sistem.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        int pages = Integer.parseInt(pageNum.getText().toString()) - 1;

        GetProductRequest newGetProduct = new GetProductRequest(pages, listener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getBaseContext());
        queue.add(newGetProduct);

        // Buttons
        prevPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPage = Integer.parseInt(pageNum.getText().toString());
                if (currentPage == 1) {
                    Toast.makeText(getContext(), "Tidak ada halaman sebelum ini.", Toast.LENGTH_SHORT).show();
                } else {
                    currentPage--;
                    pageNum.setText(String.valueOf(currentPage));
                }
            }
        });

        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPage = Integer.parseInt(pageNum.getText().toString());
                currentPage++;
                pageNum.setText(String.valueOf(currentPage));
            }
        });

        goFindProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Terdapat dua kondisi saat button onClick ini dijalankan :
                1. Saat tidak terfilter (boolean isFiltered = false), maka request /product/id/store
                2. Saat terfilter (boolean isFiltered == true), maka request ke /product/getFiltered

                Penggantian nilai boolean isFiltered akan berubah saat user melakukan klik button di
                FilterFragment. Jika user menekan button "Apply" maka isFiltered menjadi TRUE dan
                jika user menekan button "Clear" maka isFiltered menjadi FALSE.
                */
                if (!isFiltered) {
                    Response.Listener<String> listener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                List<Product> productReturned = new ArrayList<>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject newObj = jsonArray.getJSONObject(i);
                                    Product product = gson.fromJson(newObj.toString(), Product.class);
                                    productReturned.add(product);
                                }

                                ArrayAdapter<Product> allItemsAdapter = new ArrayAdapter<Product>(getActivity().getBaseContext(),
                                        android.R.layout.simple_list_item_1,
                                        productReturned);
                                lstItems.setAdapter(allItemsAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Terdapat kesalahan sistem.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    };
                    int pages = Integer.parseInt(pageNum.getText().toString()) - 1;

                    GetProductRequest newGetProduct = new GetProductRequest(pages, listener, errorListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity().getBaseContext());
                    queue.add(newGetProduct);
                } else {
                    Response.Listener<String> listener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                List<Product> productReturned = new ArrayList<>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject newObj = jsonArray.getJSONObject(i);
                                    Product product = gson.fromJson(newObj.toString(), Product.class);
                                    productReturned.add(product);
                                }

                                ArrayAdapter<Product> allItemsAdapter = new ArrayAdapter<Product>(getActivity().getBaseContext(),
                                        android.R.layout.simple_list_item_1,
                                        productReturned);
                                lstItems.setAdapter(allItemsAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Terdapat kesalahan sistem.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    };

                    int pages = Integer.parseInt(pageNum.getText().toString()) - 1;
                    String searchName = filterName;
                    int minimumPrice = filterLowestPrice;
                    int maximumPrice = filterHighestPrice;
                    ProductCategory category = filterCategory;

                    GetFilteredProductRequest newFilteredProduct = new GetFilteredProductRequest(pages,
                            getLoggedAccount().id, searchName, minimumPrice, maximumPrice, category, listener, errorListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity().getBaseContext());
                    queue.add(newFilteredProduct);
                }
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        Gson gson = new Gson();
        ListView lstItems = getView().findViewById(R.id.listView);

        if (!isFiltered) {
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<Product> productReturned = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject newObj = jsonArray.getJSONObject(i);
                            Product product = gson.fromJson(newObj.toString(), Product.class);
                            productReturned.add(product);
                        }

                        ArrayAdapter<Product> allItemsAdapter = new ArrayAdapter<Product>(getActivity().getBaseContext(),
                                android.R.layout.simple_list_item_1,
                                productReturned);
                        lstItems.setAdapter(allItemsAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Terdapat kesalahan sistem.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            int pages = Integer.parseInt(pageNum.getText().toString()) - 1;

            GetProductRequest newGetProduct = new GetProductRequest(pages, listener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(getActivity().getBaseContext());
            queue.add(newGetProduct);
        } else {
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<Product> productReturned = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject newObj = jsonArray.getJSONObject(i);
                            Product product = gson.fromJson(newObj.toString(), Product.class);
                            productReturned.add(product);
                        }

                        ArrayAdapter<Product> allItemsAdapter = new ArrayAdapter<Product>(getActivity().getBaseContext(),
                                android.R.layout.simple_list_item_1,
                                productReturned);
                        lstItems.setAdapter(allItemsAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Terdapat kesalahan sistem.",
                            Toast.LENGTH_SHORT).show();
                }
            };

            int pages = Integer.parseInt(pageNum.getText().toString()) - 1;
            String searchName = filterName;
            int minimumPrice = filterLowestPrice;
            int maximumPrice = filterHighestPrice;
            ProductCategory category = filterCategory;

            GetFilteredProductRequest newFilteredProduct = new GetFilteredProductRequest(pages,
                    getLoggedAccount().id, searchName, minimumPrice, maximumPrice, category, listener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(getActivity().getBaseContext());
            queue.add(newFilteredProduct);

        }
    }
}