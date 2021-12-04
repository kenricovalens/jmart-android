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
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Button clearButton, applyButton;
    private EditText filterProductLowestPrice, filterProductHighestPrice, filterProductName;
    private CheckBox isUsed, isNew;
    private Spinner productCategorySpinner;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filter, container, false);

        filterProductName = v.findViewById(R.id.filterProductName);
        filterProductLowestPrice = v.findViewById(R.id.filterLowestPrice);
        filterProductHighestPrice = v.findViewById(R.id.filterHighestPrice);
        isUsed = v.findViewById(R.id.filterProductIsUsed);
        isNew = v.findViewById(R.id.filterProductIsNew);
        productCategorySpinner = v.findViewById(R.id.filterProductCategory);

        clearButton = v.findViewById(R.id.filterClearButton);
        applyButton = v.findViewById(R.id.filterApplyButton);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFiltered = true;

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