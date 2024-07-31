package com.example.gtk_maps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserAccountActivity extends AppCompatActivity {

    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText loggedInFNameET,loggedInGNameET, loggedInUsernameET;
    private ImageButton loggedInAgeIB,editGNameIB,editFNameIB,editUsernameIB,
            confirmAgeIB, confirmFNameIB,confirmGNameIB,confirmUsernameIB;
    private TextView loggedInSelectedAgeTV;
    private SharedPreferences sharedPreferences;
    private String oldGName, oldFName, oldYear, oldMonth,oldDay, oldUsername, newYear,newMonth,newDay, newUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        loggedInUsernameET = findViewById(R.id.loggedInUsernameET);
        loggedInFNameET= findViewById(R.id.loggedInFNameET);
        loggedInGNameET= findViewById(R.id.loggedInGNameET);

        loggedInAgeIB = findViewById(R.id.loggedInAgeIB);
        editGNameIB = findViewById(R.id.editGNameIB);
        editFNameIB = findViewById(R.id.editFNameIB);
        editUsernameIB = findViewById(R.id.editUsernameIB);
        confirmAgeIB = findViewById(R.id.confirmAgeIB);
        confirmFNameIB = findViewById(R.id.confirmFNameIB);
        confirmGNameIB = findViewById(R.id.confirmGNameIB);
        confirmUsernameIB = findViewById(R.id.confirmUsernameIB);

        loggedInSelectedAgeTV= findViewById(R.id.loggedInSelectedAgeTV);

        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences= getSharedPreferences("FirebaseManager", Context.MODE_PRIVATE);

        firebaseManager = FirebaseManager.getInstance(UserAccountActivity.this,mAuth,mDatabase, sharedPreferences);

        loggedInUsernameET.setHint(sharedPreferences.getString("username", ""));
        loggedInFNameET.setHint(sharedPreferences.getString("family", ""));
        loggedInGNameET.setHint(sharedPreferences.getString("given", ""));
        String birthDate= sharedPreferences.getString("birthDateYear","") + "/" +
                sharedPreferences.getString("birthDateMonth","")+"/"+ sharedPreferences.getString("birthDateDay","");
        loggedInSelectedAgeTV.setText(birthDate);


        loggedInAgeIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

                loggedInAgeIB.setVisibility(View.INVISIBLE);
                confirmAgeIB.setVisibility(View.VISIBLE);

                String oldAge= loggedInSelectedAgeTV.getText().toString().trim();
                String[] splitOldAge= oldAge.split("/");
                oldYear= splitOldAge[0];
                oldMonth= splitOldAge[1];
                oldDay=splitOldAge[2];

                final Calendar calendar = Calendar.getInstance();
                int year = Integer.parseInt(splitOldAge[0]);
                int month = Integer.parseInt(splitOldAge[1])-1;
                int dayOfMonth = Integer.parseInt(splitOldAge[2]);

                // Create DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UserAccountActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String selectedDate = year + "/" + (month+1) + "/" + dayOfMonth;

                                if (!selectedDate.equals(oldAge))
                                    loggedInSelectedAgeTV.setText(selectedDate);

                                if (!String.valueOf(year).equals(oldYear)){
                                    newYear= String.valueOf(year);
                                }else {
                                    newYear= oldYear;
                                }
                                if (!String.valueOf(month+1).equals(oldMonth)){
                                    newMonth= String.valueOf(month+1);
                                }else {
                                    newMonth= oldMonth;
                                }
                                if (!String.valueOf(dayOfMonth).equals(oldDay)){
                                    newDay=String.valueOf(dayOfMonth);
                                }else {
                                    newDay= oldDay;
                                }
                            }
                        },
                        year, month, dayOfMonth);

                // Show dialog
                datePickerDialog.show();
                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        newYear= oldYear;
                        newMonth=oldMonth;
                        newDay=oldDay;
                    }
                });

            }
        });

        editGNameIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

                confirmGNameIB.setVisibility(View.VISIBLE);
                editGNameIB.setVisibility(View.INVISIBLE);
                loggedInGNameET.setEnabled(true);

                oldGName=loggedInGNameET.getHint().toString().trim();

            }
        });
        editFNameIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

                confirmFNameIB.setVisibility(View.VISIBLE);
                editFNameIB.setVisibility(View.INVISIBLE);
                loggedInFNameET.setEnabled(true);

                oldFName= loggedInFNameET.getHint().toString().trim();

            }
        });

        editUsernameIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

                confirmUsernameIB.setVisibility(View.VISIBLE);
                editUsernameIB.setVisibility(View.INVISIBLE);
                loggedInUsernameET.setEnabled(true);

                oldUsername = loggedInUsernameET.getHint().toString().trim();
            }
        });
        confirmAgeIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

                confirmAgeIB.setVisibility(View.INVISIBLE);
                loggedInAgeIB.setVisibility(View.VISIBLE);

                if (!String.valueOf(newYear).equals(oldYear)){
                    firebaseManager.changeUserBirthDate("year", String.valueOf(newYear), new FirebaseManager.ChangeUserBirthDate() {
                        @Override
                        public void Success() {
                            Toast.makeText(UserAccountActivity.this, R.string.change_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void UnSuccess() {

                            Toast.makeText(UserAccountActivity.this, R.string.change_unsuccess, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                if (!String.valueOf(newMonth).equals(oldMonth)){
                    firebaseManager.changeUserBirthDate("month", String.valueOf(newMonth), new FirebaseManager.ChangeUserBirthDate() {
                        @Override
                        public void Success() {
                            Toast.makeText(UserAccountActivity.this, R.string.change_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void UnSuccess() {

                            Toast.makeText(UserAccountActivity.this, R.string.change_unsuccess, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                if (!String.valueOf(newDay).equals(oldDay)){
                    firebaseManager.changeUserBirthDate("day", String.valueOf(newDay), new FirebaseManager.ChangeUserBirthDate() {
                        @Override
                        public void Success() {
                            Toast.makeText(UserAccountActivity.this, R.string.change_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void UnSuccess() {

                            Toast.makeText(UserAccountActivity.this, R.string.change_unsuccess, Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });

        confirmFNameIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

                confirmFNameIB.setVisibility(View.INVISIBLE);
                editFNameIB.setVisibility(View.VISIBLE);
                loggedInFNameET.setEnabled(false);

                if (!loggedInFNameET.getText().toString().trim().equals(oldFName) && !loggedInFNameET.getText().toString().trim().equals("")){
                    firebaseManager.changeName("family", loggedInFNameET.getText().toString().trim(), new FirebaseManager.ChangeName() {
                        @Override
                        public void Success() {
                            Toast.makeText(UserAccountActivity.this, R.string.change_success, Toast.LENGTH_LONG).show();
                            loggedInFNameET.setHint(loggedInFNameET.getText().toString().trim());
                            loggedInFNameET.setText("");
                        }

                        @Override
                        public void UnSuccess() {

                            Toast.makeText(UserAccountActivity.this, R.string.change_unsuccess, Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }
        });
        confirmGNameIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

                confirmGNameIB.setVisibility(View.INVISIBLE);
                editGNameIB.setVisibility(View.VISIBLE);
                loggedInGNameET.setEnabled(false);

                if (!loggedInGNameET.getText().toString().trim().equals(oldGName)&& !loggedInGNameET.getText().toString().trim().equals("")){
                    firebaseManager.changeName("given", loggedInGNameET.getText().toString().trim(), new FirebaseManager.ChangeName() {
                        @Override
                        public void Success() {
                            Toast.makeText(UserAccountActivity.this, R.string.change_success, Toast.LENGTH_LONG).show();
                            loggedInGNameET.setHint(loggedInGNameET.getText().toString().trim());
                            loggedInGNameET.setText("");
                        }

                        @Override
                        public void UnSuccess() {

                            Toast.makeText(UserAccountActivity.this, R.string.change_unsuccess, Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }
        });

        confirmUsernameIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

                confirmUsernameIB.setVisibility(View.INVISIBLE);
                editUsernameIB.setVisibility(View.VISIBLE);
                loggedInUsernameET.setEnabled(false);

                if (!loggedInUsernameET.getText().toString().trim().equals(oldUsername) && !loggedInUsernameET.getText().toString().trim().equals("")){
                    firebaseManager.changeUsername(loggedInUsernameET.getText().toString().trim(), new FirebaseManager.ChangeUsername() {
                        @Override
                        public void Success() {
                            loggedInUsernameET.setHint(loggedInUsernameET.getText().toString().trim());
                            loggedInUsernameET.setText("");
                        }

                        @Override
                        public void UnSuccess() {
                            Toast.makeText(UserAccountActivity.this, R.string.update_username_unsuccess,Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void Exists() {
                            Toast.makeText(UserAccountActivity.this, R.string.used_username,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void animateImageButton(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.image_button_click);
        view.startAnimation(animation);
    }
}