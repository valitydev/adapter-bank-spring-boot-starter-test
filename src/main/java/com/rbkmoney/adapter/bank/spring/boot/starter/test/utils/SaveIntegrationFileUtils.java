package com.rbkmoney.adapter.bank.spring.boot.starter.test.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.adapter.bank.spring.boot.starter.test.GenerationDataMethod;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class SaveIntegrationFileUtils {

    private static final String SEPARATOR = "_";

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> void saveJson(T request, String methodName, int count, String postfix, String targetPath) {
        String requestString;
        try {
            requestString = objectMapper.writeValueAsString(request);
            SaveIntegrationFileUtils.saveFile(requestString.getBytes(), methodName + postfix, targetPath, count);
        } catch (JsonProcessingException e) {
            log.error("SaveIntegrationFileUtils error when saveJson e: ", e);
        }
    }

    public static <T> T readJson(Class<T> clazz, String methodName, int count, String postfix, String targetPath) {
        try {
            byte[] bytes = readFile(methodName + postfix, targetPath, count);
            return objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            log.error("SaveIntegrationFileUtils error when readJson e: ", e);
        }
        return null;
    }

    public static void saveFile(byte[] serialize, String postfixFileName, String targetPath, int count) {
        try {
            StackTraceElement stackTraceElement = findStackElement();
            if (stackTraceElement != null) {
                String filename = postfixFileName + SEPARATOR + count;
                String packagePath = stackTraceElement.getFileName().replace(".java", "")
                        + "/" + stackTraceElement.getMethodName();
                String pathFile = targetPath + packagePath;
                File dir = new File(targetPath);
                String absolutePath = dir.getAbsolutePath();
                File makeDir = new File(absolutePath + "/" + packagePath);
                if (!makeDir.exists()) {
                    makeDir.mkdirs();
                }
                Files.write(Paths.get(pathFile + "/" + filename), serialize);
            }
        } catch (Exception e) {
            log.error("Error when write file e: ", e);
        }
    }

    public static byte[] readFile(String postfixFileName, String targetPath, int count) {
        try {
            StackTraceElement stackTraceElement = findStackElement();
            if (stackTraceElement != null) {
                String filename = postfixFileName + SEPARATOR + count;
                String packagePath = stackTraceElement.getFileName().replace(".java", "")
                        + "/" + stackTraceElement.getMethodName();
                String pathFile = targetPath + packagePath;
                return Files.readAllBytes(Paths.get(pathFile + "/" + filename));
            }
        } catch (Exception e) {
            log.error("Error when write file e: ", e);
        }
        return new byte[0];
    }

    private static StackTraceElement findStackElement() throws ClassNotFoundException {
        StackTraceElement stackTraceElement = null;
        for (int i = 0; i < Thread.currentThread().getStackTrace().length; i++) {
            StackTraceElement currentStackTrace = Thread.currentThread().getStackTrace()[i];
            String methodName = currentStackTrace.getMethodName();
            Class<?> aClass = Class.forName(currentStackTrace.getClassName());
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName) && method.getAnnotation(GenerationDataMethod.class) != null) {
                    return currentStackTrace;
                }
            }
        }
        return stackTraceElement;
    }

}
