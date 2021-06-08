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

package nmslib.agent;

import lombok.val;
import nmslib.agent.output.DebugOutput;
import nmslib.agent.output.NoneOutput;
import nmslib.agent.output.Output;
import nmslib.agent.output.SoutOutput;
import nmslib.agent.patch.parser.MinecraftPatchParser;
import nmslib.agent.patch.resolver.ResourcePatchResolver;
import nmslib.api.hook.AgentHookProvider;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

/**
 * @author whilein
 */
public final class AgentMain {

    public static void premain(final String agentArgs, final Instrumentation instrumentation) throws IOException {
        final Output output;

        if (agentArgs != null) {
            switch (agentArgs) {
                case "debug":
                    output = DebugOutput.create();
                    break;
                case "sout":
                    output = SoutOutput.INSTANCE;
                    break;
                default:
                    output = NoneOutput.INSTANCE;
            }
        } else {
            output = NoneOutput.INSTANCE;
        }

        val classPatcher = AgentClassPatcher.create(
                MinecraftPatchParser.create(
                        ResourcePatchResolver.create("patches/", ".nmspatch")
                ),
                output
        );

        instrumentation.addTransformer(classPatcher);

        AgentHookProvider.set(classPatcher);
    }

}
