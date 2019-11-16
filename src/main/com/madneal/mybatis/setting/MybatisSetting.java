package com.madneal.mybatis.setting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.madneal.mybatis.generate.GenerateModel;
import com.madneal.mybatis.generate.StatementGenerator;

import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author yanglin
 */
@State(
        name = "MybatisSettings",
        storages = @Storage(file = "$APP_CONFIG$/mybatis.xml"))
public class MybatisSetting implements PersistentStateComponent<Element> {

    private GenerateModel statementGenerateModel;

    private Gson gson = new Gson();

    private Type gsonTypeToken = new TypeToken<Set<String>>() {
    }.getType();

    public MybatisSetting() {
        statementGenerateModel = GenerateModel.START_WITH_MODEL;
    }

    public static MybatisSetting getInstance() {
        return ServiceManager.getService(MybatisSetting.class);
    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("MybatisSettings");
        element.setAttribute(StatementGenerator.INSERT_GENERATOR.getId(), gson.toJson(StatementGenerator.INSERT_GENERATOR.getPatterns()));
        element.setAttribute(StatementGenerator.DELETE_GENERATOR.getId(), gson.toJson(StatementGenerator.DELETE_GENERATOR.getPatterns()));
        element.setAttribute(StatementGenerator.UPDATE_GENERATOR.getId(), gson.toJson(StatementGenerator.UPDATE_GENERATOR.getPatterns()));
        element.setAttribute(StatementGenerator.SELECT_GENERATOR.getId(), gson.toJson(StatementGenerator.SELECT_GENERATOR.getPatterns()));
        element.setAttribute("statementGenerateModel", String.valueOf(statementGenerateModel.getIdentifier()));
        return element;
    }

    @Override
    public void loadState(Element state) {
        loadState(state, StatementGenerator.INSERT_GENERATOR);
        loadState(state, StatementGenerator.DELETE_GENERATOR);
        loadState(state, StatementGenerator.UPDATE_GENERATOR);
        loadState(state, StatementGenerator.SELECT_GENERATOR);
        statementGenerateModel = GenerateModel.getInstance(state.getAttributeValue("statementGenerateModel"));
    }

    private void loadState(Element state, StatementGenerator generator) {
        String attribute = state.getAttributeValue(generator.getId());
        if (null != attribute) {
            generator.setPatterns((Set<String>) gson.fromJson(attribute, gsonTypeToken));
        }
    }

    public GenerateModel getStatementGenerateModel() {
        return statementGenerateModel;
    }

    public void setStatementGenerateModel(GenerateModel statementGenerateModel) {
        this.statementGenerateModel = statementGenerateModel;
    }
}
