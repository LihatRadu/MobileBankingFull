package com.example.radu.mobilebanking.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.radu.mobilebanking.Model.Tranzactie;
import com.example.radu.mobilebanking.R;

import java.util.ArrayList;

public class TranzactieAdapter extends ArrayAdapter<Tranzactie> {

    private Context context;
    private int resource;

    public TranzactieAdapter(Context context, int resource, ArrayList<Tranzactie> tranzacties) {
        super(context, resource, tranzacties);

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

        Tranzactie tranzactie = getItem(position);

        ImageView imgTransactionIcon = convertView.findViewById(R.id.img_transaction);
        TextView txtTransactionTitle = convertView.findViewById(R.id.txt_transaction_type_id);
        TextView txtTransactionTimestamp = convertView.findViewById(R.id.txt_transaction_timestamp);
        TextView txtTransactionInfo = convertView.findViewById(R.id.txt_transaction_info);
        txtTransactionInfo.setVisibility(View.VISIBLE);
        TextView txtTransactionAmount = convertView.findViewById(R.id.txt_transaction_amount);

        txtTransactionTitle.setText(tranzactie.getTransactionType().toString() + " - " + tranzactie.getTransactionID());
        txtTransactionTimestamp.setText(tranzactie.getTimestamp());
        txtTransactionAmount.setText("Cantitate: " + String.format("%.2f", tranzactie.getAmount()));

        if (tranzactie.getTransactionType() == Tranzactie.TRANSACTION_TYPE.PAYMENT) {
            imgTransactionIcon.setImageResource(R.drawable.lst_payment_icon);
            txtTransactionInfo.setText("Catre Platitor: " + tranzactie.getPayee());
            txtTransactionAmount.setTextColor(Color.RED);
        } else if (tranzactie.getTransactionType() == Tranzactie.TRANSACTION_TYPE.TRANSFER) {
            imgTransactionIcon.setImageResource(R.drawable.lst_transfer_icon);
            txtTransactionInfo.setText("De la: " + tranzactie.getSendingAccount() + " - " + "la: " + tranzactie.getDestinationAccount());
            txtTransactionAmount.setTextColor(getContext().getResources().getColor(android.R.color.holo_blue_light));
        } else if (tranzactie.getTransactionType() == Tranzactie.TRANSACTION_TYPE.DEPOSIT) {
            imgTransactionIcon.setImageResource(R.drawable.lst_deposit_icon);
            txtTransactionInfo.setVisibility(View.GONE);
            txtTransactionAmount.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
        }

        return convertView;
    }
}
