package com.KenricoValensJmartBO.jmart_android;

import static com.KenricoValensJmartBO.jmart_android.LoginActivity.getLoggedAccount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;

import com.KenricoValensJmartBO.jmart_android.fragment.FilterFragment;
import com.KenricoValensJmartBO.jmart_android.fragment.ProductFragment;
import com.KenricoValensJmartBO.jmart_android.model.ProductCategory;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 2;
    public static ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    MenuItem addProduct;

    private String[] tabTitle = new String[]{"PRODUCTS", "FILTER"};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);

        // Jika tidak memiliki store, menu Add Product tidak kelihatan
        // Comment kode ini jika ingin melakukan testing terhadap penambahan product.
        invalidateOptionsMenu();
        addProduct = menu.findItem(R.id.addProduct);
        if (getLoggedAccount().store != null) {
            addProduct.setVisible(true);
        }
        else {
            addProduct.setVisible(false);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.myPager);
        pagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout =(TabLayout) findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,(tab, position) -> tab.setText(tabTitle[position])).attach();

    }

    private class MyPagerAdapter extends FragmentStateAdapter {

        public MyPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int pos) {
            switch (pos) {
                case 0: {
                    return ProductFragment.newInstance("Products Fragment");
                }
                case 1: {

                    return FilterFragment.newInstance("Filter Fragment");
                }
                default:
                    return ProductFragment.newInstance("Products Fragment, Default");
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.addProduct:
                Intent toCreateProductPage = new Intent(MainActivity.this, CreateProductActivity.class);
                startActivity(toCreateProductPage);
                return true;
            case R.id.aboutMe:
                Intent toAboutMePage = new Intent(MainActivity.this, AboutMeActivity.class);
                startActivity(toAboutMePage);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}