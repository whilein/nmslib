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
public interface ChatHoverable {

    @FactoryMethod
    static ChatHoverable create(final EnumHoverAction action, final IChatBaseComponent value) {
        throw new UnsupportedOperationException();
    }

    EnumHoverAction getAction();
    IChatBaseComponent getValue();

    interface EnumHoverAction {

        EnumHoverAction SHOW_TEXT = valueOf("SHOW_TEXT");
        EnumHoverAction SHOW_ACHIEVEMENT = valueOf("SHOW_ACHIEVEMENT");
        EnumHoverAction SHOW_ITEM = valueOf("SHOW_ITEM");
        EnumHoverAction SHOW_ENTITY = valueOf("SHOW_ENTITY");
        EnumHoverAction[] VALUES = values();

        @FactoryMethod
        static EnumHoverAction[] values() {
            throw new UnsupportedOperationException();
        }

        @FactoryMethod
        static EnumHoverAction valueOf(final String value) {
            throw new UnsupportedOperationException();
        }

        String getName();
        String name();

        int ordinal();
        boolean isAllowFromServer();
    }

}
