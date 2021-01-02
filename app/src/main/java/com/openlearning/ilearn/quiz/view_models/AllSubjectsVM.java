package com.openlearning.ilearn.quiz.view_models;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.quiz.modals.Subject;
import com.openlearning.ilearn.quiz.repositories.SubjectRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;

import java.util.List;

public class AllSubjectsVM extends ViewModel {

    private final SubjectRepository subjectRepository;
    private MutableLiveData<List<Subject>> subjectList;

    public AllSubjectsVM() {

        subjectRepository = SubjectRepository.getInstance();
        subjectList = new MutableLiveData<>();
    }

    public void getSubjects() {

        subjectRepository.getSubjectsFromDatabase(false, new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    subjectList.setValue((List<Subject>) obj);
                }
            }

            @Override
            public void onFailure(Exception ex) {

            }
        });

    }

    public MutableLiveData<List<Subject>> getSubjectList() {
        return subjectList;
    }
}
