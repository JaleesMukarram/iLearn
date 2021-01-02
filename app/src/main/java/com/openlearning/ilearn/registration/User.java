package com.openlearning.ilearn.registration;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User extends BaseObservable {

    public static final int ACCOUNT_OK = 1;
    public static final int ACCOUNT_HOLDED = 2;

    public static final int STATUS_NOT_REGISTERED = 4;
    public static final int STATUS_HALF_REGISTERED = 8;
    public static final int STATUS_COMPLETE_REGISTERED = 16;
    public static final int STATUS_UNKNOWN_REGISTERED = 32;

    public static final int TYPE_GENERAL_USER = 1;
    public static final int TYPE_INSTRUCTOR = 2;
    public static final int TYPE_ADMINISTRATOR = 4;

    private String id;
    private String name;
    private String email;
    @ServerTimestamp
    private Date createdDate;
    private int accountStatus;
    private int accountType;

    public User() {
    }

    public User(String name, int accountType) {
        this.name = name;
        this.accountType = accountType;
        this.accountStatus = ACCOUNT_OK;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdDate=" + createdDate +
                ", accountStatus=" + accountStatus +
                '}';
    }
}
