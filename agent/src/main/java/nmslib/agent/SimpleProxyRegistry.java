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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import nmslib.api.ProxyRegistry;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleProxyRegistry implements ProxyRegistry {

    BiMap<String, String> proxies;

    public static ProxyRegistry create() {
        return new SimpleProxyRegistry(HashBiMap.create());
    }

    @Override
    public void addProxy(final String nmsClass, final String apiClass) {
        this.proxies.put(nmsClass, apiClass);
    }

    @Override
    public BiMap<String, String> getProxies() {
        return Maps.unmodifiableBiMap(HashBiMap.create(proxies));
    }

    @Override
    public String getApi(final String name) {
        return proxies.get(name);
    }

    @Override
    public String getNms(final String name) {
        return proxies.inverse().get(name);
    }

    @Override
    public String toString() {
        return proxies.toString();
    }
}
