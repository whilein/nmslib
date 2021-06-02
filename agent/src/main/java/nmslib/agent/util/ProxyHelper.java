/*
 *    Copyright 2021 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package nmslib.agent.util;

import javassist.CtClass;
import lombok.experimental.UtilityClass;
import lombok.val;
import nmslib.agent.AgentContext;
import nmslib.agent.name.ClassName;

import java.util.Arrays;

/**
 * @author whilein
 */
@UtilityClass
public class ProxyHelper {

    public CtClass[] getProxy(final AgentContext ctx, final CtClass[] classes) {
        CtClass[] proxied = null;

        for (int i = 0; i < classes.length; i++) {
            val cls = classes[i];
            val proxy = getProxy(ctx, cls);

            if (proxy != cls) {
                if (proxied == null)
                    proxied = Arrays.copyOf(classes, classes.length);

                proxied[i] = getProxy(ctx, cls);
            }
        }

        return proxied == null ? classes : proxied;
    }

    public CtClass getProxy(final AgentContext ctx, final CtClass cls) {
        val name = ClassName.parse(cls.getName());
        val proxy = ctx.getProxyRegistry().getProxy(name);

        if (proxy == null)
            return cls;

        return ctx.resolve(proxy.format(ctx.getVersion()).convertToString());
    }

}
