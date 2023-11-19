package org.example;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/grader")
public class Application extends javax.ws.rs.core.Application {
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(SolutionController.class);
        return resources;
    }
}
