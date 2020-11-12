package com.xiaogua.mybatis.utils.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.Iterator;
import java.util.List;

/**
 * 生成带表名的字段列表
 *
 * @author: wangyc
 * @date: 2020-11-12
 **/
public class TableBaseColumnListPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement answer = new XmlElement("sql");
        answer.addAttribute(new Attribute("id", "Table_Base_Column_List"));
        context.getCommentGenerator().addComment(answer);
        StringBuilder sb = new StringBuilder();
        Iterator<IntrospectedColumn> iter = introspectedTable.getNonBLOBColumns().iterator();
        while (iter.hasNext()) {
            sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
            sb.append('.');

            sb.append(MyBatis3FormattingUtilities.getSelectListPhrase(iter.next()));
            if (iter.hasNext()) {
                sb.append(", ");
            }
            if (sb.length() > 80) {
                answer.addElement(new TextElement(sb.toString()));
                sb.setLength(0);
            }
        }

        if (sb.length() > 0) {
            answer.addElement(new TextElement(sb.toString()));
        }

        XmlElement parentElement = document.getRootElement();
        parentElement.addElement(answer);
        return true;
    }
}
