package ru.dmorozzov.testchecker.model;

import org.apache.poi.hwpf.usermodel.Range;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.xwpf.usermodel.*;
import ru.dmorozzov.testchecker.util.TestUtil;

/**
 * Created by dmorozzov on 26.03.2016.
 */
public class CandidateTestDocument extends TestDocument {

    private double score;

    public CandidateTestDocument(TestDocumentBuilder builder) {
        super(builder);
    }


    public void showTest() {
        System.out.println("===========================================");
        System.out.println("Test: " + testDocumentName);
        //getQuestions().forEach(question -> {System.out.print(question +": "); ((Answer)getAnswer(question)).showCandidates(); System.out.println();});
        System.out.println("Warnings: ");
        System.out.print(warningStackBuilder);
        System.out.println("===========================================");
    }

    public String getWarnings() {
        StringBuilder warningBuilder = new StringBuilder(testDocumentName).append(": ");
        warningBuilder.append(warningStackBuilder.isEmpty() ? "ok" : warningStackBuilder);
        warningBuilder.append(".");
        return warningBuilder.toString();
    }

    private void removeAllDocxParagraphs(XWPFDocument doc) {
        List<IBodyElement> bodyElements = doc.getBodyElements();
        int i = 0;
        while (bodyElements.size() > 0) {
            IBodyElement element = bodyElements.get(i);
            if (element.getElementType() == BodyElementType.PARAGRAPH) {
                doc.removeBodyElement(i);
                i = bodyElements.size() - 1;
            }
        }
    }

    public void writeToFileSystem(boolean appendRights) {

        List<String> listQuestion = new ArrayList(getQuestions());
        Collections.sort(listQuestion, new QuestionComparator());

        if (isDocx()) {
            removeAllDocxParagraphs(xwpfDocument);
            XWPFParagraph tmpParagraph;
            XWPFRun tmpRun;

            for (String question : listQuestion) {
                tmpParagraph = xwpfDocument.createParagraph();
                Answer answer = getAnswer(question);
                tmpRun = tmpParagraph.createRun();
                tmpRun.setText(checkedRow(question, answer, appendRights));
            }
            tmpParagraph = xwpfDocument.createParagraph();
            tmpRun = tmpParagraph.createRun();
            tmpRun.setText("Результат: " + score + "%");
        }

        if (isDoc()) {
            Range range = hwpfDocument.getRange();
            range.replaceText("", false);

            for (String question : listQuestion) {
                Answer answer = getAnswer(question);
                range.insertAfter(checkedRow(question, answer, appendRights));
                range.insertAfter(TestUtil.DOC_NEW_LINE_APPENDER);
            }
            range.insertAfter("Результат: " + score + "%");
        }

        String oldFullPath = filePath.toString();
        int lastSeparatorIndex = oldFullPath.lastIndexOf(File.separator);
        int lastDotIndex = oldFullPath.lastIndexOf(".");

        StringBuilder newFullPath = new StringBuilder(oldFullPath.substring(0, lastSeparatorIndex + 1));
        newFullPath.append(TestUtil.TEST_CHECKED_FOLDER).append(File.separator);
        checkDirExist(newFullPath.toString());
        newFullPath
                .append(oldFullPath.substring(lastSeparatorIndex + 1, lastDotIndex))
                .append(TestUtil.TEST_CHECKED)
                .append(oldFullPath.substring(lastDotIndex));

        try (FileOutputStream out = new FileOutputStream(newFullPath.toString())) {
            if (isDoc()) {
                hwpfDocument.write(out);
            }
            if (isDocx()) {
                xwpfDocument.write(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkDirExist(String path) {
        Path newPath = Paths.get(path.toString());
        if (!Files.exists(newPath)) {
            try {
                Files.createDirectory(newPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class QuestionComparator implements Comparator<String> {
        public int compare(String quest1, String quest2) {
            int q1 = Integer.parseInt(quest1);
            int q2 = Integer.parseInt(quest2);
            return q1 - q2;
        }
    }

    private String checkedRow(String question, Answer answer, boolean appendRights) {
        StringBuilder row = new StringBuilder();
        row.append(question)
                .append(") \"")
                .append(answer.getCandidateAnswer().trim())
                .append("\"  ");
        if (!answer.isCorrect()) {
            row.append("(-)");
            if (appendRights) {
                row.append(" Правильный ответ: [")
                        .append(String.join(", ", answer.getEtalonVariants()))
                        .append("]");
            }
        } else {
            row.append("(+)");
        }
        return row.toString();
    }

    public String getName() {
        return testDocumentName;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
