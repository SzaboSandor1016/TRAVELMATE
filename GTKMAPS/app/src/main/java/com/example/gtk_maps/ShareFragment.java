package com.example.gtk_maps;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShareFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShareFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";*/

    // TODO: Rename and change types of parameters
    /*private String mParam1;
    private String mParam2;*/

    private EditText addresseeUsername,shareTitle,shareNote;
    private ImageButton addAddressee;
    private Button shareShare;
    private RecyclerView recentAddresseesRecyclerView;

    private String selectedTransport;
    private int selectedDistance;
    private ArrayList<Pair<String, Boolean>> recentAddressees;
    private ArrayList<String> selectedAddressees;
    private ArrayList<String> selectedCategories;
    private ArrayList<Place> selectedPlaces;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;
    private ShareRecentAddresseeRecyclerViewAdapter shareRecentAdapter;
    private FirebaseManager firebaseManager;
    private Resources resources;
    private Context context;
    private FragmentsViewModel fragmentsViewModel;

    public ShareFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShareFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShareFragment newInstance(String param1, String param2) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();/*
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/


        fragmentsViewModel = new ViewModelProvider(requireActivity()).get(FragmentsViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.context = requireContext();
        this.resources = getResources();

        recentAddressees = new ArrayList<>();
        selectedAddressees = new ArrayList<>();
        selectedPlaces = new ArrayList<>();
        selectedCategories = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = context.getSharedPreferences("FirebaseManager", Context.MODE_PRIVATE);
        firebaseManager = FirebaseManager.getInstance(context, mAuth, mDatabase, sharedPreferences);

        /*fragmentsViewModel.getDistance().observe( getViewLifecycleOwner(),new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                selectedDistance = integer;
            }
        });
        fragmentsViewModel.getCategories().observe(getViewLifecycleOwner(),new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> arrayList) {
                selectedCategories.clear();
                selectedCategories.addAll(arrayList);
            }
        });
        fragmentsViewModel.getPlaces().observe(getViewLifecycleOwner(),new Observer<ArrayList<Place>>() {
            @Override
            public void onChanged(ArrayList<Place> places) {
                selectedPlaces.clear();
                selectedPlaces.addAll(places);
            }
        });
        fragmentsViewModel.getTransport().observe(getViewLifecycleOwner(),new Observer<String>() {
            @Override
            public void onChanged(String s) {
                selectedTransport = s;
            }
        });*/

        fragmentsViewModel.getUpdated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                selectedPlaces.clear();

                selectedCategories.clear();

                /*List<SearchWithPlaces> searchWithPlaces =  fragmentsViewModel.getSearchWithPlaces();

                selectedDistance = (int) searchWithPlaces.get(0).getSearch().getDistance();
                selectedTransport = searchWithPlaces.get(0).getSearch().getTransport();

                selectedPlaces.addAll(searchWithPlaces.get(0).getPlaces());


                selectedCategories.addAll(searchWithPlaces.get(0).getCategoriesAsStrings());*/

                ExecutorService executorService = Executors.newSingleThreadExecutor();

                executorService.execute(() -> {

                    try {

                        List<Search> search = fragmentsViewModel.getSearch();
                            /*
                            List<SearchWithPlaces> searchWithPlaces = fragmentsViewModel.getSearchWithPlaces();*/

                        new Handler(Looper.getMainLooper()).post(() -> {
                            executorService.execute(() -> {

                                try {

                                    List<Place> tempPlace = fragmentsViewModel.getPlaces(search.get(0).getId());

                                    new Handler(Looper.getMainLooper()).post(() -> {

                                        executorService.execute(() -> {
                                            try {

                                                List<String> categoryEntities = fragmentsViewModel.getCategories(search.get(0).getId());

                                                new Handler(Looper.getMainLooper()).post(() -> {

                                                    selectedDistance = (int) search.get(0).getDistance();
                                                    selectedTransport = search.get(0).getTransport();

                                                    selectedPlaces.addAll(tempPlace);


                                                    selectedCategories.addAll(categoryEntities);

                                                });
                                            }catch (Exception exception){}

                                        });

                                    });

                                }catch (Exception ignored){}

                            });


                        });
                    } catch (Exception e) {
                    }

                });

            }
        });


        recentAddresseesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        shareRecentAdapter = new ShareRecentAddresseeRecyclerViewAdapter(context, recentAddressees, new ShareRecentAddresseeRecyclerViewAdapter.SelectAddresseeItem() {
            @Override
            public void select(Pair<String,Boolean> pair) {
                selectedAddressees.add(pair.first);
            }

            @Override
            public void unselect(Pair<String,Boolean> pair) {
                selectedAddressees.remove(pair.first);
            }
        }, resources);
        recentAddresseesRecyclerView.setAdapter(shareRecentAdapter);




        /*if (mAuth.getCurrentUser()!=null || sharedPreferences.getBoolean("loggedIn", false)){
            shareSearchBTN.setVisibility(View.VISIBLE);
        }else {
            shareSearchBTN.setVisibility(View.INVISIBLE);
        }*/

        firebaseManager.getRecentSharedWithUsernames(new FirebaseManager.RecentUsernames() {
            @Override
            public void onSuccess(ArrayList<String> usernames) {
                recentAddressees.clear();
                ArrayList<Pair<String, Boolean>> usernamePairs = new ArrayList<>();
                for (String username : usernames) {
                    Pair pair = new Pair(username, false);
                    usernamePairs.add(pair);
                }
                recentAddressees.addAll(usernamePairs);

                shareRecentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure() {

            }
        });

        addAddressee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateImageButton(v);
                if (!selectedAddressees.contains(addresseeUsername.getText().toString().trim())) {
                    if (!addresseeUsername.getText().toString().trim().equals("")) {
                        recentAddressees.add(new Pair<>(addresseeUsername.getText().toString().trim(), true));
                        selectedAddressees.add(addresseeUsername.getText().toString().trim());
                        shareRecentAdapter.notifyDataSetChanged();


                        addresseeUsername.setText("");
                    } else {
                        Toast.makeText(context, R.string.empty_addressee, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        shareShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedAddressees.size() != 0) {
                    if (selectedPlaces.size() != 0) {
                    if (!shareTitle.getText().toString().trim().equals("")) {

                        ArrayList<String> recentUsernames = getRecentUsernames();

                        for (String username : selectedAddressees) {
                            if (!recentUsernames.contains(username)) {
                                recentUsernames.add(username);
                            }
                        }

                        firebaseManager.addRecentlySharedWithUsername(recentUsernames);


                        /*TODO get selectedPlaces, transport, categories, startPlace, distance*/

                        String title = shareTitle.getText().toString().trim();
                        String note = shareNote.getText().toString().trim();

                        firebaseManager.shareSearch(title, selectedAddressees, selectedPlaces, selectedTransport, selectedCategories, selectedPlaces.get(0).getName(), selectedDistance, selectedPlaces.get(0).getAddress(), getCurrentDate(), note);


                            /*if (selectedPlaces.size() > 1) {
                                String title = shareTitle.getText().toString().trim();
                                String note = shareNote.getText().toString().trim();

                                firebaseManager.shareSearch(title, selectedAddressees, selectedPlaces, selectedTransport, selectedCategories, startPlace.getName(), selectedDistance, startPlace.getAddress(), getCurrentDate(), note);

                            } else {
                                String title = shareTitle.getText().toString().trim();
                                String note = shareNote.getText().toString().trim();

                                firebaseManager.shareSearch(title, selectedAddressees, places, selectedTransport, allCategories, startPlace.getName(), selectedDistance, startPlace.getAddress(), getCurrentDate(), note);

                            }*/
                    } else {
                        Toast.makeText(context, R.string.share_title_empty, Toast.LENGTH_LONG).show();
                    }
                    } else {
                        Toast.makeText(context, R.string.empty_addressee_list, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, R.string.share_empty_place_list, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share, container, false);


        addresseeUsername = view.findViewById(R.id.addressee_username);
        shareTitle = view.findViewById(R.id.share_title_main);
        addAddressee = view.findViewById(R.id.add_addressee);
        shareShare = view.findViewById(R.id.share_share);
        shareNote = view.findViewById(R.id.share_note);
        recentAddresseesRecyclerView = view.findViewById(R.id.recent_addressees_recycler_view);

        return view;
    }

    private void animateImageButton(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.image_button_click);
        view.startAnimation(animation);
    }
    private ArrayList<String> getRecentUsernames(){
        ArrayList<String> usernames = new ArrayList<>();

        for (Pair<String,Boolean> recentAddressee: recentAddressees){
            usernames.add(recentAddressee.first);
        }

        return usernames;
    }
    private String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }
}