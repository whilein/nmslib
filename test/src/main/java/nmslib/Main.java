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
import nmslib.api.cb.entity.CraftPlayer;
import nmslib.api.hook.AgentHook;
import nmslib.api.nms.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author whilein
 */
public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("test-factories").setExecutor(this);
        getCommand("test-patchers").setExecutor(this);
        getCommand("test-enums").setExecutor(this);
    }

    @Override
    public boolean onCommand(
            final CommandSender sender,
            final Command command,
            final String label,
            final String[] args
    ) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("Command available only from console");
            return false;
        }

        switch (label) {
            case "test-patchers": {
                val proxies = AgentHook.getInstance().getProxyResolver().getProxies();

                for (val entry : proxies.entrySet()) {
                    val key = entry.getKey();
                    val value = entry.getValue();

                    String api;
                    String nms;

                    if (key.startsWith("nmslib/api/")) {
                        api = key;
                        nms = value;
                    } else {
                        api = value;
                        nms = key;
                    }

                    api = api.replace('/', '.');
                    nms = nms.replace('/', '.');

                    try {
                        val apiClass = Class.forName(api);
                        val nmsClass = Class.forName(nms);

                        test(apiClass, nmsClass);
                    } catch (Throwable t) {
                        getLogger().info("Skip " + api + " (" + nms + "):");
                        getLogger().info("  " + t);
                    }
                }
                break;
            }
            case "test-factories": {
                getLogger().info("NBTTagByte: " + NBTTagByte.create((byte) 10));
                getLogger().info("NBTTagShort: " + NBTTagShort.create((short) 15));
                getLogger().info("NBTTagInt: " + NBTTagInt.create(20));
                getLogger().info("NBTTagLong: " + NBTTagLong.create(25L));
                getLogger().info("NBTTagFloat: " + NBTTagFloat.create(3.5f));
                getLogger().info("NBTTagDouble: " + NBTTagDouble.create(2.5));
                getLogger().info("NBTTagString: " + NBTTagString.create("String Value"));
                getLogger().info("ChatClickable: " + ChatClickable.create(ChatClickable.EnumClickAction.OPEN_URL,
                        "http://localhost"));
                break;
            }
            case "test-packets": {
                final Player player = Bukkit.getPlayerExact(args[0]);

                final nmslib.api.cb.entity.CraftPlayer craftPlayer
                        = (CraftPlayer) player;

                final nmslib.api.nms.EntityPlayer entityPlayer = craftPlayer.getHandle();

                final nmslib.api.nms.PlayerConnection playerConnection = entityPlayer.getPlayerConnection();

                playerConnection.sendPacket(
                        PacketPlayOutChat.create(
                                ChatComponentText.create("Hello world!"),
                                PacketPlayOutChat.Position.HOTBAR
                        )
                );

                playerConnection.sendPacket(
                        PacketPlayOutTitle.create(
                                PacketPlayOutTitle.EnumTitleAction.TITLE,
                                ChatComponentText.create("*Title*")
                        )
                );

                playerConnection.sendPacket(
                        PacketPlayOutTitle.create(
                                PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                                ChatComponentText.create("*Subtitle*")
                        )
                );

                break;
            }
            case "test-enums": {
                getLogger().info("EnumChatFormat: " + Arrays.toString(EnumChatFormat.VALUES));
                getLogger().info("EnumClickAction: " + Arrays.toString(ChatClickable.EnumClickAction.VALUES));
                getLogger().info("EnumHoverAction: " + Arrays.toString(ChatHoverable.EnumHoverAction.VALUES));
                getLogger().info("EnumTitleAction: " + Arrays.toString(PacketPlayOutTitle.EnumTitleAction.VALUES));
            }
        }

        return false;
    }

    private void test(
            final Class<?> api,
            final Class<?> nms
    ) {
        boolean implemented = nms.getSuperclass() == api;

        if (!implemented) {
            for (Class<?> implementation : nms.getInterfaces()) {
                if (implementation == api) {
                    implemented = true;
                    break;
                }
            }
        }

        if (!implemented) {
            getLogger().info(nms.getName() + " does not implements/extends " + api.getName());
            return;
        }

        for (val apiMethod : api.getDeclaredMethods()) {
            if (Modifier.isStatic(apiMethod.getModifiers()))
                continue;

            if (!Modifier.isAbstract(apiMethod.getModifiers())) {
                continue;
            }

            if (apiMethod.isBridge())
                continue;

            boolean fail = true;
            val similars = new ArrayList<Method>();

            for (val method : nms.getMethods()) {
                if (method.getDeclaringClass() == api)
                    continue;

                if (method.getName().equals(apiMethod.getName())) {
                    if (Arrays.equals(method.getParameterTypes(), apiMethod.getParameterTypes())
                            && method.getReturnType().equals(apiMethod.getReturnType())) {
                        fail = false;
                        continue;
                    }

                    similars.add(method);
                }
            }

            if (fail) {
                getLogger().info(nms.getName() +
                        " does not implements method " + getMethodDisplay(apiMethod));

                if (!similars.isEmpty()) {
                    getLogger().info("  But it has similar methods:");

                    for (val similar : similars) {
                        getLogger().info("   - " + getMethodDisplay(similar));
                    }
                }
            }
        }
    }

    private String getMethodDisplay(final Method method) {
        val params = Arrays.stream(method.getParameterTypes())
                .map(Class::getName)
                .collect(Collectors.joining(", "));

        val returnType = method.getReturnType().getName();
        val name = method.getName();

        return returnType + " " + name + "(" + params + ")";
    }
}
