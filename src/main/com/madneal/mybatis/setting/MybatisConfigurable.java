package com.madneal.mybatis.setting;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.madneal.mybatis.generate.GenerateModel;
import com.madneal.mybatis.generate.StatementGenerator;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author yanglin
 */
public class MybatisConfigurable implements SearchableConfigurable {

    private MybatisSetting mybatisSetting;

    private MybatisSettingForm mybatisSettingForm;

    private String separator = ";";

    private Splitter splitter = Splitter.on(separator).omitEmptyStrings().trimResults();

    private Joiner joiner = Joiner.on(separator);

    public MybatisConfigurable() {
        mybatisSetting = MybatisSetting.getInstance();
    }

    @Override
    public String getId() {
        return "Mybatis";
    }

    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return getId();
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return getId();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (null == mybatisSettingForm) {
            this.mybatisSettingForm = new MybatisSettingForm();
        }
        return mybatisSettingForm.mainPanel;
    }

    @Override
    public boolean isModified() {
        return mybatisSetting.getStatementGenerateModel().getIdentifier() != mybatisSettingForm.modelComboBox.getSelectedIndex()
                || !joiner.join(StatementGenerator.INSERT_GENERATOR.getPatterns()).equals(mybatisSettingForm.insertPatternTextField.getText())
                || !joiner.join(StatementGenerator.DELETE_GENERATOR.getPatterns()).equals(mybatisSettingForm.deletePatternTextField.getText())
                || !joiner.join(StatementGenerator.UPDATE_GENERATOR.getPatterns()).equals(mybatisSettingForm.updatePatternTextField.getText())
                || !joiner.join(StatementGenerator.SELECT_GENERATOR.getPatterns()).equals(mybatisSettingForm.selectPatternTextField.getText());
    }

    @Override
    public void apply() throws ConfigurationException {
        mybatisSetting.setStatementGenerateModel(GenerateModel.getInstance(mybatisSettingForm.modelComboBox.getSelectedIndex()));
        StatementGenerator.INSERT_GENERATOR.setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.insertPatternTextField.getText())));
        StatementGenerator.DELETE_GENERATOR.setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.deletePatternTextField.getText())));
        StatementGenerator.UPDATE_GENERATOR.setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.updatePatternTextField.getText())));
        StatementGenerator.SELECT_GENERATOR.setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.selectPatternTextField.getText())));
    }

    @Override
    public void reset() {
        mybatisSettingForm.modelComboBox.setSelectedIndex(mybatisSetting.getStatementGenerateModel().getIdentifier());
        mybatisSettingForm.insertPatternTextField.setText(joiner.join(StatementGenerator.INSERT_GENERATOR.getPatterns()));
        mybatisSettingForm.deletePatternTextField.setText(joiner.join(StatementGenerator.DELETE_GENERATOR.getPatterns()));
        mybatisSettingForm.updatePatternTextField.setText(joiner.join(StatementGenerator.UPDATE_GENERATOR.getPatterns()));
        mybatisSettingForm.selectPatternTextField.setText(joiner.join(StatementGenerator.SELECT_GENERATOR.getPatterns()));
    }

    @Override
    public void disposeUIResources() {
        mybatisSettingForm.mainPanel = null;
    }

}
