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
import nmslib.agent.patch.MinecraftPatch;
import nmslib.agent.patch.Patch;
import nmslib.agent.patch.proxy.SimpleProxyRegistry;
import nmslib.agent.patch.reader.PatchReader;
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

    final PatchReader patchReader;
    final ClassPool classPool;

    public static ClassFileTransformer create(final PatchReader reader) {
        return new AgentClassPatcher(reader, ClassPool.getDefault());
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
                val name = className.replace('/', '.');

                if (patch == null) {
                    if (name.startsWith("net.minecraft.server.")) {
                        version = MinecraftVersion.getByName(name.split("\\.")[3]);

                        patch = MinecraftPatch.create(version, SimpleProxyRegistry.create());

                        val parsedPatch = patchReader.read("root");
                        parsedPatch.apply(patch);

                        val patches = patch.countPatches();
                        notSupported = patches == 0;

                        System.out.println(
                                "Version detected { name = " + version
                                        + (notSupported ? ", notSupported" : ", patches = " + patches)
                                        + " }"
                        );

                        if (notSupported) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }

                val patchClass = patch.findMatches(name).orElse(null);

                if (patchClass != null) {
                    val ctClass = classPool.get(name);

                    patchClass.patch(SimpleAgentContext.create(version, patch.getProxyRegistry(), classPool, ctClass));

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
