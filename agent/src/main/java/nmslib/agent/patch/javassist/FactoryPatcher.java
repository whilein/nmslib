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

package nmslib.agent.patch.javassist;

import javassist.Modifier;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.AgentContext;
import nmslib.agent.name.ClassName;
import nmslib.agent.name.Name;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FactoryPatcher implements JavassistClassPatcher {

    Name produces;

    public static JavassistClassPatcher create(final @NonNull Name produces) {
        return new FactoryPatcher(produces);
    }

    @Override
    public void patch(final AgentContext ctx) throws Exception {
        val current = ctx.getCurrent();
        val proxies = ctx.getProxyRegistry();
        val version = ctx.getVersion();

        for (val method : current.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            val params = method.getParameterTypes();
            val proxiedParams = new Name[params.length];

            for (int i = 0; i < params.length; i++) {
                val name = ClassName.parse(params[i].getName());
                val proxy = proxies.getProxy(name);

                proxiedParams[i] = proxy == null
                        ? name : proxy;
            }

            val producesName = produces.format(version).convertToString();

            val sb = new StringBuilder();
            sb.append("{ return new ").append(producesName).append('(');

            for (int i = 0; i < params.length; i++) {
                if (i != 0) sb.append(',');
                sb.append('(').append(proxiedParams[i]).append(")$").append(i+1);
            }

            sb.append("); }");

            method.setBody(sb.toString());
        }
    }
}
