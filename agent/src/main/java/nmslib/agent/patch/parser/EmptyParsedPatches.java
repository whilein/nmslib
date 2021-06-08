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

package nmslib.agent.patch.parser;

import nmslib.agent.patch.Patch;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author whilein
 */
public final class EmptyParsedPatches implements ParsedPatches {

    public static final ParsedPatches INSTANCE = new EmptyParsedPatches();

    @Override
    public void apply(final Patch patch) {
    }

    @Override
    public List<String[]> getCommands() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, ParsedPatches> getPatches() {
        return Collections.emptyMap();
    }

}
