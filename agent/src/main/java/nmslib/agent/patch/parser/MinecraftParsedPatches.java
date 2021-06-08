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

package nmslib.agent.patch.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.patch.Patch;
import nmslib.agent.patch.parser.command.SimpleCommandContext;
import nmslib.agent.patch.parser.command.SimpleCommandManager;
import nmslib.api.Version;

import java.io.IOException;
import java.util.*;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftParsedPatches implements ParsedPatches {

    List<String[]> commands;
    Map<String, ParsedPatches> patches;

    public static ParsedPatches parse(
            final PatchParser reader,
            final List<String[]> tokens
    ) throws IOException {
        val commands = new ArrayList<String[]>();
        val commandBuffer = new ArrayList<String[]>();
        val patches = new HashMap<String, ParsedPatches>();
        val patchesBuffer = new HashMap<String, ParsedPatches>();

        String version = null;

        for (val token : tokens) {
            switch (token[0]) {
                case "version":
                    if (!commandBuffer.isEmpty() || !patchesBuffer.isEmpty()) {
                        patches.put(version, new MinecraftParsedPatches(
                                Collections.unmodifiableList(new ArrayList<>(commandBuffer)),
                                Collections.unmodifiableMap(new HashMap<>(patchesBuffer)))
                        );

                        commandBuffer.clear();
                        patchesBuffer.clear();
                    }

                    version = token[1];
                    break;
                case "include":
                    val includePatch = reader.read(token[1]);

                    if (version != null) {
                        commandBuffer.addAll(includePatch.getCommands());
                        patchesBuffer.putAll(includePatch.getPatches());
                    } else {
                        commands.addAll(includePatch.getCommands());
                        patches.putAll(includePatch.getPatches());
                    }

                    break;
            }

            if (version == null) {
                commands.add(token);
            } else {
                commandBuffer.add(token);
            }
        }

        if (version != null && (!commandBuffer.isEmpty() || !patchesBuffer.isEmpty())) {
            patches.put(version, new MinecraftParsedPatches(
                    Collections.unmodifiableList(new ArrayList<>(commandBuffer)),
                    Collections.unmodifiableMap(new HashMap<>(patchesBuffer)))
            );
        }

        return new MinecraftParsedPatches(
                Collections.unmodifiableList(commands),
                Collections.unmodifiableMap(patches)
        );
    }

    private String replaceKeys(final String to, final Version version) {
        return to.replace('.', '/')
                .replace("$nms", "net/minecraft/server/" + version.getName())
                .replace("$cb", "org/bukkit/craftbukkit/" + version.getName())
                .replace("$api", "nmslib/api");
    }

    @Override
    public void apply(final Patch patch) {
        val context = SimpleCommandContext.create(patch);
        val manager = SimpleCommandManager.createDefault();

        for (val command : commands) {
            manager.process(command, context);
        }

        val byVersion = patches.get(patch.getVersion().getName());

        if (byVersion != null) {
            byVersion.apply(patch);
        }
    }


}
