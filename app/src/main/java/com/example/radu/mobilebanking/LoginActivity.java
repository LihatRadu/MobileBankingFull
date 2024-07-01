package com.example.radu.mobilebanking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import com.example.radu.mobilebanking.Model.Profile;
import com.example.radu.mobilebanking.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class LoginActivity extends Fragment {

    private Bundle bundle;
    private String username;
    private String password;

    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private CheckBox chkRememberCred;
    private Button btnCreateAccount;

    private Profile lastProfileUsed;
    private Gson gson;
    private String json;
    private SharedPreferences userPreferences;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view.getId() == btnLogin.getId()) {
                validateAccount();
            } else if (view.getId() == btnCreateAccount.getId()) {
                createAccount();
            }
        }
    };

    public LoginActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = this.getArguments();
        if (bundle != null) {
            username = bundle.getString("Username", "");
            password = bundle.getString("Password", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        edtUsername = rootView.findViewById(R.id.edt_username);
        edtPassword = rootView.findViewById(R.id.edt_password);
        btnLogin = rootView.findViewById(R.id.btn_login);
        chkRememberCred = rootView.findViewById(R.id.chk_remember);
        btnCreateAccount = rootView.findViewById(R.id.btn_create_account);

        getActivity().setTitle(getResources().getString(R.string.app_name));
        ((PornireActivity) getActivity()).removeUpButton();

        setupViews();

        if (bundle != null) {
            edtUsername.setText(username);
            edtPassword.setText(password);
            chkRememberCred.setChecked(true);
        }

        return rootView;
    }


    private void setupViews() {

        btnLogin.setOnClickListener(clickListener);
        btnCreateAccount.setOnClickListener(clickListener);

        userPreferences = getActivity().getSharedPreferences("UltimulProfilUtilizat", MODE_PRIVATE);

        chkRememberCred.setChecked(userPreferences.getBoolean("tinemaLogat", false));
        if (chkRememberCred.isChecked()) {

            gson = new Gson();
            json = userPreferences.getString("UltimulProfilUtilizat", "");
            lastProfileUsed = gson.fromJson(json, Profile.class);

            edtUsername.setText(lastProfileUsed.getUsername());
            edtPassword.setText(lastProfileUsed.getPassword());


        }

    }

    @Override
    public void onStop() {
        if (lastProfileUsed != null) {
            if (edtUsername.getText().toString().equals(lastProfileUsed.getUsername()) && edtPassword.getText().toString().equals(lastProfileUsed.getPassword())) {
                userPreferences.edit().putBoolean("tinemaLogat", chkRememberCred.isChecked()).apply();
            } else {
                userPreferences.edit().putBoolean("tinemaLogat", false).apply();
            }
        }

        super.onStop();
    }

    private void validateAccount() {
        ApplicationDB applicationDB = new ApplicationDB(getActivity().getApplicationContext());
        ArrayList<Profile> profiles = applicationDB.getAllProfiles();

        boolean match = false;

        if (profiles.size() > 0) {
            for (int i = 0; i < profiles.size(); i++) {
                if (edtUsername.getText().toString().equals(profiles.get(i).getUsername()) && edtPassword.getText().toString().equals(profiles.get(i).getPassword())) {

                    match = true;

                    userPreferences.edit().putBoolean("tinemaLogat", chkRememberCred.isChecked()).apply();

                    lastProfileUsed = profiles.get(i);

                    SharedPreferences.Editor prefsEditor = userPreferences.edit();
                    gson = new Gson();
                    json = gson.toJson(lastProfileUsed);
                    prefsEditor.putString("UltimulProfilUtilizat", json).apply();

                    ((PornireActivity) getActivity()).login();
                }
            }
            if (!match) {
                Toast.makeText(getActivity(), R.string.incorrect_login, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.incorrect_login, Toast.LENGTH_SHORT).show();
        }


    }


    private void createAccount() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_frm_content, new CreareProfil())
                .addToBackStack(null)
                .commit();
    }

}
