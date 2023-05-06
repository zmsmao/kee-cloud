package com.kee.common.reptiles.dev;


import lombok.Data;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.Event;
import org.openqa.selenium.devtools.idealized.Domains;
import org.openqa.selenium.devtools.idealized.target.model.SessionID;
import org.openqa.selenium.devtools.v109.network.Network;
import org.openqa.selenium.devtools.v109.network.model.Cookie;
import org.openqa.selenium.devtools.v109.network.model.Headers;
import org.openqa.selenium.devtools.v109.network.model.Request;
import org.openqa.selenium.devtools.v109.network.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @Description : Object
 * @author: zms
 */
@Data
public abstract class AbstractDevToolsBase implements DevToolsBase{

    protected DevTools devTools;

    private  List<Request> requestList;

    private List<Response> responseList;

    protected AbstractDevToolsBase(){
        requestList = new ArrayList<>();
        responseList = new ArrayList<>();
    }

    /**
     * 必须初始化initDevtools
     * @param driver
     */
    protected abstract void initDevtools(WebDriver driver);

    @Override
    public  List<Cookie> getAllCookies(){
        return devTools.send(Network.getAllCookies());
    }

    @Override
    public  List<Cookie> getUrlCookies(String url){
        List<String> urls = new ArrayList<>();
        urls.add(url);
        return devTools.send(Network.getCookies(Optional.of(urls)));
    }

    @Override
    public void listenAllResponse(){
        devTools.addListener(Network.responseReceived(),handle->{
            responseList.add(handle.getResponse());
        });
    }

    @Override
    public void listenAllRequest(){
        devTools.addListener(Network.requestWillBeSent(),handle->{
            requestList.add(handle.getRequest());
        });
    }

    @Override
    public  Response getOneUrlResponse(String url){
        for (Response response : responseList) {
            if(response.getUrl().contains(url)){
                return response;
            }
        }
        return null;
    }

    @Override
    public Request getOneUrlRequest(String url){
        for (Request request : requestList) {
            if (request.getUrl().contains(url)){
                return request;
            }
        }
        return null;
    }

    @Override
    public  Headers getOneUrlHeaders(Request request){
        return request.getHeaders();
    }

    @Override
    public  Headers getOneUrlHeaders(Response response){
        return response.getHeaders();
    }

    @Override
    public void disable(){
        devTools.send(Network.disable());
    }

    @Override
    public void enable(){
        devTools.send(Network.enable(Optional.empty(),Optional.empty(),Optional.empty()));
    }


    @Override
    public Domains getDomains() {
        return devTools.getDomains();
    }

    @Override
    public void close() {
        devTools.close();
    }

    @Override
    public void disconnectSession() {
        devTools.disconnectSession();
    }

    @Override
    public <X> X send(Command<X> command) {
        return devTools.send(command);
    }

    @Override
    public <X> void addListener(Event<X> event, Consumer<X> handler) {
        devTools.addListener(event,handler);
    }

    @Override
    public void clearListeners() {
        devTools.clearListeners();
    }

    @Override
    public void createSessionIfThereIsNotOne() {
        devTools.createSessionIfThereIsNotOne();
    }

    @Override
    public void createSession() {
        devTools.createSession();
    }

    @Override
    public SessionID getCdpSession() {
        return devTools.getCdpSession();
    }
}
