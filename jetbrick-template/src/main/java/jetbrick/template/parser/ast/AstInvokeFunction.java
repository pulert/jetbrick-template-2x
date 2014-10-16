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
package jetbrick.template.parser.ast;

import jetbrick.template.Errors;
import jetbrick.template.JetTemplateMacro;
import jetbrick.template.resolver.ParameterUtils;
import jetbrick.template.resolver.SignatureUtils;
import jetbrick.template.resolver.function.FunctionInvoker;
import jetbrick.template.runtime.InterpretContext;
import jetbrick.template.runtime.InterpretException;
import jetbrick.util.ArrayUtils;

public final class AstInvokeFunction extends AstExpression {
    private final String name;
    private final AstExpressionList argumentList;
    private FunctionInvoker last;

    public AstInvokeFunction(String name, AstExpressionList argumentList, Position position) {
        super(position);
        this.name = name;
        this.argumentList = argumentList;
    }

    @Override
    public Object execute(InterpretContext ctx) throws InterpretException {
        Object[] arguments;
        if (argumentList == null) {
            arguments = ArrayUtils.EMPTY_OBJECT_ARRAY;
        } else {
            arguments = argumentList.execute(ctx);
        }

        return doInvoke(ctx, last, arguments);
    }

    private Object doInvoke(InterpretContext ctx, FunctionInvoker fn, Object[] arguments) throws InterpretException {
        boolean useLatest = (fn != null);

        if (fn == null) {
            Class<?>[] argumentTypes = ParameterUtils.getParameterTypes(arguments);
            fn = resolveFunction(ctx, argumentTypes);

            if (fn == null) {
                String signature = SignatureUtils.getFunctionSignature(name, argumentTypes);
                throw new InterpretException(Errors.FUNCTION_NOT_FOUND, signature).set(position);
            }

            this.last = fn; // 找到一个新的 invoker
        }

        try {
            return fn.invoke(arguments);
        } catch (RuntimeException e) {
            if (useLatest && Errors.isReflectIllegalArgument(e)) {
                // 重新查找匹配的 Invoker
                return doInvoke(ctx, null, arguments);
            }
            throw new InterpretException(Errors.FUNCTION_INVOKE_ERROR).cause(e).set(position);
        }
    }

    private FunctionInvoker resolveFunction(InterpretContext ctx, Class<?>[] argumentTypes) {
        // 优先查找宏定义
        JetTemplateMacro macro = ctx.getTemplate().resolveMacro(name, argumentTypes);
        if (macro != null) {
            return macro;
        }
        return ctx.getGlobalResolver().resolveFunction(name, argumentTypes);
    }
}
