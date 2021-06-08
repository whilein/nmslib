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
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SetterVisitorLinker implements VisitorLinker {

    String field;
    String setter;

    public static VisitorLinker create(
            final @NonNull String field,
            final @NonNull String setter
    ) {
        return new SetterVisitorLinker(field, setter);
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
            public FieldVisitor visitField(
                    final int access,
                    final String name,
                    final String descriptor,
                    final String signature,
                    final Object value
            ) {
                if (name.equals(field)) {
                    val type = Type.getType(descriptor);

                    val setterMethod = visitMethod(
                            Opcodes.ACC_PUBLIC, setter,
                            Type.getMethodDescriptor(Type.VOID_TYPE, type),
                            null, new String[0]
                    );

                    setterMethod.visitCode();
                    setterMethod.visitVarInsn(Opcodes.ALOAD, 0);
                    setterMethod.visitVarInsn(type.getOpcode(Opcodes.ILOAD), 1);
                    setterMethod.visitFieldInsn(Opcodes.PUTFIELD, this.name, name, descriptor);
                    setterMethod.visitInsn(Opcodes.RETURN);
                    setterMethod.visitMaxs(1 + type.getSize(), 1 + type.getSize());
                    setterMethod.visitEnd();

                    output.log("[nms/Setter] [" + this.name + "] Create setter method for field " + field
                            + " " + descriptor);
                }

                return super.visitField(access, name, descriptor, signature, value);
            }
        };
    }
}
