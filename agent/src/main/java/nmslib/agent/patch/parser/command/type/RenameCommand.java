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
import lombok.val;
import nmslib.agent.patch.parser.command.Command;
import nmslib.agent.patch.parser.command.CommandContext;
import nmslib.agent.target.ExactMethodTarget;
import nmslib.agent.target.NameMethodTarget;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RenameCommand implements Command {

    public static Command create() {
        return new RenameCommand();
    }

    @Override
    public String getName() {
        return "rename";
    }

    @Override
    public void execute(final CommandContext ctx, final String[] args) {
        if (args.length < 2) {
            throw new IllegalStateException("missing args for 'rename'; "
                    + "usage: rename <method name> [descriptor] <new name>");
        }

        val patchClass = ctx.getPatchClass();

        if (patchClass == null) {
            throw new IllegalStateException("cannot execute 'rename' without 'class'");
        }

        patchClass.renameMethod(
                args.length > 2 ? ExactMethodTarget.create(
                        args[0],
                        replaceKeys(args[1], ctx.getVersion())
                ) : NameMethodTarget.create(args[0]),
                args[args.length - 1]
        );
    }

}
