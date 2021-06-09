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
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AdapterVisitorLinker implements VisitorLinker {

    String value;

    public static VisitorLinker create(final String value) {
        return new AdapterVisitorLinker(value);
    }

    @Override
    public ClassVisitor link(final ClassVisitor visitor, final Output output, final PatchClass patch) {
        return new ClassVisitor(Opcodes.ASM9, visitor) {

            @Override
            public void visit(
                    final int version,
                    final int access,
                    final String name,
                    final String signature,
                    final String superName,
                    final String[] interfaces
            ) {
                super.visit(version, access, name, signature, superName, interfaces);

                if (!value.equals("packet-listen")) return;

                val fieldDescriptor = "Ljava/util/List;";
                val fieldSignature = "Ljava/util/List<Lnmslib/api/protocol/ProtocolListener<L" + name + ";>;>;";
                visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, "LISTENERS",
                        fieldDescriptor,
                        fieldSignature,
                        null).visitEnd();

                val mv = visitMethod(Opcodes.ACC_STATIC,
                        "<clinit>", "()V",
                        null, null
                );

                mv.visitCode();
                mv.visitTypeInsn(Opcodes.NEW, "java/util/concurrent/CopyOnWriteArrayList");
                mv.visitInsn(Opcodes.DUP);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArrayList",
                        "<init>", "()V", false);
                mv.visitFieldInsn(Opcodes.PUTSTATIC, name, "LISTENERS", fieldDescriptor);

                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(2, 0);
                mv.visitEnd();

                val getListeners = visitMethod(Opcodes.ACC_PUBLIC,
                        "getListeners", "()" + fieldDescriptor, "()" + fieldSignature, null);
                getListeners.visitCode();
                getListeners.visitFieldInsn(Opcodes.GETSTATIC, name, "LISTENERS", fieldDescriptor);
                getListeners.visitInsn(Opcodes.ARETURN);
                getListeners.visitMaxs(1, 1);
                getListeners.visitEnd();
            }

        };
    }

}
