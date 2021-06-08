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
public interface EnumChatFormat {

    EnumChatFormat BLACK = valueOf("BLACK");
    EnumChatFormat DARK_BLUE = valueOf("DARK_BLUE");
    EnumChatFormat DARK_GREEN = valueOf("DARK_GREEN");
    EnumChatFormat DARK_AQUA = valueOf("DARK_AQUA");
    EnumChatFormat DARK_RED = valueOf("DARK_RED");
    EnumChatFormat DARK_PURPLE = valueOf("DARK_PURPLE");
    EnumChatFormat GOLD = valueOf("GOLD");
    EnumChatFormat GRAY = valueOf("GRAY");
    EnumChatFormat DARK_GRAY = valueOf("DARK_GRAY");
    EnumChatFormat BLUE = valueOf("BLUE");
    EnumChatFormat GREEN = valueOf("GREEN");
    EnumChatFormat AQUA = valueOf("AQUA");
    EnumChatFormat RED = valueOf("RED");
    EnumChatFormat LIGHT_PURPLE = valueOf("LIGHT_PURPLE");
    EnumChatFormat YELLOW = valueOf("YELLOW");
    EnumChatFormat WHITE = valueOf("WHITE");
    EnumChatFormat OBFUSCATED = valueOf("OBFUSCATED");
    EnumChatFormat BOLD = valueOf("BOLD");
    EnumChatFormat STRIKETHROUGH = valueOf("STRIKETHROUGH");
    EnumChatFormat UNDERLINE = valueOf("UNDERLINE");
    EnumChatFormat ITALIC = valueOf("ITALIC");
    EnumChatFormat RESET = valueOf("RESET");

    @FactoryMethod
    static EnumChatFormat valueOf(final String name) {
        throw new UnsupportedOperationException();
    }

    String name();
    int ordinal();

}
