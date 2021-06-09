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
import nmslib.api.ProxyRegistry;
import nmslib.api.Version;

import java.util.HashMap;
import java.util.Map;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftPatch implements Patch {

    @Getter
    final Version version;

    @Getter
    final ProxyRegistry proxyRegistry;

    final Map<String, PatchClass> classes;

    public static Patch create(final Version version, final ProxyRegistry proxyRegistry) {
        return new MinecraftPatch(version, proxyRegistry, new HashMap<>());
    }

    @Override
    public String toString() {
        return classes.toString();
    }

    @Override
    public int countPatches() {
        int mods = 0;

        for (val cls : classes.values()) {
            mods += cls.countPatches();
        }

        return mods;
    }

    @Override
    public PatchClass forClass(final String name) {
        return classes.computeIfAbsent(name, __ -> MinecraftPatchClass.create(name, this));
    }

    @Override
    public PatchClass get(final String name) {
        return classes.get(name);
    }

}
