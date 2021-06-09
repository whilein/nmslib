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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.output.Output;
import nmslib.agent.patch.MinecraftPatch;
import nmslib.agent.patch.Patch;
import nmslib.agent.patch.PatchClass;
import nmslib.agent.patch.parser.PatchParser;
import nmslib.agent.protocol.AgentProtocolManager;
import nmslib.api.ProxyResolver;
import nmslib.api.Version;
import nmslib.api.protocol.ProtocolManager;

import java.security.ProtectionDomain;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AgentNmsPatcher extends NmsPatcher {

    Patch patch;

    @Getter
    int patchesCount;

    @Getter
    Version version;

    @Getter
    boolean notSupported;

    @Getter
    ProtocolManager protocolManager;

    final PatchParser patchParser;
    final Output output;

    public static NmsPatcher create(final PatchParser reader, final Output output) {
        return new AgentNmsPatcher(reader, output);
    }

    @Override
    public byte[] transform(
            final ClassLoader loader,
            final String name,
            final Class<?> classBeingRedefined,
            final ProtectionDomain protectionDomain,
            final byte[] classfileBuffer
    ) {
        if (notSupported || name == null) {
            return null;
        }

        try {
            if (patch == null) {
                if (name.startsWith("net/minecraft/server/")) {
                    version = MinecraftVersion.getByName(name.split("/")[3]);

                    patch = MinecraftPatch.create(version, SimpleProxyRegistry.create());

                    val parsedPatch = patchParser.read("root");
                    parsedPatch.apply(patch);

                    patch.forClass("net/minecraft/server/" + version + "/ServerConnection$")
                            .injectPipeline();

                    protocolManager = AgentProtocolManager.create(patch);

                    patchesCount = patch.countPatches();
                    notSupported = patchesCount == 0;

                    output.log(
                            "[nms/Init] Version detected { name = " + version
                                    + (notSupported ? ", notSupported" : ", patches = " + patchesCount)
                                    + " }"
                    );

                    if (notSupported) {
                        return null;
                    }
                } else {
                    return null;
                }
            }


            PatchClass patchClass = patch.get(name);

            if (patchClass == null) {
                val innerSeparator = name.indexOf('$');

                if (innerSeparator != -1) {
                    patchClass = patch.get(name.substring(0, innerSeparator + 1));
                }
            }

            if (patchClass != null) {
                return patchClass.patch(patch.getProxyRegistry(), output, name, classfileBuffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ProxyResolver getProxyResolver() {
        if (notSupported) {
            throw new UnsupportedOperationException();
        }

        return patch.getProxyRegistry();
    }
}
