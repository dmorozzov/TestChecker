package ru.dmorozzov.testchecker.util;


import ru.dmorozzov.testchecker.model.Answer;
import ru.dmorozzov.testchecker.model.CandidateTestDocument;
import ru.dmorozzov.testchecker.model.EtalonTestDocument;

import java.util.Set;

/**
 * Created by dmorozzov on 10.04.2016.
 */
public class Checker {
    public static void check (EtalonTestDocument etalon, CandidateTestDocument candidate) {
        double correctAnswers = 0;
        Set<String> etalonVariants;
        Set<String> candidateVariants;

        Set<String> etalonQuestions = etalon.getQuestions();
        for (String question : etalonQuestions) {
            Answer candAnswer = candidate.getAnswer(question);
            if (candAnswer != null) {
                Answer etalonAnswer = etalon.getAnswer(question);
                etalonVariants = etalonAnswer.getEtalonVariants();
                candidateVariants = candAnswer.getCandidateVariants();

                //System.out.println(question +" "+etalonVariants.equals(candidateVariants) + " " +etalonVariants+" "+candidateVariants);

                if (etalonVariants.equals(candidateVariants)) {
                    correctAnswers++;
                    candAnswer.setCorrect(true);
                } else {
                    candAnswer.setEtalonVariants(etalonVariants);
                    candAnswer.setCorrect(false);
                }
            }
        }
        double score = (correctAnswers / etalonQuestions.size()) * 100;
        candidate.setScore(score);
    }
}
