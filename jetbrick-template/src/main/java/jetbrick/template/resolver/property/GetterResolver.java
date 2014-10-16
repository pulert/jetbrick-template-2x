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
package jetbrick.template.resolver.property;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import jetbrick.bean.*;
import jetbrick.template.resolver.SignatureUtils;

/**
 * 全局用于查找 obj.name 访问
 */
public final class GetterResolver {
    private static final ConcurrentMap<String, Getter> cache = new ConcurrentHashMap<String, Getter>(256);

    /**
     * 查找属性访问
     */
    public static Getter resolve(Class<?> clazz, String name) {
        String signature = SignatureUtils.getFieldSignature(clazz, name);
        Getter found = cache.get(signature);
        if (found != null) {
            return found;
        }

        Getter getter = doGetGetter(clazz, name);

        if (getter != null) {
            cache.put(signature, getter);
            return getter;
        }

        return null;
    }

    /**
     * 查找属性访问
     */
    private static Getter doGetGetter(Class<?> clazz, String name) {
        // map.key
        if (Map.class.isAssignableFrom(clazz)) {
            return new MapGetter(name);
        }
        // array.length
        if ("length".equals(name) && clazz.isArray()) {
            return ArrayLengthGetter.INSTANCE;
        }

        KlassInfo klass = KlassInfo.create(clazz);

        // getXXX() or isXXX()
        PropertyInfo property = klass.getProperty(name);
        if (property != null && property.readable()) {
            return property;
        }

        // object.field (only public)
        FieldInfo field = klass.getField(name);
        if (field != null && field.isPublic()) {
            return field;
        }

        return null;
    }

}
