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

package nmslib.agent.protocol;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import nmslib.api.nms.Packet;
import nmslib.api.protocol.ProtocolListener;
import nmslib.api.protocol.RegisteredProtocolListener;

import java.lang.reflect.Field;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImmutableRegisteredProtocolListener<T extends Packet> implements RegisteredProtocolListener<T> {

    Field listenersField;
    ProtocolListener<T> listener;

    public static <T extends Packet> RegisteredProtocolListener<T> create(
            final Field listenersField,
            final ProtocolListener<T> listener
    ) {
        return new ImmutableRegisteredProtocolListener<>(listenersField, listener);
    }

}
