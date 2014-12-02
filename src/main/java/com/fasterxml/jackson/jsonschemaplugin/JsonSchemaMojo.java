package com.fasterxml.jackson.jsonschemaplugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.SelectorUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Json Schema Generator.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyCollection = ResolutionScope.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true)
public class JsonSchemaMojo extends AbstractMojo {
    /**
     * Location of the result schema.
     */
    @Parameter(defaultValue = "${project.build.directory}/schema.json", property = "schema")
    File outputSchema;

    /**
     * Patterns (ant-ish) of classes to generate.
     * This may not be empty; there is no default.
     */
    @Parameter(required = true)
    List<String> includes;

    /**
     * Patterns of classes to exclude.
     */
    @Parameter
    List<String> excludes = Lists.newArrayList(); // default to an empty list instead of null.

    @Component
    MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        ObjectMapper m = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        try {
            for (Class<?> clazz : getClassesToProcess()) {
                m.acceptJsonFormatVisitor(m.constructType(clazz), visitor);
            }
        } catch (IOException e) {
            throw new MojoFailureException("Failed to construct compile classpath class loader.", e);
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoFailureException("Failed to resolve dependencies.", e);
        }
        JsonSchema jsonSchema = visitor.finalSchema();
        try {
            m.writeValue(outputSchema, jsonSchema);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write result schema.", e);
        }
    }

    private List<Class<?>> getClassesToProcess() throws IOException, DependencyResolutionRequiredException {
        ClassLoader loader = getClassLoader(); // get the compile classpath class loader.
        List<Class<?>> results = Lists.newArrayList();
        ClassPath classPath = ClassPath.from(loader);
        for (ClassPath.ClassInfo info : classPath.getAllClasses()) {
            boolean included = false;
            for (String pattern : includes) {
                pattern = pattern.replace("/", "."); // map from typical / syntax to class name.
                if (SelectorUtils.matchPath(pattern, info.getName(), ".", true)) {
                    included = true;
                    break;
                }
            }
            if (included) {
                boolean excluded = false;
                for (String pattern : excludes) {
                    pattern = pattern.replace("/", "."); // map from typical / syntax to class name.
                    if (SelectorUtils.matchPath(pattern, info.getName(), ".", true)) {
                        excluded = true;
                        break;
                    }
                }
                if (!excluded) {
                    results.add(info.load());
                }
            }
        }
        return results;
    }

    protected ClassLoader getClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
        List<URL> urls = new ArrayList<URL>();
        for (String path : project.getCompileClasspathElements()) {
                urls.add(new File(path).toURI().toURL());
            }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]));
    }
}
