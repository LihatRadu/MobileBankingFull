package com.example.radu.mobilebanking.Model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.radu.mobilebanking.Model.Cont;
import com.example.radu.mobilebanking.Model.Platitor;
import com.example.radu.mobilebanking.Model.Profile;
import com.example.radu.mobilebanking.Model.Tranzactie;

import java.util.ArrayList;


public class ApplicationDB {

    private SQLiteDatabase database;
    private SQLiteOpenHelper openHelper;

    private static final String DB_NAME = "useraccounts.db";
    private static final int DB_VERSION = 2;

    //------------------------------------------------------------------- PROFIL TABLE ----------------------- \\
    private static final String PROFILES_TABLE = "Profiles";

    private static final String PROFILE_ID = "_ProfileID";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String COUNTRY = "country";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private static final int PROFILE_ID_COLUMN = 0;
    private static final int FIRST_NAME_COLUMN = 1;
    private static final int LAST_NAME_COLUMN = 2;
    private static final int COUNTRY_COLUMN = 3;
    private static final int USERNAME_COLUMN = 4;
    private static final int PASSWORD_COLUMN = 5;
    //------------------------------------------------------------------- PROFIL TABLE ----------------------- \\

    //------------------------------------------------------------------- PLATITOR TABLE ----------------------- \\
    private static final String PAYEES_TABLE = "Payees";

    private static final String PAYEE_ID = "_PayeeID";
    private static final String PAYEE_NAME = "PayeeName";

    private static final int PAYEE_ID_COLUMN = 1;
    private static final int PAYEE_NAME_COLUMN = 2;
    //------------------------------------------------------------------- PLATITOR TABLE ----------------------- \\

    //------------------------------------------------------------------- CONT TABLE ----------------------- \\
    private static final String ACCOUNTS_TABLE = "Accounts";

    private static final String ACCOUNT_NO = "_AccountNo";
    private static final String ACCOUNT_NAME = "AccountName";
    private static final String ACCOUNT_BALANCE = "AccountBalance";

    private static final int ACCOUNT_NO_COLUMN = 1;
    private static final int ACCOUNT_NAME_COLUMN = 2;
    private static final int ACCOUNT_BALANCE_COLUMN = 3;
    //------------------------------------------------------------------- CONT TABLE ----------------------- \\

    //------------------------------------------------------------------- TRANZACTIE TABLE ----------------------- \\
    private static final String TRANSACTIONS_TABLE = "Transactions";

    private static final String TRANSACTION_ID = "_TransactionID";
    private static final String TIMESTAMP = "Timestamp";
    private static final String SENDING_ACCOUNT = "SendingAccount";
    private static final String DESTINATION_ACCOUNT = "DestinationAccount";
    private static final String TRANSACTION_PAYEE = "Payee";
    private static final String TRANSACTION_AMOUNT = "Amount";
    private static final String TRANS_TYPE = "Type";

    private static final int TRANSACTION_ID_COLUMN = 2;
    private static final int TIMESTAMP_COLUMN = 3;
    private static final int SENDING_ACCOUNT_COLUMN = 4;
    private static final int DESTINATION_ACCOUNT_COLUMN = 5;
    private static final int TRANSACTION_PAYEE_COLUMN = 6;
    private static final int TRANSACTION_AMOUNT_COLUMN = 7;
    private static final int TRANSACTION_TYPE_COLUMN = 8;
    //------------------------------------------------------------------- TRANZACTIE TABLE ----------------------- \\

    private static final String CREATE_PROFILES_TABLE =
            "CREATE TABLE " + PROFILES_TABLE + " (" +
                    PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FIRST_NAME + " TEXT, " +
                    LAST_NAME + " TEXT, " +
                    COUNTRY + " TEXT, " +
                    USERNAME + " TEXT, " +
                    PASSWORD + " TEXT)";

    private static final String CREATE_PAYEES_TABLE =
            "CREATE TABLE " + PAYEES_TABLE + " (" +
                    PROFILE_ID + " INTEGER NOT NULL, " +
                    PAYEE_ID + " TEXT NOT NULL, " +
                    PAYEE_NAME + " TEXT, " +
                    "PRIMARY KEY(" + PROFILE_ID + "," + PAYEE_ID + "), " +
                    "FOREIGN KEY(" + PROFILE_ID + ") REFERENCES " + PROFILES_TABLE + "(" + PROFILE_ID + "))";

    private static final String CREATE_ACCOUNTS_TABLE =
            "CREATE TABLE " + ACCOUNTS_TABLE + " (" +
                    PROFILE_ID + " INTEGER NOT NULL, " +
                    ACCOUNT_NO + " TEXT NOT NULL, " +
                    ACCOUNT_NAME + " TEXT, " +
                    ACCOUNT_BALANCE + " REAL, " +
                    "PRIMARY KEY(" + PROFILE_ID + "," + ACCOUNT_NO + "), " +
                    "FOREIGN KEY(" + PROFILE_ID + ") REFERENCES " + PROFILES_TABLE + "(" + PROFILE_ID + "))";

    private static final String CREATE_TRANSACTIONS_TABLE =
            "CREATE TABLE " + TRANSACTIONS_TABLE + " (" +
                    PROFILE_ID + " INTEGER NOT NULL, " +
                    ACCOUNT_NO + " TEXT NOT NULL, " +
                    TRANSACTION_ID + " TEXT NOT NULL, " +
                    TIMESTAMP + " TEXT, " +
                    SENDING_ACCOUNT + " TEXT, " +
                    DESTINATION_ACCOUNT + " TEXT, " +
                    TRANSACTION_PAYEE + " TEXT, " +
                    TRANSACTION_AMOUNT + " REAL, " +
                    TRANS_TYPE + " TEXT, " +
                    "PRIMARY KEY(" + PROFILE_ID + "," + ACCOUNT_NO + "," + TRANSACTION_ID + "), " +
                    "FOREIGN KEY(" + PROFILE_ID + "," + ACCOUNT_NO + ") REFERENCES " +
                    ACCOUNTS_TABLE + "(" + PROFILE_ID + "," + ACCOUNT_NO + ")," +
                    "FOREIGN KEY(" + PROFILE_ID + ") REFERENCES " + PROFILES_TABLE + "(" + PROFILE_ID + "))";

    public ApplicationDB(Context context){
        openHelper = new DBHelper(context, DB_NAME, DB_VERSION);
    }

    //TODO: Remove a profile?
    //TODO: Not needed unless I add implementation for modifying profile information such as name, password, username, etc.
    public void overwriteProfile(Profile profile) {

        database = openHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PROFILE_ID,profile.getDbId());
        cv.put(FIRST_NAME,profile.getFirstName());
        cv.put(LAST_NAME,profile.getLastName());
        cv.put(COUNTRY, profile.getCountry());
        cv.put(USERNAME,profile.getUsername());
        cv.put(PASSWORD,profile.getPassword());

        database.update(PROFILES_TABLE, cv, PROFILE_ID + "=?", new String[] {String.valueOf(profile.getDbId())});
        database.close();
    }

    public void saveNewProfile(Profile profile) {

        database = openHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FIRST_NAME, profile.getFirstName());
        cv.put(LAST_NAME, profile.getLastName());
        cv.put(COUNTRY, profile.getCountry());
        cv.put(USERNAME, profile.getUsername());
        cv.put(PASSWORD, profile.getPassword());

        long id = database.insert(PROFILES_TABLE, null, cv);

        profile.setDbId(id);

        database.close();
    }

    //TODO: Overwrite or remove payee?
    public void saveNewPayee(Profile profile, Platitor platitor) {
        database = openHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(PROFILE_ID, profile.getDbId());
        cv.put(PAYEE_ID, platitor.getPayeeID());
        cv.put(PAYEE_NAME, platitor.getPayeeName());

        long id = database.insert(PAYEES_TABLE, null, cv);

        platitor.setDbId(id);

        database.close();
    }

    public void saveNewTransaction(Profile profile, String accountNo, Tranzactie tranzactie) {
        database = openHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PROFILE_ID, profile.getDbId());
        cv.put(ACCOUNT_NO, accountNo);
        cv.put(TRANSACTION_ID, tranzactie.getTransactionID());
        cv.put(TIMESTAMP, tranzactie.getTimestamp());

        if (tranzactie.getTransactionType() == Tranzactie.TRANSACTION_TYPE.TRANSFER) {
            cv.put(SENDING_ACCOUNT, tranzactie.getSendingAccount());
            cv.put(DESTINATION_ACCOUNT, tranzactie.getDestinationAccount());
            cv.putNull(TRANSACTION_PAYEE);
        } else if (tranzactie.getTransactionType() == Tranzactie.TRANSACTION_TYPE.PAYMENT) {
            cv.putNull(SENDING_ACCOUNT);
            cv.putNull(DESTINATION_ACCOUNT);
            cv.put(TRANSACTION_PAYEE, tranzactie.getPayee());
        } else if (tranzactie.getTransactionType() == Tranzactie.TRANSACTION_TYPE.DEPOSIT) {
            cv.putNull(SENDING_ACCOUNT);
            cv.putNull(DESTINATION_ACCOUNT);
            cv.putNull(TRANSACTION_PAYEE);
        }

        cv.put(TRANSACTION_AMOUNT, tranzactie.getAmount());
        cv.put(TRANS_TYPE, tranzactie.getTransactionType().toString());

        long id = database.insert(TRANSACTIONS_TABLE, null, cv);

        tranzactie.setDbId(id);

        database.close();
    }

    //TODO: Remove an account?
    public void overwriteAccount(Profile profile, Cont cont) {

        database = openHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PROFILE_ID,profile.getDbId());
        cv.put(ACCOUNT_NO, cont.getAccountNo());
        cv.put(ACCOUNT_NAME, cont.getAccountName());
        cv.put(ACCOUNT_BALANCE, cont.getAccountBalance());

        database.update(ACCOUNTS_TABLE, cv, PROFILE_ID + "=? AND " + ACCOUNT_NO +"=?",
                new String[] {String.valueOf(profile.getDbId()), cont.getAccountNo()});
        database.close();
    }
    public void saveNewAccount(Profile profile, Cont cont) {

        database = openHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PROFILE_ID, profile.getDbId());
        cv.put(ACCOUNT_NO, cont.getAccountNo());
        cv.put(ACCOUNT_NAME, cont.getAccountName());
        cv.put(ACCOUNT_BALANCE, cont.getAccountBalance());

        long id = database.insert(ACCOUNTS_TABLE, null, cv);

        cont.setDbID(id);

        database.close();
    }

    public ArrayList<Profile> getAllProfiles(){

        ArrayList<Profile> profiles = new ArrayList<>();
        database = openHelper.getReadableDatabase();
        Cursor cursor = database.query(PROFILES_TABLE, null,null,null,null,
                null, null);
        getProfilesFromCursor(profiles, cursor);

        cursor.close();
        database.close();

        return profiles;
    }
    private void getProfilesFromCursor(ArrayList<Profile> profiles, Cursor cursor) {
        // returns true if pointed to a record
        while (cursor.moveToNext()){

            long id = cursor.getLong(PROFILE_ID_COLUMN);
            String firstName = cursor.getString(FIRST_NAME_COLUMN);
            String lastName = cursor.getString(LAST_NAME_COLUMN);
            String country = cursor.getString(COUNTRY_COLUMN);
            String username = cursor.getString(USERNAME_COLUMN);
            String password = cursor.getString(PASSWORD_COLUMN);

            //ArrayList<Account> accounts = new ArrayList<>();
            //ArrayList<Payee> payees = new ArrayList<>();

            profiles.add(new Profile(firstName, lastName, country, username, password, id));
        }
    }

    public ArrayList<Platitor> getPayeesFromCurrentProfile(long profileID) {
        ArrayList<Platitor> platitors = new ArrayList<>();
        database = openHelper.getReadableDatabase();

        Cursor cursor = database.query(PAYEES_TABLE, null, null, null, null,
                null ,null);
        getPayeesFromCursor(profileID, platitors, cursor);

        cursor.close();
        database.close();

        return platitors;
    }
    private void getPayeesFromCursor(long profileID, ArrayList<Platitor> platitors, Cursor cursor) {

        while (cursor.moveToNext()) {

            if (profileID == cursor.getLong(PROFILE_ID_COLUMN)) {
                long id = cursor.getLong(PROFILE_ID_COLUMN);
                String payeeID = cursor.getString(PAYEE_ID_COLUMN);
                String payeeName = cursor.getString(PAYEE_NAME_COLUMN);

                platitors.add(new Platitor(payeeID, payeeName, id));
            }
        }
    }

    public ArrayList<Tranzactie> getTransactionsFromCurrentAccount(long profileID, String accountNo) {

        ArrayList<Tranzactie> tranzacties = new ArrayList<>();
        database = openHelper.getReadableDatabase();

        Cursor cursor = database.query(TRANSACTIONS_TABLE, null, null, null, null,
                null ,null);

        getTransactionsFromCursor(profileID, accountNo, tranzacties, cursor);

        cursor.close();
        database.close();

        return tranzacties;
    }
    private void getTransactionsFromCursor(long profileID, String accountNo, ArrayList<Tranzactie> tranzacties, Cursor cursor) {

        while (cursor.moveToNext()) {

            if (profileID == cursor.getLong(PROFILE_ID_COLUMN)) {
                long id = cursor.getLong(PROFILE_ID_COLUMN);
                if (accountNo.equals(cursor.getString(ACCOUNT_NO_COLUMN))) {
                    String transactionID = cursor.getString(TRANSACTION_ID_COLUMN);
                    String timestamp = cursor.getString(TIMESTAMP_COLUMN);
                    String sendingAccount = cursor.getString(SENDING_ACCOUNT_COLUMN);
                    String destinationAccount = cursor.getString(DESTINATION_ACCOUNT_COLUMN);
                    String payee = cursor.getString(TRANSACTION_PAYEE_COLUMN);
                    double amount = cursor.getDouble(TRANSACTION_AMOUNT_COLUMN);
                    Tranzactie.TRANSACTION_TYPE transactionType = Tranzactie.TRANSACTION_TYPE.valueOf(cursor.getString(TRANSACTION_TYPE_COLUMN));

                    if (transactionType == Tranzactie.TRANSACTION_TYPE.PAYMENT) {
                        tranzacties.add(new Tranzactie(transactionID, timestamp, payee, amount, id));
                    } else if (transactionType == Tranzactie.TRANSACTION_TYPE.TRANSFER) {
                        tranzacties.add(new Tranzactie(transactionID, timestamp, sendingAccount, destinationAccount, amount, id));
                    } else if (transactionType == Tranzactie.TRANSACTION_TYPE.DEPOSIT) {
                        tranzacties.add(new Tranzactie(transactionID, timestamp, amount, id));
                    }
                }

            }
        }
    }

    public ArrayList<Cont> getAccountsFromCurrentProfile(long profileID) {

        ArrayList<Cont> conts = new ArrayList<>();
        database = openHelper.getReadableDatabase();
        Cursor cursor = database.query(ACCOUNTS_TABLE, null, null, null, null,
                null ,null);
        getAccountsFromCursor(profileID, conts, cursor);

        cursor.close();
        database.close();

        return conts;
    }
    private void getAccountsFromCursor(long profileID, ArrayList<Cont> conts, Cursor cursor) {

        while (cursor.moveToNext()) {

            if (profileID == cursor.getLong(PROFILE_ID_COLUMN)) {
                long id = cursor.getLong(PROFILE_ID_COLUMN);
                String accountNo = cursor.getString(ACCOUNT_NO_COLUMN);
                String accountName = cursor.getString(ACCOUNT_NAME_COLUMN);
                double accountBalance = cursor.getDouble(ACCOUNT_BALANCE_COLUMN);

                conts.add(new Cont(accountName, accountNo, accountBalance, id));
            }
        }
    }

    private static class DBHelper extends SQLiteOpenHelper{

        private DBHelper(Context context, String name, int version) {
            super(context, name, null, version);
        }

        // if the db doesn't exist , the runtime calls this fn . we dont have to check if it exists
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_PROFILES_TABLE);
            db.execSQL(CREATE_PAYEES_TABLE);
            db.execSQL(CREATE_ACCOUNTS_TABLE);
            db.execSQL(CREATE_TRANSACTIONS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // drop the table
            db.execSQL("DROP TABLE IF EXISTS " + PROFILES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PAYEES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TRANSACTIONS_TABLE);
            onCreate(db);
        }
    }
}
