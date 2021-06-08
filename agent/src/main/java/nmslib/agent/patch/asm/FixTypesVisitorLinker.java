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

package nmslib.agent.patch.asm;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import nmslib.agent.output.Output;
import nmslib.agent.patch.PatchClass;
import nmslib.agent.util.AsmUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;

/**
 * @author whilein
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FixTypesVisitorLinker implements VisitorLinker {

    public static final VisitorLinker INSTANCE = new FixTypesVisitorLinker();

    @Override
    public ClassVisitor link(final ClassVisitor visitor, final Output output, final PatchClass patch) {
        return new ClassVisitor(Opcodes.ASM9, visitor) {

            String name;

            @Override
            public void visit(
                    final int version,
                    final int access,
                    final String name,
                    final String signature,
                    final String superName,
                    final String[] interfaces
            ) {
                super.visit(Opcodes.V1_8, access, name, signature, superName, interfaces);

                this.name = name;
            }

            @Override
            public MethodVisitor visitMethod(
                    final int access,
                    final String name,
                    final String descriptor,
                    final String signature,
                    final String[] exceptions
            ) {
                if ((access & Opcodes.ACC_STATIC) != 0
                        || name.equals("<init>") || name.equals("<clinit>")) {
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }

                val proxyRegistry = patch.getPatch().getProxyRegistry();

                val returnType = Type.getReturnType(descriptor);
                val params = Type.getArgumentTypes(descriptor);

                val fixReturnType = AsmUtils.getProxyApi(proxyRegistry, returnType);

                val fixParams = Arrays.stream(params)
                        .map(type -> AsmUtils.getProxyApi(proxyRegistry, type))
                        .toArray(Type[]::new);

                val paramsNotFixed = Arrays.equals(params, fixParams);

                if (paramsNotFixed && returnType.equals(fixReturnType)) {
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }

                val fixedModifier = Opcodes.ACC_PUBLIC
                        | (paramsNotFixed ? Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC : 0);

                val fixedDescriptor = Type.getMethodDescriptor(fixReturnType, fixParams);

                val fixedMethod = visitMethod(
                        fixedModifier, name,
                        fixedDescriptor,
                        null, exceptions
                );

                fixedMethod.visitCode();
                fixedMethod.visitVarInsn(Opcodes.ALOAD, 0);

                int pos = 1;

                for (int i = 0; i < params.length; i++) {
                    val param = params[i];
                    val fixParam = fixParams[i];

                    fixedMethod.visitVarInsn(fixParam.getOpcode(Opcodes.ILOAD), pos);
                    pos += fixParam.getSize();

                    if (!param.equals(fixParam)) {
                        fixedMethod.visitTypeInsn(Opcodes.CHECKCAST, params[i].getInternalName());
                    }
                }

                fixedMethod.visitMethodInsn(Opcodes.INVOKEVIRTUAL, this.name, name, descriptor, false);
                fixedMethod.visitInsn(fixReturnType.getOpcode(Opcodes.IRETURN));

                fixedMethod.visitMaxs(pos, pos);

                fixedMethod.visitEnd();

                output.log(
                        "[nms/Fix] [" + this.name + "] " + name + " modify descriptor"
                                + " from '" + descriptor + "'"
                                + " to '" + fixedDescriptor + "'");

                return super.visitMethod(access, name, descriptor,
                        signature, exceptions);
            }
        };
    }
}