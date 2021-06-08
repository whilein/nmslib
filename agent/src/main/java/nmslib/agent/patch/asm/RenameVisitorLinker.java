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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.output.Output;
import nmslib.agent.patch.PatchClass;
import nmslib.agent.target.MethodTarget;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RenameVisitorLinker implements VisitorLinker {

    final MethodTarget target;
    final String renameTo;

    public static VisitorLinker create(
            final MethodTarget target,
            final String proxyName
    ) {
        return new RenameVisitorLinker(target, proxyName);
    }

    @Override
    public ClassVisitor link(final ClassVisitor visitor, final Output output, final PatchClass patch) {
        return new ClassVisitor(Opcodes.ASM9, visitor) {
            String name;
            String signature;
            boolean isInterface;

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
                this.signature = signature;
                this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;

                super.visit(Opcodes.V1_8, access, name, signature, superName, interfaces);
            }

            @Override
            public MethodVisitor visitMethod(
                    final int access,
                    final String name,
                    final String descriptor,
                    final String signature,
                    final String[] exceptions
            ) {
                val returnType = Type.getReturnType(descriptor);
                val params = Type.getArgumentTypes(descriptor);

                if (target.matches(descriptor, name)) {
                    val renamedMethod = visitMethod(Opcodes.ACC_PUBLIC, renameTo,
                            descriptor, signature, exceptions);

                    renamedMethod.visitCode();
                    renamedMethod.visitVarInsn(Opcodes.ALOAD, 0);

                    int pos = 1;

                    for (val param : params) {
                        renamedMethod.visitVarInsn(param.getOpcode(Opcodes.ILOAD), pos);
                        pos += param.getSize();
                    }

                    renamedMethod.visitMethodInsn(
                            isInterface
                                    ? Opcodes.INVOKEINTERFACE
                                    : Opcodes.INVOKEVIRTUAL,
                            this.name, name, descriptor, isInterface
                    );


                    renamedMethod.visitInsn(returnType.getOpcode(Opcodes.IRETURN));
                    renamedMethod.visitMaxs(pos, pos);

                    renamedMethod.visitEnd();

                    output.log("[nms/Rename] [" + this.name + "] Method " + name + " to " + renameTo);
                }

                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        };
    }
}
