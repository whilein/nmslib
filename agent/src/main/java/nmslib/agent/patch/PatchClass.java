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

import nmslib.agent.AgentContext;
import nmslib.agent.name.Name;
import nmslib.agent.patch.javassist.JavassistClassPatcher;
import nmslib.agent.patch.proxy.ProxyTarget;

/**
 * @author whilein
 */
public interface PatchClass {

    Patch getPatch();

    PatchClass patch(JavassistClassPatcher patcher);

    PatchClass proxyMethod(String name, String proxyName);
    PatchClass proxyMethod(ProxyTarget name, String proxyName);

    PatchClass fieldSetter(String field, String setter);
    PatchClass fieldGetter(String field, String getter);
    PatchClass fieldSetter(String field);
    PatchClass fieldGetter(String field);
    PatchClass fieldAccessor(String field, String getter, String setter);
    PatchClass fieldAccessor(String field);

    PatchClass implement(Name name);

    void patch(AgentContext ctx) throws Exception;

}
