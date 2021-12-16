package com.KenricoValensJmartBO.jmart_android.model;

import java.util.Date;

public class Invoice extends Serializable {
    public static enum Status {
        CANCELLED,
        COMPLAINT,
        DELIVERED,
        FAILED,
        FINISHED,
        ON_DELIVERY,
        ON_PROGRESS,
        WAITING_CONFIRMATION
    }

    public static enum Rating {
        NONE,
        BAD,
        NEUTRAL,
        GOOD
    }

    public class Record {
        public Status status;
        public Date date;
        public String message;
    }

    public Date date = new Date();
    public int buyerId;
    public int productId;
    public int complaintId = -1;
    public Rating rating = Rating.NONE;
}
