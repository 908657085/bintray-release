package com.novoda.gradle.release.internal

import com.android.build.gradle.api.LibraryVariant
import com.novoda.gradle.release.MavenPublicationAttachments
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.util.GradleVersion

class AndroidAttachments extends MavenPublicationAttachments {

    private static final String ANDROID_SOFTWARE_COMPONENT_COMPAT_4_1 = 'com.novoda.release.internal.compat.gradle4_1.AndroidSoftwareComponentCompat_Gradle_4_1'
    private static final String ANDROID_SOFTWARE_COMPONENT_COMPAT_4_5 = 'com.novoda.release.internal.compat.gradle4_5.AndroidSoftwareComponentCompat_Gradle_4_5'
    private static final String ANDROID_SOFTWARE_COMPONENT_COMPAT_4_8 = 'com.novoda.release.internal.compat.gradle4_8.AndroidSoftwareComponentCompat_Gradle_4_8'
    private static final String ANDROID_SOFTWARE_COMPONENT_COMPAT_5_2 = 'com.novoda.release.internal.compat.gradle5_2.AndroidSoftwareComponentCompat_Gradle_5_2'
    private static final String ANDROID_SOFTWARE_COMPONENT_COMPAT_5_3 = 'com.novoda.release.internal.compat.gradle5_3.AndroidSoftwareComponentCompat_Gradle_5_3'

    AndroidAttachments(String publicationName, Project project, def variant) {
        super(androidComponentFrom(project),
                androidSourcesJarTask(project, publicationName, variant),
                androidJavadocsJarTask(project, publicationName, variant),
                androidArchivePath(variant))
    }

    private static SoftwareComponent androidComponentFrom(Project project) {
        def currentGradleVersion = GradleVersion.current()
        if (currentGradleVersion >= GradleVersion.version('5.3')) {
            def clazz = this.classLoader.loadClass(ANDROID_SOFTWARE_COMPONENT_COMPAT_5_3)
            return project.objects.newInstance(clazz) as SoftwareComponent
        }
        if (currentGradleVersion >= GradleVersion.version('5.2')) {
            def clazz = this.classLoader.loadClass(ANDROID_SOFTWARE_COMPONENT_COMPAT_5_2)
            return project.objects.newInstance(clazz) as SoftwareComponent
        }
        if (currentGradleVersion >= GradleVersion.version('4.8')) {
            def clazz = this.classLoader.loadClass(ANDROID_SOFTWARE_COMPONENT_COMPAT_4_8)
            return project.objects.newInstance(clazz) as SoftwareComponent
        }
        if (currentGradleVersion >= GradleVersion.version('4.5')) {
            def clazz = this.classLoader.loadClass(ANDROID_SOFTWARE_COMPONENT_COMPAT_4_5)
            return project.objects.newInstance(clazz) as SoftwareComponent
        }
        def clazz = this.classLoader.loadClass(ANDROID_SOFTWARE_COMPONENT_COMPAT_4_1)
        return clazz.newInstance(project.objects, project.configurations) as SoftwareComponent
    }

    private static Task androidSourcesJarTask(Project project, String publicationName, def variant) {
        def sourcePaths = variant.sourceSets.collect { it.javaDirectories }.flatten()
        return sourcesJarTask(project, publicationName, sourcePaths)
    }

    private static Task androidJavadocsJarTask(Project project, String publicationName, LibraryVariant variant) {
        Javadoc javadoc = project.task("javadoc${publicationName.capitalize()}", type: Javadoc) { Javadoc javadoc ->
            javadoc.source = variant.javaCompileProvider.getOrNull().source
            javadoc.classpath = variant.javaCompileProvider.getOrNull() .classpath
        } as Javadoc
        return javadocsJarTask(project, publicationName, javadoc)
    }

    private static def androidArchivePath(LibraryVariant variant) {
        return variant.packageLibraryProvider.getOrNull()
    }
}
