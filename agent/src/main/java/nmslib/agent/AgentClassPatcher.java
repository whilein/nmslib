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

package nmslib.agent;

import javassist.ClassPool;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.name.ClassName;
import nmslib.agent.name.ConstNames;
import nmslib.agent.patch.Patch;
import nmslib.agent.patch.Patches;
import nmslib.agent.version.MinecraftVersion;
import nmslib.agent.version.Version;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AgentClassPatcher implements ClassFileTransformer {

    Patch patch;
    Version version;
    boolean notSupported;

    final ClassPool classPool;
    final Patches patches;

    public static ClassFileTransformer create(final Patches patches) {
        return new AgentClassPatcher(ClassPool.getDefault(), patches);
    }

    @Override
    public byte[] transform(
            final ClassLoader loader,
            final String className,
            final Class<?> classBeingRedefined,
            final ProtectionDomain protectionDomain,
            final byte[] classfileBuffer
    ) {
        if (notSupported) {
            return null;
        }

        try {
            if (className != null) {
                val name = ClassName.parseInternal(className);

                if (patch == null) {
                    if (name.startsWith(ConstNames.nms, false)) {
                        version = MinecraftVersion.getByName(name.valueAt(3));

                        val patch = patches.get(version);
                        patch.ifPresent(value -> this.patch = value);

                        notSupported = !patch.isPresent();
                        System.out.println("Version detected { name = " + version + ", support ?= " + patch.isPresent() + " }");
                    } else {
                        return null;
                    }
                }

                val patchClass = patch.findMatches(name).orElse(null);

                if (patchClass != null) {
                    val ctClass = classPool.get(name.convertToString());

                    patchClass.patch(SimpleAgentContext.create(version, patches.getProxyRegistry(),
                            classPool, ctClass));

                    val result = ctClass.toBytecode();
                    ctClass.detach();

                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
