package com.example.radu.mobilebanking.Model;

import java.util.ArrayList;
import java.util.Locale;



public class Cont {

    private String accountName;
    private String accountNo;
    private double accountBalance;
    private ArrayList<Tranzactie> tranzacties;
    private long dbID;

    public Cont(String accountName, String accountNo, double accountBalance) {
        this.accountName = accountName;
        this.accountNo = accountNo;
        this.accountBalance = accountBalance;
        tranzacties = new ArrayList<>();
    }

    public Cont(String accountName, String accountNo, double accountBalance, long dbID) {
        this(accountName, accountNo, accountBalance);
        this.dbID = dbID;
    }


    public String getAccountName() {
        return accountName;
    }
    public String getAccountNo() {
        return accountNo;
    }
    public double getAccountBalance() {
        return accountBalance;
    }

    public void setDbID(long dbID) { this.dbID = dbID; }

    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }

    public ArrayList<Tranzactie> getTranzacties() {
        return tranzacties;
    }

    public void addPaymentTransaction (String payee, double amount) {
        accountBalance -= amount;

        int paymentCount = 0;

        for (int i = 0; i < tranzacties.size(); i++) {
            if (tranzacties.get(i).getTransactionType() == Tranzactie.TRANSACTION_TYPE.PAYMENT)  {
                paymentCount++;
            }
        }

        Tranzactie payment = new Tranzactie("T" + (tranzacties.size() + 1) + "-P" + (paymentCount+1), payee, amount);
        tranzacties.add(payment);
    }

    public void addDepositTransaction(double amount) {
        accountBalance += amount;


        int depositsCount = 0;

        for (int i = 0; i < tranzacties.size(); i++) {
            if (tranzacties.get(i).getTransactionType() == Tranzactie.TRANSACTION_TYPE.DEPOSIT)  {
                depositsCount++;
            }
        }

        Tranzactie deposit = new Tranzactie("T" + (tranzacties.size() + 1) + "-D" + (depositsCount+1), amount);
        tranzacties.add(deposit);
    }

    public String toString() {
        return (accountName + " ($" + String.format(Locale.getDefault(), "%.2f",accountBalance) + ")");
    }

    public String toTransactionString() { return (accountName + " (" + accountNo + ")"); }

    public void setTranzacties(ArrayList<Tranzactie> tranzacties) {
        this.tranzacties = tranzacties;
    }
}
