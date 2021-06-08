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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.patch.parser.command.Command;
import nmslib.agent.patch.parser.command.CommandContext;
import org.objectweb.asm.Type;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImplementCommand implements Command {

    boolean extend;

    public static Command create(final boolean extend) {
        return new ImplementCommand(extend);
    }

    @Override
    public String getName() {
        return extend ? "extend" : "implement";
    }

    @Override
    public void execute(final CommandContext ctx, final String[] args) {
        if (args.length < 1) {
            throw new IllegalStateException("missing args for '" + getName() + "'; "
                    + "usage: " + getName() + " <class name>");
        }

        val patchClass = ctx.getPatchClass();

        if (patchClass == null) {
            throw new IllegalStateException("cannot execute '" + getName() + "' without 'class'");
        }

        val typeName = replaceKeys(args[0], ctx.getVersion());

        ctx.getPatch().getProxyRegistry().addProxy(
                patchClass.getName(),
                typeName
        );

        val type = Type.getObjectType(typeName);

        if (extend) {
            patchClass.extend(type);
        } else {
            patchClass.implement(type);
        }
    }

}
