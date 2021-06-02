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

package nmslib.agent.name;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.util.Arrays;
import java.util.Map;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassName implements Name {

    final String[] path;
    String toStringCache;
    String toInternalStringCache;
    boolean formatCache;

    public static Name parseInternal(final @NonNull String path) {
        return new ClassName(path.split("/"));
    }

    public static Name parse(final @NonNull String path) {
        return new ClassName(path.split("\\."));
    }

    public static Name empty() {
        return new ClassName(new String[0]);
    }

    public static Name of(final @NonNull String... path) {
        return new ClassName(path);
    }

    @Override
    public Name format(final Object... values) {
        if (values.length == 0 || formatCache) {
            return this;
        }

        String[] pathCopy = null;

        for (int i = 0; i < path.length; i++) {
            val element = path[i];
            if (element.length() < 2) continue;
            if (element.charAt(0) != '#') continue;

            val formatIndex = Integer.parseInt(element.substring(1));
            if (formatIndex >= values.length) continue;

            if (pathCopy == null)
                pathCopy = toArray();

            pathCopy[i] = values[formatIndex].toString();
        }

        if (pathCopy == null) {
            formatCache = true;
        }

        return pathCopy == null
                ? this : new ClassName(pathCopy);
    }

    @Override
    public boolean startsWith(final Name name) {
        return startsWith(name, true);
    }

    @Override
    public boolean startsWith(final Name name, final boolean strict) {
        val size = name.size();
        if (size > path.length) return false;

        for (int i = 0; i < size; i++) {
            val elementThis = valueAt(i);
            val elementAnother = name.valueAt(i);

            if (elementThis.isEmpty()) {
                if (!elementAnother.isEmpty())
                    return false;

                continue;
            }

            if (elementThis.charAt(0) == '#' || elementAnother.charAt(0) == '#')
                continue;

            if (!elementThis.equals(elementAnother))
                return false;
        }

        return true;
    }

    @Override
    public float matches(final Name name) {
        val size = name.size();
        if (size != path.length) return 0F;

        int strictMatches = 0;

        for (int i = 0; i < size; i++) {
            val elementAnother = name.valueAt(i);
            val elementThis = valueAt(i);

            if (elementThis.isEmpty()) {
                if (!elementAnother.isEmpty())
                    return 0F;

                continue;
            }

            if (elementThis.charAt(0) == '#' || elementAnother.charAt(0) == '#')
                continue;

            if (elementThis.equals(elementAnother))
                strictMatches++;
            else
                return 0F;
        }

        return strictMatches / (float) size;
    }

    @Override
    public <V> V almostMatches(final Map<Name, V> in) {
        V almostMatch = null;
        float almostMatchPercentage = 0;

        for (val entry : in.entrySet()) {
            val key = entry.getKey();
            val value = entry.getValue();

            val matchPercentage = key.matches(this);
            if (matchPercentage == 0) continue;

            if (almostMatchPercentage < matchPercentage) {
                almostMatch = value;
                almostMatchPercentage = matchPercentage;
            }
        }

        return almostMatch;
    }

    @Override
    public int size() {
        return path.length;
    }

    @Override
    public Name resolve(final String value) {
        val newPath = Arrays.copyOf(path, path.length + 1);
        newPath[path.length] = value;

        return new ClassName(newPath);
    }

    @Override
    public Name resolve(final String... value) {
        val newPath = new String[path.length + value.length];
        System.arraycopy(path, 0, newPath, 0, path.length);
        System.arraycopy(value, 0, newPath, path.length, value.length);

        return new ClassName(newPath);
    }

    @Override
    public String[] toArray() {
        return Arrays.copyOf(path, path.length);
    }

    @Override
    public String valueAt(final int i) {
        return path[i];
    }

    @Override
    public String convertToInternalString() {
        if (toInternalStringCache != null)
            return toInternalStringCache;

        return toInternalStringCache = String.join("/", path);
    }

    @Override
    public String convertToString() {
        if (toStringCache != null)
            return toStringCache;

        return toStringCache = String.join(".", path);
    }

    @Override
    public String toString() {
        return convertToString();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(path);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ClassName)) return false;

        val that = (ClassName) obj;
        return Arrays.equals(path, that.path);
    }
}
