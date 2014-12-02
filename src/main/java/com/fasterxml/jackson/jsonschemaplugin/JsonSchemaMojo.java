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

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.sonatype.aether.RepositorySystemSession;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    List<String> excludes;

    @Component
    MavenProject project;

    @Component
    RepositorySystemSession repoSession;

    @Component
    ProjectDependenciesResolver projectDependenciesResolver;

    public void execute() throws MojoExecutionException {

    }

    Set<Artifact> getDependencyArtifacts() throws MojoExecutionException {

        DefaultDependencyResolutionRequest dependencyResolutionRequest = new DefaultDependencyResolutionRequest(project, repoSession);
        DependencyResolutionResult dependencyResolutionResult;

        try {
            dependencyResolutionResult = projectDependenciesResolver.resolve(dependencyResolutionRequest);
        } catch (DependencyResolutionException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }

        Set<Artifact> artifacts = new LinkedHashSet<Artifact>();
        if (dependencyResolutionResult.getDependencyGraph() != null
                && !dependencyResolutionResult.getDependencyGraph().getChildren().isEmpty()) {
            RepositoryUtils.toArtifacts(artifacts, dependencyResolutionResult.getDependencyGraph().getChildren(),
                    Collections.singletonList(project.getArtifact().getId()), null);
        }
        return artifacts;
    }
}
