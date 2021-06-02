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
    public final Name nmsNBTTagEnd = nms.resolve("NBTTagEnd");
    public final Name nmsNBTTagByte = nms.resolve("NBTTagByte");
    public final Name nmsNBTTagShort = nms.resolve("NBTTagShort");
    public final Name nmsNBTTagInt = nms.resolve("NBTTagInt");
    public final Name nmsNBTTagLong = nms.resolve("NBTTagLong");
    public final Name nmsNBTTagFloat = nms.resolve("NBTTagFloat");
    public final Name nmsNBTTagDouble = nms.resolve("NBTTagDouble");
    public final Name nmsNBTTagIntArray = nms.resolve("NBTTagIntArray");
    public final Name nmsNBTTagByteArray = nms.resolve("NBTTagByteArray");
    public final Name nmsNBTTagString = nms.resolve("NBTTagString");
    public final Name nmsNBTTagList = nms.resolve("NBTTagLost");
    public final Name nmsNBTTagCompound = nms.resolve("NBTTagCompound");
    public final Name nmsNBTBaseNBTNumber = nms.resolve("NBTBase$NBTNumber");

    public final Name cbEntityCraftPlayer = cb.resolve("entity", "CraftPlayer");
    public final Name cbEntityCraftEntity = cb.resolve("entity", "CraftEntity");
    public final Name cbEntityCraftLivingEntity = cb.resolve("entity", "CraftLivingEntity");
    public final Name cbEntityCraftHumanEntity = cb.resolve("entity", "CraftHumanEntity");

    public final Name apiNmsItem = api.resolve("nms", "Item");
    public final Name apiNmsItemStack = api.resolve("nms", "ItemStack");
    public final Name apiNmsPlayerInventory = api.resolve("nms", "PlayerInventory");

    public final Name apiNmsNBTTagCompound = api.resolve("nms", "NBTTagCompound");
    public final Name apiNmsNBTTagEnd = api.resolve("nms", "NBTTagEnd");
    public final Name apiNmsNBTTagByte = api.resolve("nms", "NBTTagByte");
    public final Name apiNmsNBTTagShort = api.resolve("nms", "NBTTagShort");
    public final Name apiNmsNBTTagInt = api.resolve("nms", "NBTTagInt");
    public final Name apiNmsNBTTagLong = api.resolve("nms", "NBTTagLong");
    public final Name apiNmsNBTTagFloat = api.resolve("nms", "NBTTagFloat");
    public final Name apiNmsNBTTagDouble = api.resolve("nms", "NBTTagDouble");
    public final Name apiNmsNBTTagIntArray = api.resolve("nms", "NBTTagIntArray");
    public final Name apiNmsNBTTagByteArray = api.resolve("nms", "NBTTagByteArray");
    public final Name apiNmsNBTTagString = api.resolve("nms", "NBTTagString");
    public final Name apiNmsNBTTagList = api.resolve("nms", "NBTTagList");
    public final Name apiNmsNBTBaseNBTNumber = api.resolve("nms", "NBTBase$NBTNumber");

    public final Name apiNmsNBTBase = api.resolve("nms", "NBTBase");

    public final Name apiNmsEntityPlayer = api.resolve("nms", "EntityPlayer");
    public final Name apiNmsEntityHuman = api.resolve("nms", "EntityHuman");
    public final Name apiNmsEntityLiving = api.resolve("nms", "EntityLiving");
    public final Name apiNmsEntity = api.resolve("nms", "Entity");

    public final Name apiCbEntityCraftPlayer = api.resolve("craftbukkit", "entity", "CraftPlayer");
    public final Name apiCbEntityCraftHumanEntity = api.resolve("craftbukkit", "entity", "CraftHumanEntity");
    public final Name apiCbEntityCraftLivingEntity = api.resolve("craftbukkit", "entity", "CraftLivingEntity");
    public final Name apiCbEntityCraftEntity = api.resolve("craftbukkit", "entity", "CraftEntity");

}
