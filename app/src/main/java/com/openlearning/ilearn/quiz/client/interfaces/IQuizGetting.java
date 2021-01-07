package com.openlearning.ilearn.quiz.client.interfaces;

public interface IQuizGetting {

    void makeQuiz();

    void getQuizSections();

    void onNoSectionFound();

    void getQuizSectionQuestions();

    void onSomeSectionLoaded(String sectionID);

    void onCompleteSuccess();

    void onCompleteFailure(Exception ex);

}
