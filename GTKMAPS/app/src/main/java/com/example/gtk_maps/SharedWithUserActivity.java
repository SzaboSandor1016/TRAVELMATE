package com.example.gtk_maps;

import androidx.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SharedWithUserActivity extends AppCompatActivity {

    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ListView withMeSharesLV;
    private Button selectDD, deleteDD,positiveDSWMD,negativeDSWMD;
    private TextView placeDD, transportDD,distanceDD, categoriesDD, categoriesListDD, dateDD;
    private ArrayList<String> titles;
    private ArrayList<Object> charges,names,coordinates,cuisine,openingHours,categories,labels,usernames;
    private ArrayList<DatabaseReference> sharedWithMeReferences;
    private ArrayAdapter<String> withMeArrayAdapter;
    private SharedPreferences sharedPreferences;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_with_user);
        withMeSharesLV = findViewById(R.id.withMeSharesLV);
        resources = getResources();

        titles = new ArrayList<>();
        charges = new ArrayList<>();
        names = new ArrayList<>();
        //selectedCategories = new ArrayList<>();
        coordinates = new ArrayList<>();
        cuisine = new ArrayList<>();
        openingHours = new ArrayList<>();
        categories = new ArrayList<>();
        labels = new ArrayList<>();
        //usernames = new ArrayList<>();
        sharedWithMeReferences = new ArrayList<>();



        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences= getSharedPreferences("FirebaseManager", Context.MODE_PRIVATE);

        firebaseManager= FirebaseManager.getInstance(SharedWithUserActivity.this,mAuth,mDatabase, sharedPreferences);

        withMeArrayAdapter = new SharedByArrayAdapter(SharedWithUserActivity.this, R.layout.shared_by_list_item_layout,titles);
        withMeSharesLV.setAdapter(withMeArrayAdapter);

        getDatabaseData();

        withMeSharesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

                Dialog withSharedDialog = new Dialog(SharedWithUserActivity.this,R.style.CustomDialogTheme);
                withSharedDialog.setContentView(R.layout.with_shared_details_dialog);

                Window withSharedDialogWindow = withSharedDialog.getWindow();
                if (withSharedDialogWindow!=null) {
                    withSharedDialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    /*window.setBackgroundDrawableResource(R.drawable.fade);*/
                    withSharedDialogWindow.setWindowAnimations(R.style.DialogAnimation);

                    WindowManager.LayoutParams layoutParams = withSharedDialogWindow.getAttributes();
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Match parent width
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Wrap content height
                    layoutParams.gravity = Gravity.TOP; // Position at the bottom of the screen
                    withSharedDialogWindow.setAttributes(layoutParams);
                }

                withSharedDialog.setCancelable(true);

                placeDD = withSharedDialog.findViewById(R.id.placeWSDD);
                transportDD = withSharedDialog.findViewById(R.id.transportWSDD);
                distanceDD = withSharedDialog.findViewById(R.id.distanceWSDD);
                categoriesDD = withSharedDialog.findViewById(R.id.categoriesWSDD);
                categoriesListDD= withSharedDialog.findViewById(R.id.categoriesListWSDD);
                dateDD = withSharedDialog.findViewById(R.id.dateWSDD);

                placeDD.setText(labelDetails.get("place").toString());
                String transportMode = resources.getString(R.string.transporte_form)+ " " +labelDetails.get("transportMode").toString();
                String distance = resources.getString(R.string.distance)+ " " +labelDetails.get("distance").toString();
                transportDD.setText(transportMode);
                distanceDD.setText(distance);
                categoriesDD.setText(resources.getString(R.string.categories));
                ArrayList<String> labelCategories = new ArrayList<>((Collection<? extends String>) labelDetails.get("categories"));

                StringBuilder stringBuilder = new StringBuilder();
                for (String labelCategory: labelCategories){
                    stringBuilder.append(labelCategory).append("\n");
                }

                categoriesListDD.setText(stringBuilder.toString());
                dateDD.setText(labelDetails.get("date").toString());

                selectDD = withSharedDialog.findViewById(R.id.selectWSDD);
                deleteDD = withSharedDialog.findViewById(R.id.deleteWSDD);
                selectDD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO Return the labelDetails too without "date"
                        Map<String, Object> returnLabel = new HashMap<>(labelDetails);
                        returnLabel.remove("date");
                        Log.d("returnLabel", returnLabel.toString());
                        Intent intent = new Intent();
                        intent.putExtra("label",(Serializable) returnLabel);
                        intent.putExtra("sharedWithMe", (Serializable) sharedMap);
                        setResult(RESULT_OK, intent);
                        finish();
                        withSharedDialog.dismiss();

                    }
                });
                deleteDD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog deleteDialog = new Dialog(SharedWithUserActivity.this);
                        deleteDialog.setContentView(R.layout.delete_shared_with_me_dialog);
                        deleteDialog.setCancelable(true);

                        Window deleteDialogWindow = deleteDialog.getWindow();

                        if (deleteDialogWindow!= null){
                            deleteDialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        }

                        positiveDSWMD = deleteDialog.findViewById(R.id.positiveDSWMD);

                        positiveDSWMD.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                firebaseManager.removeShare(sharedWithMeReferences.get(position), new FirebaseManager.RemoveShare() {
                                    @Override
                                    public void Executed() {
                                        getDatabaseData();
                                        deleteDialog.dismiss();
                                        withSharedDialog.dismiss();
                                    }
                                });
                            }
                        });

                        negativeDSWMD = deleteDialog.findViewById(R.id.negativeDSWMD);

                        negativeDSWMD.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteDialog.dismiss();
                            }
                        });

                        deleteDialog.show();
                    }
                });

                withSharedDialog.show();

            }
        });

    }


    private void getDatabaseData(){

        clearAll();

        FirebaseUser user = mAuth.getCurrentUser();
        String Uid= user.getUid();

        DatabaseReference databaseRef = mDatabase.child("shared");

        Query query = databaseRef.orderByChild("shared_with/"+ Uid).equalTo(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child: dataSnapshot.getChildren()){


                    ArrayList<Query> queries = new ArrayList<>();
                    ArrayList<DataSnapshot> result = new ArrayList<>();
                    DataSnapshot sharedBy = child.child("op");

                    Query sharedWithUsername = mDatabase.child("username_to_uid").orderByKey().equalTo(sharedBy.getValue().toString());
                    queries.add(sharedWithUsername);

                    firebaseManager.executeQueries(queries, result, 0, new FirebaseManager.ExecuteQueries() {
                        @Override
                        public void Executed(ArrayList<DataSnapshot> result) {

                            Log.i("databaseOp", result.toString());

                            String sharedBy = result.get(0).getValue().toString();

                            DatabaseReference meReference = child.child("shared_with").child(user.getUid()).getRef();

                            sharedWithMeReferences.add(meReference);

                            //usernames.add(sharedBy);

                            titles.add((String) child.child("title").getValue()+";"+ sharedBy);
                            DataSnapshot details = child.child("details");
                            DataSnapshot labelsDS = child.child("label");


                            for (DataSnapshot detailsChild: details.getChildren()){
                                ArrayList<String> detailArray = new ArrayList<>();

                                for (DataSnapshot detailsChildChild: detailsChild.getChildren()){
                                    detailArray.add(detailsChildChild.getValue().toString());
                                }
                                switch (detailsChild.getKey()) {
                                    case "charges":
                                        charges.add(detailArray);
                                        break;
                                    case "placeNames":
                                        names.add(detailArray);
                                        break;
                            /*case "selectedCategories":
                                selectedCategories.add(detailArray);
                                break;*/
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
                            for (DataSnapshot labelsChild: labelsDS.getChildren()){
                                if (labelsChild.getKey().equals("categories")) {
                                    ArrayList<String> labelCategories = new ArrayList<>();
                                    for (DataSnapshot categoriesDS : labelsChild.getChildren()) {
                                        labelCategories.add(categoriesDS.getValue().toString());
                                    }
                                    label.put("categories", labelCategories);
                                }else
                                    label.put(labelsChild.getKey(),labelsChild.getValue().toString());
                            }
                            labels.add(label);

                            withMeArrayAdapter.notifyDataSetChanged();
                        }
                    });

                }

                //withMeArrayAdapter = new ArrayAdapter<>(SharedWithUserActivity.this, android.R.layout.simple_list_item_1, titles);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.i("bm",databaseError.toString());
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
        withMeArrayAdapter.clear();
        sharedWithMeReferences.clear();
        resources.flushLayoutCache();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearAll();
        withMeArrayAdapter = null;
    }
}