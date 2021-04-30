package ca.cutterslade.gradle.analyze

import ca.cutterslade.gradle.analyze.helper.GradleDependency
import ca.cutterslade.gradle.analyze.helper.GroovyClass

class AnalyzeDependenciesPluginConstantsSpec extends AnalyzeDependenciesPluginBaseSpec {
    def 'simple build without dependencies results in success'() {
        setup:
        rootProject()
                .withAllProjectsPlugin('ca.cutterslade.analyze')
                .withSubProject(subProject("foo")
                        .justWarn()
                        .withMainClass(new GroovyClass("FooClass")
                                .withClassAnnotation("FooAnnotation", "BarConstants.BAR_VALUE"))
                        .withMainClass(new GroovyClass("FooAnnotation", true))
                        .withDependency(new GradleDependency(configuration: "implementation", project: "bar")))
                .withSubProject(subProject("bar")
                        .withMainClass(new GroovyClass("BarConstants")
                                .addClassConstant("BAR_VALUE", "String", "\"dummy value\"")))
                .withSubProject(subProject("baz")
                        .withMainClass(new GroovyClass("BazClass")
                                .addClassConstant("BAZ_VALUE", "String", "BarConstants.BAR_VALUE", false))
                        .withDependency(new GradleDependency(configuration: "implementation", project: "bar"))
                )
                .create(projectDir.getRoot())

        when:
        def result = gradleProject().forwardOutput().build()

        then:
        assertBuildResult(result, expectedResult, usedUndeclaredArtifacts, unusedDeclaredArtifacts)

        where:
        expectedResult | usedUndeclaredArtifacts | unusedDeclaredArtifacts
        WARNING        | []                      | ["project:bar:unspecified@jar"]
    }
}
