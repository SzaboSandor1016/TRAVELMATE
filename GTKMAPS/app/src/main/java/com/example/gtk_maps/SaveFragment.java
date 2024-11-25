package com.example.gtk_maps;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";*/

    // TODO: Rename and change types of parameters
    /*private String mParam1;
    private String mParam2;*/



    private ExtendedFloatingActionButton saveActionButton;
    private EditText saveTextView;
    private RecyclerView savesList;
    private int selectedDistance;
    private String selectedTransport;
    private ArrayList<Save> readSaves;
    private ArrayList<String> selectedCategories;
    private ArrayList<Place> selectedPlaces;
    private Boolean isSaveOpen;
    private Boolean executedFromSave;

    private static SaveRecyclerViewAdapter adapter;

    private SaveManager saveManager;

    private Resources resources;
    private Context context;
    private FragmentsViewModel fragmentsViewModel;

    public SaveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SaveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaveFragment newInstance(String param1, String param2) {
        SaveFragment fragment = new SaveFragment();
        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);
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

        saveManager = SaveManager.getInstance(context);
        savesList.setLayoutManager(new LinearLayoutManager(context));

        readSaves = new ArrayList<>();
        selectedCategories = new ArrayList<>();
        selectedPlaces = new ArrayList<>();

        isSaveOpen = false;
        executedFromSave = false;
        /*if (fragmentsViewModel.getDistance().getValue()!=null)
            selectedDistance = fragmentsViewModel.getDistance().getValue();*/

        /*fragmentsViewModel.getDistance().observeForever(*//*getViewLifecycleOwner(),*//* new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                selectedDistance = integer;
            }
        });

        *//*if (fragmentsViewModel.getCategories().getValue()!=null && fragmentsViewModel.getCategories().getValue().size()!=0){
            selectedCategories.clear();
            selectedCategories.addAll(fragmentsViewModel.getCategories().getValue());
        }*//*
        fragmentsViewModel.getCategories().observeForever(*//*getViewLifecycleOwner(),*//* new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> arrayList) {
                selectedCategories.clear();
                selectedCategories.addAll(arrayList);
            }
        });

        *//*if (fragmentsViewModel.getPlaces().getValue()!=null && fragmentsViewModel.getPlaces().getValue().size()!=0){
            selectedPlaces.clear();
            selectedPlaces.addAll(fragmentsViewModel.getPlaces().getValue());
        }*//*

        fragmentsViewModel.getPlaces().observeForever(*//*getViewLifecycleOwner(),*//* new Observer<ArrayList<Place>>() {
            @Override
            public void onChanged(ArrayList<Place> places) {
                selectedPlaces.clear();
                selectedPlaces.addAll(places);
            }
        });

        *//*if (fragmentsViewModel.getTransport().getValue()!= null){
            selectedTransport = fragmentsViewModel.getTransport().getValue();
        }*//*

        fragmentsViewModel.getTransport().observeForever(*//*getViewLifecycleOwner(),*//* new Observer<String>() {
            @Override
            public void onChanged(String s) {
                selectedTransport = s;
            }
        });*/

        fragmentsViewModel.getUpdated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                //if (!executedFromSave) {

                    selectedPlaces.clear();

                    selectedCategories.clear();

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
                                                        selectedDistance = ((int) search.get(0).getDistance());
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
                /*}else {
                    executedFromSave = false;
                }*/

            }
        });

        adapter = new SaveRecyclerViewAdapter(context,resources, readSaves, new SaveRecyclerViewAdapter.DeleteSaveItem() {
            public void deleteSave(Save save) {
                saveManager.deleteSavedSearch(save, new SaveManager.DeleteCallback() {
                    @Override
                    public void onComplete() {
                        saveManager.getSearches(new SaveManager.GetSearches() {
                            @Override
                            public void onComplete(ArrayList<Save> result) {
                                readSaves.clear();
                                readSaves.addAll(result);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }, new SaveRecyclerViewAdapter.SelectSaveItem() {
            public void select(Save save){

                //TODO return all of these via a interface

                /*fragmentsViewModel.getTransport().postValue(save.getTransport());
                fragmentsViewModel.getPlaces().postValue(save.getSavedPlaces());
                fragmentsViewModel.getDistance().postValue(save.getDistance());
                fragmentsViewModel.getCategories().postValue(save.getCategories());*/

                fragmentsViewModel.uploadToDatabase(save.getDistance(),save.getTransport(),save.getSavedPlaces(),save.getCategories());

                Log.d("ViewModel", String.valueOf(save.getSavedPlaces().size()));
                //executedFromSave = true;

                /*markCoordinatesOnMap(save.getSavedPlaces());
                places.addAll(save.getSavedPlaces());
                selectedTransport = save.getTransport();
                selectedDistance = save.getDistance();
                startPlace = save.getSavedPlaces().get(0);
                allCategories.addAll(save.getCategories());*/
            }
        });
        savesList.setAdapter(adapter);

        saveActionButton.setExtended(true);

        saveActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSaveOpen){
                    saveTextView.setVisibility(View.VISIBLE);
                    saveActionButton.shrink();
                    saveActionButton.setIcon(resources.getDrawable(R.drawable.done, getContext().getTheme()));
                    isSaveOpen = true;
                }else{
                    String name = saveTextView.getText().toString().trim();

                    //if (!selectedCategories.isEmpty())
                    if (!name.equals("")) {
                        Save save = new Save(name, selectedTransport, getCurrentDate(), selectedDistance, selectedCategories, selectedPlaces, selectedPlaces.get(0).getAddress());
                        //else
                        //    save = new Save(name, selectedTransport, getCurrentDate(), selectedDistance, allCategories, places, startPlace.getAddress());
                        saveManager.addSearch(save, new SaveManager.AddCallback() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(context, R.string.add_toast, Toast.LENGTH_LONG).show();
                                saveManager.getSearches(new SaveManager.GetSearches() {
                                    @Override
                                    public void onComplete(ArrayList<Save> result) {

                                        readSaves.clear();
                                        readSaves.addAll(result);

                                        adapter.notifyDataSetChanged();
                                    }
                                });

                            }
                        });
                    }
                    saveTextView.setText("");
                    isSaveOpen = false;
                    saveActionButton.extend();
                    saveActionButton.setIcon(resources.getDrawable(R.drawable.save, getContext().getTheme()));
                    saveTextView.setVisibility(View.GONE);
                }
            }
        });


        saveManager.getSearches(new SaveManager.GetSearches() {
            @Override
            public void onComplete(ArrayList<Save> result) {

                readSaves.clear();
                readSaves.addAll(result);

                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save, container, false);


        savesList = view.findViewById(R.id.saves_list);
        saveActionButton = view.findViewById(R.id.save_action_button);
        saveTextView = view.findViewById(R.id.save_save_fragment);
        // Inflate the layout for this fragment
        return view;
    }

    private void animateImageButton(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.image_button_click);
        view.startAnimation(animation);
    }
    private String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }
}