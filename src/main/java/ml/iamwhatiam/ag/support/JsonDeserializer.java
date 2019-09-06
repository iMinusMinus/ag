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
package ml.iamwhatiam.ag.support;

import java.lang.reflect.Type;
import java.util.Collection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.util.TypeUtils;
import ml.iamwhatiam.ag.util.ReflectionUtils;

/**
 * 使用JSON进行反序列化
 * 
 * @author iMinusMinus
 * @since 2017-09-18
 */
public class JsonDeserializer implements Deserializer {

    JsonDeserializer() {
        //for package use only
    }

    /*
     * (non-Javadoc)
     * @see ml.iamwhatiam.ag.support.Deserializer#deserialize(java.lang.String,
     * java.lang.Class)
     */
    @Override
    public <T> T deserializeObject(String data, Type type) {
        return JSON.parseObject(data, type);//FIXME
    }

    @Override
    public Object[] deserializeArray(String data, Type[] types) {
        Object[] objects = new Object[types.length];
        JSONArray array = JSON.parseArray(data);
        for (int i = 0; i < types.length; i++) {
            //TODO
            if(types[i] instanceof Class && (((Class) types[i]).isArray()) || Collection.class.isAssignableFrom((Class) types[i])) {
                objects[i] = array.getJSONArray(i);
            } else if(types[i] instanceof Class && !ReflectionUtils.isJavaBean((Class) types[i])) {
                objects[i] = TypeUtils.cast(array.getString(i), types[i], null);
            } else {
                objects[i] = deserializeObject(array.getString(i), types[i]);
            }
        }
        return objects;
    }

}
