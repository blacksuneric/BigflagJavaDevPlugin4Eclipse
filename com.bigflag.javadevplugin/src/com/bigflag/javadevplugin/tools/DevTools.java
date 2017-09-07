package com.bigflag.javadevplugin.tools;



public class DevTools {

	public static String createSingletonClass(String javaTextContent)
	{
		javaTextContent = javaTextContent.replaceAll("\r", "");
		javaTextContent = javaTextContent.replaceAll("\t", "");
		String className=getClassPackageAndClassName(javaTextContent)[1];
		StringBuilder sbSingleton=new StringBuilder();
		sbSingleton.append("\npublic class ").append(className).append(" {\n")
		.append("\tprivate static Object locker = new Object();\n")
		.append("\tprivate static ").append(className).append(" instance;\n\n")
		.append("\tprivate ").append(className).append("() {}\n")
		.append("\tpublic static ").append(className).append(" getInstance() {\n")
		.append("\t\tif (instance == null) {\n")
		.append("\t\t\tsynchronized (locker) {\n")
		.append("\t\t\t\tif (instance == null) {\n")
		.append("\t\t\t\t\tinstance = new ").append(className).append("();\n")
		.append("\t\t\t\t}\n")
		.append("\t\t\t}\n")
		.append("\t\t}\n")
		.append("\t\treturn instance;\n")
		.append("\t}\n");
		
		
		StringBuilder sb = new StringBuilder();
		String[] javaLines = javaTextContent.split("\n");
		for (String line : javaLines) {
			if (line.trim().startsWith("import")) {
				sb.append(line + "\n");
			} else if (line.trim().startsWith("package")) {
				sb.append(line + "\n");
			} else if (line.trim().equals("")) {

			} 
		}
		sb.append(sbSingleton);
		sb.append("\n}");
		return sb.toString();
	}
	
	public static String createFluentApiForBean(String javaTextContent) {
		javaTextContent = javaTextContent.replaceAll("\r", "");
		javaTextContent = javaTextContent.replaceAll("\t", "");

		String className=getClassPackageAndClassName(javaTextContent)[1];
		
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
	
	/***
	 * The function is to get the java code's package and class name
	 * @param javaTextContent
	 * @return String[] length is 2. String[0]:packagePath, String[1]:className
	 */
	private static String[] getClassPackageAndClassName(String javaTextContent)
	{
		int packageStartPos = javaTextContent.indexOf("package ");
		int packageEndPos = javaTextContent.indexOf(';');
		String packagePath = javaTextContent.substring(packageStartPos + "package ".length(), packageEndPos).trim();
		int classStartPos = javaTextContent.indexOf("class ");
		int classEndPos = javaTextContent.indexOf(" ", classStartPos + "class ".length());
		String className = javaTextContent.substring(classStartPos + "class ".length(), classEndPos).trim();
		
		return new String[]{packagePath,className};
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

	
	/***
	 * This function is to create the fluent api for beans in terms of getter, setter.
	 * @param className
	 * @param fieldName
	 * @param type
	 * @return the code generated
	 */
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
