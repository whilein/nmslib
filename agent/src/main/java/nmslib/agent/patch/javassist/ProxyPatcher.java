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
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.AgentContext;
import nmslib.agent.patch.proxy.ProxyTarget;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProxyPatcher implements JavassistClassPatcher {

    ProxyTarget originalMethodName;
    String proxiedMethodName;

    public static JavassistClassPatcher create(
            final ProxyTarget target,
            final String proxyName
    ) {
        return new ProxyPatcher(target, proxyName);
    }

    private CtMethod searchMethod(final CtClass cls) throws NotFoundException {
        for (val method : cls.getDeclaredMethods()) {
            if (originalMethodName.matches(method.getReturnType(), method.getParameterTypes(), method.getName()))
                return method;
        }

        return null;
    }

    @Override
    public void patch(final AgentContext ctx) throws Exception {
        val current = ctx.getCurrent();
        val proxies = ctx.getProxyRegistry();

        val original = searchMethod(current);

        if (original == null) {
            throw new IllegalStateException("No method found for patch: " + originalMethodName
                    + " in " + current.getName());
        }

        val originalReturnType = original.getReturnType();
        val originalParameters = original.getParameterTypes();

        val proxiedParameters = new CtClass[originalParameters.length];

        for (int i = 0; i < proxiedParameters.length; i++) {
            val proxy = proxies.getProxy(originalParameters[i].getName());

            proxiedParameters[i] = proxy != null
                    ? ctx.resolve(proxy)
                    : originalParameters[i];
        }

        val returnTypeProxy = proxies.getProxy(originalReturnType.getName());

        val returnType = returnTypeProxy != null
                ? ctx.resolve(returnTypeProxy)
                : originalReturnType;

        val body = new StringBuilder();
        body.append("public ").append(returnType.getName()).append(' ').append(proxiedMethodName).append('(');

        for (int i = 0; i < originalParameters.length; i++) {
            if (i != 0) body.append(", ");
            body.append(proxiedParameters[i].getName()).append(' ').append("__var").append(i);
        }

        body.append("){return ");

        if (returnType != originalReturnType)
            body.append('(').append(returnType.getName()).append(')');

        body.append("this.").append(originalMethodName.getMethodName()).append('(');

        for (int i = 0; i < originalParameters.length; i++) {
            if (i != 0) body.append(", ");

            if (proxiedParameters[i] != originalParameters[i])
                body.append('(').append(originalParameters[i].getName()).append(')');
            body.append("__var").append(i);
        }

        body.append(");}");
        current.addMethod(CtNewMethod.make(body.toString(), current));
    }
}
