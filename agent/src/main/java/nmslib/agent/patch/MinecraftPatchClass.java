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
import nmslib.agent.name.Name;
import nmslib.agent.patch.javassist.*;
import nmslib.agent.patch.proxy.MethodNameProxyTarget;
import nmslib.agent.patch.proxy.ProxyTarget;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftPatchClass implements PatchClass {

    @Getter
    Name name;

    @Getter
    Patch patch;

    Set<JavassistClassPatcher> patchers;

    public static PatchClass create(final Name name, final Patch patch) {
        return new MinecraftPatchClass(name, patch, new LinkedHashSet<>());
    }

    @Override
    public PatchClass implement(final Name type) {
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
            final String name,
            final String proxyName
    ) {
        return proxyMethod(MethodNameProxyTarget.create(name), proxyName);
    }

    @Override
    public PatchClass proxyMethod(
            final ProxyTarget target,
            final String proxyName
    ) {
        return patch(ProxyPatcher.create(target, proxyName));
    }

    @Override
    public PatchClass fieldGetter(final String field) {
        return patch(GetterPatcher.create(field, "get" + StringUtils.capitalize(field)));
    }

    @Override
    public PatchClass fieldSetter(final String field) {
        return patch(SetterPatcher.create(field, "set" + StringUtils.capitalize(field)));
    }

    @Override
    public PatchClass fieldGetter(final String field, final String getter) {
        return patch(GetterPatcher.create(field, getter));
    }

    @Override
    public PatchClass fieldAccessor(final String field, final String getter, final String setter) {
        return fieldGetter(field, getter).fieldSetter(field, setter);
    }

    @Override
    public PatchClass fieldAccessor(final String field) {
        return fieldGetter(field).fieldSetter(field);
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

}
