package com.alibaba.dubbo.common.serialize.support.json;

import java.io.InputStream;
import java.io.Reader;

/**
 * @author codel
 * @since 2020-01-16
 */
@Deprecated
public class FastJsonObjectInput extends org.apache.dubbo.common.serialize.fastjson.FastJsonObjectInput {

    public FastJsonObjectInput(InputStream in) {
        super(in);
    }

    public FastJsonObjectInput(Reader reader) {
        super(reader);
    }

}
