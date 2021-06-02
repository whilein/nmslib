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

import javassist.ClassPool;
import javassist.CtClass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import nmslib.agent.patch.proxy.ProxyRegistry;
import nmslib.agent.version.Version;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleAgentContext implements AgentContext {

    @Getter
    Version version;

    @Getter
    ProxyRegistry proxyRegistry;

    ClassPool classPool;

    @Getter
    CtClass current;

    public static AgentContext create(
            final Version version,
            final ProxyRegistry proxyRegistry,
            final ClassPool classPool,
            final CtClass current
    ) {
        return new SimpleAgentContext(version, proxyRegistry, classPool, current);
    }

    @Override
    public CtClass resolve(final String name) {
        return classPool.getOrNull(name);
    }

}
