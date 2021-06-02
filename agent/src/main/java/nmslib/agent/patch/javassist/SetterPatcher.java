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

package nmslib.agent.patch.javassist;

import javassist.CtNewMethod;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.AgentContext;
import nmslib.agent.name.ClassName;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SetterPatcher implements JavassistClassPatcher {

    String field;
    String setter;

    public static JavassistClassPatcher create(
            final String field,
            final String setter
    ) {
        return new SetterPatcher(field, setter);
    }

    @Override
    public void patch(final AgentContext ctx) throws Exception {
        val current = ctx.getCurrent();
        val field = current.getDeclaredField(this.field);
        val proxy = ctx.getProxyRegistry().getProxy(ClassName.parse(field.getType().getName()));

        val type = proxy == null
                ? field.getType().getName()
                : proxy.convertToString();

        val code = "public void " + setter + "(" + (proxy == null ? type : proxy) + " value) { "
                + "this." + this.field + " = " + (proxy == null ? "" : "(" + field.getType().getName() + ")") + " value; }";

        current.addMethod(CtNewMethod.make(code, current));
    }
}
