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

package nmslib.agent.patch.parser.command;

import lombok.*;
import lombok.experimental.FieldDefaults;
import nmslib.agent.patch.Patch;
import nmslib.agent.patch.PatchClass;
import nmslib.api.Version;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleCommandContext implements CommandContext {

    final Patch patch;

    @Setter
    PatchClass patchClass;

    public static CommandContext create(
            final @NonNull Patch patch
    ) {
        return new SimpleCommandContext(patch);
    }

    @Override
    public Version getVersion() {
        return patch.getVersion();
    }
}