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

package nmslib.agent.patch.parser.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.patch.parser.command.type.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleCommandManager implements CommandManager {

    Map<String, Command> commands;

    public static CommandManager create() {
        return new SimpleCommandManager(new HashMap<>());
    }

    public static CommandManager createDefault() {
        val commandManager = create();

        commandManager.add(ImplementCommand.create(false));
        commandManager.add(ImplementCommand.create(true));
        commandManager.add(AccessorCommand.create(AccessorCommand.GETTER));
        commandManager.add(AccessorCommand.create(AccessorCommand.SETTER));
        commandManager.add(AccessorCommand.create(AccessorCommand.GETTER | AccessorCommand.SETTER));
        commandManager.add(ClassCommand.create());
        commandManager.add(RenameCommand.create());
        commandManager.add(AdapterCommand.create());

        return commandManager;
    }

    @Override
    public void add(final Command command) {
        commands.put(command.getName(), command);
    }

    @Override
    public void process(
            final String[] command,
            final CommandContext context
    ) {
        val executor = commands.get(command[0]);
        if (executor == null) return;

        executor.execute(context, Arrays.copyOfRange(command, 1, command.length));
    }
}
