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

import nmslib.api.annotation.FactoryMethod;

/**
 * @author whilein
 */
public interface PacketPlayOutChat extends Packet {

    @FactoryMethod
    static PacketPlayOutChat create() {
        throw new UnsupportedOperationException();
    }

    @FactoryMethod
    static PacketPlayOutChat create(final IChatBaseComponent component) {
        throw new UnsupportedOperationException();
    }

    @FactoryMethod
    static PacketPlayOutChat create(final IChatBaseComponent component, final byte position) {
        throw new UnsupportedOperationException();
    }

    static PacketPlayOutChat create(final IChatBaseComponent component, final Position position) {
        return create(component, (byte) position.ordinal());
    }

    void setPositionIndex(byte value);
    void setComponent(IChatBaseComponent component);

    byte getPositionIndex();
    IChatBaseComponent getComponent();

    default Position getPosition() {
        return Position.VALUES[getPositionIndex()];
    }

    default void setPosition(final Position position) {
        setPositionIndex((byte) position.ordinal());
    }

    enum Position {
        CHAT, SYSTEM, HOTBAR;

        public static final Position[] VALUES = values();
    }

}
