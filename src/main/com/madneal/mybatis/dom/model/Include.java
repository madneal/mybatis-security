package com.madneal.mybatis.dom.model;

import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.madneal.mybatis.dom.converter.SqlConverter;

/**
 * @author yanglin
 */
public interface Include extends DomElement {

    @Attribute("refid")
    @Convert(SqlConverter.class)
    public GenericAttributeValue<XmlTag> getRefId();

}
