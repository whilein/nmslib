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

package nmslib;

import lombok.val;
import nmslib.api.nms.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/**
 * @author whilein
 */
public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println(NBTTagByte.create((byte) 10).asByte());
        System.out.println(NBTTagShort.create((short) 10).asShort());
        System.out.println(NBTTagInt.create(10).asInt());
        System.out.println(NBTTagLong.create(10L).asLong());
        System.out.println(NBTTagFloat.create(10f).asFloat());
        System.out.println(NBTTagDouble.create(10f).asDouble());
        System.out.println(Arrays.toString(NBTTagByteArray.create(new byte[]{1, 2, 3, 4, 5}).getData()));
        System.out.println(NBTTagString.create("Hello world!").getData());

        val compound = NBTTagCompound.create();
        compound.setByte("byte", (byte) 123);
        System.out.println(compound.keySet());
        System.out.println(compound);
    }

}
