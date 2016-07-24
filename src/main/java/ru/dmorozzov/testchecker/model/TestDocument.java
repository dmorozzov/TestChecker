package ru.dmorozzov.testchecker.model;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.*;
import ru.dmorozzov.testchecker.util.TestUtil;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dmorozzov on 26.03.2016.
 */
public abstract class TestDocument {

    protected String testDocumentName;
    protected String warningStackBuilder;
    protected int questionCount;
    protected HashMap<String, Answer> questions;
    protected Path filePath;
    protected String fileExtension;
    protected String fileType;
    protected HWPFDocument hwpfDocument;
    protected XWPFDocument xwpfDocument;

    protected TestDocument(TestDocumentBuilder builder) {
        this.testDocumentName = builder.documentName;
        this.questions = builder.documentQuestions;
        this.warningStackBuilder = builder.warningStackBuilder.toString();

        this.questionCount = builder.questionCount;
        this.filePath = builder.filePath;
        this.fileExtension = builder.fileExt;
        this.fileType = builder.fileType;
        this.hwpfDocument = builder.hwpfDocument;
        this.xwpfDocument = builder.xwpfDocument;
    }

    public Set<String> getQuestions() {
        return questions.keySet();
    }

    public Answer getAnswer(String questionName) {
        return questions.get(questionName);
    }

    public abstract void showTest();

    @Override
    public String toString() {
        return testDocumentName + " " + questionCount;
    }

    protected boolean isDocx() {
        return "docx".equals(fileType);
    }

    protected boolean isDoc() {
        return "doc".equals(fileType);
    }

    public static class TestDocumentBuilder {
        private final static String NOT_THE_ONLY_ONE_NUMBERED_LIST = "В тесте не единственный нумерованный список ответов";
        private final static String NOT_THE_ONLY_ONE_ANSWER_FORMAT = "Разный формат ответов (нумерация ответов)";
        private final static String NOT_PROPERLY_FORMAT = "Были допущены ошибки при формировании ответов (например, нет ответа на вопрос)";

        private boolean oneNumberedList = true;

        private Pattern wholeRowNonMarkedPattern = Pattern.compile("^\\d+(\\.|\\)|:|;)\\s*(\\P{Alpha}|\\w)*$");
        private Pattern questionPattern = Pattern.compile("\\d+(\\.|\\)|:|;|-|\\s)");
        private Pattern answersPattern = Pattern.compile("\\.|:|;|,|\\)");

        private String documentName;
        private boolean etalon;
        private int questionCount;
        private StringBuilder warningStackBuilder;
        private HashMap<String, Answer> documentQuestions;
        private Path filePath;
        private String fileExt;
        private String fileType;
        private HWPFDocument hwpfDocument;
        private XWPFDocument xwpfDocument;

        private int nonMarkedNumber = 0;
        private int markedNumber = 0;

        public TestDocumentBuilder(String documentName) {
            this.documentName = documentName;
            documentQuestions = new HashMap<>();
            warningStackBuilder = new StringBuilder();
        }

        public TestDocumentBuilder setAsEtalon() {
            etalon = true;
            return this;
        }

        public TestDocumentBuilder extractText(String stringPath) {
            try {
                filePath = Paths.get(stringPath);
                fileExt = getExtension(stringPath);
                String fileProbe = Files.probeContentType(filePath);

                if ((fileExt != null && ("docx".equalsIgnoreCase(fileExt) || fileProbe.contains("xml")))) {
                    fileType = "docx";
                    readDocxFile(new File(stringPath));
                } else if ((fileExt != null && ("doc".equalsIgnoreCase(fileExt) || fileProbe.contains("msword")))) {
                    fileType = "doc";
                    readDocFile(new File(stringPath));
                }

                if (markedNumber > 0 && nonMarkedNumber > 0) {
                    appendWarning("WARN", NOT_THE_ONLY_ONE_ANSWER_FORMAT);
                }

                if (documentQuestions.size() != questionCount) {
                    appendWarning("WARN", NOT_PROPERLY_FORMAT);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }

        public void readDocFile(File file) {
            int commonIlfo = 1;
            int currentIlfo = 0;
            Paragraph p = null;

            try (FileInputStream fis = new FileInputStream(file)) {
                hwpfDocument = new HWPFDocument(fis);
                Range r = hwpfDocument.getRange();
                for (int i = 0; i < r.numParagraphs(); i++) {
                    p = r.getParagraph(i);
                    currentIlfo = p.getIlfo();
                    if (currentIlfo > 0) {
                        markedNumber++;
                        parseMarked(markedNumber, p.text());
                        if (commonIlfo != currentIlfo) {
                            commonIlfo = currentIlfo;
                            if (!oneNumberedList) {
                                oneNumberedList = false;
                                appendWarning("parse doc", NOT_THE_ONLY_ONE_NUMBERED_LIST);
                            }
                        }
                    } else {
                        nonMarkedNumber++;
                        parseNonMarked(p.text());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void readDocxFile(File file) {
            XWPFParagraph para = null;
            List<XWPFParagraph> paraList = null;
            Iterator<XWPFParagraph> paraIter = null;
            XWPFNumbering numbering = null;
            BigInteger numID = null;
            XWPFNum num = null;
            String paragraphText = null;
            int numberingID = -1;

            try (FileInputStream fis = new FileInputStream(file)) {
                xwpfDocument = new XWPFDocument(fis);
                paraList = xwpfDocument.getParagraphs();
                numbering = xwpfDocument.getNumbering();
                paraIter = paraList.iterator();

                while (paraIter.hasNext()) {
                    para = paraIter.next();
                    numID = para.getNumID();
                    paragraphText = para.getParagraphText();
                    if (numID != null) {
                        if (numID.intValue() != numberingID) {
                            num = numbering.getNum(numID);
                            numberingID = numID.intValue();
                            if (!oneNumberedList) {
                                oneNumberedList = false;
                                appendWarning("parse docx", NOT_THE_ONLY_ONE_NUMBERED_LIST);
                            }
                        }
                        markedNumber++;
                        parseMarked(markedNumber, paragraphText);
                    } else {
                        nonMarkedNumber++;
                        parseNonMarked(paragraphText);
                    }
                }
            } catch (FileNotFoundException fileNotFound) {
                fileNotFound.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        private void parseNonMarked(String paragraph) {
            String question;
            String answer;

            Matcher matcher = wholeRowNonMarkedPattern.matcher(paragraph);
            if (matcher.matches()) {
                matcher = questionPattern.matcher(paragraph);
                if (matcher.find()) {
                    question = paragraph.substring(matcher.start(), matcher.end() - 1);
                    answer = paragraph.substring(matcher.end(), paragraph.length());
                    addToAnsweredQuestionSet(question, answer);
                }
            }
        }

        private void parseMarked(int markedNum, String paragraph) {
            addToAnsweredQuestionSet(String.valueOf(markedNum), paragraph);
        }

        private void addToAnsweredQuestionSet(String question, String candAnswer) {
            TreeSet answerSet;
            String variant;
            String[] answers = answersPattern.split(candAnswer);

            if (answers.length > 0) {
                answerSet = new TreeSet();
                for (int i = 0; i < answers.length; i++) {
                    variant = answers[i].trim();
                    if (!variant.isEmpty()) {
                        answerSet.add(variant.toLowerCase());
                    }
                }
                Answer completeAnswer = new Answer(candAnswer, answerSet, etalon);
                if (!answerSet.isEmpty()) {
                    questionCount++;
                } else {
                    completeAnswer.setCandidateAnswer("Нет ответа");
                }
                documentQuestions.put(question, completeAnswer);
            }
        }

        private String getExtension(String fileName) {
            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                return fileName.substring(i + 1);
            }
            return null;
        }

        private void appendWarning(String subject, String warning) {
            warningStackBuilder.append(TestUtil.APP_TAB_APPENDER).append(subject + ": " + warning).append(TestUtil.APP_NEW_LINE_APPENDER);
        }

        public TestDocument build() {
            if (etalon) {
                return new EtalonTestDocument(this);
            } else {
                return new CandidateTestDocument(this);
            }
        }
    }
}
