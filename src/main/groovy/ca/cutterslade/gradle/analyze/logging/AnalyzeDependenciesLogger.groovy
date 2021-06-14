package ca.cutterslade.gradle.analyze.logging

import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis
import org.gradle.api.logging.Logger

import java.nio.file.Path

@CompileStatic
abstract class AnalyzeDependenciesLogger {
    abstract void info(String title)

    abstract void info(String title, Collection<?> files)

    abstract void info(String title, Map<File, Set<String>> fileMap)

    static ProjectDependencyAnalysis create(
            final Logger gradleLogger,
            final Path buildDirPath,
            final boolean writeToFile,
            @ClosureParams(value = SimpleType, options = ['ca.cutterslade.gradle.analyze.logging.AnalyzeDependenciesLogger']) final Closure<ProjectDependencyAnalysis> closure
    ) {
        if (writeToFile) {
            try (AnalyzeDependenciesFileLogger logger = new AnalyzeDependenciesFileLogger(buildDirPath)) {
                return closure.call(logger)
            }
        } else {
            def logger = new AnalyzeDependenciesStandardLogger(gradleLogger)
            return closure.call(logger)
        }
    }
}