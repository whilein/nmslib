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
public interface PacketPlayOutTitle extends Packet {

    @FactoryMethod
    static PacketPlayOutTitle create() {
        throw new UnsupportedOperationException();
    }

    @FactoryMethod
    static PacketPlayOutTitle create(final EnumTitleAction action, final IChatBaseComponent value) {
        throw new UnsupportedOperationException();
    }

    @FactoryMethod
    static PacketPlayOutTitle create(final int fadeIn, final int stay, final int fadeOut) {
        throw new UnsupportedOperationException();
    }

    @FactoryMethod
    static PacketPlayOutTitle create(final EnumTitleAction action, final IChatBaseComponent value,
                                     final int fadeIn, final int stay, final int fadeOut) {
        throw new UnsupportedOperationException();
    }

    void setAction(EnumTitleAction action);
    void setFadeIn(int value);
    void setStay(int value);
    void setFadeOut(int value);

    void setComponent(IChatBaseComponent component);

    EnumTitleAction getAction();
    int getFadeIn();
    int getStay();
    int getFadeOut();
    IChatBaseComponent getComponent();

    interface EnumTitleAction {

        EnumTitleAction TITLE = valueOf("TITLE");
        EnumTitleAction SUBTITLE = valueOf("SUBTITLE");
        EnumTitleAction TIMES = valueOf("TIMES");
        EnumTitleAction CLEAR = valueOf("CLEAR");
        EnumTitleAction RESET = valueOf("RESET");
        EnumTitleAction[] VALUES = values();

        @FactoryMethod
        static EnumTitleAction[] values() {
            throw new UnsupportedOperationException();
        }

        @FactoryMethod
        static EnumTitleAction valueOf(final String value) {
            throw new UnsupportedOperationException();
        }

        String name();
        int ordinal();
    }

}
