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
import org.apache.commons.lang3.StringUtils;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccessorCommand implements Command {

    public static final int GETTER = 1;
    public static final int SETTER = 2;

    int params;

    public static Command create(final int params) {
        if (params < 1 || params > 3) {
            throw new IllegalArgumentException(String.valueOf(params));
        }

        return new AccessorCommand(params);
    }

    @Override
    public String getName() {
        switch (params) {
            case GETTER:
                return "getter";
            case SETTER:
                return "setter";
            default:
                return "accessor";
        }
    }

    @Override
    public void execute(final CommandContext ctx, final String[] args) {
        if (args.length < 1) {
            val paramLabel = "<field>"
                    + ((params & GETTER) != 0 ? " [getter name]" : "")
                    + ((params & SETTER) != 0 ? " [setter name]" : "");

            throw new IllegalStateException("missing args for '" + getName() + "'; "
                    + "usage: " + getName() + " " + paramLabel);
        }

        val patchClass = ctx.getPatchClass();

        if (patchClass == null) {
            throw new IllegalStateException("cannot execute '" + getName() + "' without 'class'");
        }

        int argOffset = 0;

        if ((params & GETTER) != 0) {
            val getterName = args.length > 1
                    ? args[++argOffset]
                    : "get" + StringUtils.capitalize(args[0]);

            patchClass.fieldGetter(args[0], getterName);
        }

        if ((params & SETTER) != 0) {
            val setterName = args.length > argOffset + 1
                    ? args[++argOffset]
                    : "set" + StringUtils.capitalize(args[0]);

            patchClass.fieldSetter(args[0], setterName);
        }
    }

}
