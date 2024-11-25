package com.example.gtk_maps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
    private Dialog deleteDialog;
    private Window deleteDialogWindow;
    private Button saveChanges;
    private TextInputEditText loggedInFNameET,loggedInGNameET, loggedInUsernameET, accountPassword, accountPasswordAgain, accountPasswordOld;
    private TextInputLayout accountPasswordAgainLayout, accountPasswordOldLayout;
    private ImageButton loggedInAgeIB, goBack, deleteAccount;
    private TextView loggedInSelectedAgeTV;
    private SharedPreferences sharedPreferences;
    private String oldGName, oldFName, selectedYear, selectedMonth, selectedDay, oldUsername, oldYear,oldMonth,oldDay, newUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        loggedInUsernameET = findViewById(R.id.loggedInUsernameET);
        loggedInFNameET= findViewById(R.id.loggedInFNameET);
        loggedInGNameET= findViewById(R.id.loggedInGNameET);

        loggedInAgeIB = findViewById(R.id.loggedInAgeIB);

        accountPassword = findViewById(R.id.account_password);
        accountPasswordAgain = findViewById(R.id.account_password_again);
        accountPasswordOld = findViewById(R.id.account_password_old);
        accountPasswordAgainLayout = findViewById(R.id.account_password_again_layout);
        accountPasswordOldLayout = findViewById(R.id.account_password_old_layout);

        loggedInSelectedAgeTV= findViewById(R.id.loggedInSelectedAgeTV);

        saveChanges = findViewById(R.id.save_changes);
        goBack = findViewById(R.id.go_back_account);
        deleteAccount = findViewById(R.id.delete_account);

        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences= getSharedPreferences("FirebaseManager", Context.MODE_PRIVATE);

        firebaseManager = FirebaseManager.getInstance(UserAccountActivity.this,mAuth,mDatabase, sharedPreferences);

        loggedInUsernameET.setText(sharedPreferences.getString("username", ""));
        loggedInFNameET.setText(sharedPreferences.getString("family", ""));
        loggedInGNameET.setText(sharedPreferences.getString("given", ""));
        String birthDate= sharedPreferences.getString("birthDateYear","") + "/" +
                sharedPreferences.getString("birthDateMonth","")+"/"+ sharedPreferences.getString("birthDateDay","");
        loggedInSelectedAgeTV.setText(birthDate);

        oldUsername = sharedPreferences.getString("username", "");
        oldFName = sharedPreferences.getString("family", "");
        oldGName = sharedPreferences.getString("given", "");

        oldYear = sharedPreferences.getString("birthDateYear","");
        oldMonth = sharedPreferences.getString("birthDateMonth","");
        oldDay = sharedPreferences.getString("birthDateDay","");


        loggedInAgeIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

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

                                loggedInSelectedAgeTV.setText(selectedDate);

                                selectedYear= String.valueOf(year);

                                selectedMonth= String.valueOf(month+1);

                                selectedMonth=String.valueOf(dayOfMonth);

                            }
                        },
                        year, month, dayOfMonth);

                // Show dialog
                datePickerDialog.show();
                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                });

            }
        });

        loggedInUsernameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // The EditText has gained focus, meaning the user is starting to edit
                    oldUsername = loggedInUsernameET.getText().toString().trim();
                }
            }
        });

        loggedInFNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // The EditText has gained focus, meaning the user is starting to edit
                    oldFName = loggedInFNameET.getText().toString().trim();
                }
            }
        });

        loggedInGNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // The EditText has gained focus, meaning the user is starting to edit
                    oldGName = loggedInGNameET.getText().toString().trim();
                }
            }
        });

        accountPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && !accountPassword.getText().toString().trim().equals("")){

                    Toast.makeText(UserAccountActivity.this, R.string.confirm_password_change_message,Toast.LENGTH_LONG).show();
                    accountPasswordAgainLayout.setVisibility(View.VISIBLE);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    v.clearFocus();

                    return true;
                }

                return false;
            }
        });


        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!loggedInUsernameET.getText().toString().trim().equals("") && !loggedInUsernameET.getText().toString().trim().equals(oldUsername)){
                    firebaseManager.changeUsername(loggedInUsernameET.getText().toString().trim(), new FirebaseManager.ChangeUsername() {
                        @Override
                        public void Success() {
                            loggedInUsernameET.setText(loggedInUsernameET.getText().toString().trim());
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
                if (!loggedInFNameET.getText().toString().trim().equals("") && !loggedInFNameET.getText().toString().trim().equals(oldFName)){
                    firebaseManager.changeName("family", loggedInFNameET.getText().toString().trim(), new FirebaseManager.ChangeName() {
                        @Override
                        public void Success() {
                            Toast.makeText(UserAccountActivity.this, R.string.change_success, Toast.LENGTH_LONG).show();
                            loggedInFNameET.setText(loggedInFNameET.getText().toString().trim());
                        }

                        @Override
                        public void UnSuccess() {

                            Toast.makeText(UserAccountActivity.this, R.string.change_unsuccess, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                if (!loggedInGNameET.getText().toString().trim().equals("") && !loggedInGNameET.getText().toString().trim().equals(oldGName)){
                    firebaseManager.changeName("given", loggedInGNameET.getText().toString().trim(), new FirebaseManager.ChangeName() {
                        @Override
                        public void Success() {
                            Toast.makeText(UserAccountActivity.this, R.string.change_success, Toast.LENGTH_LONG).show();
                            loggedInGNameET.setText(loggedInFNameET.getText().toString().trim());
                        }

                        @Override
                        public void UnSuccess() {

                            Toast.makeText(UserAccountActivity.this, R.string.change_unsuccess, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                if (!accountPassword.getText().toString().trim().equals("") && !accountPasswordOld.getText().toString().trim().equals("")){

                    if (accountPassword.getText().toString().trim().equals(accountPasswordAgain.getText().toString().trim())){
                        firebaseManager.changePassword(accountPasswordOld.getText().toString().trim(), accountPassword.getText().toString().trim(), new FirebaseManager.ChangePassword() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(UserAccountActivity.this, R.string.change_successful, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(UserAccountActivity.this, R.string.change_unsuccessful, Toast.LENGTH_LONG).show();
                            }

                        });
                    }
                }

            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);
                finish();
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);
                deleteDialog = new Dialog(UserAccountActivity.this,R.style.CustomDialogTheme);
                deleteDialog.setContentView(R.layout.delete_account_dialog);

                deleteDialog.setCancelable(true);
                EditText confirmPasswordDeleteET = deleteDialog.findViewById(R.id.delete_password);

                Button confirmDeleteBTN =deleteDialog.findViewById(R.id.confirmDeleteBTN);
                confirmDeleteBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!confirmPasswordDeleteET.getText().toString().trim().equals("")) {
                            firebaseManager.deleteUser(confirmPasswordDeleteET.getText().toString().trim(),
                                    new FirebaseManager.DeleteUser() {
                                        @Override
                                        public void onSuccess() {
                                            Intent intent = new Intent();
                                            intent.putExtra("delete", true);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }

                                        @Override
                                        public void onFailure() {
                                            Toast.makeText(UserAccountActivity.this,R.string.delete_unsuccessful,Toast.LENGTH_LONG).show();
                                        }
                                    });
                            deleteDialog.dismiss();
                        }
                    }
                });
                deleteDialog.show();

            }
        });

        /*confirmAgeIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

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
        });*/

        /*confirmFNameIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

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
        });*/
        /*confirmGNameIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

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
        });*/

        /*confirmUsernameIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);

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
        });*/
    }

    private void animateImageButton(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.image_button_click);
        view.startAnimation(animation);
    }
}