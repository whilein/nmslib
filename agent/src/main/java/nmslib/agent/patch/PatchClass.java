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

import nmslib.agent.output.Output;
import nmslib.agent.patch.asm.VisitorLinker;
import nmslib.agent.target.MethodTarget;
import nmslib.api.ProxyResolver;
import org.objectweb.asm.Type;

/**
 * @author whilein
 */
public interface PatchClass {

    String getName();
    Patch getPatch();

    void addLinker(VisitorLinker visitorLinker);
    void renameMethod(MethodTarget target, String proxyMethod);

    void fieldSetter(String field, String setter);
    void fieldGetter(String field, String getter);

    void implement(Type type);

    byte[] patch(ProxyResolver resolver, Output output, byte[] bytes) throws Exception;

    int countPatches();

}
