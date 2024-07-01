package com.example.radu.mobilebanking.Model;

import java.util.ArrayList;


public class Profile {

    private String firstName;
    private String lastName;
    private String country;
    private String username;
    private String password;
    private ArrayList<Cont> conts;
    private ArrayList<Platitor> platitors;
    private long dbId;

    public Profile (String firstName, String lastName, String country, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.username = username;
        this.password = password;
        conts = new ArrayList<>();
        platitors = new ArrayList<>();
    }

    public Profile (String firstName, String lastName, String country, String username, String password, long dbId) {
        this(firstName, lastName, country, username, password);
        this.dbId = dbId;
    }


    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getCountry() {
        return country;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public ArrayList<Cont> getConts() { return conts; }
    public ArrayList<Platitor> getPlatitors() { return platitors; }
    public long getDbId() { return dbId; }
    public void setDbId(long dbId) { this.dbId = dbId; }

    public void addAccount(String accountName, double accountBalance) {
        String accNo = "A" + (conts.size() + 1);
        Cont cont = new Cont(accountName, accNo, accountBalance);
        conts.add(cont);
    }
    public void setAccountsFromDB(ArrayList<Cont> conts) {
        this.conts = conts;
    }

    public void addTransferTransaction(Cont sendingAcc, Cont receivingAcc, double transferAmount) {

        sendingAcc.setAccountBalance(sendingAcc.getAccountBalance() - transferAmount);
        receivingAcc.setAccountBalance(receivingAcc.getAccountBalance() + transferAmount);

        int sendingAccTransferCount = 0;
        int receivingAccTransferCount = 0;
        for (int i = 0; i < sendingAcc.getTranzacties().size(); i ++) {
            if (sendingAcc.getTranzacties().get(i).getTransactionType() == Tranzactie.TRANSACTION_TYPE.TRANSFER) {
                sendingAccTransferCount++;
            }
        }
        for (int i = 0; i < receivingAcc.getTranzacties().size(); i++) {
            if (receivingAcc.getTranzacties().get(i).getTransactionType() == Tranzactie.TRANSACTION_TYPE.TRANSFER) {
                receivingAccTransferCount++;
            }
        }

        sendingAcc.getTranzacties().add(new Tranzactie("T" + (sendingAcc.getTranzacties().size() + 1) + "-T" + (sendingAccTransferCount+1), sendingAcc.toTransactionString(), receivingAcc.toTransactionString(), transferAmount));
        receivingAcc.getTranzacties().add(new Tranzactie("T" + (receivingAcc.getTranzacties().size() + 1) + "-T" + (receivingAccTransferCount+1), sendingAcc.toTransactionString(), receivingAcc.toTransactionString(), transferAmount));
    }

    public void addPayee(String payeeName) {
        String payeeID = "P" + (platitors.size() + 1);
        Platitor platitor = new Platitor(payeeID, payeeName);
        platitors.add(platitor);
    }

    public void setPayeesFromDB(ArrayList<Platitor> platitors) {
        this.platitors = platitors;
    }
}
