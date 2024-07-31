package com.example.gtk_maps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SharedByUserActivity extends AppCompatActivity {

    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ArrayList<String> titles;
    private ArrayList<Object> charges;
    private ArrayList<Object> names;
    private ArrayList<Object> coordinates;
    private ArrayList<Object> cuisine;
    private ArrayList<Object> openingHours;
    private ArrayList<Object> categories;
    private ArrayList<Object> labels;
    private ArrayList<Object> usernames;
    private ArrayList<DatabaseReference> references;

    private ListView mySharesLV;
    private Button selectDD, deleteDD;
    private TextView placeDD, transportDD,distanceDD, categoriesDD, categoriesListDD, dateDD;
    private ArrayAdapter<String> mySharesArrayAdapter;
    private SharedPreferences sharedPreferences;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_by_user);
        mySharesLV = findViewById(R.id.mySharesLV);
        resources = getResources();

        titles = new ArrayList<>();
        charges = new ArrayList<>();
        names = new ArrayList<>();
        coordinates = new ArrayList<>();
        cuisine = new ArrayList<>();
        openingHours = new ArrayList<>();
        categories = new ArrayList<>();
        labels = new ArrayList<>();
        usernames = new ArrayList<>();
        references = new ArrayList<>();

        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences= getSharedPreferences("FirebaseManager", Context.MODE_PRIVATE);

        firebaseManager= FirebaseManager.getInstance(SharedByUserActivity.this,mAuth,mDatabase, sharedPreferences);

        mySharesArrayAdapter = new SharedByArrayAdapter(SharedByUserActivity.this, R.layout.shared_by_list_item_layout, titles);
        mySharesLV.setAdapter(mySharesArrayAdapter);

        getDatabaseData();

        mySharesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String,ArrayList<String>> sharedMap= new HashMap<>();
                sharedMap.put("coordinates",(ArrayList<String>) coordinates.get(position));
                sharedMap.put("categories", (ArrayList<String>) categories.get(position));
                sharedMap.put("placeNames",(ArrayList<String>) names.get(position));
                sharedMap.put("cuisines", (ArrayList<String>) cuisine.get(position));
                sharedMap.put("openingHours", (ArrayList<String>) openingHours.get(position));
                sharedMap.put("charges", (ArrayList<String>) charges.get(position));

                Map<String, Object> labelDetails = new HashMap<>((Map) labels.get(position));

                Dialog detailsDialog = new Dialog(SharedByUserActivity.this,R.style.CustomDialogTheme);
                detailsDialog.setContentView(R.layout.my_shared_details_dialog);

                Window detailsDialogWindow = detailsDialog.getWindow();
                if (detailsDialogWindow!= null) {
                    detailsDialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    /*window.setBackgroundDrawableResource(R.drawable.fade);*/
                    detailsDialogWindow.setWindowAnimations(R.style.DialogAnimation);

                    WindowManager.LayoutParams layoutParams = detailsDialogWindow.getAttributes();
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Match parent width
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Wrap content height
                    layoutParams.gravity = Gravity.TOP; // Position at the bottom of the screen
                    detailsDialogWindow.setAttributes(layoutParams);
                }

                detailsDialog.setCancelable(true);
                placeDD = detailsDialog.findViewById(R.id.placeMSDD);
                transportDD = detailsDialog.findViewById(R.id.transportMSDD);
                distanceDD = detailsDialog.findViewById(R.id.distanceMSDD);
                categoriesDD = detailsDialog.findViewById(R.id.categoriesMSDD);
                categoriesListDD= detailsDialog.findViewById(R.id.categoriesListMSDD);
                dateDD = detailsDialog.findViewById(R.id.dateMSDD);


                placeDD.setText(labelDetails.get("place").toString());
                String transportMode = resources.getString(R.string.transporte_form)+ " " +labelDetails.get("transportMode").toString();
                String distance = resources.getString(R.string.distance)+ " " +labelDetails.get("distance").toString();
                transportDD.setText(transportMode);
                distanceDD.setText(distance);
                categoriesDD.setText(resources.getString(R.string.categories));


                ArrayList<String> labelCategories = new ArrayList<>((Collection<? extends String>) labelDetails.get("categories"));

                StringBuilder labelBuilder = new StringBuilder();
                for (String labelCategory: labelCategories){
                    labelBuilder.append(labelCategory).append("\n");
                }

                categoriesListDD.setText(labelBuilder.toString());
                dateDD.setText(labelDetails.get("date").toString());

                selectDD = detailsDialog.findViewById(R.id.selectMSDD);
                deleteDD = detailsDialog.findViewById(R.id.deleteMSDD);
                Button shareDD = detailsDialog.findViewById(R.id.shareMSDD);

                selectDD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //TODO Return the labelDetails too without "date"
                        Map<String, Object> returnLabel = new HashMap<>( labelDetails);
                        returnLabel.remove("date");
                        returnLabel.remove("categories");

                        Log.d("returnLabel", returnLabel.toString());
                        Intent intent = new Intent();
                        intent.putExtra("label",(Serializable) returnLabel);
                        intent.putExtra("sharedByMe", (Serializable) sharedMap);
                        setResult(RESULT_OK, intent);
                        finish();
                        detailsDialog.dismiss();
                    }
                });
                deleteDD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SharedByUserActivity.this);
                        builder.setMessage(R.string.delete_share_message).setCancelable(true).setPositiveButton(
                                R.string.positive,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                                /*runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        firebaseManager.(labels.get(position));
                                                    }
                                                });*/
                                        firebaseManager.removeShare(references.get(position), new FirebaseManager.RemoveShare() {
                                            @Override
                                            public void Executed() {
                                                getDatabaseData();
                                            }
                                        });

                                    }
                                }).setNegativeButton(
                                R.string.negative,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }).show();
                        detailsDialog.dismiss();
                    }
                });

                shareDD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Dialog updateSearchDialog = new Dialog(SharedByUserActivity.this,R.style.CustomDialogTheme);
                        updateSearchDialog.setContentView(R.layout.update_search_dialog);

                        Window updateSearchDialogWindow = updateSearchDialog.getWindow();
                        if (updateSearchDialogWindow!=null) {
                            updateSearchDialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            /*window.setBackgroundDrawableResource(R.drawable.fade);*/
                            updateSearchDialogWindow.setWindowAnimations(R.style.DialogAnimation);

                            WindowManager.LayoutParams layoutParams = updateSearchDialogWindow.getAttributes();
                            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Match parent width
                            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Wrap content height
                            layoutParams.gravity = Gravity.TOP; // Position at the bottom of the screen
                            updateSearchDialogWindow.setAttributes(layoutParams);
                        }

                        updateSearchDialog.setCancelable(true);

                        TextView titleUSD = updateSearchDialog.findViewById(R.id.titleUSD);
                        /*TextView addedEmailSSD = dialog.findViewById(R.id.addedEmailsSSD);*/
                        EditText withUSD = updateSearchDialog.findViewById(R.id.withUSD);
                        EditText nameUSD = updateSearchDialog.findViewById(R.id.nameUSD);

                        ListView addedEmailsUSD = updateSearchDialog.findViewById(R.id.addedEmailsUSD);
                        ListView contactsListUSD = updateSearchDialog.findViewById(R.id.contactListUSD);

                        titleUSD.setText(resources.getString(R.string.share));

                        //StringBuilder stringBuilder = new StringBuilder();
                        ArrayList<String> sharedUsernames = new ArrayList<>((Collection) usernames.get(position));
                        ArrayList<String> recentUsernames = new ArrayList<>();

                        ArrayAdapter withEmailsArrayAdapter = new ArrayAdapter(updateSearchDialog.getContext(),android.R.layout.simple_list_item_1,sharedUsernames);
                        addedEmailsUSD.setAdapter(withEmailsArrayAdapter);

                        ArrayAdapter contactsArrayAdapter = new ArrayAdapter<>(updateSearchDialog.getContext(), android.R.layout.simple_list_item_1, recentUsernames);
                        contactsListUSD.setAdapter(contactsArrayAdapter);

                        addedEmailsUSD.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                                withEmailsArrayAdapter.remove(sharedUsernames.get(position));
                                withEmailsArrayAdapter.notifyDataSetChanged();
                                return true;
                            }
                        });

                        contactsListUSD.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (!sharedUsernames.contains((String) recentUsernames.get(position))) {
                                    sharedUsernames.add((String) recentUsernames.get(position));
                                    withEmailsArrayAdapter.notifyDataSetChanged();
                                }
                            }
                        });

                        firebaseManager.getRecentSharedWithUsernames(new FirebaseManager.RecentUsernames() {
                            @Override
                            public void onSuccess(ArrayList<String> usernames) {
                                recentUsernames.addAll(usernames);

                                contactsArrayAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure() {

                            }
                        });

                        ImageButton addMoreUSD = updateSearchDialog.findViewById(R.id.addMoreUSD);
                        ImageButton contactsUSD = updateSearchDialog.findViewById(R.id.contactsUSD);


                        contactsUSD.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                animateImageButton(v);
                                if (contactsListUSD.getVisibility()== View.GONE){
                                    contactsListUSD.setVisibility(View.VISIBLE);
                                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                                    contactsListUSD.startAnimation(animation);

                                }else {
                                    contactsListUSD.setVisibility(View.GONE);
                                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_slide_down);
                                    contactsListUSD.startAnimation(animation);
                                }
                            }
                        });

                        addMoreUSD.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                animateImageButton(v);
                                if (!sharedUsernames.contains(withUSD.getText().toString().trim())) {

                                    if (!withUSD.getText().toString().trim().equals("")) {

                                        sharedUsernames.add(withUSD.getText().toString().trim());

                                        withEmailsArrayAdapter.notifyDataSetChanged();


                                        withUSD.setText("");
                                    }else {
                                        Toast.makeText(SharedByUserActivity.this,R.string.empty_addressee,Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });


                        Button saveUSD =updateSearchDialog.findViewById(R.id.saveUSD);
                        saveUSD.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (sharedUsernames.size()!=0) {
                                    //if (!nameSSD.getText().toString().trim().equals("")) {

                                    for (String username : sharedUsernames) {
                                        if (!recentUsernames.contains(username)) {
                                            recentUsernames.add(username);
                                        }
                                    }

                                    firebaseManager.addRecentlySharedWithUsername(recentUsernames);

                                    DatabaseReference positionReference = references.get(position);

                                    firebaseManager.updateSharedWith(nameUSD.getText().toString().trim(),sharedUsernames, positionReference, new FirebaseManager.UpdateShare() {
                                        @Override
                                        public void Executed() {
                                            updateSearchDialog.dismiss();

                                            getDatabaseData();

                                        }
                                    });


                                }else {
                                    Toast.makeText(SharedByUserActivity.this,R.string.empty_addressee_list,Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        updateSearchDialog.show();
                    }
                });

                detailsDialog.show();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearAll();
        resources.flushLayoutCache();
    }

    private void getDatabaseData(){

        clearAll();

        FirebaseUser user = mAuth.getCurrentUser();
        String Uid= user.getUid();

        DatabaseReference databaseRef = mDatabase.child("shared");


        // A query létrehozása az 'op' mező alapján
        Query query = databaseRef.orderByChild("op").equalTo(Uid);

        // A lekérdezés végrehajtása
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    ArrayList<Query> queries = new ArrayList<>();
                    ArrayList<DataSnapshot> result = new ArrayList<>();
                    DataSnapshot sharedWith = child.child("shared_with");

                    for (DataSnapshot uid: sharedWith.getChildren()){
                        Log.i("databaseUid", uid.getKey());
                        Query sharedWithUsername = mDatabase.child("username_to_uid").orderByKey().equalTo(uid.getKey());
                        queries.add(sharedWithUsername);
                    }

                    firebaseManager.executeQueries(queries, result, 0, new FirebaseManager.ExecuteQueries() {
                        @Override
                        public void Executed(ArrayList<DataSnapshot> result) {

                            StringBuilder username = new StringBuilder(" ");
                            ArrayList<String> usernameArray = new ArrayList<>();
                            if (result!=null) {
                                for (DataSnapshot sharedUsername : result) {
                                    Log.i("databaseUsername", sharedUsername.getValue().toString());
                                    username.append(sharedUsername.getValue().toString()).append(" ");
                                    usernameArray.add(sharedUsername.getValue().toString());
                                }
                            }
                            String title = child.child("title").getValue()+";"+ username;
                            titles.add(title);

                            Log.i("databaseTitles", String.valueOf(titles));

                            usernames.add(usernameArray);

                            references.add(child.getRef());

                            DataSnapshot details = child.child("details");
                            DataSnapshot labelsDS = child.child("label");


                            for (DataSnapshot detailsChild : details.getChildren()) {
                                ArrayList<String> detailArray = new ArrayList<>();

                                for (DataSnapshot detailsChildChild : detailsChild.getChildren()) {
                                    detailArray.add(detailsChildChild.getValue().toString());
                                }
                                switch (detailsChild.getKey()) {
                                    case "charges":
                                        charges.add(detailArray);
                                        break;
                                    case "placeNames":
                                        names.add(detailArray);
                                        break;
                                    case "coordinates":
                                        coordinates.add(detailArray);
                                        break;
                                    case "cuisines":
                                        cuisine.add(detailArray);
                                        break;
                                    case "openingHours":
                                        openingHours.add(detailArray);
                                        break;
                                    case "categories":
                                        categories.add(detailArray);
                                        break;
                                }
                            }
                            Map<String, Object> label = new HashMap<>();
                            for (DataSnapshot labelsChild : labelsDS.getChildren()) {
                                if (labelsChild.getKey().equals("categories")) {
                                    ArrayList<String> labelCategories = new ArrayList<>();
                                    for (DataSnapshot categoriesDS : labelsChild.getChildren()) {
                                        labelCategories.add(categoriesDS.getValue().toString());
                                    }
                                    label.put("categories", labelCategories);
                                } else
                                    label.put(labelsChild.getKey(), labelsChild.getValue().toString());
                            }
                            labels.add(label);

                            mySharesArrayAdapter.notifyDataSetChanged();
                        }

                    });
                }
                Log.i("databaseTitles", String.valueOf(titles));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hibakezelés
                Log.i("databaseError", databaseError.toString());
            }
        });
    }

    private void clearAll(){

        titles.clear();
        charges.clear();
        names.clear();
        coordinates.clear();
        cuisine.clear();
        openingHours.clear();
        categories.clear();
        labels.clear();
        usernames.clear();
        references.clear();
        mySharesArrayAdapter.clear();
        /*detailsDialog = null;
        updateSearchDialog = null;
        detailsDialogWindow = null;
        updateSearchDialogWindow = null;*/
    }

    private void animateImageButton(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.image_button_click);
        view.startAnimation(animation);
    }
}