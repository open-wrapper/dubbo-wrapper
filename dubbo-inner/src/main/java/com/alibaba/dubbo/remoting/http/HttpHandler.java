package com.alibaba.dubbo.remoting.http;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author codel
 * @since 2020-01-19
 */
public interface HttpHandler extends org.apache.dubbo.remoting.http.HttpHandler {
    /**
     * invoke.
     *
     * @param request  request.
     * @param response response.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}
