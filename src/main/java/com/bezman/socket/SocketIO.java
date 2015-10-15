package com.bezman.socket;

import com.bezman.Reference.Reference;
import com.bezman.Reference.util.Util;
import com.bezman.annotation.OnSocketConnect;
import com.bezman.annotation.OnSocketDisconnect;
import com.bezman.annotation.OnSocketEvent;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import org.apache.tomcat.jni.Global;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

@Component
public class SocketIO
{
    @Autowired
    ApplicationContext applicationContext;

    HashMap<OnSocketConnect, Method> connectionListeners = new HashMap<>();
    HashMap<OnSocketDisconnect, Method> disconnectionListeners = new HashMap();
    HashMap<OnSocketEvent, Method> eventListeners = new HashMap();

    private static Class[] nonSerializable = {String.class, Integer.class, Double.class, Float.class, Boolean.class, Byte.class, Short.class, Long.class};

    @Bean
    public SocketIOServer socketIOServer()
    {
        Configuration configuration = new Configuration();
//        configuration.setOrigin("http://local.bezcode.com:9090");
        configuration.setHostname("192.168.1.11");
        configuration.setPort(83);

        SocketIOServer socketIOServer = new SocketIOServer(configuration);

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("com.bezman.socket"))
                .setScanners(new MethodAnnotationsScanner()));

        reflections.getMethodsAnnotatedWith(OnSocketConnect.class).stream()
                .forEach(method -> connectionListeners.put(method.getAnnotation(OnSocketConnect.class), method));

        reflections.getMethodsAnnotatedWith(OnSocketDisconnect.class).stream()
                .forEach(method -> disconnectionListeners.put(method.getAnnotation(OnSocketDisconnect.class), method));

        reflections.getMethodsAnnotatedWith(OnSocketEvent.class).stream()
                .forEach(method -> eventListeners.put(method.getAnnotation(OnSocketEvent.class), method));

        System.out.println(eventListeners);

        socketIOServer.addConnectListener(socketIOClient -> {
            connectionListeners.values().forEach(method -> {
                if (method.getParameterCount() == 1)
                {
                    try
                    {
                        method.invoke(applicationContext.getBean(method.getDeclaringClass()), socketIOClient);
                    } catch (IllegalAccessException | InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        });

        socketIOServer.addEventListener("global", GlobalEvent.class, (socketIOClient, globalEvent, ackRequest) -> {
            System.out.println(globalEvent.getEvent());
            eventListeners.
                    forEach((onEvent, method) -> {
                        try
                        {
                            if (globalEvent.getEvent().equals(onEvent.value()))
                            {
                                Object instance = applicationContext.getBean(method.getDeclaringClass());
                                Class type = method.getParameterTypes()[1];

                                Object castedValue = null;

                                if (Arrays.asList(nonSerializable).stream().anyMatch(type::equals))
                                {
                                    castedValue = Util.toObject(type, globalEvent.getData());
                                } else
                                {
                                    castedValue  = Reference.objectMapper.readValue(globalEvent.getData(), method.getParameterTypes()[1]);
                                }

                                method.invoke(instance, socketIOClient, castedValue, ackRequest);
                            }
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                        }
                    });
        });

        return socketIOServer;
    }


}
