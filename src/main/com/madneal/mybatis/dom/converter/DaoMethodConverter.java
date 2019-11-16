package com.madneal.mybatis.dom.converter;

import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import com.madneal.mybatis.util.JavaUtils;
import com.madneal.mybatis.util.MapperUtils;
import com.madneal.mybatis.dom.model.Mapper;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public class DaoMethodConverter extends ConverterAdaptor<PsiMethod> {

    @Nullable
    @Override
    public PsiMethod fromString(@Nullable @NonNls String id, ConvertContext context) {
        Mapper mapper = MapperUtils.getMapper(context.getInvocationElement());
        return JavaUtils.findMethod(context.getProject(), MapperUtils.getNamespace(mapper), id).orNull();
    }

}