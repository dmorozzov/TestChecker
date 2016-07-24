package ru.dmorozzov.testchecker.ui;

/**
 * Created by dmorozzov on 23.04.2016.
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Point;

public class TestCheckerShell extends Shell {
    private Text textEtalonFile;
    private Text textCandFile;
    private Button btnChooseEtalonFile;
    private Button btnChooseCandFiles;
    private Button buttonCreateReports;
    private Button buttonAppendRights;
    private Button btnRunCheck;
    private List listCheckedTests;
    private List listInfo;

    /**
     * Create the shell.
     *
     * @param display
     */
    public TestCheckerShell(Display display) {
        super(display, SWT.SHELL_TRIM);
        setMinimumSize(new Point(600, 400));
        FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
        fillLayout.marginHeight = 5;
        fillLayout.marginWidth = 10;
        setLayout(fillLayout);

        Composite compositeCommon = new Composite(this, SWT.NONE);
        GridLayout gl_compositeCommon = new GridLayout(3, false);
        gl_compositeCommon.verticalSpacing = 6;
        gl_compositeCommon.marginWidth = 7;
        compositeCommon.setLayout(gl_compositeCommon);

        Label lblEtalonFile = new Label(compositeCommon, SWT.NONE);
        lblEtalonFile.setBounds(0, 0, 70, 20);
        lblEtalonFile.setText("\u042D\u0442\u0430\u043B\u043E\u043D\u043D\u044B\u0439 \u0442\u0435\u0441\u0442");

        textEtalonFile = new Text(compositeCommon, SWT.BORDER);
        GridData gd_textEtalonFile = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_textEtalonFile.minimumWidth = 200;
        textEtalonFile.setLayoutData(gd_textEtalonFile);
        textEtalonFile.setBounds(0, 0, 78, 26);

        btnChooseEtalonFile = new Button(compositeCommon, SWT.NONE);
        btnChooseEtalonFile.setBounds(0, 0, 90, 30);
        btnChooseEtalonFile.setText("\u0412\u044B\u0431\u0440\u0430\u0442\u044C \u0444\u0430\u0439\u043B");

        Label lblCandFile = new Label(compositeCommon, SWT.NONE);
        lblCandFile.setBounds(0, 0, 70, 20);
        lblCandFile.setText("\u041F\u0440\u043E\u0432\u0435\u0440\u044F\u0435\u043C\u044B\u0435 \u0442\u0435\u0441\u0442\u044B");

        textCandFile = new Text(compositeCommon, SWT.BORDER);
        GridData gd_textCandFile = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_textCandFile.minimumWidth = 200;
        textCandFile.setLayoutData(gd_textCandFile);
        textCandFile.setBounds(0, 0, 78, 26);

        btnChooseCandFiles = new Button(compositeCommon, SWT.NONE);
        btnChooseCandFiles.setText("\u0412\u044B\u0431\u0440\u0430\u0442\u044C \u0444\u0430\u0439\u043B\u044B");

        Group group = new Group(compositeCommon, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        group.setText("\u041E\u043F\u0446\u0438\u0438");
        FillLayout fl_group = new FillLayout(SWT.HORIZONTAL);
        fl_group.marginHeight = 10;
        fl_group.spacing = 5;
        group.setLayout(fl_group);

        buttonCreateReports = new Button(group, SWT.CHECK);
        buttonCreateReports.setText("\u0421\u043E\u0437\u0434\u0430\u0432\u0430\u0442\u044C \u043E\u0442\u0447\u0435\u0442\u044B");
        buttonCreateReports.setSelection(true);

        buttonAppendRights = new Button(group, SWT.CHECK);
        buttonAppendRights.setText("\u0414\u043E\u043F\u0438\u0441\u044B\u0432\u0430\u0442\u044C \u043F\u0440\u0430\u0432\u0438\u043B\u044C\u043D\u044B\u0435 \u043E\u0442\u0432\u0435\u0442\u044B");
        buttonAppendRights.setSelection(true);

        listCheckedTests = new List(compositeCommon, SWT.BORDER | SWT.H_SCROLL);
        listCheckedTests.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));

        btnRunCheck = new Button(compositeCommon, SWT.CENTER);
        GridData gd_btnRunCheck = new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1);
        gd_btnRunCheck.heightHint = 60;
        gd_btnRunCheck.minimumHeight = 50;
        btnRunCheck.setLayoutData(gd_btnRunCheck);
        btnRunCheck.setText("\u041F\u0440\u043E\u0432\u0435\u0440\u0438\u0442\u044C");

        Composite compositeInfo = new Composite(this, SWT.BORDER);
        compositeInfo.setToolTipText("");
        compositeInfo.setLayout(new FillLayout(SWT.VERTICAL));

        listInfo = new List(compositeInfo, SWT.BORDER | SWT.H_SCROLL);
        listInfo.setItems(new String[]{"\u0418\u043D\u0444\u043E\u0440\u043C\u0430\u0446\u0438\u044F \u043E \u0432\u044B\u043F\u043E\u043B\u043D\u0435\u043D\u0438\u0438:"});
        //listInfo.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
        listInfo.setToolTipText("\u0418\u043D\u0444\u043E\u0440\u043C\u0430\u0446\u0438\u044F");
        createContents();
    }

    /**
     * Create contents of the shell.
     */
    protected void createContents() {
        setText("\u0422\u0435\u0441\u0442\u043E\u043F\u0440\u043E\u0432\u0435\u0440\u044F\u0442\u0435\u043B\u044C");
        setSize(640, 550);

    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public Text getTextEtalonFile() {
        return textEtalonFile;
    }

    public Text getTextCandFile() {
        return textCandFile;
    }

    public Button getBtnChooseEtalonFile() {
        return btnChooseEtalonFile;
    }

    public Button getBtnChooseCandFiles() {
        return btnChooseCandFiles;
    }

    public Button getButtonCreateReports() {
        return buttonCreateReports;
    }

    public Button getButtonAppendRights() {
        return buttonAppendRights;
    }

    public Button getBtnRunCheck() {
        return btnRunCheck;
    }

    public List getListCheckedTests() {
        return listCheckedTests;
    }

    public List getListInfo() {
        return listInfo;
    }
}