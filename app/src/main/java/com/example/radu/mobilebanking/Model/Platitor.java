package com.example.radu.mobilebanking.Model;



public class Platitor {

    private String payeeID;
    private String payeeName;
    private long dbId;

    public Platitor(String payeeID, String payeeName) {
        this.payeeID = payeeID;
        this.payeeName = payeeName;
    }

    public Platitor(String payeeID, String payeeName, long dbId) {
        this(payeeID, payeeName);
        this.dbId = dbId;
    }


    public String getPayeeName() {
        return payeeName;
    }
    public String getPayeeID() { return payeeID; }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public String toString() { return (payeeName + " (" + payeeID + ")"); }
}
