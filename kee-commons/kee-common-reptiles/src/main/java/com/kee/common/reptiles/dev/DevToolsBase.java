package com.kee.common.reptiles.dev;

import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.Event;
import org.openqa.selenium.devtools.idealized.Domains;
import org.openqa.selenium.devtools.idealized.target.model.SessionID;
import org.openqa.selenium.devtools.v109.network.model.Cookie;
import org.openqa.selenium.devtools.v109.network.model.Headers;
import org.openqa.selenium.devtools.v109.network.model.Request;
import org.openqa.selenium.devtools.v109.network.model.Response;

import java.util.List;
import java.util.function.Consumer;

/**
 * @Description : Object
 * @author: zeng.maosen
 */
public interface DevToolsBase {
    /**
     * 获取所有cookies
     *
     * @return
     */
    List<Cookie> getAllCookies();

    /**
     * 获取固定的url的cookies
     * @param url
     * @return
     */
    List<Cookie> getUrlCookies(String url);

    /**
     * 获取所有响应
     *
     */
    void listenAllResponse();

    /**
     * 获取所有请求
     *
     * @return
     */
    void listenAllRequest();

    /**
     * 获取固定的url的响应
     *
     * @param url
     * @return
     */
    Response getOneUrlResponse(String url);

    /**
     * 获取固定的url的请求
     *
     * @param url
     * @return
     */
    Request getOneUrlRequest(String url);

    /**
     * 获取固定的请求头
     *
     * @param request
     * @return
     */
    Headers getOneUrlHeaders(Request request);

    /**
     * 获取固定的响应头
     *
     * @param response
     * @return
     */
    Headers getOneUrlHeaders(Response response);

    void close();

    void disconnectSession();

    <X> X send(Command<X> command);

    <X> void addListener(Event<X> event, Consumer<X> handler);

    void clearListeners();

    void createSessionIfThereIsNotOne();

    void createSession();

    SessionID getCdpSession();

     Domains getDomains();

    /**
     * 关闭监听
     */
    void disable();

    /**
     * 开启监听
     */
    void enable();
}
