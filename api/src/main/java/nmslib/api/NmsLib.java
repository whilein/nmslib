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

package nmslib.api;

import nmslib.api.protocol.ProtocolManager;

/**
 * @author whilein
 */
public abstract class NmsLib {

    // костылиии :))))
    private static NmsLib INSTANCE;

    public static NmsLib getInstance() {
        return INSTANCE;
    }

    protected NmsLib() {
        if (!getClass().getName().equals("nmslib.agent.AgentNmsPatcher"))
            throw new IllegalStateException("Only AgentNmsPatcher may implement NmsLib");

        INSTANCE = this;
    }

    public abstract Version getVersion();

    public abstract boolean isNotSupported();
    public abstract int getPatchesCount();

    public abstract ProxyResolver getProxyResolver();
    public abstract ProtocolManager getProtocolManager();

}
