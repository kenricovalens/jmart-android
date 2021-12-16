package com.KenricoValensJmartBO.jmart_android.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.KenricoValensJmartBO.jmart_android.LoginActivity;
import com.KenricoValensJmartBO.jmart_android.MainActivity;

import java.util.HashMap;

/**
 * Class SessionManager digunakan untuk manajemen session pada aplikasi Jmart.
 */
public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    private static final String KEY_IS_LOGGED_IN = "IsLoggedIn";
    public static final String KEY_ACCOUNTID = "accountId";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences("AccountSession", 0);
        editor = pref.edit();
    }

    /**
     * Method createLoginSession digunakan untuk membuat session saat melakukan login
     * @param accountId ID dari akun yang sedang log in
     */
    public void createLoginSession(int accountId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_ACCOUNTID, accountId);
        editor.commit();
    }

    /**
     * Untuk mendapatkan informasi yang disimpan pada session, maka digunakan method ini. Informasi
     * disimpan dengan bentuk HashMap<String, Integer>
     * @return HashMap berisi informasi user
     */
    public HashMap<String, Integer> getUserDetails() {
        HashMap<String, Integer> user = new HashMap<String, Integer>();

        user.put(KEY_ACCOUNTID, pref.getInt(KEY_ACCOUNTID, 999)); // 999 adalah placeholder jika tidak ditemukan akun

        return user;
    }

    /**
     * Jika ingin melakukan log out, maka gunakan method ini. logoutUser() akan menghapus isi dari
     * SharedPreferences sehingga user harus log in
     */
    public void logoutUser() {
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }

    /**
     * Method isLoggedIn untuk mengecek apakah ada session yang belum dihapus.
     * @return true jika masih ada session, false jika tidak ada.
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}
