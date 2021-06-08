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

/**
 * @author whilein
 */
public interface ChatModifier {

    static ChatModifier create() {
        throw new UnsupportedOperationException();
    }

    EnumChatFormat getColor();

    boolean isBold();
    boolean isItalic();
    boolean isStrikethrough();
    boolean isUnderlined();
    boolean isRandom();
    String getInsertion();
    ChatClickable getChatClickable();
    ChatHoverable getChatHoverable();

    ChatModifier setColor(EnumChatFormat value);
    ChatModifier setBold(Boolean value);
    ChatModifier setItalic(Boolean value);
    ChatModifier setUnderline(Boolean value);
    ChatModifier setStrikethrough(Boolean value);
    ChatModifier setRandom(Boolean value);
    ChatModifier setInsertion(String value);
    ChatModifier setChatClickable(ChatClickable click);
    ChatModifier setChatHoverable(ChatHoverable hover);
    ChatModifier clone();

}
