package com.KenricoValensJmartBO.jmart_android.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.KenricoValensJmartBO.jmart_android.MainActivity;
import com.KenricoValensJmartBO.jmart_android.R;
import com.KenricoValensJmartBO.jmart_android.model.ProductCategory;

import java.util.Objects;

/**
 * FilterFragment adalah fragment yang bertugas untuk melakukan pencarian produk menggunakan filter
 * menurut field yang ada. Penggunaan fragment dikarenakan ingin membentuk dua buah halaman pada satu
 * activity sehingga menggunakan fragment.
 */
public class FilterFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // Inisiasikan semua field pada .xml
    private Button clearButton, applyButton;
    private EditText filterProductLowestPrice, filterProductHighestPrice, filterProductName;
    private CheckBox isUsed, isNew;
    private Spinner productCategorySpinner;

    // Semua fields ini digunakan sebagai isi dari filternya untuk melakukan request.
    // Fields ini akan diakses oleh ProductFragment untuk request.
    public static boolean isFiltered = false;
    public static String filterName;
    public static int filterLowestPrice, filterHighestPrice;
    public static boolean filterIsNew, filterIsUsed;
    public static ProductCategory filterCategory;

    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance(String param1) {
        FilterFragment fragment = new FilterFragment();
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
     * Method onCreateView pada FilterFragment berfungsi sebagai awalan saat fragment ini dibuat.
     * View dan semua fields lainnya dicari menurut layout .xml.
     * @param inflater Untuk menampilkan layout fragment pada MainActivity
     * @param container Activity container (MainActivity)
     * @param savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Melakukan findViewById pada Fragment berbeda dari Activity. Kita harus mendapatkan View
         * pada method onCreateView. View ini nantinya digunakan sebagai getApplicationContext().
         */
        View v = inflater.inflate(R.layout.fragment_filter, container, false);

        // Cari ID masing-masing dari layout .xml
        filterProductName = v.findViewById(R.id.filterProductName);
        filterProductLowestPrice = v.findViewById(R.id.filterLowestPrice);
        filterProductHighestPrice = v.findViewById(R.id.filterHighestPrice);
        isUsed = v.findViewById(R.id.filterProductIsUsed);
        isNew = v.findViewById(R.id.filterProductIsNew);
        productCategorySpinner = v.findViewById(R.id.filterProductCategory);

        clearButton = v.findViewById(R.id.filterClearButton);
        applyButton = v.findViewById(R.id.filterApplyButton);

        /**
         * setOnClickListener untuk button Apply Filter.
         */
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Jika button ini di-click, maka boolean isFiltered menjadi true.
                isFiltered = true;

                // Error Catching untuk null fields
                if(filterProductName.getText().toString().isEmpty()
                        || filterProductLowestPrice.getText().toString().isEmpty()
                        || filterProductHighestPrice.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Tidak boleh ada field yang kosong.", Toast.LENGTH_SHORT).show();
                }
                else {
                    filterName = filterProductName.getText().toString();
                    filterLowestPrice = Integer.parseInt(filterProductLowestPrice.getText().toString());
                    filterHighestPrice = Integer.parseInt(filterProductHighestPrice.getText().toString());

                    if(isUsed.isChecked()) {
                        filterIsUsed = true;
                    }
                    else {
                        filterIsUsed = false;
                    }

                    if(isNew.isChecked()) {
                        filterIsNew = true;
                    }
                    else {
                        filterIsNew = false;
                    }
                    filterCategory = ProductCategory.valueOf(productCategorySpinner.getSelectedItem().toString());

                    Toast.makeText(getActivity(), "Filter sudah diapply, silahkan cek ke tab Products", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * Button Clear ini akan mengosongkan semua fields pada FilterFragment dan juga mengubah
         * value dari isFiltered menjadi false. Ini menjadi informasi bagi ProductFragment untuk
         * melakukan pencarian terhadap product tanpa filter.
         */
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFiltered = false;

                filterProductName = v.findViewById(R.id.filterProductName);
                filterProductLowestPrice = v.findViewById(R.id.filterLowestPrice);
                filterProductHighestPrice = v.findViewById(R.id.filterHighestPrice);

                isUsed = v.findViewById(R.id.filterProductIsUsed);
                isNew = v.findViewById(R.id.filterProductIsNew);

                productCategorySpinner =  v.findViewById(R.id.filterProductCategory);

                filterProductName.setText("");
                filterProductLowestPrice.setText("");
                filterProductHighestPrice.setText("");
                isUsed.setChecked(false);
                isNew.setChecked(false);

                productCategorySpinner.setSelection(0);

                Toast.makeText(getActivity(), "Filter sudah dihapus.", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}