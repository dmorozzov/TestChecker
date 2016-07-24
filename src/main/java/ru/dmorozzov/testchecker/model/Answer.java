package ru.dmorozzov.testchecker.model;

import java.util.Set;

/**
 * Created by dmorozzov on 01.04.2016.
 */
public class Answer {

    private String candidateAnswer;
    private Set<String> candidateVariants;
    private Set<String> etalonVariants;
    private boolean correct;

    public Answer(String candidateAnswer, Set<String> variants, boolean correct) {
        this.candidateAnswer = candidateAnswer;
        this.correct = correct;
        if (correct) {
            this.etalonVariants = variants;
        } else {
            this.candidateVariants = variants;
        }
    }

    public void showCandidates() {
        if (correct) {
            etalonVariants.forEach(candidate -> System.out.print(candidate+", "));
        } else {
            candidateVariants.forEach(candidate -> System.out.print(candidate+", "));
        }
    }

    public Answer(Set<String> candidateVariants) {
        this.candidateVariants = candidateVariants;
    }

    public Set<String> getEtalonVariants() {
        return etalonVariants;
    }

    public void setEtalonVariants(Set<String> etalonVariants) {
        this.etalonVariants = etalonVariants;
    }

    public Set<String> getCandidateVariants() {
        return candidateVariants;
    }

    public void setCandidateVariants(Set<String> candidateVariants) {
        this.candidateVariants = candidateVariants;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getCandidateAnswer() {
        return candidateAnswer;
    }

    public void setCandidateAnswer(String candidateAnswer) {
        this.candidateAnswer = candidateAnswer;
    }
}
