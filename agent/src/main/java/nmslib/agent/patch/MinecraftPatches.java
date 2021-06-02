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
import nmslib.agent.patch.proxy.ProxyRegistry;
import nmslib.agent.patch.proxy.SimpleProxyRegistry;
import nmslib.agent.version.Version;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftPatches implements Patches {

    @Getter
    ProxyRegistry proxyRegistry;

    Map<Version, Patch> patches;

    public static Patches create() {
        return new MinecraftPatches(SimpleProxyRegistry.create(), new HashMap<>());
    }

    @Override
    public Optional<Patch> get(final Version version) {
        return Optional.ofNullable(patches.get(version));
    }

    @Override
    public Patch patch(final Version version) {
        return patches.computeIfAbsent(version,
                __ -> MinecraftPatch.create(this, version));
    }
}
