package com.usoft.mybatis.utils;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.Properties;
import java.util.Set;

/**
 * @author wangcanyi
 * @date 2018-07-31 17:57
 */
public class MybatisCommentGenerator implements CommentGenerator {

	@Override
	public void addConfigurationProperties(Properties properties) {

	}

	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
		this.addComment(field, introspectedTable, introspectedColumn);
	}

	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable) {

	}

	@Override
	public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		// 添加字段注释
		StringBuffer comment = new StringBuffer();
		topLevelClass.addJavaDocLine("/**");
		topLevelClass.addJavaDocLine(" * <pre>");
		if (introspectedTable.getRemarks() != null) {
			topLevelClass.addJavaDocLine(" * " + introspectedTable.getRemarks());
		}
		comment.append(" * 表名 : ");
		comment.append(introspectedTable.getFullyQualifiedTable());
		topLevelClass.addJavaDocLine(comment.toString());
		topLevelClass.addJavaDocLine(" * </pre>");
		topLevelClass.addJavaDocLine(" * @author: Mybatis Generator");
		topLevelClass.addJavaDocLine(" */");
	}

	@Override
	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {

	}

	@Override
	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {

	}

	@Override
	public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {

	}

	@Override
	public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
		this.addComment(method, introspectedTable, introspectedColumn);
	}

	@Override
	public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
		this.addComment(method, introspectedTable, introspectedColumn);
	}

	@Override
	public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
		method.addJavaDocLine("/**");
		method.addJavaDocLine(" * " + method.getName());
		method.addJavaDocLine(" * ");
		for (Parameter parameter : method.getParameters()) {
			method.addJavaDocLine(" * @param " + parameter.getName());
		}
		String returnType = method.getReturnType().toString();
		returnType = returnType.lastIndexOf(".") != -1 ? returnType.substring(returnType.lastIndexOf(".") + 1) : returnType;
		method.addJavaDocLine(" * @return " + returnType);
		method.addJavaDocLine(" */");
	}

	@Override
	public void addJavaFileComment(CompilationUnit compilationUnit) {

	}

	@Override
	public void addComment(XmlElement xmlElement) {

	}

	@Override
	public void addRootComment(XmlElement rootElement) {

	}

	@Override
	public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

	}

	@Override
	public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

	}

	@Override
	public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

	}

	@Override
	public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

	}

	@Override
	public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

	}

	private void addComment(JavaElement javaElement, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
		// 添加字段注释
		StringBuffer comment = new StringBuffer();
		javaElement.addJavaDocLine("/**");
		javaElement.addJavaDocLine(" * <pre>");
		if (introspectedColumn.getRemarks() != null) {
			javaElement.addJavaDocLine(" * " + introspectedColumn.getRemarks());
		}
		comment.append(" * 表字段 : ");
		comment.append(introspectedTable.getFullyQualifiedTable());
		comment.append('.');
		comment.append(introspectedColumn.getActualColumnName());
		javaElement.addJavaDocLine(comment.toString());
		javaElement.addJavaDocLine(" * </pre>");
		javaElement.addJavaDocLine(" */");
	}
}
