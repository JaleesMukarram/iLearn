package com.openlearning.ilearn.quiz.admin.view_models;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.admin.repositories.SubjectRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class AllSubjectsVM extends ViewModel {

    private final SubjectRepository subjectRepository;
    private final UserRegistration userRegistration;
    private final MutableLiveData<List<Subject>> subjectList;
    private final MutableLiveData<Boolean> subjectEmpty;

    public AllSubjectsVM() {

        subjectRepository = SubjectRepository.getInstance();
        userRegistration = UserRegistration.getInstance();
        subjectList = new MutableLiveData<>();
        subjectEmpty = new MutableLiveData<>();

    }

    public void getSubjects(Activity activity, boolean fromServer) {

        subjectRepository.getSubjectsFromDatabase(fromServer, userRegistration.getUserID(), new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    subjectList.setValue((List<Subject>) obj);

                } else {

                    subjectEmpty.setValue(true);
                }
            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showDangerDialogue(activity, "Failed to load subjects\n" + ex.getLocalizedMessage());

            }
        });

    }

    public MutableLiveData<List<Subject>> getSubjectList() {
        return subjectList;
    }

    public MutableLiveData<Boolean> getSubjectEmpty() {
        return subjectEmpty;
    }
}
