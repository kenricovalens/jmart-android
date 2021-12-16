package com.KenricoValensJmartBO.jmart_android.model;

import java.util.ArrayList;
import java.util.Date;

public class Payment extends Invoice {
    public ArrayList<Record> history = new ArrayList<>();
    public Shipment shipment;
    public int productCount;

    public static class Record {
        public Date date;
        public String message;
        public Invoice.Status status;
    }

    public String toString() {
        return String.valueOf(productId);
    }
}
