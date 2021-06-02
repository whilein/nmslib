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
import nmslib.agent.name.Name;
import nmslib.agent.version.Version;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftPatch implements Patch {

    @Getter
    final Patches patches;

    @Getter
    final Version version;

    final Map<Name, PatchClass> classes;

    Version extend;

    public static Patch create(final Patches patches, final Version version) {
        return new MinecraftPatch(patches, version, new HashMap<>());
    }

    @Override
    public void extend(final Version version) {
        this.extend = version;
    }

    @Override
    public String toString() {
        return classes.toString();
    }

    @Override
    public PatchClass forClass(final Name name) {
        return classes.computeIfAbsent(name, __ -> MinecraftPatchClass.create(name, this));
    }

    @Override
    public Optional<PatchClass> findMatches(final Name name) {
        val almostMatch = name.almostMatches(classes);

        if (almostMatch == null && extend != null) {
            return patches.get(extend)
                    .flatMap(patch -> patch.findMatches(name));
        }

        return Optional.ofNullable(almostMatch);
    }

}
