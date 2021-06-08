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

package nmslib.agent.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import nmslib.api.ProxyResolver;
import org.objectweb.asm.Type;

/**
 * @author whilein
 */
@UtilityClass
public class AsmUtils {

    public static Type getProxyApi(final ProxyResolver resolver, final Type type) {
        return _getProxy(resolver, type, true);
    }

    public static Type getProxyNms(final ProxyResolver resolver, final Type type) {
        return _getProxy(resolver, type, false);
    }

    private static Type _getProxy(final ProxyResolver resolver, final Type type, final boolean api) {
        val descriptor = type.getDescriptor();
        val isArray = descriptor.charAt(0) == '[';

        final String typeName;

        if (isArray) {
            typeName = type.getElementType().getInternalName();
        } else {
            typeName = type.getInternalName();
        }

        val proxy = api
                ? resolver.getApi(typeName)
                : resolver.getNms(typeName);

        if (proxy == null) {
            return type;
        }

        val out = new StringBuilder();

        if (isArray) {
            for (int i = 0; i < type.getDimensions(); i++) {
                out.append('[');
            }
        }

        out.append('L').append(proxy).append(';');

        return Type.getType(out.toString());
    }

}
