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
public interface NBTTagList extends NBTBase {

    static NBTTagList create() {
        throw new UnsupportedOperationException();
    }

    int getListType();

    int size();
    boolean isEmpty();

    NBTBase getElement(int i);
    NBTBase removeElement(int i);

    void setElement(int i, NBTBase nbt);
    void addElement(NBTBase nbt);


}
