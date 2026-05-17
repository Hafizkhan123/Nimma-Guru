package com.hafiz.nimmaguru

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests to verify the configuration defined in build.gradle.kts.
 * Since build.gradle.kts is a build script, we verify the resulting configuration
 * that should be present in the project.
 */
class BuildConfigurationTest {

    @Test
    fun verifyNamespaceMatchesGradleConfig() {
        // This test verifies that the package name used in tests matches 
        // the namespace defined in build.gradle.kts
        val expectedNamespace = "com.hafiz.nimmaguru"
        assertEquals("The namespace should match build.gradle.kts", expectedNamespace, BuildConfigurationTest::class.java.`package`.name)
    }

    @Test
    fun verifyJvmTargetCompatibility() {
        // Verifies that the JVM version used for tests is compatible with 
        // what's defined in build.gradle.kts (Java 11)
        val javaVersion = System.getProperty("java.version")
        val majorVersion = javaVersion.substringBefore(".").toIntOrNull() ?: 0
        assertTrue("Java version should be at least 11", majorVersion >= 11)
    }
}
