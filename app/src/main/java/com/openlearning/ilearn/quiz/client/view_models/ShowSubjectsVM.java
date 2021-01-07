package com.openlearning.ilearn.quiz.client.view_models;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.client.repositories.SubjectRepositoryClient;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class ShowSubjectsVM extends ViewModel {

    private final SubjectRepositoryClient subjectRepositoryClient;
    private final MutableLiveData<List<Subject>> subjectsList;
    private final MutableLiveData<Boolean> subjectsEmpty;

    public ShowSubjectsVM() {

        subjectRepositoryClient = SubjectRepositoryClient.getInstance();
        subjectsList = new MutableLiveData<>();
        subjectsEmpty = new MutableLiveData<>();
    }

    public void getSubjects(Activity activity, boolean fromServer) {

        subjectRepositoryClient.getSubjectsFromDatabase(fromServer, new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    subjectsList.setValue((List<Subject>) obj);
                } else {

                    subjectsEmpty.setValue(true);
                }

            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showWarningDialogue(activity, "There was error while fetching subjects\n" + ex.getLocalizedMessage());

            }
        });
    }

    public MutableLiveData<Boolean> getSubjectsEmpty() {
        return subjectsEmpty;
    }

    public MutableLiveData<List<Subject>> getSubjectsList() {
        return subjectsList;
    }
}
