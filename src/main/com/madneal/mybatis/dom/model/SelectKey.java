package com.madneal.mybatis.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.madneal.mybatis.dom.converter.AliasConverter;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public interface SelectKey extends GroupOne {
    @NotNull
    @Attribute("resultType")
    @Convert(AliasConverter.class)
    GenericAttributeValue<PsiClass> getResultType();
}
