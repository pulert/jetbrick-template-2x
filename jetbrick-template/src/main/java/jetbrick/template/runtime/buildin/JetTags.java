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
package jetbrick.template.runtime.buildin;

import java.io.IOException;
import java.util.Map;
import jetbrick.template.runtime.JetTagContext;

/**
 * 系统自带的 Tag
 */
public final class JetTags {
    /**
     * 类似于 #include, 但是支持 layout 页面集成当前页面的变量.
     *
     * @param ctx Tag 上下文对象
     * @param file layout模板路径
     */
    public static void layout(JetTagContext ctx, String file) throws IOException {
        ctx.getInterpretContext().invokeInclude(file, null, true, null);
    }

    /**
     * 类似于 #include, 但是支持 layout 页面集成当前页面的变量.
     *
     * @param ctx Tag 上下文对象
     * @param file layout模板路径
     * @param parameters 私有参数
     */
    public static void layout(JetTagContext ctx, String file, Map<String, Object> parameters) throws IOException {
        ctx.getInterpretContext().invokeInclude(file, parameters, true, null);
    }

    /**
     * 将一个 layout_block 的内容保存到一个 JetContext 变量中.
     *
     * @param ctx Tag 上下文对象
     * @param name 保存到 JetContext 的变量名
     */
    public static void layout_block(JetTagContext ctx, String name) {
        String bodyContent = ctx.getBodyContent();
        ctx.getValueStack().setLocal(name, bodyContent);
    }

    /**
     * 如果不存在指定的 JetContext 变量，那么输出 layout_block_default 块内容，否则输出指定的 JetContext 变量.
     *
     * @param ctx Tag 上下文对象
     * @param name JetContext 的变量名
     */
    public static void layout_block_default(JetTagContext ctx, String name) throws IOException {
        Object value = ctx.getValueStack().getValue(name);
        if (value == null) {
            ctx.invoke();
        } else {
            ctx.getWriter().print(value.toString());
        }
    }

}
