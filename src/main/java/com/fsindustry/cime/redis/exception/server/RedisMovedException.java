/**
 * Copyright 2018 Nikita Koksharov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fsindustry.cime.redis.exception.server;

import java.net.URI;

import com.fsindustry.cime.redis.exception.RedisException;

import lombok.Getter;

/**
 * Redis数据moved异常
 *
 * @author fuzhengxin
 */
@Getter
public class RedisMovedException extends RedisException {

    private final int slot;
    private final URI url;

    public RedisMovedException(int slot, String url) {
        this.slot = slot;
        this.url = create("//" + url);
    }

    private static URI create(String uri) {
        URI u = URI.create(uri);
        // Let's assuming most of the time it is OK.
        if (u.getHost() != null) {
            return u;
        }
        String s = uri.substring(0, uri.lastIndexOf(":")).replaceFirst("redis://", "").replaceFirst("rediss://", "");
        // Assuming this is an IPv6 format, other situations will be handled by
        // Netty at a later stage.
        return URI.create(uri.replace(s, "[" + s + "]"));
    }
}
