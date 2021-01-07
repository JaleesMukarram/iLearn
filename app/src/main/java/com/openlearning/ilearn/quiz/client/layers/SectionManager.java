package com.openlearning.ilearn.quiz.client.layers;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;


import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.admin.modals.QuizSection;
import com.openlearning.ilearn.quiz.client.adapter.QuizPagerAdapter;
import com.openlearning.ilearn.quiz.client.dialogues.DialogueSectionDetails;

import java.util.ArrayList;
import java.util.List;

public class SectionManager {

    private static final String TAG = "SecMGR_TAG";
    private final QuizLogic logicLayer;
    private final List<List<SingleQuestionFragment>> allQuestionFragments;
    QuizPagerAdapter quizPagerAdapter;
    private int sectionIndex;

    // Tracking variables
    private int answered;
    private int notAnswered;
    private int marked;

    public SectionManager(QuizLogic logicLayer) {

        this.logicLayer = logicLayer;
        this.allQuestionFragments = new ArrayList<>();
    }

    public int getTotalSections() {

        return logicLayer.getQuiz().getQuizSectionList().size();
    }

    public void initializeSection() {

        for (QuizSection quizSection : getQuiz().getQuizSectionList()) {

            List<SingleQuestionFragment> oneSection = new ArrayList<>();

            for (int i = 0; i < quizSection.getQuestionList().size(); i++) {

                SingleQuestionFragment oneQuestion = new SingleQuestionFragment(this, quizSection.getQuestionList().get(i), i);
                oneSection.add(oneQuestion);
            }

            allQuestionFragments.add(oneSection);
        }

        selectThisIndexedSection(0);
    }

    private Quiz getQuiz() {

        return logicLayer.getQuiz();
    }

    public Fragment getQuestionFragmentAt(int position) {

        return allQuestionFragments.get(sectionIndex).get(position);
    }

    public int getTotalQuestionOfActiveSection() {

        return allQuestionFragments.get(sectionIndex).size();
    }

    public int getTotalQuestionOfThisSection(int position) {

        return allQuestionFragments.get(position).size();
    }

    public void moveToThisPosition(int position) {

        logicLayer.getAllQuestionsPager().setCurrentItem(position);
    }

    public void moveToNextQuestion() {

        try {
            logicLayer.getAllQuestionsPager().setCurrentItem(logicLayer.getAllQuestionsPager().getCurrentItem() + 1);

        } catch (Exception ignored) {
        }

    }

    public void selectThisIndexedSection(int index) {

        sectionIndex = index;
        quizPagerAdapter = new QuizPagerAdapter((FragmentActivity) logicLayer.getHomeScreen(), this);
        logicLayer.getAllQuestionsPager().setAdapter(quizPagerAdapter);
        setViewPagerCallback();
        if (logicLayer.getSectionAdapter() != null) {
            logicLayer.getSectionAdapter().selectThisView(index);

        }

    }

    void setViewPagerCallback() {

        logicLayer.getAllQuestionsPager().registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                markThisQuestionAsVisited(position);
            }
        });
    }

    private void markThisQuestionAsVisited(int position) {

        allQuestionFragments.get(sectionIndex).get(position).getmQuestion().setVisited(true);

    }

    public void showSectionSieView() {

        DialogueSectionDetails dialogue = new DialogueSectionDetails(logicLayer, this);
        dialogue.initViews();
    }

    public Context getContext() {
        return logicLayer.getHomeScreen();
    }

    // Section Status variables

    public void validateThings() {

        int answered = 0;
        int notAnswered = 0;
        int marked = 0;

        List<SingleQuestionFragment> currentList = allQuestionFragments.get(sectionIndex);

        for (int i = 0; i < currentList.size(); i++) {

            if (currentList.get(i).getmQuestion().isVisited() && currentList.get(i).getmQuestion().isAnswered()) {
                answered++;
            }

            if (currentList.get(i).getmQuestion().isVisited() && !currentList.get(i).getmQuestion().isAnswered()) {
                notAnswered++;
            }

            if (currentList.get(i).getmQuestion().isMarked()) {
                marked++;

            }
        }

        this.answered = answered;
        this.notAnswered = notAnswered;
        this.marked = marked;

    }

    public int getTotalAnswered() {

        return answered;
    }

    public int getTotalAnsweredOfThisSection(int position) {

        int answered = 0;

        List<SingleQuestionFragment> currentList = allQuestionFragments.get(position);

        for (int i = 0; i < currentList.size(); i++) {

            if (currentList.get(i).getmQuestion().isVisited() && currentList.get(i).getmQuestion().isAnswered()) {
                answered++;
            }
        }

        return answered;
    }

    public int getTotalNotAnswered() {

        return notAnswered;
    }

    public int getTotalNotAnsweredOfThisSection(int position) {

        int notAnswered = 0;

        List<SingleQuestionFragment> currentList = allQuestionFragments.get(position);

        for (int i = 0; i < currentList.size(); i++) {

            if (currentList.get(i).getmQuestion().isVisited() && !currentList.get(i).getmQuestion().isAnswered()) {
                notAnswered++;
            }
        }

        return notAnswered;
    }

    public int getTotalNotVisited() {

        return allQuestionFragments.get(sectionIndex).size() - (answered + notAnswered);
    }

    public int getTotalNotVisitedOfThisSection(int position) {

        return allQuestionFragments.get(position).size() - (getTotalAnsweredOfThisSection(position) + getTotalNotAnsweredOfThisSection(position));
    }

    public int getTotalMarked() {

        return marked;
    }

    public int getTotalSectionCorrectCount(int position) {

        int correct = 0;

        for (SingleQuestionFragment fragment : allQuestionFragments.get(position)) {

            correct += fragment.getThisQuestionObtainedMarks() > 0 ? 1 : 0;
        }

        return correct;

    }

    public int getTotalSectionInCorrectCount(int position) {

        int incorrect = 0;

        for (SingleQuestionFragment fragment : allQuestionFragments.get(position)) {

            if (fragment.getmQuestion().isAnswered()) {

                int value = fragment.getThisQuestionObtainedMarks() > 0 ? 0 : 1;
                incorrect += value;

            }
        }

        return incorrect;
    }

    public double getObtainedMarksForThisSection(int position) {

        double obtained = 0;

        for (SingleQuestionFragment fragment : allQuestionFragments.get(position)) {

            obtained += fragment.getThisQuestionObtainedMarks();
        }

        return obtained;
    }

    public double getNegativeMarksForThisSection(int position) {

        double negative = 0;

        for (SingleQuestionFragment fragment : allQuestionFragments.get(position)) {

            negative += fragment.getThisQuestionNegativeMarks();
        }

        return negative;
    }

    public int getAccuracyOfThisSection(int position) {

        int accuracy = 0;

        if (getTotalAnswered() != 0) {

            accuracy = (int) (getTotalSectionCorrectCount(position) / (getTotalAnsweredOfThisSection(position) * 1f) * 100);

        }

        return accuracy;
    }
    // Result things


    public QuizLogic getLogicLayer() {
        return logicLayer;
    }

    // Helpers

    public QuizQuestion getCurrentSectionQuestionAt(int position) {

        return allQuestionFragments.get(sectionIndex).get(position).getmQuestion();
    }

    public int getSectionIndex() {
        return sectionIndex;
    }

    public void lockCurrentAndMoveTo(int position) {

        selectThisIndexedSection(position);

    }


    public boolean isPractice() {

        return logicLayer.isPractice();
    }


    public void showAllResult() {

        for (int i = 0; i < allQuestionFragments.size(); i++) {

            for (int j = 0; j < allQuestionFragments.get(i).size(); j++) {

                allQuestionFragments.get(i).get(j).markCorrectQuestion();
            }
        }
    }

}
