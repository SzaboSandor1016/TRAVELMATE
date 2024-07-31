package com.example.gtk_maps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class AddresseesActivity extends AppCompatActivity {

    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> recentUsernames, toBeDeleted;

    private ArrayList<View> views;
    private ListView addresseesLV;
    private LinearLayout deleteRecentLL;
    private Button deleteRecentBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressees);

        addresseesLV = findViewById(R.id.addresseesLV);
        deleteRecentBTN = findViewById(R.id.deleteRecentBTN);
        deleteRecentLL = findViewById(R.id.deleteRecentLL);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences= getSharedPreferences("FirebaseManager", Context.MODE_PRIVATE);

        recentUsernames = new ArrayList<>();
        views= new ArrayList<>();

        ArrayAdapter adapter = new ArrayAdapter<>(AddresseesActivity.this, android.R.layout.simple_list_item_1, recentUsernames);
        addresseesLV.setAdapter(adapter);


        firebaseManager = FirebaseManager.getInstance(AddresseesActivity.this,mAuth,mDatabase, sharedPreferences);

        firebaseManager.getRecentSharedWithUsernames(new FirebaseManager.RecentUsernames() {
            @Override
            public void onSuccess(ArrayList<String> usernames) {
                recentUsernames.addAll(usernames);
                adapter.notifyDataSetChanged();
                toBeDeleted = new ArrayList<>();


                addresseesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (toBeDeleted.contains(recentUsernames.get(position))){
                            toBeDeleted.remove(recentUsernames.get(position));
                            view.setAlpha(1.0F);
                            views.remove(view);
                        }else {
                            toBeDeleted.add(recentUsernames.get(position));
                            view.setAlpha(0.5F);
                            views.add(view);
                        }
                        if (toBeDeleted.size()>0){
                            deleteRecentBTN.setVisibility(View.VISIBLE);
                        }else{
                            deleteRecentBTN.setVisibility(View.INVISIBLE);
                        }
                    }

                });
            }

            @Override
            public void onFailure() {

            }
        });

        deleteRecentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecentBTN.setVisibility(View.INVISIBLE);

                for (View view: views){
                    view.setAlpha(1.0F);
                }

                recentUsernames.removeAll(toBeDeleted);
                for (String username: toBeDeleted){
                    adapter.remove(username);

                }
                adapter.notifyDataSetChanged();


                firebaseManager.deleteRecentSharedWithUsernames(recentUsernames);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recentUsernames.clear();
        toBeDeleted.clear();
    }
}