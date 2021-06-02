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

import lombok.experimental.UtilityClass;

/**
 * @author whilein
 */
@UtilityClass
public class ConstNames {
    public final Name nms = ClassName.of("net", "minecraft", "server", "#0");
    public final Name api = ClassName.of("nmslib", "api");
    public final Name cb = ClassName.of("org", "bukkit", "craftbukkit", "#0");

    public final Name nmsEntityPlayer = nms.resolve("EntityPlayer");
    public final Name nmsEntity = nms.resolve("Entity");
    public final Name nmsEntityLiving = nms.resolve("EntityLiving");
    public final Name nmsEntityHuman = nms.resolve("EntityHuman");

    public final Name nmsItem = nms.resolve("Item");
    public final Name nmsItemStack = nms.resolve("ItemStack");
    public final Name nmsPlayerInventory = nms.resolve("PlayerInventory");

    public final Name nmsNBTBase = nms.resolve("NBTBase");
    public final Name nmsNBTTagCompound = nms.resolve("NBTTagCompound");

    public final Name cbEntityCraftPlayer = cb.resolve("entity", "CraftPlayer");
    public final Name cbEntityCraftEntity = cb.resolve("entity", "CraftEntity");
    public final Name cbEntityCraftLivingEntity = cb.resolve("entity", "CraftLivingEntity");
    public final Name cbEntityCraftHumanEntity = cb.resolve("entity", "CraftHumanEntity");

    public final Name apiNmsItem = api.resolve("nms", "Item");
    public final Name apiNmsItemStack = api.resolve("nms", "ItemStack");
    public final Name apiNmsPlayerInventory = api.resolve("nms", "PlayerInventory");

    public final Name apiNBTTagCompound = api.resolve("nms", "NBTTagCompound");
    public final Name apiNBTBase = api.resolve("nms", "NBTBase");

    public final Name apiNmsEntityPlayer = api.resolve("nms", "EntityPlayer");
    public final Name apiNmsEntityHuman = api.resolve("nms", "EntityHuman");
    public final Name apiNmsEntityLiving = api.resolve("nms", "EntityLiving");
    public final Name apiNmsEntity = api.resolve("nms", "Entity");

    public final Name apiCbEntityCraftPlayer = api.resolve("craftbukkit", "entity", "CraftPlayer");
    public final Name apiCbEntityCraftHumanEntity = api.resolve("craftbukkit", "entity", "CraftHumanEntity");
    public final Name apiCbEntityCraftLivingEntity = api.resolve("craftbukkit", "entity", "CraftLivingEntity");
    public final Name apiCbEntityCraftEntity = api.resolve("craftbukkit", "entity", "CraftEntity");

}
