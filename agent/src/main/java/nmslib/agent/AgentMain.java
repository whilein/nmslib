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

package nmslib.agent;

import lombok.val;
import nmslib.agent.patch.MinecraftPatches;
import nmslib.agent.patcher.Patcher1_8_R3;

import java.lang.instrument.Instrumentation;

import static nmslib.agent.name.ConstNames.*;

/**
 * @author whilein
 */
public final class AgentMain {

    public static void premain(final String agentArgs, final Instrumentation instrumentation) {
        val patches = MinecraftPatches.create();

        patches.getProxyRegistry()
                .addProxy(nmsItem, apiNmsItem)
                .addProxy(nmsItemStack, apiNmsItemStack)
                .addProxy(nmsPlayerInventory, apiNmsPlayerInventory)
                .addProxy(nmsNBTBase, apiNBTBase)
                .addProxy(nmsNBTTagCompound, apiNBTTagCompound)
                .addProxy(nmsEntity, apiNmsEntity)
                .addProxy(nmsEntityLiving, apiNmsEntityLiving)
                .addProxy(nmsEntityHuman, apiNmsEntityHuman)
                .addProxy(nmsEntityPlayer, apiNmsEntityPlayer)
                .addProxy(cbEntityCraftEntity, apiCbEntityCraftEntity)
                .addProxy(cbEntityCraftLivingEntity, apiCbEntityCraftLivingEntity)
                .addProxy(cbEntityCraftHumanEntity, apiCbEntityCraftHumanEntity)
                .addProxy(cbEntityCraftPlayer, apiCbEntityCraftPlayer);

        Patcher1_8_R3.apply(patches);

        instrumentation.addTransformer(AgentClassPatcher.create(patches));
    }

}
