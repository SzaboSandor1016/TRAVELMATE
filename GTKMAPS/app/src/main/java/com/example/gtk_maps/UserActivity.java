package com.example.gtk_maps;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private static final int MY_SHARES_REQUEST_CODE = 1;
    private static final int SHARED_WITH_ME_REQUEST_CODE = 2;
    private static final int USER_ACCOUNT_REQUEST_CODE = 3;
    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Resources resources;
    private boolean isLoggedIn;

    private Dialog deleteDialog, changeDialog;
    private Window changeDialogWindow, deleteDialogWindow;
    private EditText emailET,passwdET,fNameET,gNameET,
            registerEmailET, registerPasswdET, registerConfirmPasswdET, registerUsernameET;
    private TextView loggedInEmailTV, sharesTV, accountTV;
    private Button logInBTN,confirmRegisterBTN,registerBTN,cancelRegisterBTN, privacyBTN;

    private ImageButton ageIB, logOutBTN;
    private TextView selectedAgeTV;
    private LinearLayout loggedInLL, registerLL, logInLL;
    private ListView userAccountLV, moreOptionsLV, sharesLV;

    private ArrayAdapter<String> userAccountAdapter, moreOptionsAdapter, sharesArrayAdapter;
    private SharedPreferences sharedPreferences;

    private String selectedYear= "", selectedMonth = "", selectedDayOfMonth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        resources= getResources();

        registerPasswdET = findViewById(R.id.registerPasswdET);
        registerConfirmPasswdET = findViewById(R.id.registerConfirmPasswdET);
        registerEmailET = findViewById(R.id.registerEmailET);
        registerUsernameET = findViewById(R.id.registerUsernameET);
        emailET= findViewById(R.id.emailET);
        passwdET= findViewById(R.id.passwdET);
        fNameET= findViewById(R.id.fNameET);
        gNameET= findViewById(R.id.gNameET);
        loggedInEmailTV= findViewById(R.id.loggedInEmailTV);

        selectedAgeTV = findViewById(R.id.selectedAgeTV);

        ageIB= findViewById(R.id.ageIB);

        logInBTN= findViewById(R.id.logInBTN);
        confirmRegisterBTN= findViewById(R.id.confirmRegisterBTN);
        registerBTN= findViewById(R.id.registerBTN);
        logOutBTN= findViewById(R.id.logOutBTN);
        cancelRegisterBTN= findViewById(R.id.cancelRegisterBTN);
        privacyBTN = findViewById(R.id.privacyBTN);

        loggedInLL= findViewById(R.id.loggedInLL);
        logInLL = findViewById(R.id.logInLL);
        registerLL= findViewById(R.id.registerLL);
        userAccountLV = findViewById(R.id.userAccountLV);
        moreOptionsLV = findViewById(R.id.moreOptionsLV);
        sharesLV = findViewById(R.id.sharesLV);

        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences= getSharedPreferences("FirebaseManager", Context.MODE_PRIVATE);
        isLoggedIn=sharedPreferences.getBoolean("loggedIn", false);

        firebaseManager = FirebaseManager.getInstance(UserActivity.this,mAuth,mDatabase, sharedPreferences);

        if (mAuth.getCurrentUser()!=null || isLoggedIn){
            refreshUIAfterRegisterOrLogIn();

            loggedInEmailTV.setText(sharedPreferences.getString("username",""));
        }else{
           refreshUIForLogIn();
        }


        userAccountAdapter = new ArrayAdapter<>(UserActivity.this, android.R.layout.simple_list_item_1,resources.getStringArray(R.array.account_data));
        userAccountLV.setAdapter(userAccountAdapter);

        userAccountLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent userAccount = new Intent(UserActivity.this,UserAccountActivity.class);
                        startActivityForResult(userAccount, USER_ACCOUNT_REQUEST_CODE);
                        break;
                    case 1:
                        if (moreOptionsLV.getVisibility()==View.GONE){
                            moreOptionsLV.setVisibility(View.VISIBLE);
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                            moreOptionsLV.startAnimation(animation);
                        }else {
                            moreOptionsLV.setVisibility(View.GONE);
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_slide_down);
                            moreOptionsLV.startAnimation(animation);
                        }
                        break;
                }
            }
        });

        moreOptionsAdapter = new ArrayAdapter<>(UserActivity.this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.further_settings_options));
        moreOptionsLV.setAdapter(moreOptionsAdapter);

        moreOptionsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        //TODO CHANGE PASSWORD
                        changeDialog = new Dialog(UserActivity.this,R.style.CustomDialogTheme);
                        changeDialog.setContentView(R.layout.change_password_dialog);

                        changeDialogWindow = changeDialog.getWindow();
                        if (changeDialogWindow!=null) {
                            changeDialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            /*window.setBackgroundDrawableResource(R.drawable.fade);*/
                            changeDialogWindow.setWindowAnimations(R.style.DialogAnimation);

                            WindowManager.LayoutParams layoutParams = changeDialogWindow.getAttributes();
                            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Match parent width
                            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Wrap content height
                            layoutParams.gravity = Gravity.TOP; // Position at the bottom of the screen
                            changeDialogWindow.setAttributes(layoutParams);
                        }

                        changeDialog.setCancelable(true);
                        EditText confirmPasswordET = changeDialog.findViewById(R.id.confirmPasswordET);
                        EditText confirmPasswordAgainET = changeDialog.findViewById(R.id.confirmPasswordAgainET);
                        EditText confirmNewPasswordET = changeDialog.findViewById(R.id.confirmNewPasswordET);
                        EditText confirmNewPasswordAgainET = changeDialog.findViewById(R.id.confirmNewPasswordAgainET);

                        Button confirmChangeBTN =changeDialog.findViewById(R.id.confirmChangeBTN);
                        confirmChangeBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!confirmPasswordET.getText().toString().trim().equals("") && !confirmPasswordAgainET.getText().toString().trim().equals("") &&
                                !confirmNewPasswordET.getText().toString().trim().equals("") && !confirmNewPasswordAgainET.getText().toString().trim().equals("")) {
                                    if (confirmPasswordET.getText().toString().trim().equals(confirmPasswordAgainET.getText().toString().trim())) {
                                        if (confirmNewPasswordET.getText().toString().trim().equals(confirmNewPasswordAgainET.getText().toString().trim())) {
                                            firebaseManager.changePassword(confirmPasswordET.getText().toString().trim(),confirmNewPasswordET.getText().toString().trim(),
                                                    new FirebaseManager.ChangePassword() {
                                                        @Override
                                                        public void onSuccess() {
                                                            Toast.makeText(UserActivity.this, R.string.change_successful, Toast.LENGTH_LONG).show();
                                                        }

                                                        @Override
                                                        public void onFailure() {
                                                            Toast.makeText(UserActivity.this, R.string.change_unsuccessful, Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                            changeDialog.dismiss();
                                        }else {
                                            Toast.makeText(UserActivity.this,R.string.new_password_mismatch,Toast.LENGTH_LONG).show();
                                        }
                                    }else{
                                        Toast.makeText(UserActivity.this,R.string.old_password_mismatch,Toast.LENGTH_LONG).show();
                                    }
                                }else {
                                    Toast.makeText(UserActivity.this,R.string.empty_message,Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        changeDialog.show();

                        break;
                    case 1:
                        //TODO DELETE ACCOUNT

                        deleteDialog = new Dialog(UserActivity.this,R.style.CustomDialogTheme);
                        deleteDialog.setContentView(R.layout.delete_account_dialog);

                        deleteDialogWindow = deleteDialog.getWindow();
                        if (deleteDialogWindow!=null) {
                            deleteDialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            /*window.setBackgroundDrawableResource(R.drawable.fade);*/
                            deleteDialogWindow.setWindowAnimations(R.style.DialogAnimation);

                            WindowManager.LayoutParams deleteDialogWindowAttributes = deleteDialogWindow.getAttributes();
                            deleteDialogWindowAttributes.width = WindowManager.LayoutParams.MATCH_PARENT; // Match parent width
                            deleteDialogWindowAttributes.height = WindowManager.LayoutParams.WRAP_CONTENT; // Wrap content height
                            deleteDialogWindowAttributes.gravity = Gravity.TOP; // Position at the bottom of the screen
                            deleteDialogWindow.setAttributes(deleteDialogWindowAttributes);
                        }

                        deleteDialog.setCancelable(true);
                        EditText confirmEmailDeleteET = deleteDialog.findViewById(R.id.confirmDeleteEmailET);
                        EditText confirmPasswordDeleteET = deleteDialog.findViewById(R.id.confirmDeletePasswordET);

                        Button confirmDeleteBTN =deleteDialog.findViewById(R.id.confirmDeleteBTN);
                        confirmDeleteBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!confirmEmailDeleteET.getText().toString().trim().equals("") && !confirmPasswordDeleteET.getText().toString().trim().equals("")) {
                                    firebaseManager.deleteUser(confirmEmailDeleteET.getText().toString().trim(), confirmPasswordDeleteET.getText().toString().trim(),
                                            new FirebaseManager.DeleteUser() {
                                                @Override
                                                public void onSuccess() {
                                                    Toast.makeText(UserActivity.this,R.string.delete_successful,Toast.LENGTH_LONG).show();
                                                    animateLinearLayouts(logInLL, loggedInLL);
                                                    refreshUIForLogIn();
                                                }

                                                @Override
                                                public void onFailure() {
                                                    Toast.makeText(UserActivity.this,R.string.delete_unsuccessful,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                    deleteDialog.dismiss();
                                }else {
                                    Toast.makeText(UserActivity.this,R.string.empty_log_in,Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        deleteDialog.show();

                        break;

                }
            }
        });

        sharesArrayAdapter = new ArrayAdapter<>(UserActivity.this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.shares_options));
        sharesLV.setAdapter(sharesArrayAdapter);

        sharesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent recentAddressees = new Intent(UserActivity.this, AddresseesActivity.class);
                        startActivity(recentAddressees);
                        break;
                    case 1:
                        Intent sharedWith = new Intent(UserActivity.this,SharedWithUserActivity.class);
                        startActivityForResult(sharedWith, SHARED_WITH_ME_REQUEST_CODE);
                        break;
                    case 2:
                        Intent sharedBy = new Intent(UserActivity.this,SharedByUserActivity.class);
                        startActivityForResult(sharedBy, MY_SHARES_REQUEST_CODE);
                        break;
                }
            }
        });


        logInBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!emailET.getText().toString().trim().equals("") && !passwdET.getText().toString().trim().equals("")) {
                    firebaseManager.signIn(emailET.getText().toString().trim(), passwdET.getText().toString().trim(), new FirebaseManager.AuthListener() {
                        @Override
                        public void onSignUpSuccess() {
                            animateLinearLayouts(loggedInLL, logInLL);
                            refreshUIAfterRegisterOrLogIn();

                            User loggedInUser = firebaseManager.getLoggedInUser();

                            loggedInEmailTV.setText(loggedInUser.getUsername());
                        }

                        @Override
                        public void onSignUpFailure(Exception exception) {

                        }
                    });
                }else {
                    Toast.makeText(UserActivity.this,R.string.empty_log_in,Toast.LENGTH_LONG).show();
                }
            }
        });
        logOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateLinearLayouts(logInLL,loggedInLL);
                refreshUIForLogIn();
                firebaseManager.signOut();

            }
        });
        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateLinearLayouts(registerLL,logInLL);
                refreshUIForRegister();
                loggedInEmailTV.setText(sharedPreferences.getString("username",""));
            }
        });
        confirmRegisterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email,family,given,username;

                email=registerEmailET.getText().toString().trim();
                family=fNameET.getText().toString().trim();
                given=gNameET.getText().toString().trim();
                username = registerUsernameET.getText().toString().trim();

                boolean isEmpty= email.equals("") || username.equals("") || family.equals("") || given.equals("") || registerPasswdET.getText().toString().trim().equals("") ||
                        selectedMonth.equals("") || selectedYear.equals("") || selectedDayOfMonth.equals("");
                //boolean isPasswordCorrect = (registerPasswdET.getText().toString().trim().length()>=8) && containsNum(registerPasswdET.getText().toString().trim()) && containsLetter(registerPasswdET.getText().toString().trim());

                if (!isEmpty) {
                    if(registerPasswdET.getText().toString().trim().equals(registerConfirmPasswdET.getText().toString().trim())) {
                        //if(isPasswordCorrect) {
                        firebaseManager.signUp(email, username, registerPasswdET.getText().toString().trim(), family
                                , given, selectedYear, selectedMonth, selectedDayOfMonth, new FirebaseManager.AuthListener() {
                                    @Override
                                    public void onSignUpSuccess() {
                                        animateLinearLayouts(loggedInLL, registerLL);
                                        refreshUIAfterRegisterOrLogIn();

                                        User loggedInUser = firebaseManager.getLoggedInUser();

                                        loggedInEmailTV.setText(loggedInUser.getUsername());
                                    }

                                    @Override
                                    public void onSignUpFailure(Exception exception) {

                                    }
                                });
                    }else {
                        Toast.makeText(UserActivity.this,R.string.password_mismatch, Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(UserActivity.this,R.string.empty_message, Toast.LENGTH_LONG).show();
                }
                //}else {
                    //Toast.makeText(UserActivity.this,"Valami Üres", Toast.LENGTH_LONG).show();
                //}
            }
        });
        cancelRegisterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateLinearLayouts(logInLL,registerLL);
                refreshUIForLogIn();
            }
        });

        privacyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                builder.setTitle(resources.getString(R.string.privacy_policy));
                builder.setMessage(resources.getString(R.string.privacy_policy_text));
                builder.setCancelable(true);
                builder.create().show();
            }
        });


        ageIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animateImageButton(v);

                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Create DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UserActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String selectedDate = year + "/" + (month+1) + "/" + dayOfMonth;

                                selectedYear= String.valueOf(year);
                                selectedMonth= String.valueOf(month+1);
                                selectedDayOfMonth= String.valueOf(dayOfMonth);

                                selectedAgeTV.setText(selectedDate);
                            }
                        },
                        year, month, dayOfMonth);

                // Show dialog
                datePickerDialog.show();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearAll();
        resources.flushLayoutCache();
    }

    private void clearAll(){
        deleteDialogWindow = null;
        changeDialogWindow = null;

        sharesArrayAdapter = null;
        moreOptionsAdapter = null;
        userAccountAdapter = null;

    }

    private void refreshUIForLogIn(){
        loggedInLL.setVisibility(View.GONE);
        logInLL.setVisibility(View.VISIBLE);
        moreOptionsLV.setVisibility(View.GONE);
        registerLL.setVisibility(View.GONE);

        clearEditTexts();
    }
    private void refreshUIForRegister(){
        loggedInLL.setVisibility(View.GONE);
        logInLL.setVisibility(View.GONE);
        registerLL.setVisibility(View.VISIBLE);

        clearEditTexts();
    }
    private void refreshUIAfterRegisterOrLogIn(){
        loggedInLL.setVisibility(View.VISIBLE);
        logInLL.setVisibility(View.GONE);
        registerLL.setVisibility(View.GONE);

        clearEditTexts();
    }
    private void clearEditTexts(){
        registerPasswdET.setText("");
        registerEmailET.setText("");
        registerConfirmPasswdET.setText("");
        registerUsernameET.setText("");
        fNameET.setText("");
        gNameET.setText("");

        emailET.setText("");
        passwdET.setText("");
    }

    private void animateImageButton(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.image_button_click);
        view.startAnimation(animation);
    }

    private void animateLinearLayouts(LinearLayout open,LinearLayout close){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.delayed_slide_down);
        open.startAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_slide_down);
        close.startAnimation(animation2);
    }

    private static boolean containsNum(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
    private static boolean containsLetter(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SHARES_REQUEST_CODE){
            if (resultCode== RESULT_OK) {
                if (data.getSerializableExtra("sharedByMe")!=null){
                    Intent intent = new Intent();
                    intent.putExtra("label", data.getSerializableExtra("label"));
                    intent.putExtra("sharedMap", data.getSerializableExtra("sharedByMe"));
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        }
        if (requestCode == SHARED_WITH_ME_REQUEST_CODE){
            if (resultCode== RESULT_OK) {
                if (data.getSerializableExtra("sharedWithMe")!=null){
                    Intent intent = new Intent();
                    intent.putExtra("label", data.getSerializableExtra("label"));
                    intent.putExtra("sharedMap", data.getSerializableExtra("sharedWithMe"));
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        }
        if (requestCode == USER_ACCOUNT_REQUEST_CODE){
            if (resultCode== RESULT_OK) {
                if(data.getBooleanExtra("delete",false)){
                    refreshUIForLogIn();
                    animateLinearLayouts(logInLL, loggedInLL);
                }
            }
        }
    }
}