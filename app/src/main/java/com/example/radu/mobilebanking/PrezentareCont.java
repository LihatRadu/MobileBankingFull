package com.example.radu.mobilebanking;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.radu.mobilebanking.Adapters.ContAdapter;
import com.example.radu.mobilebanking.Model.Cont;
import com.example.radu.mobilebanking.Model.Profile;
import com.example.radu.mobilebanking.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class PrezentareCont extends Fragment {

    private FloatingActionButton fab;
    private ListView lstAccounts;
    private TextView txtTitleMessage;
    private TextView txtDetailMessage;
    private EditText edtAccountName;
    private EditText edtInitAccountBalance;
    private Button btnCancel;
    private Button btnAddAccount;

    private Gson gson;
    private Profile userProfile;
    private SharedPreferences userPreferences;

    private boolean displayAccountDialogOnLaunch;

    private View.OnClickListener addAccountClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnCancel.getId()) {
                accountDialog.dismiss();
                Toast.makeText(getActivity(), "Creare cont anulata", Toast.LENGTH_SHORT).show();
            } else if (view.getId() == btnAddAccount.getId()) {
                addAccount();
            }
        }
    };

    private Dialog accountDialog;


    private int selectedAccountIndex;

    public PrezentareCont() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        displayAccountDialogOnLaunch = false;

        if (bundle != null) {
            displayAccountDialogOnLaunch = bundle.getBoolean("AfiseazaCont", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_account_overview, container, false);

        fab = rootView.findViewById(R.id.floating_action_btn);

        lstAccounts = rootView.findViewById(R.id.lst_accounts);
        txtTitleMessage = rootView.findViewById(R.id.txt_title_msg);
        txtDetailMessage = rootView.findViewById(R.id.txt_details_msg);

        getActivity().setTitle("Conturi");
        ((StocareActivity) getActivity()).showDrawerButton();

        setValues();

        if (displayAccountDialogOnLaunch) {
            displayAccountDialog();
            displayAccountDialogOnLaunch = false;
        }
        return rootView;
    }

    private void displayAccountDialog() {

        accountDialog = new Dialog(getActivity());
        accountDialog.setContentView(R.layout.account_dialog);

        accountDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        accountDialog.setCanceledOnTouchOutside(true);
        accountDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            Toast.makeText(getActivity(), "Creare cont amanata", Toast.LENGTH_SHORT).show();
            }
        });

        edtAccountName = accountDialog.findViewById(R.id.edt_payee_name);
        edtInitAccountBalance = accountDialog.findViewById(R.id.edt_init_bal);

        btnCancel = accountDialog.findViewById(R.id.btn_cancel_dialog);
        btnAddAccount = accountDialog.findViewById(R.id.btn_add_payee);

        btnCancel.setOnClickListener(addAccountClickListener);
        btnAddAccount.setOnClickListener(addAccountClickListener);

        accountDialog.show();

    }

    private void setValues() {
        selectedAccountIndex = 0;

        userPreferences = this.getActivity().getSharedPreferences("UltimulProfilUtilizat", MODE_PRIVATE);
        gson = new Gson();
        String json = userPreferences.getString("UltimulProfilUtilizat", "");
        userProfile = gson.fromJson(json, Profile.class);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userProfile.getConts().size() >= 10) {
                    Toast.makeText(getActivity(), "Ai ajuns la numarul maxim de conturi", Toast.LENGTH_SHORT).show();
                } else {
                    displayAccountDialog();
                }
            }
        });


        if (userProfile.getConts().size() == 0) {
            txtTitleMessage.setText("Adauga un cont cu butonul de mai jos");
            txtDetailMessage.setVisibility(View.GONE);
            lstAccounts.setVisibility(View.GONE);
        } else {
            txtTitleMessage.setText("Selecteaza un cont pentru a vizualiza tranzactia");
            txtDetailMessage.setVisibility(View.VISIBLE);
            lstAccounts.setVisibility(View.VISIBLE);
        }

        ContAdapter adapter = new ContAdapter(this.getActivity(), R.layout.lst_accounts, userProfile.getConts());
        lstAccounts.setAdapter(adapter);

        lstAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAccountIndex = i;
                viewAccount();
            }
        });
    }


    private void viewAccount() {
        TranzactieActivity transactionsFragment = new TranzactieActivity();
        Bundle bundle = new Bundle();
        bundle.putInt("ContSelectat", selectedAccountIndex);

        transactionsFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, transactionsFragment,"gasitiAceastaActivitate")
                .addToBackStack(null)
                .commit();
    }


    private void addAccount() {

        String balance = edtInitAccountBalance.getText().toString();
        boolean isNum = false;
        double initDepositAmount = 0;

        if (!(edtAccountName.getText().toString().equals(""))) {

            try {
                initDepositAmount = Double.parseDouble(edtInitAccountBalance.getText().toString());
                isNum = true;
            } catch (Exception e) {
                if (!edtInitAccountBalance.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Introduceti o suma valida", Toast.LENGTH_SHORT).show();
                    edtInitAccountBalance.getText().clear();
                }
            }

            if (edtAccountName.getText().toString().length() > 10) {

                Toast.makeText(this.getActivity(), R.string.account_name_exceeds_char, Toast.LENGTH_SHORT).show();
                edtAccountName.getText().clear();

            } else if ((isNum) || balance.equals("")) {

                boolean match = false;

                for (int i = 0; i < userProfile.getConts().size(); i++) {
                    if (edtAccountName.getText().toString().equalsIgnoreCase(userProfile.getConts().get(i).getAccountName())) {
                        match = true;
                    }
                }

                if (!match) {

                    ApplicationDB applicationDb = new ApplicationDB(getActivity().getApplicationContext());

                    userProfile.addAccount(edtAccountName.getText().toString(), 0);

                    if (!balance.equals("")) {
                        if (isNum) {
                           if (initDepositAmount >= 0.01) {
                               userProfile.getConts().get(userProfile.getConts().size()-1).addDepositTransaction(initDepositAmount);
                               applicationDb.saveNewTransaction(userProfile, userProfile.getConts().get(userProfile.getConts().size()-1).getAccountNo(), userProfile.getConts().get(userProfile.getConts().size()-1).getTranzacties().get(userProfile.getConts().get(userProfile.getConts().size()-1).getTranzacties().size()-1));
                           }
                        }
                    }

                    applicationDb.saveNewAccount(userProfile, userProfile.getConts().get(userProfile.getConts().size()-1));

                    Toast.makeText(this.getActivity(), R.string.acc_saved_successfully, Toast.LENGTH_SHORT).show();

                    if (userProfile.getConts().size() == 1) {
                        txtTitleMessage.setText("Selectati un cont pentru a realiza transactia");
                        txtDetailMessage.setVisibility(View.VISIBLE);
                        lstAccounts.setVisibility(View.VISIBLE);
                    }
                    ArrayList<Cont> conts = userProfile.getConts();

                    ContAdapter adapter = new ContAdapter(getActivity(), R.layout.lst_accounts, conts);
                    lstAccounts.setAdapter(adapter);

                    SharedPreferences.Editor prefsEditor = userPreferences.edit();
                    String json = gson.toJson(userProfile);
                    prefsEditor.putString("UltimulProfilUtilizat", json).apply();

                    accountDialog.dismiss();

                } else {
                    Toast.makeText(this.getActivity(), R.string.account_name_error, Toast.LENGTH_SHORT).show();
                    edtAccountName.getText().clear();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Introduceti numele contului", Toast.LENGTH_SHORT).show();
        }
    }

}
