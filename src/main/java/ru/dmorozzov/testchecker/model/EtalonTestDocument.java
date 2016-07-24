package ru.dmorozzov.testchecker.model;

/**
 * Created by dmorozzov on 26.03.2016.
 */
public class EtalonTestDocument extends TestDocument {
    public EtalonTestDocument(TestDocumentBuilder builder) {
        super(builder);
    }

    public void showTest() {
        System.out.println("===========================================");
        System.out.println("Test: "+testDocumentName+", isEtalon");
        //getQuestions().forEach(question -> {System.out.print(question +": "); ((Answer)getAnswer(question)).showCandidates(); System.out.println();});
        System.out.println("Warnings: ");System.out.print(warningStackBuilder);
        System.out.println("===========================================");
    }
}
