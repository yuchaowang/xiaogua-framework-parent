package com.usoft.mybatis.utils.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 生成 selectByCode 插件
 *
 * @author: wangcanyi
 * @date: 2018-09-04 10:55
 **/
public class SelectByCodePlugin extends PluginAdapter {
	private static final String CODE_NAME = "code";
	private static final String METHOD_NAME = "selectByCode";

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		boolean super_result = super.clientGenerated(interfaze, topLevelClass, introspectedTable);
		IntrospectedColumn codeColumn = introspectedTable.getColumn(CODE_NAME);
		//不存在Code列时，则不需要生成
		if (codeColumn == null) {
			return super_result;
		}

		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
		Method method = new Method();
		//设置方法可见度
		method.setVisibility(JavaVisibility.PUBLIC);

		//设置返回类型
		FullyQualifiedJavaType returnType = introspectedTable.getRules().calculateAllFieldsClass();
		method.setReturnType(returnType);
		importedTypes.add(returnType);
		//设置方法名
		method.setName(METHOD_NAME);
		//设置参数
		FullyQualifiedJavaType type = codeColumn.getFullyQualifiedJavaType();
		importedTypes.add(type);
		Parameter parameter = new Parameter(type, codeColumn.getJavaProperty());
		//parameter.addAnnotation("@Param(\"" + codeColumn.getJavaProperty() + "\")");
		method.addParameter(parameter);

		//引入注释
		context.getCommentGenerator().addGeneralMethodComment(method,
				introspectedTable);

		//添加方法生成
		interfaze.addImportedTypes(importedTypes);
		interfaze.addMethod(method);
		return true;
	}

	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		boolean super_result = super.sqlMapDocumentGenerated(document, introspectedTable);
		IntrospectedColumn codeColumn = introspectedTable.getColumn(CODE_NAME);
		//不存在Code列时，则不需要生成
		if (codeColumn == null) {
			return super_result;
		}

		XmlElement answer = new XmlElement("select");
		answer.addAttribute(new Attribute("id", METHOD_NAME));
		if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
			answer.addAttribute(new Attribute("resultMap", introspectedTable.getResultMapWithBLOBsId()));
		} else {
			answer.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));
		}
		answer.addAttribute(new Attribute("parameterType", codeColumn.getFullyQualifiedJavaType().toString()));

		context.getCommentGenerator().addComment(answer);

		answer.addElement(new TextElement("select "));
		answer.addElement(getBaseColumnListElement(introspectedTable));
		if (introspectedTable.hasBLOBColumns()) {
			answer.addElement(new TextElement(","));
			answer.addElement(getBlobColumnListElement(introspectedTable));
		}
		answer.addElement(new TextElement("from " + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()));
		answer.addElement(new TextElement("where " + CODE_NAME + " = " + MyBatis3FormattingUtilities.getParameterClause(codeColumn)));

		XmlElement parentElement = document.getRootElement();
		parentElement.addElement(answer);

		return true;
	}

	private XmlElement getBaseColumnListElement(IntrospectedTable introspectedTable) {
		XmlElement answer = new XmlElement("include");
		answer.addAttribute(new Attribute("refid", introspectedTable.getBaseColumnListId()));
		return answer;
	}

	private XmlElement getBlobColumnListElement(IntrospectedTable introspectedTable) {
		XmlElement answer = new XmlElement("include");
		answer.addAttribute(new Attribute("refid", introspectedTable.getBlobColumnListId()));
		return answer;
	}
}
