package com.example.radu.mobilebanking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.radu.mobilebanking.Model.Profile;
import com.google.gson.Gson;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class TablaActivity extends Fragment {

    private ImageView imgTime;
    private TextView txtWelcome;
    private TextView txtMessage;
    private Button btnAddAccount;

    public TablaActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        imgTime = rootView.findViewById(R.id.img_time);
        txtWelcome = rootView.findViewById(R.id.txt_welcome);
        txtMessage = rootView.findViewById(R.id.txt_details_msg);
        btnAddAccount = rootView.findViewById(R.id.btn_add_account);

        setupViews();
        return rootView;

    }


    private void setupViews() {

        SharedPreferences userPreferences = getActivity().getSharedPreferences("UltimulProfilUtilizat", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = userPreferences.getString("UltimulProfilUtilizat", "");
        Profile userProfile = gson.fromJson(json, Profile.class);

        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("AfisareCont", true);
                ((StocareActivity) getActivity()).manualNavigation(StocareActivity.manualNavID.ACCOUNTS_ID, bundle);
            }
        });

        if (userProfile.getConts().size() == 0) {
            txtMessage.setVisibility(View.VISIBLE);
            btnAddAccount.setVisibility(View.VISIBLE);
            txtMessage.setText("Nu ai niciun cont, apasa mai jos pentru adaugarea unui cont");
        } else {
            txtMessage.setVisibility(View.GONE);
            btnAddAccount.setVisibility(View.GONE);


        }

        StringBuilder welcomeString = new StringBuilder();

        Calendar calendar = Calendar.getInstance();

        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 5 && timeOfDay < 12) {
            welcomeString.append(getString(R.string.good_morning));
            imgTime.setImageResource(R.drawable.morning_icon_96);
        } else if (timeOfDay >= 12 && timeOfDay < 17) {
            welcomeString.append(getString(R.string.good_afternoon));
            imgTime.setImageResource(R.drawable.day_icon_96);
        } else {
            welcomeString.append(getString(R.string.good_evening));
            imgTime.setImageResource(R.drawable.sunset);
        }

        welcomeString.append(", ")
                .append(userProfile.getFirstName())
                .append(". Bine ati venit la Mobile Banking App. ")
                .append(getString(R.string.happy))
                .append(" ");

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String[] days = getResources().getStringArray(R.array.days);
        String dow = "";

        switch(day) {
            case Calendar.SUNDAY:
                dow = days[0];
                break;
            case Calendar.MONDAY:
                dow = days[1];
                break;
            case Calendar.TUESDAY:
                dow = days[2];
                break;
            case Calendar.WEDNESDAY:
                dow = days[3];
                break;
            case Calendar.THURSDAY:
                dow = days[4];
                break;
            case Calendar.FRIDAY:
                dow = days[5];
                break;
            case Calendar.SATURDAY:
                dow = days[6];
                break;
            default:
                break;
        }

        welcomeString.append(dow)
                .append(".");

        txtWelcome.setText(welcomeString.toString());
    }

}
