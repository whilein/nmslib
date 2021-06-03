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
import nmslib.agent.patch.proxy.ExactProxyTarget;
import nmslib.agent.patch.proxy.MethodNameProxyTarget;
import nmslib.agent.patch.reader.PatchReader;
import nmslib.agent.version.Version;
import org.apache.commons.lang3.StringUtils;

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

    private String[] parseAll(final String[] values, final Version version) {
        val names = new String[values.length];

        for (int i = 0; i < names.length; i++) {
            names[i] = parse(values[i], version);
        }

        return names;
    }

    private String parse(final String value, final Version version) {
        val parts = value.split("\\.");
        val resultParts = new ArrayList<String>();

        for (val part : parts) {
            if (part.startsWith("$")) {
                switch (part) {
                    case "$nms":
                        resultParts.addAll(Arrays.asList("net", "minecraft", "server", version.getName()));
                        continue;
                    case "$cb":
                        resultParts.addAll(Arrays.asList("org", "bukkit", "craftbukkit", version.getName()));
                        continue;
                    case "$api":
                        resultParts.addAll(Arrays.asList("nmslib", "api"));
                        continue;
                }
            }

            resultParts.add(part);
        }

        return String.join(".", resultParts);
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

                    patchClass = patch.forClass(parse(command[1], version));
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

                    patchClass.implement(parse(command[1], version));
                    break;
                case "proxy":
                    if (command.length < 3) {
                        throw new IllegalStateException("missing args for 'proxy'; "
                                + "usage: proxy <class 1> <class 2>");
                    }

                    patch.getProxyRegistry().addProxy(
                            parse(command[1], version),
                            parse(command[2], version)
                    );
                    break;
                case "proxym":
                    if (command.length < 3) {
                        throw new IllegalStateException("missing args for 'proxym'; "
                                + "usage: proxym <method name> [return type] [...params] <proxy name>");
                    }

                    if (patchClass == null) {
                        throw new IllegalStateException("cannot execute 'proxym' without 'class'");
                    }

                    patchClass.proxyMethod(command.length > 3
                                    ? ExactProxyTarget.create(
                            command[1],
                            parse(command[2], patch.getVersion()),
                            parseAll(Arrays.copyOfRange(command, 3, command.length - 1), version)
                            ) : MethodNameProxyTarget.create(command[1]), command[command.length - 1]
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
