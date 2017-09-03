package com.bigflag.javadevplugin.tools;

import java.lang.reflect.Field;
import java.util.Date;

public class DevTools {

	public static String createFluentApiForBean(String javaTextContent) throws ClassNotFoundException {
		// javaTextContent = "asdfasfpackage com.bigflag.devel.framework  ;";

		javaTextContent = javaTextContent.replaceAll("\r", "");
		// javaTextContent=javaTextContent.replaceAll("\n", "");
		javaTextContent = javaTextContent.replaceAll("\t", "");

		int packageStartPos = javaTextContent.indexOf("package ");
		int packageEndPos = javaTextContent.indexOf(';');
		String packagePath = javaTextContent.substring(packageStartPos + "package ".length(), packageEndPos).trim();
		System.out.println(packagePath);
		int classStartPos = javaTextContent.indexOf("class ");
		int classEndPos = javaTextContent.indexOf("{");
		String className = javaTextContent.substring(classStartPos + "class ".length(), classEndPos).trim();
		String classPath = packagePath + "." + className;
		// Class clazz = Class.forName(classPath);
		// return createBeanCode(clazz);
		StringBuilder sb = new StringBuilder();
		StringBuilder fieldSB = new StringBuilder();
		String[] javaLines = javaTextContent.split("\n");
		for (String line : javaLines) {
			if (line.trim().startsWith("import")) {
				sb.append(line + "\n");
			} else if (line.trim().startsWith("package")) {
				sb.append(line + "\n");
			} else if (line.trim().equals("")) {

			} else if (line.trim().contains(" class ")) {
				sb.append("\n"+line + "\n");
			} else if (line.trim().equals("}")) {
//				sb.append(line.trim() + "\n");
			} else if (line.trim().startsWith("private") || line.trim().startsWith("public")) {
				sb.append("\t"+line + "\n");
				String[] filedInfos = getFiledNameAndType(line.trim());
				fieldSB.append(createGetterSetter(className, filedInfos[1], filedInfos[0]));
			}

		}
		sb.append("\n\tpublic static ").append(className).append(" newInstance(){\n\t\treturn new " + className + "();\n\t}");
		sb.append(fieldSB);
		sb.append("\n}");
		return sb.toString();
	}

	public static String[] getFiledNameAndType(String line) {
		if (line.trim().startsWith("private")) {
			line = line.substring("private".length());
		} else if (line.trim().startsWith("public")) {
			line = line.substring("public".length());
		}
		line = line.replaceAll(";", "");
		if (line.contains("=")) {
			line = line.substring(0, line.indexOf("="));
		}
		line=line.trim();

		String[] filedInfos = new String[2];
		if (line.startsWith("String")) {
			filedInfos[0] = "String";
			filedInfos[1] = line.substring("String".length()).trim();
		} else if (line.startsWith("int")) {
			filedInfos[0] = "int";
			filedInfos[1] = line.substring("int".length()).trim();
		} else if (line.startsWith("boolean")) {
			filedInfos[0] = "boolean";
			filedInfos[1] = line.substring("boolean".length()).trim();
		} else if (line.startsWith("long")) {
			filedInfos[0] = "long";
			filedInfos[1] = line.substring("long".length()).trim();
		} else if (line.startsWith("double")) {
			filedInfos[0] = "double";
			filedInfos[1] = line.substring("double".length()).trim();
		} else if (line.startsWith("float")) {
			filedInfos[0] = "float";
			filedInfos[1] = line.substring("float".length()).trim();
		} else if (line.startsWith("Date")) {
			filedInfos[0] = "Date";
			filedInfos[1] = line.substring("Date".length()).trim();
		} else if (line.startsWith("byte")) {
			filedInfos[0] = "byte";
			filedInfos[1] = line.substring("byte".length()).trim();
		}

		return filedInfos;
	}

	public static String createBeanCode(Class clazz) {
		StringBuilder sb = new StringBuilder();
		sb.append("public static ").append(clazz.getSimpleName()).append(" newInstance(){\n\treturn new " + clazz.getSimpleName() + "();\n}");

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			try {
				if (field.getName().equalsIgnoreCase("uuid")) {
					continue;
				}
				if (field.getName().equalsIgnoreCase("id")) {
					continue;
				}

				String fieldName = field.getName();

				if (field.getGenericType().toString().contains("java.lang.String")) {
					sb.append(createGetterSetter(clazz.getSimpleName(), fieldName, "String"));
				} else if (field.getGenericType().toString().equalsIgnoreCase("boolean")) {
					sb.append(createGetterSetter(clazz.getSimpleName(), fieldName, "boolean"));
				} else if (field.getGenericType().toString().contains("java.util.Date")) {
					sb.append(createGetterSetter(clazz.getSimpleName(), fieldName, "Date"));
				} else if (field.getGenericType().toString().contains("int")) {
					sb.append(createGetterSetter(clazz.getSimpleName(), fieldName, "int"));
				} else if (field.getGenericType().toString().contains("long")) {
					sb.append(createGetterSetter(clazz.getSimpleName(), fieldName, "long"));
				} else if (field.getGenericType().toString().contains("double")) {
					sb.append(createGetterSetter(clazz.getSimpleName(), fieldName, "double"));
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
	
	private static String createGetterSetter(String className, String fieldName, String type) {
		String newFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n\tpublic void set").append(newFieldName).append("(").append(type).append(" ").append(fieldName).append("){\n\t\t").append("this." + fieldName).append("=").append(fieldName)
				.append(";\n\t}");

		sb.append("\n\n\tpublic ").append(type).append(" get").append(newFieldName).append("(){\n\t\t").append("return this." + fieldName).append(";\n\t}");

		sb.append("\n\n\tpublic ").append(className).append(" ").append(fieldName).append("(").append(type).append(" ").append(fieldName).append("){\n\t\t").append("this." + fieldName).append("=")
				.append(fieldName).append(";\n").append("\t\treturn this;\n\t}");
		return sb.toString();
	}

}
