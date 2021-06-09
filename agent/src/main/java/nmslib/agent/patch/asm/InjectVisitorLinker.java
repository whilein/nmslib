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

package nmslib.agent.patch.asm;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import nmslib.agent.output.Output;
import nmslib.agent.patch.PatchClass;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InjectVisitorLinker implements VisitorLinker {

    public static VisitorLinker create() {
        return new InjectVisitorLinker();
    }

    @Override
    public ClassVisitor link(final ClassVisitor visitor, final Output output, final PatchClass patch) {
        val nettyPacketHandler = "nmslib/agent/netty/NettyPacketHandler";

        val addBeforeOwner = "io/netty/channel/ChannelPipeline";
        val addBeforeDescriptor = "(Ljava/lang/String;Ljava/lang/String;Lio/netty/channel/ChannelHandler;)"
                + "Lio/netty/channel/ChannelPipeline;";

        val networkManager = "net/minecraft/server/" + patch.getPatch().getVersion() + "/NetworkManager";
        val apiNetworkManager = patch.getPatch().getProxyRegistry().getApi(networkManager);

        if (apiNetworkManager == null) {
            return visitor; // networkmanager isn't patched
        }

        return new ClassVisitor(Opcodes.ASM9, visitor) {

            boolean isInitializer;

            @Override
            public void visit(
                    final int version,
                    final int access,
                    final String name,
                    final String signature,
                    final String superName,
                    final String[] interfaces
            ) {
                isInitializer = superName.equals("io/netty/channel/ChannelInitializer");

                cv.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public MethodVisitor visitMethod(
                    final int access,
                    final String name,
                    final String descriptor,
                    final String signature,
                    final String[] exceptions
            ) {
                val method = cv.visitMethod(access, name, descriptor, signature, exceptions);

                if (isInitializer && name.equals("initChannel")) {
                    output.log("[nms/Inject] [" + name + "] Inject to pipeline");

                    return new MethodVisitor(Opcodes.ASM9, method) {

                        boolean packetHandler;
                        boolean injected;

                        int networkManagerId;

                        @Override
                        public void visitLdcInsn(final Object value) {
                            packetHandler = !injected && value.equals("packet_handler");

                            super.visitLdcInsn(value);
                        }

                        @Override
                        public void visitVarInsn(final int opcode, final int var) {
                            if (opcode == Opcodes.ASTORE) {
                                networkManagerId = var;
                            }

                            super.visitVarInsn(opcode, var);
                        }

                        @Override
                        public void visitMethodInsn(
                                final int opcode,
                                final String owner,
                                final String name,
                                final String descriptor,
                                final boolean isInterface
                        ) {
                            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

                            if (!injected && packetHandler
                                    && opcode == Opcodes.INVOKEINTERFACE
                                    && name.equals("addLast")) {

                                packetHandler = false;
                                injected = true;

                                mv.visitLdcInsn("packet_handler");
                                mv.visitLdcInsn("nmslib_inject");

                                mv.visitVarInsn(Opcodes.ALOAD, networkManagerId);

                                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                        nettyPacketHandler, "create",
                                        "(L" + apiNetworkManager + ";)Lio/netty/channel/ChannelHandler;",
                                        false
                                );

                                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                                        addBeforeOwner, "addBefore",
                                        addBeforeDescriptor,
                                        true
                                );
                            }
                        }

                    };
                }

                return method;
            }
        };
    }

}
