package com.ducksoup.dilivideolive.websocket.context;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserContext {

    public static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

}
