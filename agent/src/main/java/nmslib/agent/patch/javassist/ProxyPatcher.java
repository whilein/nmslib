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

import javassist.CtClass;
import javassist.CtNewMethod;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.AgentContext;
import nmslib.agent.name.ClassName;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProxyPatcher implements JavassistClassPatcher {

    String originalMethodName;
    String proxiedMethodName;

    public static JavassistClassPatcher create(
            final String name,
            final String proxyName
    ) {
        return new ProxyPatcher(name, proxyName);
    }

    @Override
    public void patch(final AgentContext ctx) throws Exception {
        val current = ctx.getCurrent();
        val proxies = ctx.getProxyRegistry();

        val original = current.getDeclaredMethod(originalMethodName);
        val originalReturnType = original.getReturnType();
        val originalParameters = original.getParameterTypes();

        val proxiedParameters = new CtClass[originalParameters.length];

        for (int i = 0; i < proxiedParameters.length; i++) {
            val proxy = proxies.getProxy(ClassName.parse(originalParameters[i].getName()));

            proxiedParameters[i] = proxy != null
                    ? ctx.resolve(proxy.convertToString())
                    : originalParameters[i];
        }

        val returnTypeProxy = proxies.getProxy(ClassName.parse(originalReturnType.getName()));

        val returnType = returnTypeProxy != null
                ? ctx.resolve(returnTypeProxy.convertToString())
                : originalReturnType;

        val body = new StringBuilder();
        body.append("public ").append(returnType.getName()).append(' ').append(proxiedMethodName).append('(');

        boolean hasParams = false;

        for (int i = 0; i < originalParameters.length; i++) {
            if (hasParams) {
                body.append(", ");
            } else {
                hasParams = true;
            }

            body.append(proxiedParameters[i].getName()).append(' ').append("__var").append(i);
        }

        body.append("){return ");

        if (returnType != originalReturnType)
            body.append('(').append(returnType.getName()).append(')');

        body.append("this.").append(originalMethodName).append('(');

        hasParams = false;

        for (int i = 0; i < originalParameters.length; i++) {
            if (hasParams) {
                body.append(", ");
            } else {
                hasParams = true;
            }

            if (proxiedParameters[i] != originalParameters[i])
                body.append('(').append(originalParameters[i].getName()).append(')');
            body.append("__var").append(i);
        }

        body.append(");}");
        current.addMethod(CtNewMethod.make(body.toString(), current));
    }
}
