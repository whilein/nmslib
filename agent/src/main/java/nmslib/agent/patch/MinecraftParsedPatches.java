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
import nmslib.agent.patch.reader.PatchReader;
import nmslib.agent.target.ExactMethodTarget;
import nmslib.agent.target.NameMethodTarget;
import nmslib.api.Version;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Type;

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
            final PatchReader reader,
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
        val version = patch.getVersion();

        PatchClass patchClass = null;

        for (val command : commands) {
            switch (command[0]) {
                case "class":
                    if (command.length < 2) {
                        throw new IllegalStateException("missing args for 'class'; "
                                + "usage: class <name>");
                    }

                    patchClass = patch.forClass(replaceKeys(command[1], version));
                    break;
                case "getter":
                    if (command.length < 2) {
                        throw new IllegalStateException("missing args for 'getter'; "
                                + "usage: getter <field> [getter name]");
                    }

                    if (patchClass == null) {
                        throw new IllegalStateException("cannot execute 'getter' without 'class'");
                    }

                    patchClass.fieldGetter(command[1],
                            command.length <= 2
                                    ? "get" + StringUtils.capitalize(command[1])
                                    : command[2]);
                    break;
                case "setter":
                    if (command.length < 2) {
                        throw new IllegalStateException("missing args for 'setter'; "
                                + "usage: setter <field> [setter name]");
                    }

                    if (patchClass == null) {
                        throw new IllegalStateException("cannot execute 'setter' without 'class'");
                    }

                    patchClass.fieldSetter(command[1],
                            command.length <= 2
                                    ? "set" + StringUtils.capitalize(command[1])
                                    : command[2]);
                    break;
                case "accessor":
                    if (command.length < 2) {
                        throw new IllegalStateException("missing args for 'accessor'; "
                                + "usage: accessor <field> [<setter name> <getter name>]");
                    }

                    if (patchClass == null) {
                        throw new IllegalStateException("cannot execute 'accessor' without 'class'");
                    }

                    val capitalized = StringUtils.capitalize(command[1]);

                    patchClass.fieldSetter(command[1],
                            command.length <= 4
                                    ? "set" + capitalized
                                    : command[2]);

                    patchClass.fieldGetter(command[1],
                            command.length <= 4
                                    ? "get" + capitalized
                                    : command[3]);
                    break;
                case "implement":
                    if (command.length < 2) {
                        throw new IllegalStateException("missing args for 'implement'; "
                                + "usage: implement <class name>");
                    }

                    if (patchClass == null) {
                        throw new IllegalStateException("cannot execute 'implement' without 'class'");
                    }

                    val implement = replaceKeys(command[1], version);

                    patch.getProxyRegistry().addProxy(
                            patchClass.getName(),
                            implement
                    );

                    patchClass.implement(Type.getObjectType(implement));
                    break;
                case "rename":
                    if (command.length < 3) {
                        throw new IllegalStateException("missing args for 'rename'; "
                                + "usage: rename <method name> [descriptor] <new name>");
                    }

                    if (patchClass == null) {
                        throw new IllegalStateException("cannot execute 'rename' without 'class'");
                    }

                    patchClass.renameMethod(
                            command.length > 3 ? ExactMethodTarget.create(
                                    command[1],
                                    replaceKeys(command[2], version)
                            ) : NameMethodTarget.create(command[1]),
                            command[command.length - 1]
                    );

                    break;
            }
        }

        val byVersion = patches.get(patch.getVersion().getName());

        if (byVersion != null) {
            byVersion.apply(patch);
        }
    }


}
