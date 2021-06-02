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

import nmslib.api.craftbukkit.entity.CraftPlayer;
import nmslib.api.nms.EntityPlayer;
import nmslib.api.nms.ItemStack;
import nmslib.api.nms.NBTTagCompound;
import nmslib.api.nms.PlayerInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author whilein
 */
public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        CraftPlayer player = (CraftPlayer) event.getPlayer();
        EntityPlayer nms = player.getHandle();
        PlayerInventory inventory = nms.getInventory();

        ItemStack hand = inventory.getItemInHand();
        if (hand == null) return;

        NBTTagCompound tag = hand.getTag();

        if (tag == null) {
            player.sendMessage("No tag");
        } else {
            player.sendMessage("Tag: " + tag.clone());
        }
    }

}
