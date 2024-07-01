package com.example.radu.mobilebanking;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.radu.mobilebanking.Model.Cont;
import com.example.radu.mobilebanking.Model.Profile;
import com.example.radu.mobilebanking.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.Locale;

public class StocareActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public enum manualNavID {
        DASHBOARD_ID,
        ACCOUNTS_ID
    }

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    private SharedPreferences userPreferences;
    private Gson gson;
    private String json;

    private Profile userProfile;

    private Dialog depositDialog;
    private Spinner spnAccounts;
    private ArrayAdapter<Cont> accountAdapter;
    private EditText edtDepositAmount;
    private Button btnCancel;
    private Button btnDeposit;

    private View.OnClickListener depositClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnCancel.getId()) {
                depositDialog.dismiss();
                manualNavigation(manualNavID.ACCOUNTS_ID, null);
                Toast.makeText(StocareActivity.this, "Depozitare anulata", Toast.LENGTH_SHORT).show();
            } else if (view.getId() == btnDeposit.getId()) {
                makeDeposit();
            }
        }
    };

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("AfiseazaCont", true);
                manualNavigation(manualNavID.ACCOUNTS_ID, bundle);
            }
        }
    };

    public void manualNavigation(manualNavID id, Bundle bundle) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (id == manualNavID.DASHBOARD_ID) {
            ft.replace(R.id.flContent, new TablaActivity()).commit();
            navView.setCheckedItem(R.id.nav_dashboard);
            setTitle("Tabla");
        } else if (id == manualNavID.ACCOUNTS_ID) {
            PrezentareCont prezentareCont = new PrezentareCont();
            if (bundle != null) {
                prezentareCont.setArguments(bundle);
            }
            ft.replace(R.id.flContent, prezentareCont).commit();
            navView.setCheckedItem(R.id.nav_accounts);
            setTitle("Conturi");
        }

        drawerLayout.closeDrawers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        drawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        userPreferences = this.getSharedPreferences("UltimulProfilUtilizat", MODE_PRIVATE);
        gson = new Gson();
        json = userPreferences.getString("UltimulProfilUtilizat", "");
        userProfile = gson.fromJson(json, Profile.class);

        loadFromDB();

        SharedPreferences.Editor prefsEditor = userPreferences.edit();
        json = gson.toJson(userProfile);
        prefsEditor.putString("UltimulProfilUtilizat", json).apply();

        setupDrawerListener();
        setupHeader();



        manualNavigation(manualNavID.DASHBOARD_ID, null);
    }


    private void setupDrawerListener() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
    }

    private void setupHeader() {

        View headerView = navView.getHeaderView(0);

        TextView txtName = headerView.findViewById(R.id.txt_name);
        TextView txtUsername = headerView.findViewById(R.id.txt_username);

        String name = userProfile.getFirstName() + " " + userProfile.getLastName();
        txtName.setText(name);

        txtUsername.setText(userProfile.getUsername());
    }

    private void loadFromDB() {
        ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());

        userProfile.setPayeesFromDB(applicationDb.getPayeesFromCurrentProfile(userProfile.getDbId()));
        userProfile.setAccountsFromDB(applicationDb.getAccountsFromCurrentProfile(userProfile.getDbId()));

        for (int iAccount = 0; iAccount < userProfile.getConts().size(); iAccount++) {
            userProfile.getConts().get(iAccount).setTranzacties(applicationDb.getTransactionsFromCurrentAccount(userProfile.getDbId(), userProfile.getConts().get(iAccount).getAccountNo()));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    public void showDrawerButton() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.syncState();
    }

    public void showUpButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void displayAccountAlertADialog(String option) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(String.format("%s Eroare", option))
                .setMessage(String.format("Nu sunt suficiente conturi pentru a face transferul %s. Adauga un alt cont pentru a face transferul %s.", option, option.toLowerCase()))
                .setNegativeButton("Inchide", dialogClickListener)
                .setPositiveButton("Adauga Cont", dialogClickListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayDepositDialog() {

        depositDialog = new Dialog(this);
        depositDialog.setContentView(R.layout.deposit_dialog);

        depositDialog.setCanceledOnTouchOutside(true);
        depositDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                manualNavigation(manualNavID.ACCOUNTS_ID, null);
                Toast.makeText(StocareActivity.this, "Deposit Inchis", Toast.LENGTH_SHORT).show();
            }
        });

        spnAccounts = depositDialog.findViewById(R.id.dep_spn_accounts);
        accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userProfile.getConts());
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAccounts.setAdapter(accountAdapter);
        spnAccounts.setSelection(0);

        edtDepositAmount = depositDialog.findViewById(R.id.edt_deposit_amount);

        btnCancel = depositDialog.findViewById(R.id.btn_cancel_deposit);
        btnDeposit = depositDialog.findViewById(R.id.btn_deposit);

        btnCancel.setOnClickListener(depositClickListener);
        btnDeposit.setOnClickListener(depositClickListener);

        depositDialog.show();

    }


    private void makeDeposit() {

        int selectedAccountIndex = spnAccounts.getSelectedItemPosition();

        double depositAmount = 0;
        boolean isNum = false;

        try {
            depositAmount = Double.parseDouble(edtDepositAmount.getText().toString());
            isNum = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (depositAmount < 0.01 && !isNum) {
            Toast.makeText(this, "Introduceti o suma valida", Toast.LENGTH_SHORT).show();
        } else {

            Cont cont = userProfile.getConts().get(selectedAccountIndex);
            cont.addDepositTransaction(depositAmount);

            SharedPreferences.Editor prefsEditor = userPreferences.edit();
            gson = new Gson();
            json = gson.toJson(userProfile);
            prefsEditor.putString("UltimulProfilUtilizat", json).apply();

            ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());
            applicationDb.overwriteAccount(userProfile, cont);
            applicationDb.saveNewTransaction(userProfile, cont.getAccountNo(),
                    cont.getTranzacties().get(cont.getTranzacties().size()-1));

            Toast.makeText(this, "Depositarea  " + String.format(Locale.getDefault(), "%.2f",depositAmount) + " " + "s-a facut cu success", Toast.LENGTH_SHORT).show();

            accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userProfile.getConts());
            accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnAccounts.setAdapter(accountAdapter);



            depositDialog.dismiss();
            drawerLayout.closeDrawers();
            manualNavigation(manualNavID.ACCOUNTS_ID, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public void displayHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Ajutor")
                .setMessage("Se v-a oferi ajutor tuturor utilizatorilor");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_about) {
            displayHelpDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        userPreferences = this.getSharedPreferences("UltimulProfilUtilizat", MODE_PRIVATE);
        gson = new Gson();
        json = userPreferences.getString("UltimulProfilUtilizat", "");
        userProfile = gson.fromJson(json, Profile.class);

        FragmentManager fragmentManager = getSupportFragmentManager();


        Class fragmentClass = null;
        String title = item.getTitle().toString();

        switch(item.getItemId()) {
            case R.id.nav_dashboard:
                fragmentClass = TablaActivity.class;
                break;
            case R.id.nav_accounts:
                fragmentClass = PrezentareCont.class;
                break;
            case R.id.nav_deposit:
                if (userProfile.getConts().size() > 0) {
                    displayDepositDialog();
                } else {
                    displayAccountAlertADialog("Depositare");
                }
                break;
            case R.id.nav_transfer:
                if (userProfile.getConts().size() < 2) {
                    displayAccountAlertADialog("Transfer");
                } else {
                    title = "Transfer";
                    fragmentClass = TransferActivity.class;
                }
                break;
            case R.id.nav_payment:
                if (userProfile.getConts().size() < 1) {
                    displayAccountAlertADialog("Platit");
                } else {
                    title = "Platit";
                    fragmentClass = PlataActivity.class;
                }
                break;
            case R.id.nav_settings:

                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), PornireActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                fragmentClass = TablaActivity.class;
        }

        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            item.setChecked(true);
            setTitle(title);
            drawerLayout.closeDrawers();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


}
