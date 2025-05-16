/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.utils;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import static java.lang.String.format;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.http.client.utils.URLEncodedUtils;
import org.cmdbuild.utils.lang.CmConvertUtils;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;

public class CliCommandParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public Map<String, CliAction> parseActions(Object service) {
        Map<String, CliAction> map = map();
        ReflectionUtils.doWithMethods(service.getClass(), (method) -> {
            if (method.isAnnotationPresent(CliCommand.class)) {
                CliCommand annotation = method.getAnnotation(CliCommand.class);

                List<String> names = new ArrayList<>();
                names.addAll(asList(annotation.alias()));
                if (!isBlank(annotation.value())) {
                    names.add(annotation.value());
                }
                names.add(method.getName());

                names = names.stream().map(String::toLowerCase).distinct().collect(toList());

                String helpAliases = Joiner.on("|").join(names);
                String helpParameters = describeParameters(method);

                CliAction action = new CliActionImpl(service, method, helpAliases, helpParameters, false);

                names.stream().map((n) -> format("%s_%s", n, method.getParameterCount())).forEach((name) -> map.put(name, action));

                if (method.getParameterCount() == 1 && asList(method.getParameterAnnotations()[0]).stream().anyMatch(Nullable.class::isInstance)) {
                    CliAction noParamAction = new CliActionImpl(service, method, helpAliases, "", true);
                    names.stream().map((n) -> format("%s_0", n)).forEach((name) -> map.put(name, noParamAction));
                }
            }
        });
        return map;
    }

    public static void printActionHelp(Map<String, CliAction> actions) {
        actions.values().stream().distinct().sorted(Ordering.natural().onResultOf(CliAction::getHelpAliases)).forEach((action -> {
            System.out.printf("\t%-32s\t%s\n", action.getHelpAliases(), action.getHelpParameters());
        }));

    }

    private String describeParameters(Method method) {
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        List<String> descs;
        if (parameterNames != null) {
            descs = asList(parameterNames);
        } else {
            descs = asList(method.getParameterTypes()).stream().map((param) -> param.getSimpleName().toLowerCase()).collect(toList());
        }
        return Joiner.on(" ").join(descs);
    }

    private class CliActionImpl implements CliAction {

        private final Object service;
        private final Method method;
        private final String helpAliases, helpParameters;
        private final boolean invokeWithNullArgs;

        public CliActionImpl(Object service, Method method, String helpAliases, String helpParameters, boolean invokeWithNullArgs) {
            this.service = checkNotNull(service);
            this.method = checkNotNull(method);
            this.helpAliases = checkNotNull(helpAliases);
            this.helpParameters = checkNotNull(helpParameters);
            this.invokeWithNullArgs = invokeWithNullArgs;
        }

        @Override
        public void execute(List<String> params) {
            try {
                logger.debug("execute method {} with params {}", method, params);
                method.setAccessible(true);
                Object[] args = getMethodArgs(params);
                logger.trace("execute method {} with args {}", method, args);
                method.invoke(service, args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public String getHelpAliases() {
            return helpAliases;
        }

        @Override
        public String getHelpParameters() {
            return helpParameters;
        }

        private Object[] getMethodArgs(List<String> params) {
            if (invokeWithNullArgs) {
                return new Object[method.getParameterCount()];
            } else {
                checkArgument(params.size() == method.getParameterCount(), "prameter count mismatch; this method requires %s parameters, got %s", method.getParameterCount(), params.size());
                Iterator<Class<?>> iterator = asList(method.getParameterTypes()).iterator();
                List processedParams = (List) params.stream().map((value) -> {
                    Class paramClass = iterator.next();
                    Object res;
                    if (paramClass.equals(Map.class)) {
                        return parseDataParam(value);
                    } else {
                        return CmConvertUtils.convert(value, paramClass);
                    }
                }).collect(toList());
                return processedParams.toArray();
            }
        }

        @Override
        public String getName() {
            return method.getName().toLowerCase();
        }

    }

    private Map<String, String> parseDataParam(String data) {
        CmMapUtils.FluentMap<String, String> map = map();
        URLEncodedUtils.parse(data, Charset.defaultCharset()).stream().forEach((entry) -> {
            checkArgument(!map.containsKey(entry.getName()), "duplicate param key = %s", entry.getName());
            map.put(entry.getName(), entry.getValue());
            logger.trace("processing data param {} = {}", entry.getName(), entry.getValue());
        });
        return map.immutable();
    }
}
