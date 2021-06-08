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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.patch.resolver.PatchResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftPatchParser implements PatchParser {

    @Getter
    PatchResolver resolver;

    public static PatchParser create(final PatchResolver resolver) {
        return new MinecraftPatchParser(resolver);
    }

    @Override
    public ParsedPatches read(final String name) throws IOException {
        val stream = resolver.resolve(name);

        if (stream == null) {
            return EmptyParsedPatches.INSTANCE;
        }

        return read(stream);
    }

    private ParsedPatches read(final InputStream is) throws IOException {
        val lineTokens = new ArrayList<String>();
        val tokens = new ArrayList<String[]>();

        val buf = new StringBuilder();

        int ch;
        boolean str = false;
        boolean mstr = false;
        boolean brackets = false;
        boolean lineJustBegun = true;

        while ((ch = is.read()) != -1) {
            if ((ch == '\r' || ch == '\n') && str && !mstr) {
                throw new IllegalStateException("Cannot use line feed or carriage return in string");
            }

            if (ch == '>' && !mstr && !str) {
                str = true;
                mstr = true;
                continue;
            }

            if (ch == '<' && mstr) {
                str = false;
                mstr = false;

                val oldValue = buf.toString();
                buf.setLength(0);

                buf.append(
                        oldValue.replace("\n", "")
                                .replace("\r", "")
                );

                continue;
            }

            if (ch == '"' && !mstr) {
                str = !str;
                mstr = false;
                continue;
            }

            if (ch == '[' && !brackets) {
                brackets = true;
                continue;
            }

            if (ch == ']' && brackets) {
                brackets = false;
                continue;
            }

            if (!str && (ch == ' ' || ch == '\n')) {
                if (ch == ' ' && lineJustBegun) {
                    continue;
                }

                if (buf.length() != 0) {
                    lineTokens.add(buf.toString().trim());
                    buf.setLength(0);
                }

                if (ch == '\n') {
                    if (!lineTokens.isEmpty()) {
                        tokens.add(lineTokens.toArray(new String[0]));
                        lineTokens.clear();
                    }

                    lineJustBegun = true;
                }

                continue;
            }

            if (ch == '\r')
                continue;

            buf.append((char) ch);
            lineJustBegun = false;
        }

        if (buf.length() != 0) {
            lineTokens.add(buf.toString());
        }

        if (!lineTokens.isEmpty()) {
            tokens.add(lineTokens.toArray(new String[0]));
        }

        return tokens.isEmpty() ? EmptyParsedPatches.INSTANCE : MinecraftParsedPatches.parse(this, tokens);
    }

}
