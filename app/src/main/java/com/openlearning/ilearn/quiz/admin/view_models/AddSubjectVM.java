package com.openlearning.ilearn.quiz.admin.view_models;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.dialogues.LoadingDialogue;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.admin.repositories.SubjectRepository;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.utils.CommonUtils;

import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_LARGE;
import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_MEDIUM;
import static com.openlearning.ilearn.utils.CommonUtils.MIN_LENGTH_SMALL;
import static com.openlearning.ilearn.utils.CommonUtils.STRING_EMPTY;
import static com.openlearning.ilearn.utils.CommonUtils.VALIDATION_SUCCESS;

public class AddSubjectVM extends ViewModel {

    private static final String TAG = "AddSubjVM";
    private final SubjectRepository subjectRepository;
    private final UserRegistration userRegistration;
    private Subject editSubject;
    private boolean active;

    public AddSubjectVM() {

        subjectRepository = SubjectRepository.getInstance();
        userRegistration = UserRegistration.getInstance();

    }

    public void checkSubject(Activity activity, String subjectName, String subjectDescription) {

        String subjectNameStatus = validateSubjectName(subjectName);
        if (!subjectNameStatus.equals(VALIDATION_SUCCESS)) {
            CommonUtils.showWarningDialogue(activity, subjectNameStatus);
            return;
        }

        String subjectDescriptionStatus = validateSubjectDescription(subjectDescription);
        if (!subjectDescriptionStatus.equals(VALIDATION_SUCCESS)) {
            CommonUtils.showWarningDialogue(activity, subjectDescriptionStatus);
            return;
        }

        Subject subject = makeSubject(subjectName, subjectDescription);
        addSubjectToDatabase(activity, subject);
    }

    private void addSubjectToDatabase(Activity activity, Subject subject) {


        if (editSubject != null) {

            LoadingDialogue loadingDialogue = CommonUtils.showLoadingDialogue(activity, "Please wait...", "Please wait while we editing your new Subject");


            subjectRepository.updateSubject(subject, new FirebaseSuccessListener() {
                @Override
                public void onSuccess(Object obj) {

                    loadingDialogue.cancel();
                    CommonUtils.showSuccessDialogue(activity, "Subject edited successfully");

                }

                @Override
                public void onFailure(Exception ex) {

                    loadingDialogue.cancel();
                    CommonUtils.showDangerDialogue(activity, "Subject editing failed\n" + ex.getLocalizedMessage());

                }
            });

        } else {

            LoadingDialogue loadingDialogue = CommonUtils.showLoadingDialogue(activity, "Please wait...", "Please wait while we adding your new Subject");
            subjectRepository.insertNewSubject(subject, new FirebaseSuccessListener() {
                @Override
                public void onSuccess(Object obj) {

                    loadingDialogue.cancel();
                    CommonUtils.showSuccessDialogue(activity, "Subject added successfully");

                }

                @Override
                public void onFailure(Exception ex) {

                    loadingDialogue.cancel();
                    CommonUtils.showDangerDialogue(activity, ex.getLocalizedMessage());

                }
            });

        }
    }

    public String validateSubjectName(String subjectName) {

        Log.d(TAG, "Subject Name validation started");

        if (subjectName == null || subjectName.equals(STRING_EMPTY)) {

            String status = "Subject Name Empty";
            Log.d(TAG, status);
            return status;

        } else if (subjectName.length() < MIN_LENGTH_SMALL) {

            String status = "Subject Name less than " + MIN_LENGTH_SMALL + " characters";
            Log.d(TAG, status);
            return status;

        } else if (subjectName.length() > MAX_LENGTH_MEDIUM) {

            String status = "Subject Name greater than " + MAX_LENGTH_MEDIUM + " characters";
            Log.d(TAG, status);
            return status;

        }

        return VALIDATION_SUCCESS;

    }

    public String validateSubjectDescription(String subjectDescription) {

        Log.d(TAG, "Subject Description validation started");

        if (subjectDescription == null || subjectDescription.equals(STRING_EMPTY)) {

            String status = "Subject Description Empty";
            Log.d(TAG, status);
            return status;

        } else if (subjectDescription.length() < MIN_LENGTH_SMALL) {

            String status = "Subject Description less than " + MIN_LENGTH_SMALL + " characters";
            Log.d(TAG, status);
            return status;

        } else if (subjectDescription.length() > MAX_LENGTH_LARGE) {

            String status = "Subject Description greater than " + MAX_LENGTH_LARGE + " characters";
            Log.d(TAG, status);
            return status;

        }

        return VALIDATION_SUCCESS;

    }

    private Subject makeSubject(String subjectName, String subjectDescription) {

        if (editSubject != null) {

            editSubject.setName(subjectName);
            editSubject.setDescription(subjectDescription);
            editSubject.setActive(active);
            return editSubject;
        }

        return new Subject(subjectName, subjectDescription, userRegistration.getUserID(), active);
    }

    public void setEditSubject(Subject editSubject) {
        this.editSubject = editSubject;
        Log.d(TAG, "setEditSubject: Editing mode");
    }

    public void setActive(boolean active) {
        this.active = active;
        Log.d(TAG, "setActive: " + active);
    }
}
