package com.example.gtk_maps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FirebaseManager extends AppCompatActivity {
    private static FirebaseManager instance;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private User loggedInUser;
    private SharedPreferences sharedPreferences;
    private Map<String, Integer> stats = new HashMap<>();
    private Map<String, Integer> transportStats = new HashMap<>();
    private Map<String, Integer> distanceStats = new HashMap<>();
    private static String[] activities = {"accomodation", "food", "shopping", "monumentchurch", "museumexhibition", "entertainment",
            "castlefort", "beach", "music", "spa", "sports", "watersport", "concerts", "park", "theatre",
            "themepark", "farm", "lookout", "hiking", "cycling", "boat", "nationalpark"};
    private static String[] transportModes = {"car", "walk"};
    private static String[] distances = {"15", "30", "45"};

    public interface AuthListener {
        void onSignUpSuccess();

        void onSignUpFailure(Exception exception);
    }

    private interface Share {
        void onSuccess(boolean isSuccess);

        void onFailure(boolean isSuccess);
    }

    public interface DeleteUser {
        void onSuccess();

        void onFailure();
    }

    public interface ChangePassword {
        void onSuccess();

        void onFailure();
    }

    public interface ExecuteQueries{
        void Executed(ArrayList<DataSnapshot> result);
    }
    public interface RecentUsernames{
        void onSuccess(ArrayList<String> usernames);
        void onFailure();
    }
    public interface UpdateShare{
        void Executed();
    }
    public interface RemoveShare{
        void Executed();
    }
    public interface RemoveReference{
        void Executed();
    }
    private interface CheckUsername{
        void Exists();
        void NotExists();
    }
    public interface ChangeUsername{
        void Success();
        void UnSuccess();
        void Exists();
    }
    public interface ChangeName{
        void Success();
        void UnSuccess();
    }
    public interface ChangeUserBirthDate{
        void Success();
        void UnSuccess();
    }


    FirebaseManager(Context context, FirebaseAuth mAuth, DatabaseReference mDatabase, SharedPreferences sharedPreferences) {
        this.context = context;
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
        this.sharedPreferences = sharedPreferences;
    }

    public static FirebaseManager getInstance(Context context, FirebaseAuth mAuth, DatabaseReference mDatabase, SharedPreferences sharedPreferences) {
        if (instance == null && context != null && mAuth != null && mDatabase != null) {
            instance = new FirebaseManager(context, mAuth, mDatabase, sharedPreferences);
        }
        return instance;
    }

    private void initialStats() {

        for (String activity : activities) {
            stats.put(activity, 0);
        }
        Log.d("stats", stats.toString());
        for (String mode : transportModes) {
            transportStats.put(mode, 0);
        }
        Log.d("transportStats", transportStats.toString());
        for (String distance : distances) {
            distanceStats.put(distance, 0);
        }
        Log.d("distanceStats", distanceStats.toString());
    }

    private void uploadSearch(Map<String, Object> searchDetails, Share share) {
        DatabaseReference reference = mDatabase.child("shared").push();
        reference.setValue(searchDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    share.onFailure(false);
                } else {
                    share.onSuccess(true);
                }
            }
        });

    }

    private String getAgeCategory(String year) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int age = currentYear - Integer.parseInt(year);

        if (age >= 18 && age <= 24) {
            return "18-24";
        } else if (age >= 25 && age <= 34) {
            return "25-34";
        } else if (age >= 35 && age <= 44) {
            return "35-44";
        } else if (age >= 45 && age <= 54) {
            return "45-54";
        } else if (age >= 55 && age <= 64) {
            return "55-64";
        } else if (age >= 65) {
            return "65+";
        } else {
            return "Unknown";
        }
    }

    private void setInitialAgeCategoryData(DatabaseReference statsReference, String year) {
        String ageCategory = getAgeCategory(year);
        initialStats();
        statsReference.child(ageCategory).child("search_details")
                .child("transport").setValue(transportStats);
        statsReference.child(ageCategory).child("search_details")
                .child("distances").setValue(distanceStats);
        statsReference.child(ageCategory).child("stats").setValue(stats);
    }

    private void removeReference(DatabaseReference reference, RemoveReference removeReference) {
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                removeReference.Executed();
            }
        });
    }

    private void checkUsername(String username, CheckUsername checkUsername){

        Query usernameQuery = mDatabase.child("username_to_uid").orderByValue().equalTo(username);

        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean exists = false;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getValue().toString().equals(username))
                        exists = true;

                }
                if (exists){
                    checkUsername.Exists();
                }else {
                    checkUsername.NotExists();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkUsername.Exists();
            }


        });
    }

    public void executeQueries(ArrayList<Query> queries,ArrayList<DataSnapshot> result, int index, ExecuteQueries executeQueries){

        Log.i("querySize", String.valueOf(queries.size()));

        if (queries.size()!=0) {
            Query query = queries.get(index);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        result.add(ds);
                    }

                    if (index == queries.size() - 1) {
                        executeQueries.Executed(result);
                    } else {
                        executeQueries(queries, result, index + 1, executeQueries);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("databaseError", error.toString());
                    executeQueries.Executed(null);
                }
            });
        }else {
            executeQueries.Executed(null);
        }

    }

    public void addRecentlySharedWithUsername(ArrayList<String> usernames){
        Map<String, Object> usernamesMap = new HashMap<>();
        FirebaseUser user = mAuth.getCurrentUser();
        usernamesMap.put("users/"+ user.getUid()+ "/shared_usernames", usernames);

        mDatabase.updateChildren(usernamesMap);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void signUp(String email, String username,String passwd, String family, String given, String year, String month, String day, AuthListener listener) {

        checkUsername(username, new CheckUsername() {
            @Override
            public void Exists() {
                Toast.makeText(context,R.string.used_username,Toast.LENGTH_LONG).show();
                Exception exception = new Exception("placeholder error");
                listener.onSignUpFailure(exception);
            }

            @Override
            public void NotExists() {
                    mAuth.createUserWithEmailAndPassword(email, passwd)
                            .addOnCompleteListener(FirebaseManager.this ,new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {


                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("signUpState", "createUserWithEmail:success");
                                        FirebaseUser user = task.getResult().getUser();
                                        String userId = user.getUid();
                                        String email = user.getEmail();
                                        loggedInUser = new User(email,username, family, given, year, month, day);

                                        Map<String, Object> userMap = new HashMap<>();
                                        Map<String, String> userNameMap = new HashMap<>();
                                        Map<String, String> userBirthdayMap = new HashMap<>();

                                        userNameMap.put("family", loggedInUser.getFamily());
                                        userNameMap.put("given", loggedInUser.getGiven());
                                        userMap.put("name", userNameMap);

                                        userBirthdayMap.put("year",loggedInUser.getYear());
                                        userBirthdayMap.put("month", loggedInUser.getMonth());
                                        userBirthdayMap.put("day", loggedInUser.getDay());
                                        userMap.put("birthDate", userBirthdayMap);

                                        DatabaseReference reference = mDatabase.child("users");

                                        DatabaseReference statsReference = mDatabase.child("age_categories");

                                        DatabaseReference mapReference = mDatabase.child("username_to_uid");

                                        mapReference.child(userId).setValue(loggedInUser.getUsername());

                                        String ageCategory = getAgeCategory(year);
                                        setInitialAgeCategoryData(statsReference, year);
                                        userMap.put("age_category", ageCategory);
                                        userMap.put("username", loggedInUser.getUsername());

                                        reference.child(userId).setValue(userMap);
                                        //reference.child(userId).child("stats").setValue(loggedInUser.getStats());


                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("loggedIn", true);
                                        editor.putString("email", email);
                                        editor.putString("username", loggedInUser.getUsername());
                                        editor.putString("given", loggedInUser.getGiven());
                                        editor.putString("family", loggedInUser.getFamily());
                                        editor.putString("birthDateYear", loggedInUser.getYear());
                                        editor.putString("birthDateMonth", loggedInUser.getMonth());
                                        editor.putString("birthDateDay", loggedInUser.getDay());
                                        editor.apply();

                                        listener.onSignUpSuccess();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("signUpState", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(context, R.string.signup_unsuccess,
                                                Toast.LENGTH_SHORT).show();
                                        listener.onSignUpFailure(task.getException());
                                    }
                                }
                            });
                }
        });

    }

    public void signIn(String email, String passwd, AuthListener listener) {

        mAuth.signInWithEmailAndPassword(email, passwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signInState", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            String email = user.getEmail();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("loggedIn", true);
                            editor.apply();
                            mDatabase.child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    } else {
                                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                        String username, fName, gName, year, month, day;
                                        DataSnapshot dataSnapshot = task.getResult();
                                        username = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                                        fName = Objects.requireNonNull(dataSnapshot.child("name").child("family").getValue()).toString();
                                        gName = Objects.requireNonNull(dataSnapshot.child("name").child("given").getValue()).toString();
                                        year = Objects.requireNonNull(dataSnapshot.child("birthDate").child("year").getValue()).toString();
                                        month = Objects.requireNonNull(dataSnapshot.child("birthDate").child("month").getValue()).toString();
                                        day = Objects.requireNonNull(dataSnapshot.child("birthDate").child("day").getValue()).toString();

                                        loggedInUser = new User(email, username, fName, gName, year, month, day);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("email", email);
                                        editor.putString("username", loggedInUser.getUsername());
                                        editor.putString("given", loggedInUser.getGiven());
                                        editor.putString("family", loggedInUser.getFamily());
                                        editor.putString("birthDateYear", loggedInUser.getYear());
                                        editor.putString("birthDateMonth", loggedInUser.getMonth());
                                        editor.putString("birthDateDay", loggedInUser.getDay());
                                        editor.apply();
                                        listener.onSignUpSuccess();
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signInState", "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, R.string.login_unsuccess,
                                    Toast.LENGTH_SHORT).show();
                            listener.onSignUpFailure(task.getException());
                        }
                    }
                });

    }

    public void signOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loggedIn", false);
        editor.remove("username");
        editor.remove("email");
        editor.remove("given");
        editor.remove("family");
        editor.remove("birthDateYear");
        editor.remove("birthDateMonth");
        editor.remove("birthDateDay");
        editor.apply();
        loggedInUser = null;
        FirebaseAuth.getInstance().signOut();
    }

    public void incrementDatabaseStat(ArrayList<String> searchedCategories, String transportMode, String distance) {
        if (searchedCategories.size() != 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                mDatabase.child("users").child(userId).child("age_category").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            DataSnapshot dataSnapshot = task.getResult();
                            String ageCategory = (String) dataSnapshot.getValue();

                            mDatabase.child("age_categories").child(ageCategory).child("search_details").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    } else {
                                        Map<String, Object> finalStats = new HashMap<>();
                                        DataSnapshot dataSnapshot = task.getResult();
                                        Map<String, Map<String, Object>> stats = (Map<String, Map<String, Object>>) dataSnapshot.getValue();

                                        stats.get("transport").replace(transportMode, (long) stats.get("transport").get(transportMode) + 1);
                                        stats.get("distances").replace(distance, (long) stats.get("distances").get(distance) + 1);

                                        finalStats.put("/age_categories/" + ageCategory + "/search_details", stats);
                                        mDatabase.updateChildren(finalStats);
                                    }
                                }
                            });

                            mDatabase.child("age_categories").child(ageCategory).child("stats").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    } else {
                                        Map<String, Object> finalStats = new HashMap<>();
                                        DataSnapshot dataSnapshot = task.getResult();
                                        Map<String, Object> stats = (Map<String, Object>) dataSnapshot.getValue();

                                        for (String category : searchedCategories) {
                                            stats.replace(category, (long) stats.get(category) + 1);
                                        }

                                        finalStats.put("/age_categories/" + ageCategory + "/stats", stats);
                                        mDatabase.updateChildren(finalStats);
                                    }
                                }
                            });


                        }
                    }
                });
            }

        }
    }

    public void changeName(String detail, String newDetail, ChangeName changeName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        Map<String, Object> finalStats = new HashMap<>();
        finalStats.put("/users/" + userId + "/name/" + detail, newDetail);
        mDatabase.updateChildren(finalStats).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    changeName.UnSuccess();
                } else {
                    changeName.Success();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(detail, newDetail);
                    editor.apply();
                }
            }
        });

    }

    public void changeUserBirthDate(String detail, String newDetail, ChangeUserBirthDate changeUserBirthDate) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference reference = mDatabase.child("age_categories");

        Map<String, Object> finalStats = new HashMap<>();
        finalStats.put("/users/" + userId + "/birthDate/" + detail, newDetail);
        mDatabase.updateChildren(finalStats).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    changeUserBirthDate.UnSuccess();
                    Toast.makeText(context, R.string.change_unsuccess, Toast.LENGTH_LONG).show();
                } else {
                    changeUserBirthDate.Success();
                    Toast.makeText(context, R.string.change_success, Toast.LENGTH_LONG).show();
                }
            }
        });
        if (detail.equals("year")) {
            setInitialAgeCategoryData(reference, newDetail);
            Map<String, Object> finalYear = new HashMap<>();
            String ageCategory = getAgeCategory(newDetail);
            finalYear.put("/users/" + userId + "/age_category", ageCategory);
            mDatabase.updateChildren(finalYear);
        }
    }

    public void changeUsername(String username, ChangeUsername changeUsername){

        Map<String,Object> changeMap = new HashMap<>();
        FirebaseUser user = mAuth.getCurrentUser();


        checkUsername(username, new CheckUsername() {
            @Override
            public void Exists() {
                changeUsername.Exists();
            }

            @Override
            public void NotExists() {
                    changeMap.put("/username_to_uid/"+ user.getUid(), username);
                    changeMap.put("/users/"+ user.getUid()+"/username", username);

                    mDatabase.updateChildren(changeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()){
                                changeUsername.UnSuccess();
                            }else {
                                changeUsername.Success();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username", username);
                                editor.apply();
                            }
                        }
                    });
                }

        });

    }

    public void shareSearch(
            ArrayList<String> with,
            String title,
            Map<String, Object> labelData,
            Map<String, Object> shareData) {
        DatabaseReference mapReference = mDatabase.child("username_to_uid");
        ArrayList<Query> queries = new ArrayList<>();
        for (String element: with){
            Query query = mapReference.orderByValue().equalTo(element);
            queries.add(query);
        }
        ArrayList<DataSnapshot> result = new ArrayList<>();
        executeQueries(queries,result,0, new ExecuteQueries() {
            @Override
            public void Executed(ArrayList<DataSnapshot> result) {
                Map<String, Boolean> withUsernames = new HashMap<>();
                for (DataSnapshot snapshot : result) {
                    withUsernames.put(snapshot.getKey(), true);
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String Uid = user.getUid();

                Map<String, Object> shareMap = new HashMap<>();

                shareMap.put("details", shareData);
                shareMap.put("label", labelData);
                shareMap.put("op", Uid);
                shareMap.put("title", title);
                shareMap.put("shared_with", withUsernames);

                uploadSearch(shareMap, new Share() {
                    @Override
                    public void onSuccess(boolean isSuccess) {
                        Toast.makeText(context, R.string.share_success, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(boolean isSuccess) {
                        Toast.makeText(context, R.string.share_unsuccess, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
    public void updateSharedWith(String newTitle,ArrayList<String> updatedWith, DatabaseReference referenceToUpdate, UpdateShare updateShare){

        DatabaseReference sharedWithReference = referenceToUpdate.child("shared_with");
        DatabaseReference titleReference = referenceToUpdate.child("title");

        if (!newTitle.equals("")) {
            removeReference(titleReference, new RemoveReference() {
                @Override
                public void Executed() {
                    titleReference.setValue(newTitle);
                }
            });
        }

        removeReference(sharedWithReference, new RemoveReference() {
            @Override
            public void Executed() {

                DatabaseReference mapReference = mDatabase.child("username_to_uid");
                ArrayList<Query> queries = new ArrayList<>();
                for (String element: updatedWith){
                    Query query = mapReference.orderByValue().equalTo(element);
                    queries.add(query);
                }
                ArrayList<DataSnapshot> result = new ArrayList<>();

                executeQueries(queries,result,0, new ExecuteQueries() {
                    @Override
                    public void Executed(ArrayList<DataSnapshot> result) {
                        Map<String, Boolean> withUsernames = new HashMap<>();
                        for (DataSnapshot snapshot : result) {
                            withUsernames.put(snapshot.getKey(), true);
                        }

                        sharedWithReference.setValue(withUsernames).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                updateShare.Executed();

                            }
                        });
                    }
                });
            }
        });

    }

    public void getRecentSharedWithUsernames( RecentUsernames recentUsernames){
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference recentlySharedWithUsernames = mDatabase.child("users").child(user.getUid()).child("shared_usernames");
        recentlySharedWithUsernames.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    recentUsernames.onFailure();
                }else {
                    ArrayList<String> usernames = new ArrayList<>();
                    for (DataSnapshot snapshot: task.getResult().getChildren()){
                        usernames.add(snapshot.getValue().toString());
                    }
                    recentUsernames.onSuccess(usernames);
                }
            }
        });
    }
    public void deleteRecentSharedWithUsernames(ArrayList<String> updatedSharedUsernames){
        FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference recentSharedWithUsernames = mDatabase.child("users").child(user.getUid()).child("shared_usernames");
        removeReference(recentSharedWithUsernames, new RemoveReference() {
            @Override
            public void Executed() {
                recentSharedWithUsernames.setValue(updatedSharedUsernames);
            }
        });
    }

    public void deleteUser(String email, String password, DeleteUser deleteUser){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    deleteUser.onFailure();
                }else {
                    DatabaseReference userRef = mDatabase.child("users").child(user.getUid());
                    userRef.removeValue();

                    String Uid = user.getUid();
                    DatabaseReference emailRef = mDatabase.child("username_to_uid").child(Uid);
                    emailRef.removeValue();

                    DatabaseReference sharedRef = mDatabase.child("shared");
                    Query op = sharedRef.orderByChild("op").equalTo(user.getUid());
                    op.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                DatabaseReference reference = snapshot.getRef();
                                removeReference(reference, new RemoveReference() {
                                    @Override
                                    public void Executed() {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            deleteUser.onFailure();
                        }
                    });

                    DatabaseReference sharedWith = mDatabase.child("shared");

                    Query withMe = sharedWith.orderByChild("shared_with/"+ user.getUid()).equalTo(true);

                    withMe.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                DatabaseReference sharedWithMe = snapshot.child("shared_with").child(user.getUid()).getRef();
                                sharedWithMe.removeValue();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            deleteUser.onFailure();
                        }
                    });

                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("deleteAcc", "User account deleted.");
                                        SharedPreferences.Editor editor= sharedPreferences.edit();
                                        editor.putBoolean("loggedIn", false);
                                        editor.remove("email");
                                        editor.remove("given");
                                        editor.remove("family");
                                        editor.remove("birthDateYear");
                                        editor.remove("birthDateMonth");
                                        editor.remove("birthDateDay");
                                        editor.apply();
                                        loggedInUser=null;
                                        deleteUser.onSuccess();
                                    }
                                }
                            });
                }

            }
        });
    }
    public void changePassword(String oldPassword,String newPassword, ChangePassword changePassword ){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    changePassword.onFailure();
                }else {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("change_password", "User password updated.");
                                        changePassword.onSuccess();
                                    }else{
                                        changePassword.onFailure();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void removeShare(DatabaseReference toBeRemoved, RemoveShare removeShare){
        removeReference(toBeRemoved, new RemoveReference() {
            @Override
            public void Executed() {
                removeShare.Executed();
            }
        });
    }
}
