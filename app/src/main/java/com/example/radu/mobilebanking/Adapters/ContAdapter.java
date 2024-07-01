package com.example.radu.mobilebanking.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.radu.mobilebanking.Model.Cont;
import com.example.radu.mobilebanking.R;

import java.util.ArrayList;



public class ContAdapter extends ArrayAdapter<Cont> {

    private Context context;
    private int resource;

    public ContAdapter(Context context, int resource, ArrayList<Cont> conts) {
        super(context, resource, conts);

        this.context = context;
        this.resource = resource;
    }


    @Override
    @NonNull
    public View getView (int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }

        Cont cont = getItem(position);

        TextView txtAccountName = convertView.findViewById(R.id.txt_account_name);
        txtAccountName.setText(cont.getAccountName());

        TextView txtAccountNo = convertView.findViewById(R.id.txt_acc_no);
        txtAccountNo.setText(context.getString(R.string.account_no) + " " + cont.getAccountNo());

        TextView txtAccountBalance = convertView.findViewById(R.id.txt_balance);
        txtAccountBalance.setText("Balanta: " + String.format("%.2f", cont.getAccountBalance()));

        return convertView;
    }
}
