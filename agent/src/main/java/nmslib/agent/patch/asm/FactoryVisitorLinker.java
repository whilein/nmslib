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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FactoryVisitorLinker implements VisitorLinker {

    private static final MethodVisitor EMPTY = new MethodVisitor(Opcodes.ASM9) {};

    String produces;

    public static FactoryVisitorLinker create(final @NonNull String produces) {
        return new FactoryVisitorLinker(produces);
    }

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
                this.name = name;

                super.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public MethodVisitor visitMethod(
                    final int access,
                    final String name,
                    final String descriptor,
                    final String signature,
                    final String[] exceptions
            ) {
                if ((access & Opcodes.ACC_STATIC) == 0 || name.equals("<clinit>")) {
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }

                val method = super.visitMethod(access, name, descriptor, signature, exceptions);
                method.visitCode();

                if (name.equals("valueOf") && descriptor.equals("(Ljava/lang/String;)L" + this.name + ";")) {
                    method.visitVarInsn(Opcodes.ALOAD, 0);
                    method.visitMethodInsn(Opcodes.INVOKESTATIC, produces, name,
                            "(Ljava/lang/String;)L" + produces + ";", false);
                    method.visitMaxs(1, 2);

                    output.log("[nms/Factory] [" + this.name + "] Add valueOf method which references to "
                            + produces.replace('/', '.') + "#" + name);
                } else {
                    val resolver = patch.getPatch().getProxyRegistry();

                    val params = Type.getArgumentTypes(descriptor);

                    val fixParams = Arrays.stream(params)
                            .map(type -> AsmUtils.getProxyNms(resolver, type))
                            .toArray(Type[]::new);

                    method.visitCode();
                    method.visitTypeInsn(Opcodes.NEW, produces);
                    method.visitInsn(Opcodes.DUP);

                    int pos = 0;

                    for (int i = 0; i < params.length; i++) {
                        val param = params[i];
                        val fixParam = fixParams[i];

                        method.visitVarInsn(fixParam.getOpcode(Opcodes.ILOAD), pos);
                        pos += fixParam.getSize();

                        if (!param.equals(fixParam)) {
                            method.visitTypeInsn(Opcodes.CHECKCAST, params[i].getInternalName());
                        }
                    }

                    method.visitMethodInsn(Opcodes.INVOKESPECIAL, produces, "<init>",
                            Type.getMethodDescriptor(Type.VOID_TYPE, fixParams), false);

                    method.visitMaxs(pos + 2, pos);

                   output.log("[nms/Factory] [" + this.name + "] Use factory method " + name
                            + " to create " + produces
                            + " " + Arrays.toString(params) + " -> " + Arrays.toString(fixParams));
                }

                method.visitInsn(Opcodes.ARETURN);
                method.visitEnd();

                return EMPTY;
            }
        };
    }
}
