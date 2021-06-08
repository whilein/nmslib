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
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.signature.SignatureWriter;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExtendVisitorLinker implements VisitorLinker {

    Type type;

    public static VisitorLinker create(final @NonNull Type type) {
        return new ExtendVisitorLinker(type);
    }

    @Override
    public ClassVisitor link(final ClassVisitor visitor, final Output output, final PatchClass patch) {
        return new ClassVisitor(Opcodes.ASM9, visitor) {

            String interceptedSuperName;

            @Override
            public MethodVisitor visitMethod(
                    final int access,
                    final String name,
                    final String descriptor,
                    final String signature,
                    final String[] exceptions
            ) {
                val method = super.visitMethod(access, name, descriptor, signature, exceptions);

                if (name.equals("<init>")) {
                    return new MethodVisitor(Opcodes.ASM9, method) {
                        @Override
                        public void visitMethodInsn(
                                final int opcode,
                                final String owner,
                                final String name,
                                final String descriptor,
                                final boolean isInterface
                        ) {
                            final String newOwner;

                            if (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>") &&
                                    owner.equals(interceptedSuperName)) {
                                newOwner = type.getInternalName();
                            } else {
                                newOwner = owner;
                            }

                            super.visitMethodInsn(opcode, newOwner, name, descriptor, isInterface);
                        }
                    };
                }

                return method;
            }

            @Override
            public void visit(
                    final int version,
                    final int access,
                    final String name,
                    final String signature,
                    final String superName,
                    final String[] interfaces
            ) {
                final String newSignature;

                if (signature != null) { // не знаю работает это или нет)
                    val signatureReader = new SignatureReader(signature);
                    val signatureWriter = new SignatureWriter() {

                        private boolean nextSuperclass;

                        @Override
                        public SignatureVisitor visitSuperclass() {
                            nextSuperclass = true;
                            return super.visitSuperclass();
                        }

                        @Override
                        public void visitClassType(final String name) {
                            super.visitInnerClassType(nextSuperclass ? type.getInternalName() : name);
                            nextSuperclass = false;
                        }

                        @Override
                        public void visitInnerClassType(final String name) {
                            super.visitInnerClassType(nextSuperclass ? type.getInternalName() : name);
                            nextSuperclass = false;
                        }
                    };

                    signatureReader.accept(signatureWriter);

                    newSignature = signature;
                } else {
                    newSignature = null;
                }

                output.log("[nms/Extend] [" + name + "] Extend " + type.getInternalName() + " (prev = " + superName + ")");

                interceptedSuperName = superName;
                cv.visit(version, access, name, newSignature, type.getInternalName(), interfaces);
            }
        };
    }
}
