package com.bigflag.javadevplugin.tools;

public class DevTools {

	public static String createEnhancedEnum(String javaTextContent) {
		javaTextContent = javaTextContent.replaceAll("\r", "");
		javaTextContent = javaTextContent.replaceAll("\t", "");
		String className = getClassPackageAndClassName(javaTextContent)[1];
		StringBuilder sbEnum = new StringBuilder();
		StringBuilder sbClassHeader = new StringBuilder();
		sbClassHeader.append("\npublic enum ").append(className).append(" {\n");

		sbEnum.append("\tpublic final int mask;\n")
				.append("\t").append(className).append("(){\n")
				.append("\t\tmask = (1 << ordinal());\n")
				.append("\t}\n\n")
				.append("\tpublic final int getMask() {\n").append("\t\t return mask;\n").append("\t}\n")
				.append("\tpublic static boolean isEnabled(int features, ").append(className).append(" feature) {\n")
				.append("\t\t return (features & feature.mask) != 0;\n").append("\t}\n").append("\tpublic static int config(int features, ")
				.append(className).append(" feature, boolean state) {\n").append("\t\tif (state) {\n").append("\t\t\tfeatures |= feature.mask;\n")
				.append("\t\t} else {\n").append("\t\t\tfeatures &= ~feature.mask;").append("\t\t}\n").append("\t\treturn features;\n")
				.append("\t}\n")

				.append("\tpublic static int of(").append(className).append("[] features) {\n").append("\t\tif (features == null) {\n")
				.append("\t\t\treturn 0;\n").append("\t\t}\n\n").append("\t\tint value = 0;\n\n").append("\t\tfor (").append(className)
				.append(" feature: features) {\n").append("\t\t\tvalue |= feature.mask;\n").append("\t\t}\n\n").append("\t\treturn value;\n")
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
		sb.append(sbClassHeader);
		for (String line : javaLines) {
			if (line.trim().endsWith(",")) {
				sb.append("\t"+line + "\n");
			}
		}
		sb.append("\t;\n");
		sb.append(sbEnum);
		sb.append("\n}");
		return sb.toString();
	}

	public static String createSingletonClass(String javaTextContent) {
		javaTextContent = javaTextContent.replaceAll("\r", "");
		javaTextContent = javaTextContent.replaceAll("\t", "");
		String className = getClassPackageAndClassName(javaTextContent)[1];
		StringBuilder sbSingleton = new StringBuilder();
		sbSingleton.append("\npublic class ").append(className).append(" {\n").append("\tprivate static Object locker = new Object();\n")
				.append("\tprivate static ").append(className).append(" instance;\n\n").append("\tprivate ").append(className).append("() {}\n")
				.append("\tpublic static ").append(className).append(" getInstance() {\n").append("\t\tif (instance == null) {\n")
				.append("\t\t\tsynchronized (locker) {\n").append("\t\t\t\tif (instance == null) {\n").append("\t\t\t\t\tinstance = new ")
				.append(className).append("();\n").append("\t\t\t\t}\n").append("\t\t\t}\n").append("\t\t}\n").append("\t\treturn instance;\n")
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

		String className = getClassPackageAndClassName(javaTextContent)[1];

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
				sb.append("\n" + line + "\n");
			} else if (line.trim().equals("}")) {
				// sb.append(line.trim() + "\n");
			} else if (line.trim().startsWith("private") || line.trim().startsWith("public")) {
				sb.append("\t" + line + "\n");
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
	 * 
	 * @param javaTextContent
	 * @return String[] length is 2. String[0]:packagePath, String[1]:className
	 */
	private static String[] getClassPackageAndClassName(String javaTextContent) {
		int packageStartPos = javaTextContent.indexOf("package ");
		int packageEndPos = javaTextContent.indexOf(';');
		String packagePath = javaTextContent.substring(packageStartPos + "package ".length(), packageEndPos).trim();

		String className = "";

		int classStartPos = 0;
		if (javaTextContent.contains(" class ")) {
			classStartPos = javaTextContent.indexOf("class ");
			int classEndPos = javaTextContent.indexOf(" ", classStartPos + "class ".length());
			className = javaTextContent.substring(classStartPos + "class ".length(), classEndPos).trim();
		} else if (javaTextContent.contains(" enum ")) {
			classStartPos = javaTextContent.indexOf("enum ");
			int classEndPos = javaTextContent.indexOf(" ", classStartPos + "enum ".length());
			className = javaTextContent.substring(classStartPos + "enum ".length(), classEndPos).trim();
		}

		return new String[] { packagePath, className };
	}

	/***
	 * get the filed name and type of one declare line
	 * 
	 * @param line
	 * @return String[] String[0]:filed name String[1]:type name
	 */
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
		line = line.trim();

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
		} else if (line.contains("<")) {
			filedInfos[0] = line.substring(0, line.lastIndexOf(">") + ">".length());
			filedInfos[1] = line.substring(filedInfos[0].length()).trim();
		}

		return filedInfos;
	}

	/***
	 * This function is to create the fluent api for beans in terms of getter,
	 * setter.
	 * 
	 * @param className
	 * @param fieldName
	 * @param type
	 * @return the code generated
	 */
	private static String createGetterSetter(String className, String fieldName, String type) {

		String newFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n\tpublic void set").append(newFieldName).append("(").append(type).append(" ").append(fieldName).append("){\n\t\t")
				.append("this." + fieldName).append("=").append(fieldName).append(";\n\t}");

		sb.append("\n\n\tpublic ").append(type).append(" get").append(newFieldName).append("(){\n\t\t").append("return this." + fieldName)
				.append(";\n\t}");

		sb.append("\n\n\tpublic ").append(className).append(" ").append(fieldName).append("(").append(type).append(" ").append(fieldName)
				.append("){\n\t\t").append("this." + fieldName).append("=").append(fieldName).append(";\n").append("\t\treturn this;\n\t}");
		if (type.startsWith("List<")) {
			String genericType = type.substring("List<".length(), type.lastIndexOf(">"));
			String genericTypeParameterName = genericType.substring(0, 1).toLowerCase() + genericType.substring(1);
			sb.append("\n\n\tpublic ").append(className).append(" add").append(newFieldName).append("(").append(genericType).append(" ")
					.append(genericTypeParameterName).append("){\n\t\t").append("this." + fieldName).append(".add(").append(genericTypeParameterName)
					.append(");\n").append("\t\treturn this;\n\t}");
			sb.append("\n\n\tpublic ").append(className).append(" remove").append(newFieldName).append("(").append(genericType).append(" ")
					.append(genericTypeParameterName).append("){\n\t\t").append("this." + fieldName).append(".remove(")
					.append(genericTypeParameterName).append(");\n").append("\t\treturn this;\n\t}");
			sb.append("\n\n\tpublic ").append(className).append(" clear").append(newFieldName).append("(){\n\t\t").append("this." + fieldName)
					.append(".clear(").append("").append(");\n").append("\t\treturn this;\n\t}");

		} else if (type.startsWith("Map<")) {
			String genericKeyType = type.substring("Map<".length(), type.lastIndexOf(","));
			String genericValueType = type.substring(type.lastIndexOf(",") + ",".length(), type.lastIndexOf(">"));

			String genericKeyTypeParameterName = genericKeyType.substring(0, 1).toLowerCase() + genericKeyType.substring(1);
			String genericValueTypeParameterName = genericValueType.substring(0, 1).toLowerCase() + genericValueType.substring(1);
			sb.append("\n\n\tpublic ").append(className).append(" put").append(newFieldName).append("(").append(genericKeyType).append(" ")
					.append(genericKeyTypeParameterName).append(",").append(genericValueType).append(" ").append(genericValueTypeParameterName)
					.append("){\n\t\t").append("this." + fieldName).append(".put(").append(genericKeyTypeParameterName).append(",")
					.append(genericValueTypeParameterName).append(");\n").append("\t\treturn this;\n\t}");

			sb.append("\n\n\tpublic ").append(className).append(" remove").append(newFieldName).append("(").append(genericKeyType).append(" ")
					.append(genericKeyTypeParameterName).append("){\n\t\t").append("this." + fieldName).append(".remove(")
					.append(genericKeyTypeParameterName).append(");\n").append("\t\treturn this;\n\t}");

			sb.append("\n\n\tpublic ").append(className).append(" clear").append(newFieldName).append("(){\n\t\t").append("this." + fieldName)
					.append(".clear(").append("").append(");\n").append("\t\treturn this;\n\t}");

		}
		return sb.toString();
	}

}
