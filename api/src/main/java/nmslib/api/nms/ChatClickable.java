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
public interface ChatClickable {

    @FactoryMethod
    static ChatClickable create(final EnumClickAction action, final String value) {
        throw new UnsupportedOperationException();
    }

    EnumClickAction getAction();
    String getValue();

    interface EnumClickAction {

        EnumClickAction OPEN_URL = valueOf("OPEN_URL");
        EnumClickAction OPEN_FILE = valueOf("OPEN_URL");
        EnumClickAction RUN_COMMAND = valueOf("RUN_COMMAND");
        EnumClickAction TWITCH_USER_INFO = valueOf("TWITCH_USER_INFO");
        EnumClickAction SUGGEST_COMMAND = valueOf("SUGGEST_COMMAND");
        EnumClickAction CHANGE_PAGE = valueOf("CHANGE_PAGE");
        EnumClickAction[] VALUES = values();

        @FactoryMethod
        static EnumClickAction[] values() {
            throw new UnsupportedOperationException();
        }

        @FactoryMethod
        static EnumClickAction valueOf(final String value) {
            throw new UnsupportedOperationException();
        }

        String getName();
        String name();

        int ordinal();
        boolean isAllowFromServer();
    }

}
