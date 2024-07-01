package com.example.radu.mobilebanking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.radu.mobilebanking.Model.Profile;
import com.example.radu.mobilebanking.Model.db.ApplicationDB;

import java.util.ArrayList;

public class CreareProfil extends Fragment {

    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtCountry;
    private EditText edtUsername;
    private EditText edtPassword;
    private EditText edtPasswordConfirm;

    public CreareProfil() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Creare Profil");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_profile, container, false);

        edtFirstName = rootView.findViewById(R.id.edt_first_name);
        edtLastName = rootView.findViewById(R.id.edt_last_name);
        edtCountry = rootView.findViewById(R.id.edt_country);
        edtUsername = rootView.findViewById(R.id.edt_username);
        edtPassword = rootView.findViewById(R.id.edt_password);
        edtPasswordConfirm = rootView.findViewById(R.id.edt_password_confirm);
        Button btnCreateAccount = rootView.findViewById(R.id.btn_create_account);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProfile();
            }
        });

        ((PornireActivity) getActivity()).showUpButton();

        return rootView;
    }


    private void createProfile() {

        ApplicationDB applicationDb = new ApplicationDB( getActivity().getApplicationContext());
        ArrayList<Profile> profiles = applicationDb.getAllProfiles();
        boolean usernameTaken = false;

        for (int iProfile = 0; iProfile < profiles.size(); iProfile++) {
            if (edtUsername.getText().toString().equals(profiles.get(iProfile).getUsername())) {
                usernameTaken = true;
            }
        }

        if (edtFirstName.getText().toString().equals("") || edtLastName.getText().toString().equals("") || edtCountry.getText().toString().equals("") ||
                edtUsername.getText().toString().equals("") || edtPassword.getText().toString().equals("") || edtPasswordConfirm.getText().toString().equals("")) {
            Toast.makeText(getActivity(), R.string.fields_blank, Toast.LENGTH_SHORT).show();
        }

        else if (!(edtPassword.getText().toString().equals(edtPasswordConfirm.getText().toString()))) {
            Toast.makeText(getActivity(), R.string.password_mismatch, Toast.LENGTH_SHORT).show();
        }
        else if (usernameTaken) {
            Toast.makeText(getActivity(), "Un ser foloseste deja username", Toast.LENGTH_SHORT).show();
        }
        else {
            Profile userProfile = new Profile(edtFirstName.getText().toString(), edtLastName.getText().toString(), edtCountry.getText().toString(),
                    edtUsername.getText().toString(), edtPassword.getText().toString());

            applicationDb.saveNewProfile(userProfile);

            Bundle bundle = new Bundle();
            bundle.putString("Username", userProfile.getUsername());
            bundle.putString("Password", userProfile.getPassword());

            ((PornireActivity) getActivity()).profileCreated(bundle);

        }
    }
}
