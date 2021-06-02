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

package nmslib.agent.version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MinecraftVersion implements Version {
    V1_8_R3("v1_8_R3", 47),
    UNSUPPORTED("Unsupported", -1);

    private static final Map<String, Version> BY_NAME;

    static {
        val byName = new HashMap<String, Version>();

        for (val version : values()) {
            byName.put(version.getName(), version);
        }

        BY_NAME = Collections.unmodifiableMap(byName);
    }

    String name;
    int protocol;



    @Override
    public int getId() {
        return ordinal();
    }

    public static Version getByName(final String name) {
        return BY_NAME.get(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
