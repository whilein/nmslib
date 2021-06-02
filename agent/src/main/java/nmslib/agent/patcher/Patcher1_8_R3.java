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

package nmslib.agent.patcher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nmslib.agent.patch.Patch;
import nmslib.agent.patch.Patches;
import nmslib.agent.version.MinecraftVersion;

import static nmslib.agent.name.ConstNames.*;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Patcher1_8_R3 implements Patcher {

    public static void apply(final Patches patches) {
        new Patcher1_8_R3().apply(patches.patch(MinecraftVersion.V1_8_R3));
    }

    @Override
    public void apply(final Patch output) {
        output.forClass(nmsItem)
                .implement(apiNmsItem);

        output.forClass(nmsItemStack)
                .implement(apiNmsItemStack)
                .proxyMethod("c", "load")
                .proxyMethod("j", "getMaxDurability")
                .fieldAccessor("count");

        output.forClass(nmsPlayerInventory)
                .implement(apiNmsPlayerInventory)
                .fieldAccessor("armor")
                .fieldAccessor("items")
                .fieldAccessor("itemInHandIndex")
                .fieldGetter("player");

        output.forClass(nmsNBTBase)
                .implement(apiNBTBase);

        output.forClass(nmsNBTTagCompound)
                .implement(apiNBTTagCompound);

        output.forClass(nmsEntityPlayer)
                .implement(apiNmsEntityPlayer);

        output.forClass(nmsEntityHuman)
                .implement(apiNmsEntityHuman)
                .fieldGetter("inventory");

        output.forClass(nmsEntityLiving)
                .implement(apiNmsEntityLiving);

        output.forClass(nmsEntity)
                .implement(apiNmsEntity)
                .fieldAccessor("lastX")
                .fieldAccessor("lastY")
                .fieldAccessor("lastZ")
                .fieldAccessor("locX")
                .fieldAccessor("locY")
                .fieldAccessor("locZ")
                .fieldAccessor("motX")
                .fieldAccessor("motY")
                .fieldAccessor("motZ")
                .fieldAccessor("yaw")
                .fieldAccessor("pitch")
                .fieldAccessor("lastYaw")
                .fieldAccessor("lastPitch")
                .fieldAccessor("vehicle")
                .fieldAccessor("passenger");

        output.forClass(cbEntityCraftPlayer)
                .implement(apiCbEntityCraftPlayer);

        output.forClass(cbEntityCraftEntity)
                .implement(apiCbEntityCraftEntity);

        output.forClass(cbEntityCraftHumanEntity)
                .implement(apiCbEntityCraftHumanEntity);

        output.forClass(cbEntityCraftLivingEntity)
                .implement(apiCbEntityCraftLivingEntity);
    }
}
