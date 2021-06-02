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
public interface NBTTagCompound extends NBTBase {

    static NBTTagCompound create() {
        throw new UnsupportedOperationException();
    }

    NBTBase get(String name);

    void set(String name, NBTBase nbt);
    void setByte(String name, byte value);
    void setShort(String name, short value);
    void setInt(String name, int value);
    void setLong(String name, long value);
    void setFloat(String name, float value);
    void setDouble(String name, double value);
    void setByteArray(String name, byte[] value);
    void setString(String name, String value);
    void setIntArray(String name, int[] value);
    void setBoolean(String name, boolean value);

}
