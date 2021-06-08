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
public final class GetterVisitorLinker implements VisitorLinker {

    String field;
    String getter;

    public static VisitorLinker create(
            final @NonNull String field,
            final @NonNull String getter
    ) {
        return new GetterVisitorLinker(field, getter);
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

                    val getterMethod = visitMethod(
                            Opcodes.ACC_PUBLIC, getter,
                            Type.getMethodDescriptor(type),
                            null, new String[0]
                    );

                    getterMethod.visitCode();
                    getterMethod.visitVarInsn(Opcodes.ALOAD, 0);
                    getterMethod.visitFieldInsn(Opcodes.GETFIELD, this.name, name, descriptor);
                    getterMethod.visitInsn(type.getOpcode(Opcodes.IRETURN));
                    getterMethod.visitMaxs(1, 1);
                    getterMethod.visitEnd();

                    output.log("[nms/Getter] [" + this.name + "] Create getter method for field " + field
                            + " " + descriptor);
                }

                return super.visitField(access, name, descriptor, signature, value);
            }
        };
    }
}
