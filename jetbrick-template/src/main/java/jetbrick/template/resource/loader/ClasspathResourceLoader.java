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
package jetbrick.template.resource.loader;

import jetbrick.io.resource.ClasspathResource;
import jetbrick.io.resource.Resource;
import jetbrick.util.PathUtils;

public final class ClasspathResourceLoader extends AbstractResourceLoader {

    public ClasspathResourceLoader() {
        root = "";
        reloadable = false;
    }

    @Override
    public Resource load(String name) {
        String path = PathUtils.concat(root, name);
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        ClasspathResource resource = new ClasspathResource(path);
        if (!resource.exist()) {
            return null;
        }

        resource.setPath(name); // use relative name
        return resource;
    }

}
