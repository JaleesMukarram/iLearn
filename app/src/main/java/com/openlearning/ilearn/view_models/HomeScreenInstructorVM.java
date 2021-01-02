package com.openlearning.ilearn.view_models;

import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.registration.User;

public class HomeScreenInstructorVM extends ViewModel {

    private final UserRegistration userRegistration;

    public HomeScreenInstructorVM() {

        userRegistration = UserRegistration.getInstance();
    }

    public User getCurrentUser() {

        return userRegistration.getCurrentUserFromDB();
    }
}
