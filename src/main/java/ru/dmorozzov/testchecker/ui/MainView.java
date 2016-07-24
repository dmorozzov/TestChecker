package ru.dmorozzov.testchecker.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.*;
import ru.dmorozzov.testchecker.model.CandidateTestDocument;
import ru.dmorozzov.testchecker.model.EtalonTestDocument;
import ru.dmorozzov.testchecker.model.TestDocument;
import ru.dmorozzov.testchecker.util.Checker;

import java.io.File;

/**
 * Created by dmorozzov on 12.03.2016.
 */
public class MainView {

    private Display display;
    private TestCheckerShell testCheckerShell;
    private SelectionListener chooseEtalonListener;
    private SelectionListener chooseCandidatesListener;
    private SelectionListener runCheckListener;

    private final static String APP_NAME = "Тестопроверятель";

    private String fileFilterPath;
    private FileDialog etalonFileDialog;
    private FileDialog testsFileDialog;

    private final String[] filterNames = new String[]{"All files", ".doc", ".docx"};
    private final String[] filterExtensions = new String[]{"*.*", "*.doc", "*.docx"};

    MainView() {
        initMainView();
        disposeMainView();
    }

    private void initMainView() {
        display = new Display();
        testCheckerShell = new TestCheckerShell(display);
        initFileDialogs();
        initControlListeners();
        addControlListeners();
        testCheckerShell.pack();
        testCheckerShell.open();
    }

    private void initFileDialogs() {
        String envHome = System.getProperty("user.home");
        fileFilterPath = (envHome != null && !envHome.isEmpty()) ? envHome : "C:\\";
        etalonFileDialog = new FileDialog(testCheckerShell, SWT.OPEN);
        etalonFileDialog.setFilterPath(fileFilterPath);
        etalonFileDialog.setFilterNames(filterNames);
        etalonFileDialog.setFilterExtensions(filterExtensions);

        testsFileDialog = new FileDialog(testCheckerShell, SWT.MULTI);
        testsFileDialog.setFilterPath(fileFilterPath);
        testsFileDialog.setFilterNames(filterNames);
        testsFileDialog.setFilterExtensions(filterExtensions);
    }

    private String getEtalonFilePath() {
        String selectedFile = etalonFileDialog.getFileName();
        if (selectedFile != null && !selectedFile.isEmpty()) {
            return etalonFileDialog.getFilterPath() + File.separator + selectedFile;
        }
        return null;
    }

    private void initControlListeners() {
        chooseEtalonListener = new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                String selectedFile = etalonFileDialog.open();
                if (selectedFile != null) {
                    testCheckerShell.getTextEtalonFile().setText(selectedFile);
                    testsFileDialog.setFilterPath(selectedFile.substring(0, selectedFile.lastIndexOf(File.separator)));
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
            }
        };
        chooseCandidatesListener = new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                String selectedFile = testsFileDialog.open();
                if (selectedFile != null) {
                    testCheckerShell.getTextCandFile().setText(selectedFile);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
            }
        };
        runCheckListener = new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                testCheckerShell.getListCheckedTests().removeAll();
                testCheckerShell.getListInfo().removeAll();

                String path;
                EtalonTestDocument etalon;
                CandidateTestDocument candidate;

                String etalonPath = getEtalonFilePath();
                if (etalonPath == null) {
                    return;
                }

                etalon = (EtalonTestDocument) new TestDocument.TestDocumentBuilder("Etalon").setAsEtalon()
                        .extractText(etalonPath).build();

                String selectedFile = testsFileDialog.getFileName();
                if (selectedFile != null && !selectedFile.isEmpty()) {
                    String[] selectedFiles = testsFileDialog.getFileNames();
                    for (int i = 0; i < selectedFiles.length; i++) {
                        path = testsFileDialog.getFilterPath() + File.separator + selectedFiles[i];
                        candidate = (CandidateTestDocument) new TestDocument.TestDocumentBuilder(selectedFiles[i])
                                .extractText(path).build();

                        Checker.check(etalon, candidate);
                        testCheckerShell.getListCheckedTests().add((i+1)+ ") " +candidate.getName() + ":  " + candidate.getScore() + "%");
                        testCheckerShell.getListInfo().add((i+1)+ ") " +candidate.getWarnings());
                        if (testCheckerShell.getButtonCreateReports().getSelection()) {
                            candidate.writeToFileSystem(testCheckerShell.getButtonAppendRights().getSelection());
                        }
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
            }
        };
    }

    private void addControlListeners() {
        testCheckerShell.getBtnChooseEtalonFile().addSelectionListener(chooseEtalonListener);
        testCheckerShell.getBtnChooseCandFiles().addSelectionListener(chooseCandidatesListener);
        testCheckerShell.getBtnRunCheck().addSelectionListener(runCheckListener);
    }

    private void removeListeners() {
        if (!testCheckerShell.isDisposed()) {
            testCheckerShell.getBtnChooseEtalonFile().removeSelectionListener(chooseEtalonListener);
            testCheckerShell.getBtnChooseCandFiles().removeSelectionListener(chooseCandidatesListener);
            testCheckerShell.getBtnRunCheck().removeSelectionListener(runCheckListener);
        }
    }

    private void disposeMainView() {
        while (!testCheckerShell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        removeListeners();
        testCheckerShell.dispose();
        display.dispose();
    }
}
