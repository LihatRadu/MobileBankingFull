package com.example.radu.mobilebanking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.radu.mobilebanking.Model.Cont;
import com.example.radu.mobilebanking.Model.Profile;
import com.example.radu.mobilebanking.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class TransferActivity extends Fragment {

    private Spinner spnSendingAccount;
    private EditText edtTransferAmount;
    private Spinner spnReceivingAccount;
    private Button btnConfirmTransfer;

    ArrayList<Cont> conts;
    ArrayAdapter<Cont> accountAdapter;

    SharedPreferences userPreferences;
    Gson gson;
    String json;
    Profile userProfile;

    public TransferActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_transfer, container, false);

        spnSendingAccount = rootView.findViewById(R.id.spn_select_sending_acc);
        edtTransferAmount = rootView.findViewById(R.id.edt_transfer_amount);
        spnReceivingAccount = rootView.findViewById(R.id.spn_select_receiving_acc);
        btnConfirmTransfer = rootView.findViewById(R.id.btn_confirm_transfer);

        setValues();

        return rootView;
    }


    private void setValues() {

        userPreferences = getActivity().getSharedPreferences("UltimulProfilUtilizat", MODE_PRIVATE);

        gson = new Gson();
        json = userPreferences.getString("UltimulProfilUtilizat", "");
        userProfile = gson.fromJson(json, Profile.class);

        btnConfirmTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmTransfer();
            }
        });

        setAdapters();
    }


    private void setAdapters() {
        conts = userProfile.getConts();
        accountAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, conts);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnSendingAccount.setAdapter(accountAdapter);
        spnReceivingAccount.setAdapter(accountAdapter);
        spnReceivingAccount.setSelection(1);
    }


    private void confirmTransfer() {

        int receivingAccIndex = spnReceivingAccount.getSelectedItemPosition();
        boolean isNum = false;
        double transferAmount = 0;

        try {
            transferAmount = Double.parseDouble(edtTransferAmount.getText().toString());
            isNum = true;
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Introduceti suma pe care o doriti sa o transferati", Toast.LENGTH_SHORT).show();
        }
        if (isNum) {
            if (spnSendingAccount.getSelectedItemPosition() == receivingAccIndex) {
                Toast.makeText(getActivity(), "Nu puteti sa transferati pe acelasi cont", Toast.LENGTH_SHORT).show();
            }
            else if(transferAmount < 0.01) {
                Toast.makeText(getActivity(), "Suma minima pentru  transfer este $0.01", Toast.LENGTH_SHORT).show();

            } else if (transferAmount > userProfile.getConts().get(spnSendingAccount.getSelectedItemPosition()).getAccountBalance()) {

                Cont acc = (Cont) spnSendingAccount.getSelectedItem();
                Toast.makeText(getActivity(), "Contul," + " " + acc.toString() + " " + "nu are sufficiente fonduri pentru a face transferul", Toast.LENGTH_LONG).show();
            } else {

                int sendingAccIndex = spnSendingAccount.getSelectedItemPosition();
                
                Cont sendingCont = (Cont) spnSendingAccount.getItemAtPosition(sendingAccIndex);
                Cont receivingCont = (Cont) spnReceivingAccount.getItemAtPosition(receivingAccIndex);

                userProfile.addTransferTransaction(sendingCont, receivingCont, transferAmount);

                spnSendingAccount.setAdapter(accountAdapter);
                spnReceivingAccount.setAdapter(accountAdapter);

                spnSendingAccount.setSelection(sendingAccIndex);
                spnReceivingAccount.setSelection(receivingAccIndex);

                ApplicationDB applicationDb = new ApplicationDB(getActivity().getApplicationContext());

                applicationDb.overwriteAccount(userProfile, sendingCont);
                applicationDb.overwriteAccount(userProfile, receivingCont);

                applicationDb.saveNewTransaction(userProfile, sendingCont.getAccountNo(),
                        sendingCont.getTranzacties().get(sendingCont.getTranzacties().size()-1));
                applicationDb.saveNewTransaction(userProfile, receivingCont.getAccountNo(),
                        receivingCont.getTranzacties().get(receivingCont.getTranzacties().size()-1));


                SharedPreferences.Editor prefsEditor = userPreferences.edit();
                json = gson.toJson(userProfile);
                prefsEditor.putString("UltimulProfilUtilizat", json).apply();

                Toast.makeText(getActivity(), "Transferul pentru suma de" + String.format(Locale.getDefault(), "%.2f",transferAmount) + " a fost facuta cu success", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
