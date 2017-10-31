/**
 * MIT License
 * 
 * Copyright (c) 2017 iMinusMinus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ml.iamwhatiam.ag.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import ml.iamwhatiam.ag.service.CacheService;

/**
 * 内存缓存，不适用集群环境。
 * 
 * @author iMinusMinus
 * @since 2017-10-31
 * @deprecated 不支持有效期，可能导致内存溢出，慎用！
 */
@Service("memoryCache")
@Deprecated
public class MemoryCacheServiceImpl implements CacheService {

    private Map<String, Object> cache = new ConcurrentHashMap<String, Object>();

    public Object get(String key) {
        return cache.get(key);
    }

    public boolean put(String key, Object obj) {
        cache.put(key, obj);
        return true;
    }

    public boolean evict(String key) {
        cache.remove(key);
        return true;
    }

    public int clear() {
        int size = cache.size();
        cache.clear();
        return size;
    }

}
