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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.patch.Patch;
import nmslib.api.nms.Packet;
import nmslib.api.protocol.ProtocolListener;
import nmslib.api.protocol.ProtocolManager;
import nmslib.api.protocol.RegisteredProtocolListener;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author whilein
 */
@SuppressWarnings("rawtypes")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AgentProtocolManager implements ProtocolManager {

    Patch patch;

    public static ProtocolManager create(final Patch patch) {
        return new AgentProtocolManager(patch);
    }

    @Override
    public <T extends Packet> RegisteredProtocolListener<T> register(
            final Class<T> packetType,
            final ProtocolListener<T> listener
    ) {
        val nmsPacket = patch.getProxyRegistry().getNms(packetType.getName().replace('.', '/'));

        if (nmsPacket == null) {
            throw new IllegalStateException(packetType.getName() + " is not packet");
        }

        try {
            final Field field = Class.forName(nmsPacket.replace('/', '.'))
                    .getDeclaredField("LISTENERS");
            field.setAccessible(true);

            @SuppressWarnings("unchecked")
            final List<ProtocolListener> listeners = (List<ProtocolListener>) field.get(null);
            listeners.add(listener);

            return ImmutableRegisteredProtocolListener.create(field, listener);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(packetType.getName() + " is not listenable");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(
            final RegisteredProtocolListener<?> listener
    ) {
        try {
            final Field field = listener.getListenersField();
            field.setAccessible(true);

            @SuppressWarnings("unchecked")
            final List<ProtocolListener> listeners = (List<ProtocolListener>) field.get(null);
            listeners.remove(listener.getListener());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
