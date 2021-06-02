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

package nmslib.agent.patch.proxy;

import javassist.CtClass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExactProxyTarget implements ProxyTarget {

    @Getter
    String methodName;
    String returnType;
    String[] params;

    @Override
    public boolean matches(final CtClass returnType, final CtClass[] params, final String methodName) {
        return methodName.equals(this.methodName)
                && returnType.getName().equals(this.returnType)
                && testParams(params);
    }

    private boolean testParams(final CtClass[] params) {
        if (params.length != this.params.length) return false;

        for (int i = 0; i < params.length; i++) {
            if (!params[i].getName().equals(this.params[i]))
                return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "{" + returnType + " " + methodName + "(" + String.join(", ", params) + ")}";
    }

    public static ProxyTarget create(
            final String methodName,
            final String returnType,
            final String... params
    ) {
        return new ExactProxyTarget(methodName, returnType, params);
    }

}
