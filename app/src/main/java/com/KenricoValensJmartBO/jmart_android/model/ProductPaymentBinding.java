package com.KenricoValensJmartBO.jmart_android.model;

public class ProductPaymentBinding {
    public Product product;
    public Payment payment;

    public String toString() {
        return this.product.name + "\nStatus : " + this.payment.history.get(this.payment.history.size() -1).message;
    }
}
