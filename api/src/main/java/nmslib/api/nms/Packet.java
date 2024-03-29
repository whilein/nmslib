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

package nmslib.api.nms;

import lombok.val;
import nmslib.api.protocol.ProtocolEvent;
import nmslib.api.protocol.ProtocolListener;

import java.util.Collections;
import java.util.List;

/**
 * @author whilein
 */
public interface Packet {

    void read(PacketDataSerializer serializer);
    void write(PacketDataSerializer serializer);

    default List<ProtocolListener<? extends Packet>> getListeners() {
        return Collections.emptyList();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default void handle(final ProtocolEvent<?> event) {
        for (val listener : getListeners()) {
            ((ProtocolListener) listener).listen(event);
        }
    }

}
