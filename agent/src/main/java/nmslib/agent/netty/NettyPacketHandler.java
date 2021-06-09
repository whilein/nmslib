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

package nmslib.agent.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.protocol.ImmutableProtocolEvent;
import nmslib.api.nms.NetworkManager;
import nmslib.api.nms.Packet;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NettyPacketHandler extends ChannelDuplexHandler {

    NetworkManager networkManager;

    public static ChannelHandler create(final NetworkManager networkManager) {
        return new NettyPacketHandler(networkManager);
    }

    @Override
    public void write(
            final ChannelHandlerContext channelHandlerContext,
            final Object o,
            final ChannelPromise channelPromise
    ) throws Exception {
       if (o instanceof Packet) {
           val packet = (Packet) o;
           val event = ImmutableProtocolEvent.create(packet, networkManager);

           packet.handle(event);

           if (event.isCancelled()) {
               return;
           }
       }

        super.write(channelHandlerContext, o, channelPromise);
    }

    @Override
    public void channelRead(
            final ChannelHandlerContext channelHandlerContext,
            final Object o
    ) throws Exception {
        if (o instanceof Packet) {
            val packet = (Packet) o;
            val event = ImmutableProtocolEvent.create(packet, networkManager);

            packet.handle(event);

            if (event.isCancelled()) {
                return;
            }
        }

        super.channelRead(channelHandlerContext, o);
    }
}
