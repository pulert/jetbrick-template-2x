/**
 * Copyright 2013-2014 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 *   Author: Guoqiang Chen
 *    Email: subchen@gmail.com
 *   WebURL: https://github.com/subchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrick.template;

import jetbrick.template.parser.Source;
import jetbrick.template.parser.ast.Position;
import jetbrick.util.StringUtils;

public final class Errors {
    public static final String JETX_ERROR_SOURCE_DISABLED = "JETX_ERROR_SOURCE_DISABLED";

    // general error
    public static final String UNREACHABLE_CODE = "unreachable code";
    public static final String TEMPLATE_PATH_ERROR = "path is not under template root: %s";

    // syntax error
    public static final String OPTION_NAME_INVALID = "#option name is unknown: %s";
    public static final String OPTION_VALUE_INVALID = "#option value is invalid: %s";
    public static final String VAR_UNDEFINED = "undefined variable found in strict mode: %s";
    public static final String VAR_TYPE_REDEFINE = "cannot redefine variable: %s";
    public static final String PARAM_TYPE_MISSING = "missing argument type in strict mode: %s";
    public static final String INCLUDE_PARAM_MORE = "#include cannot accept more than 3 arguments";
    public static final String RETURN_NAME_INVALID = "parameter `returnName` is invalid";
    public static final String DIRECTIVE_PARAM_MISSING = "missing arguments for directive %s";
    public static final String UNKNOWN_OP_UNARY = "unknown unary operator: %s";
    public static final String UNKNOWN_OP_BINARY = "unknown binary operator: %s";
    public static final String UNKNOWN_TOKEN = "unexpected token type: %s";
    public static final String ID_IS_KEYWORD = "syntax error on token `%s`, It is a reserved/keyword identifier";
    public static final String ID_IS_RESERVED = "local variable `%s` cannot start with '$', it is a reserved identifier";
    public static final String DIRECTIVE_OUTSIDE_FOR = "`%s` cannot be used outside of `#for(...)`";
    public static final String INVALID_UNICODE_STRING = "invalid unicode in String";
    public static final String DUPLICATED_MACRO_NAME = "duplicated macro name: %s";
    public static final String NOT_IN_MACRO_BLOCK = "not in macro defination";

    // interpret error
    public static final String CLASS_NOT_FOUND = "cannot resolve class: %s";
    public static final String CTOR_NOT_FOUND = "cannot resolve constructor: %s";
    public static final String METHOD_NOT_FOUND = "cannot resolve method: %s";
    public static final String METHOD_STATIC_NOT_FOUND = "cannot resolve static method: %s";
    public static final String FIELD_STATIC_NOT_FOUND = "cannot resolve static field: %s";
    public static final String PROPERTY_NOT_FOUND = "cannot resolve property: %s";
    public static final String FUNCTION_NOT_FOUND = "cannot resolve function: %s";
    public static final String TAG_NOT_FOUND = "cannot resolve tag: %s";
    public static final String OBJECT_IS_NULL = "object is null: %s";
    public static final String INDEX_IS_NULL = "index is null: %s";
    public static final String TYPE_INCONSISTENT = "inconsistent class for variable: %s";
    public static final String OP_UNARY_UNDEFINED = "the operator `%s` is undefined for the argument type: %s";
    public static final String OP_BINARY_UNDEFINED = "the operator `%s` is undefined for the argument type(s): %s, %s";
    public static final String OP_LHS_IS_NULL = "left operand is null";
    public static final String OP_RHS_IS_NULL = "right operand is null";
    public static final String ARRAY_LEN_NULL = "array length is null";
    public static final String PARAM_NOT_INT = "type mismatch: the arguments[%s] is not an Integer";
    public static final String PARAM_IS_NULL = "the %s argument is null";
    public static final String TYPE_MISMATCH = "type mismatch: the %s argument cannot convert from %s to %s";
    public static final String METHOD_INVOKE_ERROR = "method invoke error: %s";
    public static final String STATIC_METHOD_INVOKE_ERROR = "method invoke error: %s#%s";
    public static final String FUNCTION_INVOKE_ERROR = "function invoke error: %s";
    public static final String NEW_OBJECT_ERROR = "new object error: %s";
    public static final String STATIC_FIELD_GET_ERROR = "static field get error: %s#%s";
    public static final String PROPERTY_GET_ERROR = "property get error: %s";
    public static final String FOR_ITERATOR_ERROR = "iterator error when index = %s";
    public static final String OBJECT_IS_VOID = "object is Void";
    public static final String INDEX_IS_VOID = "index is Void";
    public static final String ARGUMENT_IS_VOID = "argument is Void";

    // 判断是否是因为参数不匹配导致的错误
    public static boolean isReflectIllegalArgument(Throwable e) {
        Class<?> cls = e.getClass();
        if (cls == IllegalArgumentException.class || cls == ClassCastException.class) {
            String className = e.getStackTrace()[0].getClassName();
            //@formatter:off
            return "sun.reflect.NativeMethodAccessorImpl".equals(className)
                || "sun.reflect.GeneratedMethodAccessor1".equals(className)
                || className.startsWith("jetbrick.bean.asm.delegate.");
            //@formatter:on
        }
        return false;
    }

    public static String typeName(Object object) {
        if (object == null) {
            return "<null>";
        } else {
            return StringUtils.removeStart(object.getClass().getName(), "java.lang.");
        }
    }

    public static String format(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        return String.format(message, args);
    }

    public static String format(String message, Source source, Position position) {
        if (source == null || position == null) {
            return message;
        }

        // 允许用户禁止输出模板源码
        if ("true".equals(System.getProperty(JETX_ERROR_SOURCE_DISABLED))) {
            return message;
        }

        StringBuilder sb = new StringBuilder(256);
        sb.append(message);
        sb.append("\n\n");
        sb.append("template: ");
        sb.append(source.getFilename());
        sb.append(": ");
        sb.append(position.getLine());
        sb.append(',');
        sb.append(position.getColumn());
        sb.append('\n');
        sb.append("-------------------------------------------------------------------------------\n");
        markSourceLines(sb, source.getLines(), position.getLine(), position.getColumn());
        sb.append("-------------------------------------------------------------------------------\n");
        return sb.toString();
    }

    // 输出错误对应的模板源码
    static void markSourceLines(StringBuilder sb, String[] sourceLines, int line, int column) {
        int lineStart = Math.max(line - 3, 0);
        int lineEnd = Math.min(line + 2, sourceLines.length);

        for (int i = lineStart; i < lineEnd; i++) {
            String sourceLine = sourceLines[i];

            if (i == line - 1) {
                // 修正 column （可能存在宽字符）
                int origin_column = Math.min(column, sourceLine.length() - 1);
                for (int j = 0; j < origin_column; j++) {
                    char c = sourceLine.charAt(j);
                    if (c == '\t') {
                        column += 3; // 1 个 Tab 变成 4 个空格
                    } else if (c >= '\u2E80' && c <= '\uFE4F') {
                        column++; // 中日韩统一表意文字（CJK Unified Ideographs）
                    }
                }
            }

            sourceLine = sourceLine.replace("\t", "    ");
            sb.append(String.format("%4d: %s%n", Integer.valueOf(i + 1), sourceLine));

            // 插入错误提示符
            if (i == line - 1) {
                sb.append("      ");
                for (int j = 0; j < column; j++) {
                    sb.append(' ');
                }
                sb.append("^ -- here\n");
            }
        }
    }

}
