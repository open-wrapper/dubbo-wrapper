package com.wrapper.dubbo.statistics;

import org.apache.dubbo.rpc.RpcException;

public class ExceptionUtil {
	public static boolean isBizException(Throwable throwable) {
		return ((throwable instanceof RpcException) && ((RpcException) throwable).isBiz());
	}

}
