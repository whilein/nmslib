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

package nmslib.agent.patch.parser.command.type;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nmslib.agent.patch.parser.command.Command;
import nmslib.agent.patch.parser.command.CommandContext;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassCommand implements Command {

    public static Command create() {
        return new ClassCommand();
    }

    @Override
    public String getName() {
        return "class";
    }

    @Override
    public void execute(final CommandContext ctx, final String[] args) {
        if (args.length < 1) {
            throw new IllegalStateException("missing args for 'class'; "
                    + "usage: class <name>");
        }

        ctx.setPatchClass(ctx.getPatch().forClass(replaceKeys(args[0], ctx.getVersion())));
    }

}
