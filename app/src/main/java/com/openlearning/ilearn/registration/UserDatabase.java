package com.openlearning.ilearn.registration;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;

import java.util.Objects;

public class UserDatabase {

    public static final String USERS_COLLECTION = "iLearnUsers";
    private final FirebaseFirestore db;
    public static UserDatabase instance;
    private User currentDBUser;


    private UserDatabase() {
        db = FirebaseFirestore.getInstance();
    }

    public static UserDatabase getInstance() {

        if (instance == null) {

            instance = new UserDatabase();
        }

        return instance;
    }

    public void insertNewUser(User newUser, boolean currentUser, FirebaseSuccessListener listener) {

        db.collection(USERS_COLLECTION)
                .document(newUser.getId())
                .set(newUser)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(newUser);

                    if (currentUser)
                        currentDBUser = newUser;

                });
    }

    public void updateUser(User newUser, boolean currentUser, FirebaseSuccessListener listener) {

        db.collection(USERS_COLLECTION)
                .document(newUser.getId())
                .set(newUser, SetOptions.merge())
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        listener.onFailure(task.getException());
                        return;
                    }
                    if (currentUser)
                        currentDBUser = newUser;
                    listener.onSuccess(null);



                });
    }

    public void getUserWithThisID(String id, boolean currentUser, FireStoreObjectGetListener listener) {

        db.collection(USERS_COLLECTION)
                .document(id)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        listener.onFailure(task.getException());
                        return;
                    }

                    User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                    listener.onSuccess(user);

                    if (currentUser)
                        currentDBUser = user;

                });

    }

    public User getCurrentDBUser() {
        return currentDBUser;
    }

    public void changeUserNameIntoDatabase(String name) {

        updateUser(currentDBUser, true, new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                currentDBUser.setName(name);

            }

            @Override
            public void onFailure(Exception ex) {

            }
        });
    }
}
