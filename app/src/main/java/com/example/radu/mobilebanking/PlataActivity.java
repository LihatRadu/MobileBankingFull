package com.example.radu.mobilebanking;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.radu.mobilebanking.Model.Cont;
import com.example.radu.mobilebanking.Model.Platitor;
import com.example.radu.mobilebanking.Model.Profile;
import com.example.radu.mobilebanking.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PlataActivity extends Fragment {

    private Spinner spnSelectAccount;
    private TextView txtNoPayeesMsg;
    private Spinner spnSelectPayee;
    private EditText edtPaymentAmount;
    private Button btnMakePayment;
    private FloatingActionButton btnAddPayee;

    private Dialog payeeDialog;
    private EditText edtPayeeName;
    private Button btnCancel;
    private Button btnConfirmAddPayee;

    private View.OnClickListener addPayeeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnCancel.getId()) {
                payeeDialog.dismiss();
                Toast.makeText(getActivity(), "Creare platitor anulata", Toast.LENGTH_SHORT).show();
            } else if (view.getId() == btnConfirmAddPayee.getId()) {
                addPayee();
            }
        }
    };

    private ArrayList<Cont> conts;
    private ArrayAdapter<Cont> accountAdapter;

    private ArrayList<Platitor> platitors;
    private ArrayAdapter<Platitor> payeeAdapter;

    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == btnMakePayment.getId()) {
                makePayment();
            }
        }
    };

    private Gson gson;
    private String json;
    private Profile userProfile;
    private SharedPreferences userPreferences;

    public PlataActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_payment, container, false);

        spnSelectAccount = rootView.findViewById(R.id.spn_select_acc);
        txtNoPayeesMsg = rootView.findViewById(R.id.txt_no_payees);
        spnSelectPayee = rootView.findViewById(R.id.spn_select_payee);
        edtPaymentAmount = rootView.findViewById(R.id.edt_payment_amount);
        btnMakePayment = rootView.findViewById(R.id.btn_make_payment);
        btnAddPayee = rootView.findViewById(R.id.floating_action_btn);

        setValues();

        return rootView;
    }


    private void setValues() {

        userPreferences = getActivity().getSharedPreferences("UltimulProfilUtilizat", MODE_PRIVATE);
        gson = new Gson();
        json = userPreferences.getString("UltimulProfilUtilizat", "");
        userProfile = gson.fromJson(json, Profile.class);

        btnMakePayment.setOnClickListener(buttonClickListener);

        btnAddPayee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayPayeeDialog();
            }
        });

        conts = userProfile.getConts();
        accountAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, conts);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnSelectAccount.setAdapter(accountAdapter);

        platitors = userProfile.getPlatitors();

        payeeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, platitors);
        payeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnSelectPayee.setAdapter(payeeAdapter);

        checkPayeeInformation();
    }

    private void displayPayeeDialog() {

        payeeDialog = new Dialog(getActivity());
        payeeDialog.setContentView(R.layout.payee_dialog);

        payeeDialog.setCanceledOnTouchOutside(true);
        payeeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(getActivity(), "Plata a fost anulata", Toast.LENGTH_SHORT).show();
            }
        });

        edtPayeeName = payeeDialog.findViewById(R.id.edt_payee_name);

        btnCancel = payeeDialog.findViewById(R.id.btn_cancel_dialog);
        btnConfirmAddPayee = payeeDialog.findViewById(R.id.btn_add_payee);

        btnCancel.setOnClickListener(addPayeeClickListener);
        btnConfirmAddPayee.setOnClickListener(addPayeeClickListener);

        payeeDialog.show();
    }


    private void checkPayeeInformation() {
        if (userProfile.getPlatitors().size() == 0) {
            txtNoPayeesMsg.setVisibility(VISIBLE);

            spnSelectPayee.setVisibility(GONE);
            edtPaymentAmount.setVisibility(GONE);
            btnMakePayment.setVisibility(GONE);
        } else {
            txtNoPayeesMsg.setVisibility(GONE);

            spnSelectPayee.setVisibility(VISIBLE);
            edtPaymentAmount.setVisibility(VISIBLE);
            btnMakePayment.setVisibility(VISIBLE);
        }
    }

    private void makePayment() {

        boolean isNum = false;
        double paymentAmount = 0;

        try {
            paymentAmount = Double.parseDouble(edtPaymentAmount.getText().toString());
            if (Double.parseDouble(edtPaymentAmount.getText().toString()) >= 0.01) {
                isNum = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isNum) {

            int selectedAccountIndex = spnSelectAccount.getSelectedItemPosition();

            if (paymentAmount > userProfile.getConts().get(selectedAccountIndex).getAccountBalance()) {
                Toast.makeText(getActivity(), "Nu ai sufficiente fonduri pentru a putea plati", Toast.LENGTH_SHORT).show();
            } else {

                int selectedPayeeIndex = spnSelectPayee.getSelectedItemPosition();

                String selectedPayee = userProfile.getPlatitors().get(selectedPayeeIndex).toString();

                userProfile.getConts().get(selectedAccountIndex).addPaymentTransaction(selectedPayee, paymentAmount);

                conts = userProfile.getConts();
                spnSelectAccount.setAdapter(accountAdapter);
                spnSelectAccount.setSelection(selectedAccountIndex);

                ApplicationDB applicationDb = new ApplicationDB(getActivity().getApplicationContext());
                applicationDb.saveNewTransaction(userProfile, userProfile.getConts().get(selectedAccountIndex).getAccountNo(), userProfile.getConts().get(selectedAccountIndex).getTranzacties().get(userProfile.getConts().get(selectedAccountIndex).getTranzacties().size()-1));
                applicationDb.overwriteAccount(userProfile, userProfile.getConts().get(selectedAccountIndex));

                SharedPreferences.Editor prefsEditor = userPreferences.edit();
                gson = new Gson();
                json = gson.toJson(userProfile);
                prefsEditor.putString("UltimulProfilUtilizat", json).apply();

                Toast.makeText(getActivity(), "Plata in $" + String.format(Locale.getDefault(), "%.2f", paymentAmount) + " facuta cu success", Toast.LENGTH_SHORT).show();
                edtPaymentAmount.getText().clear();
            }
        } else {
            Toast.makeText(getActivity(), "Va rog introduceti un numar valid, mai mare decat $0.01", Toast.LENGTH_SHORT).show();
            edtPaymentAmount.getText().clear();
        }
    }


    private void addPayee() {
        if (!(edtPayeeName.getText().toString().equals(""))) {

            boolean match = false;
            for (int i = 0; i < userProfile.getPlatitors().size(); i++) {
                if (edtPayeeName.getText().toString().equalsIgnoreCase(userProfile.getPlatitors().get(i).getPayeeName())) {
                    match = true;
                }
            }

            if (!match) {
                userProfile.addPayee(edtPayeeName.getText().toString());

                edtPayeeName.setText("");

                txtNoPayeesMsg.setVisibility(GONE);
                spnSelectPayee.setVisibility(VISIBLE);
                edtPaymentAmount.setVisibility(VISIBLE);
                btnMakePayment.setVisibility(VISIBLE);

                platitors = userProfile.getPlatitors();
                spnSelectPayee.setAdapter(payeeAdapter);
                spnSelectPayee.setSelection(userProfile.getPlatitors().size()-1);

                ApplicationDB applicationDb = new ApplicationDB(getActivity().getApplicationContext());
                applicationDb.saveNewPayee(userProfile, userProfile.getPlatitors().get(userProfile.getPlatitors().size()-1));

                SharedPreferences.Editor prefsEditor = userPreferences.edit();
                gson = new Gson();
                json = gson.toJson(userProfile);
                prefsEditor.putString("UltimulProfilUtilizat", json).apply();

                Toast.makeText(getActivity(), "Plata adaugata cu Success", Toast.LENGTH_SHORT).show();

                payeeDialog.dismiss();

            } else {
                Toast.makeText(getActivity(), "Plata pe nume deja exista", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
