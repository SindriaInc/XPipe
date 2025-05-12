/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.api;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.Path;
import org.cmdbuild.utils.ws3.api.Ws3ResourceRepository;
import org.cmdbuild.utils.ws3.inner.Ws3ResourceBeanWithInterface;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Ws3Loader implements Ws3ResourceRepository {//TODO improve this; load beans using spring 5 index

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ApplicationContext applicationContext;

    public Ws3Loader(ApplicationContext applicationContext) {
        this.applicationContext = checkNotNull(applicationContext);
    }

//    @PostStartup
//    public void startWs3() {
//        logger.info("start ws3");
//        Ws3RequestHandler handler = new Ws3RequestHandlerImpl(beans);
//        Ws3RestRequestHandlerServlet.setHandler(handler);
//        logger.info("ws3 ready");
//    }
    @Override
    public Iterable<Ws3ResourceBeanWithInterface> getResources() {
        Reflections reflections = new Reflections("org.cmdbuild.service.rest.v3.endpoint", new TypeAnnotationsScanner(), new SubTypesScanner());
        List<Ws3ResourceBeanWithInterface> resources = reflections.getTypesAnnotatedWith(Path.class).stream().map(t -> new Ws3ResourceBeanWithInterface(t, applicationContext.getAutowireCapableBeanFactory().createBean(t))).collect(toList());
        return resources;
    }
}
