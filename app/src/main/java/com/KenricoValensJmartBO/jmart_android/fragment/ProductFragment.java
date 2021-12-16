package com.KenricoValensJmartBO.jmart_android.fragment;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;
import static com.KenricoValensJmartBO.jmart_android.fragment.FilterFragment.filterCategory;
import static com.KenricoValensJmartBO.jmart_android.fragment.FilterFragment.filterHighestPrice;
import static com.KenricoValensJmartBO.jmart_android.fragment.FilterFragment.filterLowestPrice;
import static com.KenricoValensJmartBO.jmart_android.fragment.FilterFragment.filterName;
import static com.KenricoValensJmartBO.jmart_android.fragment.FilterFragment.isFiltered;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.KenricoValensJmartBO.jmart_android.ItemDetailsActivity;
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
 * ProductFragment adalah fragment untuk menampilkan list untuk product yang diambil dari backend.
 */
public class ProductFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // Inisiasi widget sesuai dengan layout .xml
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

    /**
     * Method onCreateView() ini digunakan untuk menampilkan ProductFragment pada MainActivity.
     * @param inflater Inflate layout
     * @param container Container fragment
     * @param savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product, container, false);
        // Cari ListView pada layout.
        ListView lstItems = v.findViewById(R.id.listView);
        Gson gson = new Gson();

        // Inisiasi semua komponen pada layout
        prevPageBtn = v.findViewById(R.id.productPrevPage);
        nextPageBtn = v.findViewById(R.id.productNextPage);
        goFindProductBtn = v.findViewById(R.id.productGoPage);

        pageNum = v.findViewById(R.id.productPageNumber);

        // Saat dibuat, lakukan request terhadap GetProductRequest.
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Karena mendapatkan beberapa produk, maka konversi menjadi JSONArray.
                    JSONArray jsonArray = new JSONArray(response);
                    List<Product> productReturned = new ArrayList<>();

                    /* Setiap elemen pada JSONArray akan diubah ke JSONObject, lalu menggunakan gson
                       untuk mengkonversi JSONObject menjadi string, lalu masukkan ke class Product.
                       Tambahkan produk ke List.
                     */
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject newObj = jsonArray.getJSONObject(i);
                        Product product = gson.fromJson(newObj.toString(), Product.class);
                        productReturned.add(product);
                    }

                    // Buat ArrayAdapter untuk mempopulasikan ListView.
                    ArrayAdapter<Product> allItemsAdapter = new ArrayAdapter<Product>(getActivity().getBaseContext(),
                            android.R.layout.simple_list_item_1,
                            productReturned);
                    // setAdapter pada ListView untuk menampilkannya.
                    lstItems.setAdapter(allItemsAdapter);

                    /**
                     * Setiap produk bisa diklik, maka dari itu harus membuat onItemClickListener.
                     * Button akan mendapatkan posisi dan digunakan untuk mendapatkan produk pada
                     * List product yang sekarang berisi produk.
                     */
                    lstItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Product productClicked = productReturned.get(position);

                            int accountId = productClicked.accountId;
                            String productName = productClicked.name;
                            int productId = productClicked.id;
                            int productWeight = productClicked.weight;
                            boolean productConditionUsed = productClicked.conditionUsed;
                            double productDiscount = productClicked.discount;
                            double productPrice = productClicked.price;
                            byte productShipmentPlans = productClicked.shipmentPlans;
                            ProductCategory productCategory = productClicked.category;

                            /* Bundle digunakan sebagai object penyimpan pair key dengan value.
                               Bundle akan dikirim bersamaan dengan Intent sebagai nilai yang
                               dipassing ke Activity selanjutnya.
                             */
                            Bundle bundle = new Bundle();
                            bundle.putInt("accountId", accountId);
                            bundle.putInt("productId", productId);
                            bundle.putString("name", productName);
                            bundle.putInt("weight", productWeight);
                            bundle.putBoolean("conditionUsed", productConditionUsed);
                            bundle.putDouble("discount", productDiscount);
                            bundle.putDouble("price", productPrice);
                            bundle.putByte("shipmentPlans", productShipmentPlans);
                            bundle.putString("category", String.valueOf(productCategory));

                            Intent itemDetails = new Intent(getContext(), ItemDetailsActivity.class);
                            // tambahkah Bundle ke Intent.
                            itemDetails.putExtras(bundle);
                            startActivity(itemDetails);
                        }
                    });
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

        // Buat requestnya dan masukkan ke queue.
        GetProductRequest newGetProduct = new GetProductRequest(pages, listener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getBaseContext());
        queue.add(newGetProduct);

        /**
         * prevButton digunakan untuk mengurangi nomor halaman. Jika halaman sudah 1, tidak bisa mundur lagi.
         */
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

        /**
         * nextButton digunakan untuk menambah nomor halaman.
         */
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPage = Integer.parseInt(pageNum.getText().toString());
                currentPage++;
                pageNum.setText(String.valueOf(currentPage));
            }
        });

        /**
         * Button Go ini digunakan untuk melakukan pencarian terhadap halaman lainnya.
         */
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
                                lstItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                        Product productClicked = productReturned.get(position);

                                        int accountId = productClicked.accountId;
                                        String productName = productClicked.name;
                                        int productId = productClicked.id;
                                        int productWeight = productClicked.weight;
                                        boolean productConditionUsed = productClicked.conditionUsed;
                                        double productDiscount = productClicked.discount;
                                        double productPrice = productClicked.price;
                                        byte productShipmentPlans = productClicked.shipmentPlans;
                                        ProductCategory productCategory = productClicked.category;

                                        Bundle bundle = new Bundle();
                                        bundle.putInt("accountId", accountId);
                                        bundle.putInt("productId", productId);
                                        bundle.putString("name", productName);
                                        bundle.putInt("weight", productWeight);
                                        bundle.putBoolean("conditionUsed", productConditionUsed);
                                        bundle.putDouble("discount", productDiscount);
                                        bundle.putDouble("price", productPrice);
                                        bundle.putByte("shipmentPlans", productShipmentPlans);
                                        bundle.putString("category", String.valueOf(productCategory));

                                        Intent itemDetails = new Intent(getContext(), ItemDetailsActivity.class);
                                        itemDetails.putExtras(bundle);
                                        startActivity(itemDetails);
                                    }
                                });
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
                } else { // Jika tidak ada filter yang diapply
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
                                lstItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                        Product productClicked = productReturned.get(position);

                                        int accountId = productClicked.accountId;
                                        String productName = productClicked.name;
                                        int productId = productClicked.id;
                                        int productWeight = productClicked.weight;
                                        boolean productConditionUsed = productClicked.conditionUsed;
                                        double productDiscount = productClicked.discount;
                                        double productPrice = productClicked.price;
                                        byte productShipmentPlans = productClicked.shipmentPlans;
                                        ProductCategory productCategory = productClicked.category;

                                        Bundle bundle = new Bundle();
                                        bundle.putInt("accountId", accountId);
                                        bundle.putInt("productId", productId);
                                        bundle.putString("name", productName);
                                        bundle.putInt("weight", productWeight);
                                        bundle.putBoolean("conditionUsed", productConditionUsed);
                                        bundle.putDouble("discount", productDiscount);
                                        bundle.putDouble("price", productPrice);
                                        bundle.putByte("shipmentPlans", productShipmentPlans);
                                        bundle.putString("category", String.valueOf(productCategory));

                                        Intent itemDetails = new Intent(getContext(), ItemDetailsActivity.class);
                                        itemDetails.putExtras(bundle);
                                        startActivity(itemDetails);
                                    }
                                });
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

    /**
     * Method onResume ini akan dijalankan setelah user kembali ke MainActivity lagi sehingga fragment
     * akan melakukan request produk lagi. Pada method ini juga akan menggunakan dua buah tipe request,
     * yaitu yang terfilter dan tidak terfilter. Caranya sama seperti sebelumnya, yaitu menggunakan toggle
     * ifFiltered.
     */
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
                        lstItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                Product productClicked = productReturned.get(position);

                                int accountId = productClicked.accountId;
                                String productName = productClicked.name;
                                int productId = productClicked.id;
                                int productWeight = productClicked.weight;
                                boolean productConditionUsed = productClicked.conditionUsed;
                                double productDiscount = productClicked.discount;
                                double productPrice = productClicked.price;
                                byte productShipmentPlans = productClicked.shipmentPlans;
                                ProductCategory productCategory = productClicked.category;

                                Bundle bundle = new Bundle();
                                bundle.putInt("accountId", accountId);
                                bundle.putInt("productId", productId);
                                bundle.putString("name", productName);
                                bundle.putInt("weight", productWeight);
                                bundle.putBoolean("conditionUsed", productConditionUsed);
                                bundle.putDouble("discount", productDiscount);
                                bundle.putDouble("price", productPrice);
                                bundle.putByte("shipmentPlans", productShipmentPlans);
                                bundle.putString("category", String.valueOf(productCategory));

                                Intent itemDetails = new Intent(getContext(), ItemDetailsActivity.class);
                                itemDetails.putExtras(bundle);
                                startActivity(itemDetails);
                            }
                        });
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
                        lstItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                Product productClicked = productReturned.get(position);

                                int accountId = productClicked.accountId;
                                String productName = productClicked.name;
                                int productId = productClicked.id;
                                int productWeight = productClicked.weight;
                                boolean productConditionUsed = productClicked.conditionUsed;
                                double productDiscount = productClicked.discount;
                                double productPrice = productClicked.price;
                                byte productShipmentPlans = productClicked.shipmentPlans;
                                ProductCategory productCategory = productClicked.category;

                                Bundle bundle = new Bundle();
                                bundle.putInt("accountId", accountId);
                                bundle.putInt("productId", productId);
                                bundle.putString("name", productName);
                                bundle.putInt("weight", productWeight);
                                bundle.putBoolean("conditionUsed", productConditionUsed);
                                bundle.putDouble("discount", productDiscount);
                                bundle.putDouble("price", productPrice);
                                bundle.putByte("shipmentPlans", productShipmentPlans);
                                bundle.putString("category", String.valueOf(productCategory));

                                Intent itemDetails = new Intent(getContext(), ItemDetailsActivity.class);
                                itemDetails.putExtras(bundle);
                                startActivity(itemDetails);
                            }
                        });
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