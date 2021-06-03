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
import nmslib.agent.AgentContext;
import nmslib.agent.patch.javassist.*;
import nmslib.agent.patch.proxy.ProxyTarget;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftPatchClass implements PatchClass {

    String name;

    @Getter
    Patch patch;

    Set<JavassistClassPatcher> patchers;

    public static PatchClass create(final String name, final Patch patch) {
        return new MinecraftPatchClass(name, patch, new LinkedHashSet<>());
    }

    @Override
    public PatchClass implement(final String type) {
        patch.forClass(type).patch(FactoryPatcher.create(name));
        return patch(ImplementPatcher.create(type));
    }

    @Override
    public PatchClass patch(final JavassistClassPatcher patcher) {
        patchers.add(patcher);
        return this;
    }

    @Override
    public PatchClass proxyMethod(
            final ProxyTarget target,
            final String proxyName
    ) {
        return patch(ProxyPatcher.create(target, proxyName));
    }

    @Override
    public PatchClass fieldGetter(final String field, final String getter) {
        return patch(GetterPatcher.create(field, getter));
    }

    @Override
    public PatchClass fieldSetter(final String field, final String setter) {
        return patch(SetterPatcher.create(field, setter));
    }

    @Override
    public void patch(final AgentContext ctx) throws Exception {
        for (val patcher : patchers) {
            patcher.patch(ctx);
        }
    }

    @Override
    public int countPatches() {
        return patchers.size();
    }

}
