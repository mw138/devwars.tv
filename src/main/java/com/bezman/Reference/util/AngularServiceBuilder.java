package com.bezman.Reference.util;

import com.bezman.annotation.AngularServiceOmitted;
import com.bezman.annotation.JSONParam;
import com.bezman.annotation.PathModel;
import com.bezman.annotation.PreAuthorization;
import com.bezman.controller.TournamentController;
import com.bezman.controller.UserTeamController;
import com.bezman.controller.game.GameController;
import com.bezman.controller.user.UserController;
import org.reflections.Reflections;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.beans.Transient;
import java.io.File;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class AngularServiceBuilder {
    public static void buildServicesFromPackage(String packageName, String outputDir, String rootURL, String moduleNamePrefix) {
        Reflections reflections = new Reflections(packageName);

        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);

        controllers.add(UserController.class);
        controllers.add(GameController.class);
        controllers.add(UserTeamController.class);
        controllers.add(TournamentController.class);

        for (Class controller : controllers) {
            RequestMapping controllerRequestMapping = (RequestMapping) controller.getAnnotation(RequestMapping.class);

            try {
                String serviceName = (controller.getSimpleName() + "Service").replace("Controller", "");

                File file = new File(outputDir + serviceName + ".js");
                PrintWriter printWriter = new PrintWriter(file);

                printWriter.println("angular.module('" + moduleNamePrefix + "." + serviceName + "', [])");
                printWriter.println("   .factory('" + serviceName + "', ['$http', function($http){\n var " + serviceName + " = {};\n" + serviceName + ".http = {};\n");

                List<Method> methods = Arrays.asList(controller.getMethods());

                methods = methods.stream().filter(method -> method.getAnnotation(AngularServiceOmitted.class) == null).collect(Collectors.toList());

                for (Method method : methods) {
                    try {
                        HashMap<String, RequestParam> queryParams = new HashMap<>();
                        HashMap<String, PathVariable> pathParams = new HashMap<>();
                        HashMap<String, PathModel> pathModels = new HashMap<>();
                        HashMap<String, JSONParam> jsonParams = new HashMap<>();

                        HashMap<String, Class> queryParamsTypes = new HashMap<>();
                        HashMap<String, Class> pathParamsTypes = new HashMap<>();

                        RequestMapping requestMapping = null;
                        PreAuthorization preAuthorization = null;
                        boolean isTransient = false;
                        for (Annotation annotation : method.getAnnotations()) {
                            if (annotation.annotationType().equals(RequestMapping.class)) {
                                requestMapping = (RequestMapping) annotation;
                            }

                            if (annotation.annotationType().equals(Transient.class)) {
                                isTransient = true;
                            }

                            if (annotation.annotationType().equals(PreAuthorization.class)) {
                                preAuthorization = (PreAuthorization) annotation;
                            }
                        }

                        if (requestMapping != null) {
                            String absoluteUrl = "";
                            if (controllerRequestMapping != null) {
                                absoluteUrl += controllerRequestMapping.value()[0];
                            }

                            for (String produce : requestMapping.produces()) {
                                System.out.println(method.getName() + " : " + produce);
                            }

                            absoluteUrl += requestMapping.value()[0];

                            for (Parameter parameter : method.getParameters()) {
                                for (Annotation annotation : parameter.getAnnotations()) {
                                    if (annotation.annotationType().equals(RequestParam.class)) {
                                        RequestParam requestParam = (RequestParam) annotation;
                                        queryParams.put(requestParam.equals("") ? parameter.getName() : requestParam.value(), requestParam);
                                        queryParamsTypes.put(requestParam.equals("") ? parameter.getName() : requestParam.value(), parameter.getType());
                                    } else if (annotation.annotationType().equals(PathVariable.class)) {
                                        PathVariable pathVariable = (PathVariable) annotation;
                                        pathParams.put(pathVariable.value().equals("") ? parameter.getName() : pathVariable.value(), pathVariable);
                                        pathParamsTypes.put(pathVariable.value().equals("") ? parameter.getName() : pathVariable.value(), parameter.getType());
                                    } else if (annotation.annotationType().equals(JSONParam.class)) {
                                        JSONParam param = (JSONParam) annotation;
                                        jsonParams.put(param.value(), param);
                                        queryParamsTypes.put(param.value(), parameter.getType());
                                    } else if (annotation.annotationType().equals(PathModel.class)) {
                                        PathModel pathModel = (PathModel) annotation;
                                        pathModels.put(pathModel.value(), pathModel);
                                        pathParamsTypes.put(pathModel.value(), parameter.getType());
                                    }
                                }
                            }

                            printWriter.println("/*");

                            if (preAuthorization != null) {
                                printWriter.println("Required Role : " + preAuthorization.minRole());
                            }

                            pathParams.keySet()
                                .stream()
                                .forEach(p -> printWriter.println("Path Variable {" + p + "}" + " : " + pathParamsTypes.get(p).getName()));

                            queryParams.keySet()
                                .stream()
                                .forEach(p -> printWriter.println("Query Parameter {" + p + "}" + " : " + queryParamsTypes.get(p).getName()));
                            printWriter.println("*/");

                            String urlMethod = requestMapping.method().length > 0 ? requestMapping.method()[0].name() : "GET";

                            String methodParams = "";
                            ArrayList methodParamsList = new ArrayList();

                            pathParams.keySet()
                                .stream()
                                .forEach(methodParamsList::add);

                            pathModels.keySet().stream()
                                .forEach(methodParamsList::add);

                            queryParams.keySet()
                                .stream()
                                .filter(p -> queryParams.get(p).required())
                                .forEach(methodParamsList::add);

                            queryParams.keySet()
                                .stream()
                                .filter(p -> !queryParams.get(p).required())
                                .forEach(methodParamsList::add);

                            jsonParams.keySet()
                                .stream()
                                .forEach(methodParamsList::add);

                            methodParamsList.add("successCallback");
                            methodParamsList.add("errorCallback");

                            methodParams = ((ArrayList<String>) methodParamsList)
                                .stream()
                                .collect(Collectors.joining(", "));

                            String queryParamsString = queryParamsTypes.keySet().stream()
                                .map(a -> a + " : " + a)
                                .collect(Collectors.joining(","));

                            printWriter.print(serviceName + "." + method.getName() + " = function(" + methodParams + "){");
                            printWriter.print("\n$http({\n" +
                                "method: '" + urlMethod + "',\n" +
                                "url: '" + absoluteUrl.replace("{", "' + ").replace("}", " + '") + "',\n" +
                                (urlMethod.equalsIgnoreCase("get") ? "params" : "data") + ": {" +
                                queryParamsString +
                                "}" +
                                "\n})" +
                                "\n.then(function(success){\n" +
                                "successCallback(success)\n" +
                                "},\n" +
                                "function(error){\n" +
                                "errorCallback(error)\n" +
                                "});\n");
                            printWriter.print("\n};\n\n");

                            printWriter.print(serviceName + ".http." + method.getName() + " = function(" + methodParams + "){");
                            printWriter.print("\nreturn $http({\n" +
                                "method: '" + urlMethod + "',\n" +
                                "url: '" + absoluteUrl.replace("{", "' + ").replace("}", " + '") + "',\n" +
                                (urlMethod.equalsIgnoreCase("get") ? "params" : "data") + ": {" +
                                queryParamsString +
                                "}" +
                                "\n})" +
                                "};");

                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Couldn't do " + method.getName());
                        e.printStackTrace();
                    }
                }

                printWriter.print("return " + serviceName + ";");

                printWriter.println("\n}]);");
                System.out.println(file.getAbsolutePath());
                printWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
