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

import lombok.*;
import lombok.experimental.FieldDefaults;
import nmslib.api.nms.EntityPlayer;
import nmslib.api.nms.NetworkManager;
import nmslib.api.nms.Packet;
import nmslib.api.nms.PlayerConnection;
import nmslib.api.protocol.ProtocolEvent;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImmutableProtocolEvent<T extends Packet> implements ProtocolEvent<T> {

    @Setter
    boolean cancelled;

    final T packet;
    final NetworkManager networkManager;

    public static <T extends Packet> ProtocolEvent<T> create(
            final T packet,
            final NetworkManager networkManager
    ) {
        return new ImmutableProtocolEvent<>(packet, networkManager);
    }

    @Override
    public EntityPlayer getPlayer() {
        val packetListener = networkManager.getPacketListener();

        if (packetListener instanceof PlayerConnection) {
            val playerConnection = (PlayerConnection) packetListener;
            return playerConnection.getPlayer();
        }

        return null;
    }
}
