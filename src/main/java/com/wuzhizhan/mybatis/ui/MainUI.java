package com.wuzhizhan.mybatis.ui;


import com.intellij.database.psi.DbTable;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.wuzhizhan.mybatis.generate.Generate;
import com.wuzhizhan.mybatis.model.Config;
import com.wuzhizhan.mybatis.model.TableInfo;
import com.wuzhizhan.mybatis.setting.PersistentConfig;
import com.wuzhizhan.mybatis.util.JTextFieldHintListener;
import com.wuzhizhan.mybatis.util.StringUtils;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * 插件主界面
 * Created by kangtian on 2018/8/1.
 */
public class MainUI extends JFrame {


    private AnActionEvent anActionEvent;
    private Project project;
    private PersistentConfig persistentConfig;
    private PsiElement[] psiElements;
    private Map<String, Config> initConfigMap;
    private Map<String, Config> historyConfigList;
    private Config config;


    private JPanel contentPane = new JBPanel<>();
    private JButton buttonOK = new JButton("ok");
    private JButton buttonCancel = new JButton("cancel");
//    private JButton selectConfigBtn = new JButton("SELECT");
    private JButton deleteConfigBtn = new JButton("DELETE");


    private JTextField tableNameField = new JTextField(10);
    private JBTextField modelPackageField = new JBTextField(12);
    private JBTextField daoPackageField = new JBTextField(12);
    private JBTextField xmlPackageField = new JBTextField(12);
    private JTextField daoNameField = new JTextField(10);
    private JTextField modelNameField = new JTextField(10);
    private JTextField keyField = new JTextField(10);

    private TextFieldWithBrowseButton projectFolderBtn = new TextFieldWithBrowseButton();
    private JTextField modelMvnField = new JBTextField(15);
    private JTextField daoMvnField = new JBTextField(15);
    private JTextField xmlMvnField = new JBTextField(15);

    private JCheckBox offsetLimitBox = new JCheckBox("Page(分页)");
    private JCheckBox commentBox = new JCheckBox("comment(实体注释)");
    private JCheckBox overrideXMLBox = new JCheckBox("Overwrite-Xml");
    private JCheckBox needToStringHashcodeEqualsBox = new JCheckBox("toString/hashCode/equals");
    private JCheckBox useSchemaPrefixBox = new JCheckBox("Use-Schema(使用Schema前缀)");
    private JCheckBox needForUpdateBox = new JCheckBox("Add-ForUpdate(select增加ForUpdate)");
    private JCheckBox annotationDAOBox = new JCheckBox("Repository-Annotation(Repository注解)");
    private JCheckBox useDAOExtendStyleBox = new JCheckBox("Parent-Interface(公共父接口)");
    private JCheckBox jsr310SupportBox = new JCheckBox("JSR310: Date and Time API");
    private JCheckBox annotationBox = new JCheckBox("JPA-Annotation(JPA注解)");
    private JCheckBox useActualColumnNamesBox = new JCheckBox("Actual-Column(实际的列名)");
    private JCheckBox useTableNameAliasBox = new JCheckBox("Use-Alias(启用别名查询)");
    private JCheckBox useExampleBox = new JCheckBox("Use-Example");
    private JCheckBox mysql_8Box = new JCheckBox("mysql_8");



    public MainUI(AnActionEvent anActionEvent) throws HeadlessException {
        this.anActionEvent = anActionEvent;
        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        this.persistentConfig = PersistentConfig.getInstance(project);
        this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        initConfigMap = persistentConfig.getInitConfig();
        historyConfigList = persistentConfig.getHistoryConfigList();



        setTitle("mybatis generate tool");
        setPreferredSize(new Dimension(1200, 700));//设置大小
        setLocation(120, 100);
        pack();
        setVisible(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        PsiElement psiElement = psiElements[0];
        TableInfo tableInfo = new TableInfo((DbTable) psiElement);
        String tableName = tableInfo.getTableName();
        String modelName = StringUtils.dbStringToCamelStyle(tableName);
        String primaryKey = "";
        if(tableInfo.getPrimaryKeys().size()>0){
            primaryKey = tableInfo.getPrimaryKeys().get(0);
        }
        String projectFolder = project.getBasePath();


        if (psiElements.length > 1) {//多表时，只使用默认配置
            if (initConfigMap != null) {
                config = initConfigMap.get("initConfig");
            }
        } else {
            if (initConfigMap != null) {//单表时，优先使用已经存在的配置
                config = initConfigMap.get("initConfig");
            }
            if (historyConfigList == null) {
                historyConfigList = new HashMap<>();
            } else {
                if (historyConfigList.containsKey(tableName)) {
                    config = historyConfigList.get(tableName);
                }
            }
        }







        /**
         * table setting
         */
        JPanel tableNameFieldPanel = new JPanel();
        tableNameFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel tablejLabel = new JLabel("table  name:");
        tablejLabel.setSize(new Dimension(20, 30));
        tableNameFieldPanel.add(tablejLabel);
        if (psiElements.length > 1) {
            tableNameField.addFocusListener(new JTextFieldHintListener(tableNameField, "eg:db_table"));
        } else {
            tableNameField.setText(tableName);
        }
        tableNameFieldPanel.add(tableNameField);

        JPanel keyFieldPanel = new JPanel();
        keyFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        keyFieldPanel.add(new JLabel("主键（选填）:"));
        if (psiElements.length > 1) {
            keyField.addFocusListener(new JTextFieldHintListener(keyField, "eg:primary key"));
        } else {
            keyField.setText(primaryKey);
        }
        keyFieldPanel.add(keyField);

        JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tablePanel.setBorder(BorderFactory.createTitledBorder("table setting"));
        tablePanel.add(tableNameFieldPanel);
        tablePanel.add(keyFieldPanel);



        /**
         * project panel
         */
        JPanel projectFolderPanel = new JPanel();
        projectFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel projectLabel = new JLabel("project folder:");
        projectFolderPanel.add(projectLabel);
        projectFolderBtn.setTextFieldPreferredWidth(45);
        if (config != null && !StringUtils.isEmpty(config.getProjectFolder())) {
            projectFolderBtn.setText(config.getProjectFolder());
        } else {
            projectFolderBtn.setText(projectFolder);
        }
        projectFolderBtn.addBrowseFolderListener(new TextBrowseFolderListener(
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                projectFolderBtn.setText(projectFolderBtn.getText().replaceAll("\\\\", "/"));
            }
        });
        projectFolderPanel.add(projectFolderBtn);



        /**
         * model setting
         */
        JPanel modelPanel = new JPanel();
        modelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        modelPanel.setBorder(BorderFactory.createTitledBorder("model setting"));

        JPanel modelNameFieldPanel = new JPanel();
        modelNameFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        modelNameFieldPanel.add(new JLabel("file:"));
        if (psiElements.length > 1) {
            modelNameField.addFocusListener(new JTextFieldHintListener(modelNameField, "eg:DbTable"));
        } else {
            modelNameField.setText(modelName);
        }
        modelNameFieldPanel.add(modelNameField);
        modelPanel.add(modelNameFieldPanel);

        JBLabel labelLeft4 = new JBLabel("package:");
        modelPanel.add(labelLeft4);
        if (config != null && !StringUtils.isEmpty(config.getModelPackage())) {
            modelPackageField.setText(config.getModelPackage());
        } else {
            modelPackageField.setText("generator");
        }
        modelPanel.add(modelPackageField);
        JButton modelPackageFieldBtn = new JButton("...");
        modelPackageFieldBtn.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("chooser model package", project);
            chooser.selectPackage(modelPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            modelPackageField.setText(packageName);
            MainUI.this.toFront();
        });
        modelPanel.add(modelPackageFieldBtn);
        modelPanel.add(new JLabel("path:"));
        modelMvnField.setText("src/main/java");
        modelPanel.add(modelMvnField);



        /**
         * dao setting
         */
        JPanel daoPanel = new JPanel();
        daoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        daoPanel.setBorder(BorderFactory.createTitledBorder("dao setting"));

        daoPanel.add(new JLabel("name:"));
        if (psiElements.length > 1) {
            if (config != null && !StringUtils.isEmpty(config.getDaoPostfix())) {
                daoNameField.addFocusListener(new JTextFieldHintListener(daoNameField, "eg:DbTable" + config.getDaoPostfix()));
            } else {
                daoNameField.addFocusListener(new JTextFieldHintListener(daoNameField, "eg:DbTable" + "Dao"));
            }
        } else {
            if (config != null && !StringUtils.isEmpty(config.getDaoPostfix())) {
                daoNameField.setText(modelName + config.getDaoPostfix());
            } else {
                daoNameField.setText(modelName + "Dao");
            }
        }
        daoPanel.add(daoNameField);

        JLabel labelLeft5 = new JLabel("package:");
        daoPanel.add(labelLeft5);
        if (config != null && !StringUtils.isEmpty(config.getDaoPackage())) {
            daoPackageField.setText(config.getDaoPackage());
        } else {
            daoPackageField.setText("generator");
        }
        daoPanel.add(daoPackageField);
        JButton packageBtn2 = new JButton("...");
        packageBtn2.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("choose dao package", project);
            chooser.selectPackage(daoPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            daoPackageField.setText(packageName);
            MainUI.this.toFront();
        });
        daoPanel.add(packageBtn2);
        daoPanel.add(new JLabel("path:"));
        daoMvnField.setText("src/main/java");
        daoPanel.add(daoMvnField);


        /**
         * xml mapper setting
         */
        JPanel xmlMapperPanel = new JPanel();
        xmlMapperPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        xmlMapperPanel.setBorder(BorderFactory.createTitledBorder("xml mapper setting"));
        JLabel labelLeft6 = new JLabel("package:");
        xmlMapperPanel.add(labelLeft6);
        if (config != null && !StringUtils.isEmpty(config.getXmlPackage())) {
            xmlPackageField.setText(config.getXmlPackage());
        } else {
            xmlPackageField.setText("generator");
        }
        xmlMapperPanel.add(xmlPackageField);
        xmlMapperPanel.add(new JLabel("path:"));
        xmlMvnField.setText("src/main/resources");
        xmlMapperPanel.add(xmlMvnField);

        /**
         * options
         */
        JBPanel optionsPanel = new JBPanel(new GridLayout(5, 5, 5, 5));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("options"));
        if (config == null) {
            offsetLimitBox.setSelected(true);
            commentBox.setSelected(true);
            overrideXMLBox.setSelected(true);
            needToStringHashcodeEqualsBox.setSelected(true);
            useSchemaPrefixBox.setSelected(true);
            useExampleBox.setSelected(true);

        } else {
            if (config.isOffsetLimit()) {
                offsetLimitBox.setSelected(true);
            }
            if (config.isComment()) {
                commentBox.setSelected(true);
            }

            if (config.isOverrideXML()) {
                overrideXMLBox.setSelected(true);
            }
            if (config.isNeedToStringHashcodeEquals()) {
                needToStringHashcodeEqualsBox.setSelected(true);
            }
            if (config.isUseSchemaPrefix()) {
                useSchemaPrefixBox.setSelected(true);
            }
            if (config.isNeedForUpdate()) {
                needForUpdateBox.setSelected(true);
            }
            if (config.isAnnotationDAO()) {
                annotationDAOBox.setSelected(true);
            }
            if (config.isUseDAOExtendStyle()) {
                useDAOExtendStyleBox.setSelected(true);
            }
            if (config.isJsr310Support()) {
                jsr310SupportBox.setSelected(true);
            }
            if (config.isAnnotation()) {
                annotationBox.setSelected(true);
            }
            if (config.isUseActualColumnNames()) {
                useActualColumnNamesBox.setSelected(true);
            }
            if (config.isUseTableNameAlias()) {
                useTableNameAliasBox.setSelected(true);
            }
            if (config.isUseExample()) {
                useExampleBox.setSelected(true);
            }
            if (config.isMysql_8()) {
                mysql_8Box.setSelected(true);
            }
        }
        optionsPanel.add(offsetLimitBox);
        optionsPanel.add(commentBox);
        optionsPanel.add(overrideXMLBox);
        optionsPanel.add(needToStringHashcodeEqualsBox);
        optionsPanel.add(useSchemaPrefixBox);
        optionsPanel.add(needForUpdateBox);
        optionsPanel.add(annotationDAOBox);
        optionsPanel.add(useDAOExtendStyleBox);
        optionsPanel.add(jsr310SupportBox);
        optionsPanel.add(annotationBox);
        optionsPanel.add(useActualColumnNamesBox);
        optionsPanel.add(useTableNameAliasBox);
        optionsPanel.add(useExampleBox);
        optionsPanel.add(mysql_8Box);


        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.setBorder(new EmptyBorder(10, 30, 5, 40));
        mainPanel.add(tablePanel);
        mainPanel.add(projectFolderPanel);
        mainPanel.add(modelPanel);
        mainPanel.add(daoPanel);
        mainPanel.add(xmlMapperPanel);
        mainPanel.add(optionsPanel);


        JPanel paneBottom = new JPanel();//确认和取消按钮
        paneBottom.setLayout(new FlowLayout(2));
        paneBottom.add(buttonOK);
        paneBottom.add(buttonCancel);


        /**
         * historyConfig panel
         */

        this.getContentPane().add(Box.createVerticalStrut(10)); //采用x布局时，添加固定宽度组件隔开
        final DefaultListModel defaultListModel = new DefaultListModel();

        if (historyConfigList == null) {
            historyConfigList = new HashMap<>();
        }
        for (String historyConfigName : historyConfigList.keySet()) {
            defaultListModel.addElement(historyConfigName);
        }
        Map<String, Config> finalHistoryConfigList = historyConfigList;

        final JBList configJBList = new JBList(defaultListModel);
        configJBList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configJBList.setSelectedIndex(0);
        configJBList.setVisibleRowCount(25);
        JBScrollPane ScrollPane = new JBScrollPane(configJBList);


        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.add(new JLabel("      "));//用来占位置
        btnPanel.add(deleteConfigBtn);
        configJBList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (configJBList.getSelectedIndex() != -1) {
                    if (e.getClickCount() == 2) { //双击事件
                        String configName = (String) configJBList.getSelectedValue();
                        Config selectedConfig = finalHistoryConfigList.get(configName);
                        modelPackageField.setText(selectedConfig.getModelPackage());
                        daoPackageField.setText(selectedConfig.getDaoPackage());
                        xmlPackageField.setText(selectedConfig.getXmlPackage());
                        projectFolderBtn.setText(selectedConfig.getProjectFolder());
                        offsetLimitBox.setSelected(selectedConfig.isOffsetLimit());
                        commentBox.setSelected(selectedConfig.isComment());
                        overrideXMLBox.setSelected(selectedConfig.isOverrideXML());
                        needToStringHashcodeEqualsBox.setSelected(selectedConfig.isNeedToStringHashcodeEquals());
                        useSchemaPrefixBox.setSelected(selectedConfig.isUseSchemaPrefix());
                        needForUpdateBox.setSelected(selectedConfig.isNeedForUpdate());
                        annotationDAOBox.setSelected(selectedConfig.isAnnotationDAO());
                        useDAOExtendStyleBox.setSelected(selectedConfig.isUseDAOExtendStyle());
                        jsr310SupportBox.setSelected(selectedConfig.isJsr310Support());
                        annotationBox.setSelected(selectedConfig.isAnnotation());
                        useActualColumnNamesBox.setSelected(selectedConfig.isUseActualColumnNames());
                        useTableNameAliasBox.setSelected(selectedConfig.isUseTableNameAlias());
                        useExampleBox.setSelected(selectedConfig.isUseExample());
                        mysql_8Box.setSelected(selectedConfig.isMysql_8());
                    }
                }
            }
        });

        deleteConfigBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finalHistoryConfigList.remove(configJBList.getSelectedValue());
                defaultListModel.removeAllElements();
                for (String historyConfigName : finalHistoryConfigList.keySet()) {
                    defaultListModel.addElement(historyConfigName);
                }
            }
        });

        JPanel historyConfigPanel = new JPanel();
        historyConfigPanel.setLayout(new BoxLayout(historyConfigPanel, BoxLayout.Y_AXIS));
        historyConfigPanel.setBorder(BorderFactory.createTitledBorder("config history"));
        historyConfigPanel.add(ScrollPane);
        historyConfigPanel.add(btnPanel);


        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(paneBottom, BorderLayout.SOUTH);
        contentPane.add(historyConfigPanel, BorderLayout.WEST);
        setContentPane(contentPane);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        try {
            dispose();

            if (psiElements.length == 1) {
                Config generator_config = new Config();
                generator_config.setName(tableNameField.getText());
                generator_config.setTableName(tableNameField.getText());
                generator_config.setProjectFolder(projectFolderBtn.getText());

                generator_config.setModelPackage(modelPackageField.getText());
//                generator_config.setModelTargetFolder(modelFolderBtn.getText());
                generator_config.setDaoPackage(daoPackageField.getText());
//                generator_config.setDaoTargetFolder(daoFolderBtn.getText());
                generator_config.setXmlPackage(xmlPackageField.getText());
//                generator_config.setXmlTargetFolder(xmlFolderBtn.getText());
                generator_config.setDaoName(daoNameField.getText());
                generator_config.setModelName(modelNameField.getText());
                generator_config.setPrimaryKey(keyField.getText());

                generator_config.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);
                generator_config.setComment(commentBox.getSelectedObjects() != null);
                generator_config.setOverrideXML(overrideXMLBox.getSelectedObjects() != null);
                generator_config.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
                generator_config.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
                generator_config.setNeedForUpdate(needForUpdateBox.getSelectedObjects() != null);
                generator_config.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
                generator_config.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
                generator_config.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
                generator_config.setAnnotation(annotationBox.getSelectedObjects() != null);
                generator_config.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
                generator_config.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
                generator_config.setUseExample(useExampleBox.getSelectedObjects() != null);
                generator_config.setMysql_8(mysql_8Box.getSelectedObjects() != null);

                generator_config.setModelMvnPath(modelMvnField.getText());
                generator_config.setDaoMvnPath(daoMvnField.getText());
                generator_config.setXmlMvnPath(xmlMvnField.getText());


                new Generate(generator_config).execute(anActionEvent);
            } else {
                for (PsiElement psiElement : psiElements) {
                    TableInfo tableInfo = new TableInfo((DbTable) psiElement);
                    String tableName = tableInfo.getTableName();
                    String modelName = StringUtils.dbStringToCamelStyle(tableName);
                    String primaryKey = "";
                    if (tableInfo.getPrimaryKeys() != null && tableInfo.getPrimaryKeys().size() != 0) {
                        primaryKey = tableInfo.getPrimaryKeys().get(0);
                    }
                    Config generator_config = new Config();
                    generator_config.setName(tableName);
                    generator_config.setTableName(tableName);
                    generator_config.setProjectFolder(projectFolderBtn.getText());

                    generator_config.setModelPackage(modelPackageField.getText());
//                    generator_config.setModelTargetFolder(modelFolderBtn.getText());
                    generator_config.setDaoPackage(daoPackageField.getText());
//                    generator_config.setDaoTargetFolder(daoFolderBtn.getText());
                    generator_config.setXmlPackage(xmlPackageField.getText());
//                    generator_config.setXmlTargetFolder(xmlFolderBtn.getText());

                    if (this.config != null) {
                        generator_config.setDaoName(modelName + this.config.getDaoPostfix());
                    } else {
                        generator_config.setDaoName(modelName + "Dao");
                    }
                    generator_config.setModelName(modelName);
                    generator_config.setPrimaryKey(primaryKey);

                    generator_config.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);
                    generator_config.setComment(commentBox.getSelectedObjects() != null);
                    generator_config.setOverrideXML(overrideXMLBox.getSelectedObjects() != null);
                    generator_config.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
                    generator_config.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
                    generator_config.setNeedForUpdate(needForUpdateBox.getSelectedObjects() != null);
                    generator_config.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
                    generator_config.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
                    generator_config.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
                    generator_config.setAnnotation(annotationBox.getSelectedObjects() != null);
                    generator_config.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
                    generator_config.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
                    generator_config.setUseExample(useExampleBox.getSelectedObjects() != null);
                    generator_config.setMysql_8(mysql_8Box.getSelectedObjects() != null);

                    generator_config.setModelMvnPath(modelMvnField.getText());
                    generator_config.setDaoMvnPath(daoMvnField.getText());
                    generator_config.setXmlMvnPath(xmlMvnField.getText());


                    new Generate(generator_config).execute(anActionEvent);
                }

            }


        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }
}
