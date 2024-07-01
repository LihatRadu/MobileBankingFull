package com.example.radu.mobilebanking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class PornireActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.login_frm_content, new LoginActivity()).commit();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getBaseContext(), R.string.account_cancelled, Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
    public void removeUpButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void showUpButton() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_about) {
            displayHelpDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        builder.setTitle("Help")
                .setMessage("This Bank App Demo was made by Mike Banks. Soon, this dialog will give the user help, depending on where they are in the app");

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void login() {
        Intent intent = new Intent(getApplicationContext(), StocareActivity.class);
        startActivity(intent);
        finish();
    }

    public void profileCreated(Bundle bundle) {

        Toast.makeText(this, R.string.account_success, Toast.LENGTH_SHORT).show();

        LoginActivity loginActivity = new LoginActivity();
        loginActivity.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.login_frm_content, loginActivity).commit();
    }
}
