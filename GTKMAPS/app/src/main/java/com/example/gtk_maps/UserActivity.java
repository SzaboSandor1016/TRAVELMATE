package com.example.gtk_maps;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private static final int MY_SHARES_REQUEST_CODE = 1;
    private static final int SHARED_WITH_ME_REQUEST_CODE = 2;
    private static final int USER_ACCOUNT_REQUEST_CODE = 3;

    private ArrayList<Share> myShares, withMeShares;
    private ArrayList<Pair<String, Boolean>> recentUsernames;
    private ArrayList<Pair<String,Boolean>> selectedUsernames;
    private List<String> usernames;

    private Dialog resetDialog;
    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Resources resources;
    private boolean isLoggedIn, isContactsOut, isMySharesOut, isWithMeSharesOut;
    private EditText addresseeUsername, newShareTitle, shareNote;
    private TextInputEditText emailET,passwdET,fNameET,gNameET,
            registerEmailET, registerPasswdET, registerConfirmPasswdET, registerUsernameET;
    private TextView loggedInEmailTV,loggedInUsernameTV, sharesTV, accountTV;
    private Button forgotPassword,logInBTN,confirmRegisterBTN,registerBTN,cancelRegisterBTN, privacyBTN, shareShare,deleteAddressees, dismiss;

    private ImageButton ageIB, logOutBTN, addAddressee,goBack;
    private ImageView chevronContacts,chevronMy,chevronWithMe;
    private TextView selectedAgeTV, activityTitle;
    private LinearLayout loggedInLL, registerLL, logInLL, editAccount, contacts, contactsLayout, sharedWithMe, sharedWithMeLayout,
            sharedByMe,sharedByMeLayout,modifyShareLayout;

    private ArrayAdapter<String> userAccountAdapter, moreOptionsAdapter, sharesArrayAdapter;

    private RecyclerView sharedByMeRecyclerView,sharedWithMeRecyclerView, contactsRecyclerView,shareContactsRecyclerView;
    private ShareRecyclerViewAdapter sharedByMeAdapter, sharedWithMeAdapter;
    private ShareRecentAddresseeRecyclerViewAdapter addresseesAdapter;

    private SharedPreferences sharedPreferences;

    private String selectedYear= "", selectedMonth = "", selectedDayOfMonth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        resources= getResources();

        activityTitle = findViewById(R.id.activity_title);

        loggedInLL= findViewById(R.id.loggedInLL);
        logInLL = findViewById(R.id.logInLL);
        registerLL= findViewById(R.id.registerLL);

        goBack = findViewById(R.id.go_back);


        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences= getSharedPreferences("FirebaseManager", Context.MODE_PRIVATE);
        isLoggedIn=sharedPreferences.getBoolean("loggedIn", false);

        firebaseManager = FirebaseManager.getInstance(UserActivity.this,mAuth,mDatabase, sharedPreferences);

        if (mAuth.getCurrentUser()!=null){
            initAccount();

        }else{
            initLogIn();
        }


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);
                finish();
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

        sharesArrayAdapter = null;
        moreOptionsAdapter = null;
        userAccountAdapter = null;

    }

    private void initLogIn(){

        logInBTN= findViewById(R.id.logInBTN);
        registerBTN= findViewById(R.id.registerBTN);

        emailET= findViewById(R.id.emailET);
        passwdET= findViewById(R.id.passwdET);

        forgotPassword = findViewById(R.id.forgot_password);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetDialog = new Dialog(UserActivity.this,R.style.CustomDialogTheme);
                resetDialog.setContentView(R.layout.reset_password_dialog);

                resetDialog.setCancelable(true);
                EditText confirmResetEmailET = resetDialog.findViewById(R.id.reset_email);

                Button confirmResetBTN =resetDialog.findViewById(R.id.confirmResetBTN);
                confirmResetBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!confirmResetEmailET.getText().toString().trim().equals("")) {
                            firebaseManager.resetPassword(confirmResetEmailET.getText().toString().trim(), new FirebaseManager.ResetEmail() {
                                @Override
                                public void Success() {
                                    Toast.makeText(UserActivity.this, R.string.email_sent, Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void UnSuccess() {
                                    Toast.makeText(UserActivity.this, R.string.email_not_sent, Toast.LENGTH_LONG).show();
                                }
                            });
                            resetDialog.dismiss();
                        }
                    }
                });
                resetDialog.show();

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
                            initAccount();

                            //User loggedInUser = firebaseManager.getLoggedInUser();

                            //loggedInEmailTV.setText(loggedInUser.getUsername());
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

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateLinearLayouts(registerLL,logInLL);
                initRegister();
                //loggedInEmailTV.setText(sharedPreferences.getString("username",""));
            }
        });



        loggedInLL.setVisibility(View.GONE);
        logInLL.setVisibility(View.VISIBLE);
        registerLL.setVisibility(View.GONE);
        activityTitle.setText(R.string.login);
        //clearEditTexts();
    }
    private void initRegister(){

        registerPasswdET = findViewById(R.id.registerPasswdET);
        registerConfirmPasswdET = findViewById(R.id.registerConfirmPasswdET);
        registerEmailET = findViewById(R.id.registerEmailET);
        registerUsernameET = findViewById(R.id.registerUsernameET);
        fNameET= findViewById(R.id.fNameET);
        gNameET= findViewById(R.id.gNameET);
        selectedAgeTV = findViewById(R.id.selectedAgeTV);
        confirmRegisterBTN= findViewById(R.id.confirmRegisterBTN);
        cancelRegisterBTN= findViewById(R.id.cancelRegisterBTN);
        ageIB= findViewById(R.id.ageIB);
        privacyBTN = findViewById(R.id.privacyBTN);


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
                                        initAccount();

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
                initLogIn();
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


        loggedInLL.setVisibility(View.GONE);
        logInLL.setVisibility(View.GONE);
        registerLL.setVisibility(View.VISIBLE);
        activityTitle.setText(R.string.register);
        //clearEditTexts();
    }
    private void initAccount(){
        loggedInEmailTV= findViewById(R.id.loggedInEmailTV);
        loggedInUsernameTV = findViewById(R.id.loggedInUsernameTV);

        editAccount = findViewById(R.id.edit_account);
        contacts = findViewById(R.id.contacts);
        contactsLayout = findViewById(R.id.contacts_layout);
        sharedWithMe = findViewById(R.id.shared_with_me);
        sharedWithMeLayout = findViewById(R.id.shared_with_me_layout);
        sharedByMe = findViewById(R.id.shared_by_me);
        sharedByMeLayout = findViewById(R.id.shared_by_me_layout);

        sharedByMeRecyclerView = findViewById(R.id.shared_by_me_recycler_view);
        sharedWithMeRecyclerView = findViewById(R.id.shared_with_me_recycler_view);
        contactsRecyclerView = findViewById(R.id.contacts_recycler_view);

        chevronContacts = findViewById(R.id.chevron_contacts);
        chevronMy = findViewById(R.id.chevron_my);
        chevronWithMe = findViewById(R.id.chevron_with_me);

        deleteAddressees = findViewById(R.id.delete_addressees);

        logOutBTN= findViewById(R.id.log_out);

        loggedInUsernameTV.setText(sharedPreferences.getString("username",""));
        loggedInEmailTV.setText(mAuth.getCurrentUser().getEmail());


        myShares = new ArrayList<>();
        withMeShares = new ArrayList<>();
        recentUsernames = new ArrayList<>();
        usernames = new ArrayList<>();
        selectedUsernames = new ArrayList<>();

        isContactsOut = false;
        isMySharesOut = false;
        isWithMeSharesOut = false;

        sharedByMeAdapter = new ShareRecyclerViewAdapter(UserActivity.this, myShares, resources, true, new ShareRecyclerViewAdapter.DeleteShare() {
            @Override
            public void deleteShare(Share toBeDeleted) {
                firebaseManager.removeShare(toBeDeleted.getReferenceToShare(), new FirebaseManager.RemoveShare() {
                    @Override
                    public void Executed() {
                        int index = myShares.indexOf(toBeDeleted);
                        myShares.remove(index);
                        sharedByMeAdapter.notifyItemRemoved(index);
                    }
                });
            }
        }, new ShareRecyclerViewAdapter.ModifyShare() {
            @Override
            public void modifyShare(Share toBeModified) {

                int position = myShares.indexOf(toBeModified);


                modifyShareLayout = findViewById(R.id.modify_share_layout);
                shareShare = findViewById(R.id.share_share_user);
                addresseeUsername = findViewById(R.id.addressee_username_user);
                addAddressee = findViewById(R.id.add_addressee_user);
                newShareTitle = findViewById(R.id.new_share_title_user);
                shareNote = findViewById(R.id.share_note_user);
                dismiss = findViewById(R.id.dismiss_user);


                shareContactsRecyclerView = findViewById(R.id.share_contacts_recycler_view_user);
                shareContactsRecyclerView.setLayoutManager(new LinearLayoutManager(UserActivity.this));

                ArrayList<String> sharedUsernames = new ArrayList<>();
                if (toBeModified.getSharedWithStrings()!=null){
                    sharedUsernames.addAll(toBeModified.getSharedWithStrings());
                }

                firebaseManager.getRecentSharedWithUsernames(new FirebaseManager.RecentUsernames() {
                    @Override
                    public void onSuccess(ArrayList<String> usernames) {
                        ArrayList<Pair<String, Boolean>> usernamePairs = new ArrayList<>();
                        for (String username : usernames) {
                            Pair pair;
                            if (sharedUsernames.contains(username)) {
                                pair = new Pair(username, true);
                            } else {
                                pair = new Pair<>(username, false);
                            }
                            usernamePairs.add(pair);
                        }
                        ShareRecentAddresseeRecyclerViewAdapter addresseeRecyclerViewAdapter = new ShareRecentAddresseeRecyclerViewAdapter(UserActivity.this, usernamePairs, new ShareRecentAddresseeRecyclerViewAdapter.SelectAddresseeItem() {
                            @Override
                            public void select(Pair<String,Boolean> pair) {
                                sharedUsernames.add(pair.first);
                            }

                            @Override
                            public void unselect(Pair<String,Boolean> pair) {
                                sharedUsernames.remove(pair.first);
                            }
                        }, resources);

                        shareContactsRecyclerView.setAdapter(addresseeRecyclerViewAdapter);

                        addAddressee.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                animateImageButton(v);
                                if (!sharedUsernames.contains(addresseeUsername.getText().toString().trim())) {
                                    if (!addresseeUsername.getText().toString().trim().equals("")) {
                                        usernamePairs.add(new Pair<>(addresseeUsername.getText().toString().trim(), true));
                                        sharedUsernames.add(addresseeUsername.getText().toString().trim());
                                        addresseeRecyclerViewAdapter.notifyItemInserted(usernamePairs.size() - 1);


                                        addresseeUsername.setText("");
                                    } else {
                                        Toast.makeText(UserActivity.this, R.string.empty_addressee, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });

                        shareShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (sharedUsernames.size() != 0) {
                                    for (String username : sharedUsernames) {
                                        if (!usernames.contains(username)) {
                                            usernames.add(username);
                                        }
                                    }

                                    firebaseManager.addRecentlySharedWithUsername(usernames);

                                    String title = newShareTitle.getText().toString().trim();
                                    String note = shareNote.getText().toString().trim();

                                    firebaseManager.updateShare( title, note, sharedUsernames, toBeModified.getReferenceToShare(), new FirebaseManager.UpdateShare() {
                                        @Override
                                        public void Executed() {
                                            Toast.makeText(UserActivity.this, R.string.update_share_success, Toast.LENGTH_LONG).show();

                                            modifyShareLayout.animate().translationY(editAccount.getHeight() + contacts.getHeight() + sharedWithMe.getHeight() - modifyShareLayout.getHeight()).setDuration(300).start();

                                            if (!title.equals("")) {
                                                toBeModified.setTitle(title);
                                            }
                                            if(!note.equals("")) {
                                                toBeModified.setNote(note);
                                            }
                                            toBeModified.setSharedWithStrings(sharedUsernames);

                                            sharedByMeAdapter.notifyItemChanged(position);



                                            shareShare.setOnClickListener(null);
                                            dismiss.setOnClickListener(null);

                                            shareContactsRecyclerView = null;
                                            modifyShareLayout = null;
                                            shareShare = null;
                                            addresseeUsername = null;
                                            addAddressee = null;
                                            newShareTitle = null;
                                            shareNote = null;
                                            dismiss = null;
                                        }
                                    });
                                } else {
                                    Toast.makeText(UserActivity.this, R.string.empty_addressee_list, Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                modifyShareLayout.animate().translationY(editAccount.getHeight() + contacts.getHeight() + sharedWithMe.getHeight() - modifyShareLayout.getHeight()).setDuration(300).start();

                                shareShare.setOnClickListener(null);
                                dismiss.setOnClickListener(null);

                                shareContactsRecyclerView = null;
                                modifyShareLayout = null;
                                shareShare = null;
                                addresseeUsername = null;
                                addAddressee = null;
                                newShareTitle = null;
                                shareNote = null;
                                dismiss = null;
                            }
                        });
                    }

                    @Override
                    public void onFailure() {

                    }
                });

                modifyShareLayout.animate().translationY(editAccount.getHeight() + contacts.getHeight() + sharedWithMe.getHeight()).setDuration(300).start();

            }
        }, new ShareRecyclerViewAdapter.SelectShare() {
            @Override
            public void select(Share share) {
                Intent intent = new Intent();
                intent.putExtra("result", share);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        sharedWithMeAdapter = new ShareRecyclerViewAdapter(UserActivity.this, withMeShares, resources, false, new ShareRecyclerViewAdapter.DeleteShare() {
            @Override
            public void deleteShare(Share toBeDeleted) {

                DatabaseReference referenceToBeDeleted = toBeDeleted.getReferenceToShare().child("sharedWith").child(toBeDeleted.getOp());
                firebaseManager.removeShare(referenceToBeDeleted, new FirebaseManager.RemoveShare() {
                    @Override
                    public void Executed() {
                        int index = withMeShares.indexOf(toBeDeleted);
                        withMeShares.remove(index);
                        sharedWithMeAdapter.notifyItemRemoved(index);
                    }
                });

            }
        }, null, new ShareRecyclerViewAdapter.SelectShare() {
            @Override
            public void select(Share share) {
                Intent intent = new Intent();
                intent.putExtra("result",share);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        addresseesAdapter = new ShareRecentAddresseeRecyclerViewAdapter(UserActivity.this, recentUsernames, new ShareRecentAddresseeRecyclerViewAdapter.SelectAddresseeItem() {
            @Override
            public void select(Pair<String,Boolean> pair) {
                selectedUsernames.add(pair);
            }

            @Override
            public void unselect(Pair<String,Boolean> pair) {
                selectedUsernames.remove(pair);
            }
        },resources);

        sharedByMeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sharedWithMeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedByMeRecyclerView.setAdapter(sharedByMeAdapter);
        sharedWithMeRecyclerView.setAdapter(sharedWithMeAdapter);
        contactsRecyclerView.setAdapter(addresseesAdapter);

        getDatabaseData(false);
        getDatabaseData(true);


        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isContactsOut){
                    chevronContacts.animate().rotation(90).setDuration(100).start();
                    contactsLayout.animate().translationY(editAccount.getHeight()).setDuration(300).start();

                    firebaseManager.getRecentSharedWithUsernames(new FirebaseManager.RecentUsernames() {
                        @Override
                        public void onSuccess(ArrayList<String> usernames) {
                            recentUsernames.clear();

                            ArrayList<Pair<String,Boolean>> usernamePairs = new ArrayList<>();
                            for(String username: usernames){
                                Pair pair = new Pair(username,false);
                                usernamePairs.add(pair);
                            }

                            recentUsernames.addAll(usernamePairs);

                            addresseesAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure() {

                        }
                    });

                    deleteAddressees.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<String> temp = new ArrayList<>();

                            for (Pair<String,Boolean> username : recentUsernames){
                                if (selectedUsernames.contains(username)){
                                    int index = recentUsernames.indexOf(username);
                                    recentUsernames.remove(index);
                                    addresseesAdapter.notifyItemRemoved(index);

                                }else{
                                    temp.add(username.first);
                                }
                            }

                            firebaseManager.deleteRecentSharedWithUsernames(temp);
                        }
                    });

                }else{
                    chevronContacts.animate().rotation(180).setDuration(100).start();
                    contactsLayout.animate().translationY(-contactsLayout.getHeight()+ contacts.getHeight()+ editAccount.getHeight()).setDuration(300).start();
                }
                isContactsOut = !isContactsOut;
            }
        });

        sharedWithMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWithMeSharesOut){
                    chevronWithMe.animate().rotation(90).setDuration(100).start();

                    sharedWithMeLayout.animate().translationY(editAccount.getHeight()+ contacts.getHeight()).setDuration(300).start();
                }else{
                    chevronWithMe.animate().rotation(180).setDuration(100).start();
                    sharedWithMeLayout.animate().translationY(-sharedWithMeLayout.getHeight()+ editAccount.getHeight()+ contacts.getHeight() + sharedWithMe.getHeight()).setDuration(300).start();
                }
                isWithMeSharesOut = !isWithMeSharesOut;
            }
        });
        sharedByMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMySharesOut){
                    chevronMy.animate().rotation(90).setDuration(100).start();

                    sharedByMeLayout.animate().translationY(editAccount.getHeight()+ contacts.getHeight() + sharedWithMe.getHeight()).setDuration(300).start();
                }else{
                    chevronMy.animate().rotation(180).setDuration(100).start();
                    sharedByMeLayout.animate().translationY(-sharedByMeLayout.getHeight()+ editAccount.getHeight()+ contacts.getHeight() + sharedWithMe.getHeight() + sharedByMe.getHeight()).setDuration(300).start();
                }
                isMySharesOut = !isMySharesOut;
            }
        });

        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this,UserAccountActivity.class);
                startActivityForResult(intent, USER_ACCOUNT_REQUEST_CODE);
            }
        });

        logOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateLinearLayouts(logInLL,loggedInLL);
                logOutBTN.setVisibility(View.INVISIBLE);
                initLogIn();
                firebaseManager.signOut();

            }
        });



        loggedInLL.setVisibility(View.VISIBLE);
        logInLL.setVisibility(View.GONE);
        registerLL.setVisibility(View.GONE);

        logOutBTN.setVisibility(View.VISIBLE);
        activityTitle.setText(R.string.account);
        //clearEditTexts();
    }

    private void resetLogIn(){

        logInBTN.setOnClickListener(null);
        registerBTN.setOnClickListener(null);

        logInBTN= null;
        registerBTN= null;

        emailET= null;
        passwdET = null;


    }
    private void resetRegister(){

        confirmRegisterBTN.setOnClickListener(null);
        cancelRegisterBTN.setOnClickListener(null);
        privacyBTN.setOnClickListener(null);
        ageIB.setOnClickListener(null);

        registerPasswdET = null;
        registerConfirmPasswdET = null;
        registerEmailET = null;
        registerUsernameET = null;
        fNameET= null;
        gNameET= null;
        selectedAgeTV = null;
        confirmRegisterBTN= null;
        cancelRegisterBTN= null;
        ageIB= null;
        privacyBTN = null;

    }
    private void resetAccount(){

        sharedByMeAdapter = null;
        sharedWithMeAdapter = null;
        addresseesAdapter = null;

        contacts.setOnClickListener(null);
        sharedWithMe.setOnClickListener(null);
        sharedByMe.setOnClickListener(null);
        editAccount.setOnClickListener(null);
        logOutBTN.setOnClickListener(null);

        deleteAddressees.setOnClickListener(null);

        loggedInEmailTV = null;
        loggedInUsernameTV = null;

        editAccount = null;
        contacts = null;
        contactsLayout = null;
        sharedWithMe = null;
        sharedWithMeLayout = null;
        sharedByMe = null;
        sharedByMeLayout = null;

        sharedByMeRecyclerView = null;
        sharedWithMeRecyclerView = null;
        contactsRecyclerView = null;

        deleteAddressees = null;

        logOutBTN = null;

        myShares = null;
        withMeShares = null;
        recentUsernames = null;
        usernames = null;

        isContactsOut = false;
        isMySharesOut = false;
        isWithMeSharesOut = false;


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_ACCOUNT_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                if (data!= null) {
                    if (data.getBooleanExtra("delete", false)) {
                            initLogIn();
                            logOutBTN.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
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

    private void getDatabaseData( boolean isMyShares){

        clearAll();

        FirebaseUser user = mAuth.getCurrentUser();
        String Uid= user.getUid();

        DatabaseReference databaseRef = mDatabase.child("shared");

        Query query;
        // A query létrehozása az 'op' mező alapján
        if (isMyShares) {
            query = databaseRef.orderByChild("op").equalTo(Uid);
            myShares.clear();
        }
        else {
            withMeShares.clear();
            query = databaseRef.orderByChild("sharedWith/"+ Uid).equalTo(true);

        }

        // A lekérdezés végrehajtása
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child: dataSnapshot.getChildren()){
                    DatabaseReference databaseRef = mDatabase.child("username_to_uid");
                    DatabaseReference referenceToShare = child.getRef();
                    Share share = child.getValue(Share.class);
                    share.setReferenceToShare(referenceToShare);
                    ArrayList<Query> queries = new ArrayList<>();
                    ArrayList<DataSnapshot> result = new ArrayList<>();
                    if (isMyShares) {
                        if (share.getSharedWith() != null) {
                            for (String uid : share.getSharedWith().keySet()) {
                                Query usernameQuery = databaseRef.orderByKey().equalTo(uid);
                                queries.add(usernameQuery);
                            }
                            firebaseManager.executeQueries(queries, result, 0, new FirebaseManager.ExecuteQueries() {
                                @Override
                                public void Executed(ArrayList<DataSnapshot> result) {
                                    if (result != null) {
                                        ArrayList<String> sharedWithStrings = new ArrayList<>();
                                        for (DataSnapshot sharedUsername : result) {
                                            //Log.d("databaseUsername", sharedUsername.getValue().toString());

                                            sharedWithStrings.add(sharedUsername.getValue().toString());

                                        }
                                        share.setSharedWithStrings(sharedWithStrings);

                                        //Log.d("databaseWithFinal",share.getSharedWithStrings().toString());

                                        myShares.add(share);

                                        sharedByMeAdapter.notifyItemInserted(myShares.size() - 1);
                                    }
                                }
                            });
                        }else{
                            myShares.add(share);

                            sharedByMeAdapter.notifyItemInserted(myShares.size() - 1);
                        }

                    }else {

                        DataSnapshot sharedBy = child.child("op");

                        Query sharedWithUsername = mDatabase.child("username_to_uid").orderByKey().equalTo(sharedBy.getValue().toString());
                        queries.add(sharedWithUsername);

                        firebaseManager.executeQueries(queries, result, 0, new FirebaseManager.ExecuteQueries() {
                            @Override
                            public void Executed(ArrayList<DataSnapshot> result) {

                                //Log.d("databaseOp", result.get(0).getValue().toString());

                                String opActual = result.get(0).getValue().toString();

                                share.setOpActual(opActual);

                                //Log.d("databaseOpFinal", share.getOpActual());

                                withMeShares.add(share);

                                sharedWithMeAdapter.notifyItemInserted(withMeShares.size()-1);
                            }
                        });
                    }
                }

                //TODO do it some other way

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hibakezelés
                Log.i("databaseError", databaseError.toString());
            }
        });
    }

}