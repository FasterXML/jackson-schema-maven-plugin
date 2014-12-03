package com.fasterxml.jackson.jsonschemaplugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jsonschemaplugin.api.JsonSchemaObjectMapperFactory;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
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
     * Name a class that implements {@link com.fasterxml.jackson.jsonschemaplugin.api.JsonSchemaObjectMapperFactory}.
     * This class path must be in the compile classpath or the plugin class loader (the dependencies declared
     * in the pom <strong>for the plugin</strong>). The plugin will
     * instantiate an object of this class and call it to obtain an {@link com.fasterxml.jackson.databind.ObjectMapper},
     * thus seeing any customizations applied to that object mapper.
     * If you use a class that is in the plugin class loader and <strong>not</strong> in the compile classpath,
     * it will not apply customizations to classes unless the classes themselves are also in the plugin class loader.
     * Therefore, using the plugin class loader is probably not a good idea.
     */
    @Parameter
    String objectMapperFactoryClassName;

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

    @Parameter(defaultValue="${project}", readonly = true)
    MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        ClassLoader compileClassLoader; // get the compile classpath class loader.
        try {
            compileClassLoader = getClassLoader();
        } catch (Exception e) {
            throw new MojoFailureException("Failed to build class loader for compile classpath.", e);
        }

        ObjectMapper m = getObjectMapper(compileClassLoader);
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        try {
            for (Class<?> clazz : getClassesToProcess(compileClassLoader)) {
                m.acceptJsonFormatVisitor(m.constructType(clazz), visitor);
            }
        } catch (IOException e) {
            throw new MojoFailureException("Failed to construct compile classpath class loader.", e);
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoFailureException("Failed to resolve dependencies.", e);
        }
        JsonSchema jsonSchema = visitor.finalSchema();
        try {
            m.writerWithDefaultPrettyPrinter().writeValue(outputSchema, jsonSchema);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write result schema.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private ObjectMapper getObjectMapper(ClassLoader compileClassLoader) throws MojoExecutionException {
        if (objectMapperFactoryClassName != null) {
            /*
             * Note that the plugin class loader is the parent of the class loader passed in here.
             * So, if the factory is sitting in the plugin class loader, we'll load it from there.
             */
            Class<? extends JsonSchemaObjectMapperFactory> factoryClass = null;
            try {
                factoryClass = (Class<? extends JsonSchemaObjectMapperFactory>) compileClassLoader.loadClass(objectMapperFactoryClassName);
            } catch (ClassNotFoundException e) {
                getLog().debug("No class " + objectMapperFactoryClassName + " in compile class path.");
            }

            if (factoryClass == null) {
                throw new MojoExecutionException("Unable to load object mapper factory class " + objectMapperFactoryClassName);
            }
            try {
                return factoryClass.newInstance().newMapper();
            } catch (Exception e) {
                throw new MojoExecutionException("Error obtaining object mapper from " + objectMapperFactoryClassName, e);
            }
        } else {
            return new ObjectMapper();
        }
    }

    private List<Class<?>> getClassesToProcess(ClassLoader loader) throws IOException, DependencyResolutionRequiredException {
        List<Class<?>> results = Lists.newArrayList();
        ClassPath classPath = ClassPath.from(loader);
        for (ClassPath.ClassInfo info : classPath.getAllClasses()) {
            getLog().debug("Class: " + info.getName());
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
                    /*
                     * Note that the plugin class loader is a parent of
                     */
                    Class<?> clazz = info.load();
                    results.add(clazz);
                }
            }
        }
        return results;
    }

    /**
     * @return a class loader made up of all the compile-scope dependencies, with the plugin class loader as a parent.
     * @throws DependencyResolutionRequiredException
     * @throws MalformedURLException
     */
    protected ClassLoader getClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
        List<URL> urls = new ArrayList<URL>();
        for (String path : project.getCompileClasspathElements()) {
            urls.add(new File(path).toURI().toURL());
        }
        // be sure to use our classpath as a parent, so that we end up with only one copy of Jackson.
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
    }
}
