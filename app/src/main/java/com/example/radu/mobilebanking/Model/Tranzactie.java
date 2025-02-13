package com.example.radu.mobilebanking.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Tranzactie {

    public enum TRANSACTION_TYPE {
        PAYMENT,
        TRANSFER,
        DEPOSIT
    }

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd - hh:mm a");

    private String transactionID;
    private String timestamp;
    private String sendingAccount;
    private String destinationAccount;
    private String payee;
    private double amount;
    private TRANSACTION_TYPE transactionType;
    private long dbId;

    public Tranzactie(String transactionID, String payee, double amount) {
        this.transactionID = transactionID;
        timestamp = DATE_FORMAT.format(new Date());
        this.payee = payee;
        this.amount = amount;
        transactionType = TRANSACTION_TYPE.PAYMENT;
    }

    public Tranzactie(String transactionID, String timestamp, String payee, double amount, long dbId) {
        this(transactionID, payee, amount);
        this.timestamp = timestamp;
        this.dbId = dbId;
    }

    public Tranzactie(String transactionID, double amount) {
        this.transactionID = transactionID;
        timestamp = DATE_FORMAT.format(new Date());
        this.amount = amount;
        transactionType = TRANSACTION_TYPE.DEPOSIT;
    }

    public Tranzactie(String transactionID, String timestamp, double amount, long dbId) {
        this(transactionID, amount);
        this.timestamp = timestamp;
        this.dbId = dbId;
    }

    public Tranzactie(String transactionID, String sendingAccount, String destinationAccount, double amount) {
        this.transactionID = transactionID;
        this.timestamp = DATE_FORMAT.format(new Date());
        this.sendingAccount = sendingAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        transactionType = TRANSACTION_TYPE.TRANSFER;
    }

    public Tranzactie(String transactionID, String timestamp, String sendingAccount, String destinationAccount, double amount, long dbId) {
        this(transactionID, sendingAccount, destinationAccount, amount);
        this.timestamp = timestamp;
        this.dbId = dbId;
    }


    public String getTransactionID() { return transactionID; }
    public String getTimestamp() { return timestamp; }
    public String getSendingAccount() {
        return sendingAccount;
    }
    public String getDestinationAccount() {
        return destinationAccount;
    }
    public String getPayee() { return payee; }
    public double getAmount() {
        return amount;
    }
    public TRANSACTION_TYPE getTransactionType() {
        return transactionType;
    }

    public void setDbId(long dbId) { this.dbId = dbId; }

}
