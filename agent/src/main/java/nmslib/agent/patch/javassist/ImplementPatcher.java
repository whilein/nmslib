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
import javassist.bytecode.AccessFlag;
import javassist.bytecode.Bytecode;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.AgentContext;
import nmslib.agent.name.Name;
import nmslib.agent.util.ProxyHelper;

import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImplementPatcher implements JavassistClassPatcher {

    Name implementation;

    public static JavassistClassPatcher create(final @NonNull Name implementation) {
        return new ImplementPatcher(implementation);
    }

    public int countSize(final CtClass[] classes) {
        return Arrays.stream(classes).map(CtClass::getName)
                .mapToInt(name -> name.equals("long") || name.equals("double") ? 2 : 1)
                .sum();
    }

    @Override
    public void patch(final AgentContext ctx) throws Exception {
        val cls = ctx.getCurrent();
        cls.addInterface(ctx.resolve(implementation.convertToString()));

        for (val method : cls.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()))
                continue;

            val returnType = method.getReturnType();
            val params = method.getParameterTypes();

            val proxyReturnType = ProxyHelper.getProxy(ctx, returnType);
            val proxyParams = ProxyHelper.getProxy(ctx, params);

            val differentParameters = params != proxyParams;

            if (!differentParameters && returnType == proxyReturnType)
                continue;

            if (differentParameters) { // not a bridge, just proxy
                val body = new StringBuilder("{");

                if (!proxyReturnType.getName().equals("void")) {
                    body.append("return ");
                }

                body.append("$0.").append(method.getName()).append("(");

                for (int i = 0; i < params.length; i++) {
                    if (i != 0) body.append(',');

                    body.append('(').append(params[i].getName()).append(')').append("$").append(i + 1);
                }

                body.append(");}");

                val paramProxy = CtNewMethod.make(AccessFlag.PUBLIC, proxyReturnType,
                        method.getName(), proxyParams, method.getExceptionTypes(),
                        body.toString(), cls);

                cls.addMethod(paramProxy);
            } else {
                val descriptor = Descriptor.ofMethod(proxyReturnType, params);

                val constPool = cls.getClassFile().getConstPool();
                val methodInfo = new MethodInfo(constPool, method.getName(), descriptor);
                methodInfo.setAccessFlags(AccessFlag.PUBLIC | AccessFlag.SYNTHETIC | AccessFlag.BRIDGE);

                val stackSize = countSize(params) + 1;

                val code = new Bytecode(constPool, stackSize, stackSize);
                code.addAload(0);

                int stackPos = 1;

                for (val param : params) {
                    switch (param.getName()) {
                        case "byte":
                        case "short":
                        case "boolean":
                        case "char":
                        case "int":
                            code.addIload(stackPos++);
                            break;
                        case "float":
                            code.addFload(stackPos++);
                            break;
                        case "double":
                            code.addDload(stackPos);
                            stackPos += 2;
                            break;
                        case "long":
                            code.addLload(stackPos);
                            stackPos += 2;
                            break;
                        default:
                            code.addAload(stackPos++);
                            break;
                    }
                }

                code.addInvokevirtual(cls, method.getName(), method.getReturnType(), method.getParameterTypes());
                code.addReturn(proxyReturnType);

                methodInfo.setCodeAttribute(code.toCodeAttribute());

                cls.addMethod(CtMethod.make(methodInfo, cls));
                cls.writeFile("code");
                cls.defrost();
            }
        }
    }
}
