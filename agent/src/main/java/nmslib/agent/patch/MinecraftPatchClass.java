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

package nmslib.agent.patch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.output.Output;
import nmslib.agent.patch.asm.*;
import nmslib.agent.target.MethodTarget;
import nmslib.api.ProxyResolver;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftPatchClass implements PatchClass {

    @Getter
    String name;

    @Getter
    Patch patch;

    List<VisitorLinker> visitorLinkers;

    public static PatchClass create(final String name, final Patch patch) {
        return new MinecraftPatchClass(name, patch, new ArrayList<>());
    }

    @Override
    public void implement(final Type type) {
        addLinker(ImplementVisitorLinker.create(type));
        patch.forClass(type.getInternalName()).factory(name);
    }

    @Override
    public void extend(final Type type) {
        addLinker(ExtendVisitorLinker.create(type));
        patch.forClass(type.getInternalName()).factory(name);
    }

    private void addLinker(final VisitorLinker visitorLinker) {
        visitorLinkers.add(visitorLinker);
    }

    @Override
    public void renameMethod(
            final MethodTarget target,
            final String proxyName
    ) {
        addLinker(RenameVisitorLinker.create(target, proxyName));
    }

    @Override
    public void fieldGetter(final String field, final String getter) {
        addLinker(GetterVisitorLinker.create(field, getter));
    }

    @Override
    public void factory(final String produces) {
        addLinker(FactoryVisitorLinker.create(produces));
    }

    @Override
    public void fieldSetter(final String field, final String setter) {
        addLinker(SetterVisitorLinker.create(field, setter));
    }

    @Override
    public byte[] patch(final ProxyResolver resolver, final Output output, final byte[] input) throws Exception {
        val reader = new ClassReader(input);
        val writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        ClassVisitor visitor = FixTypesVisitorLinker.INSTANCE.link(writer, output, this);

        for (val linker : visitorLinkers) {
            visitor = linker.link(visitor, output, this);
        }

        reader.accept(visitor, 0);

        val bytes=  writer.toByteArray();
        output.logClass(bytes, name);

        return bytes;
    }

    @Override
    public int countPatches() {
        return visitorLinkers.size();
    }

}
