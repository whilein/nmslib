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

package nmslib.api.protocol;

import nmslib.api.nms.EntityPlayer;
import nmslib.api.nms.NetworkManager;
import nmslib.api.nms.Packet;

/**
 * @author whilein
 */
public interface ProtocolEvent<T extends Packet> {

    void setCancelled(boolean cancelled);
    boolean isCancelled();

    T getPacket();
    NetworkManager getNetworkManager();

    EntityPlayer getPlayer();

}
