import org.gradle.internal.buildtree.RunTasksRequirements

plugins {
    kotlin("multiplatform") version "1.8.10"
    application
}

group = "com.ultreon"
version = "0.1.0"

repositories {
    mavenCentral()
}
kotlin {
    linuxX64 {
        binaries {
            executable {
                runTask?.args("main.ulan", "--internal-errors", "--tokens")
                runTask?.workingDir = file("${rootProject.projectDir}/run/").also {
                    it.mkdirs()
                }
                entryPoint = "main"
            }
        }
    }
    mingwX64 {
        binaries {
            executable {
                runTask?.args("main.ulan", "--internal-errors", "--tokens")
                runTask?.workingDir = file("${rootProject.projectDir}/run/").also {
                    it.mkdirs()
                }
                entryPoint = "main"
            }
        }
    }
//    mingwX86 {
//        binaries {
//            executable {
//                entryPoint = "main"
//            }
//        }
//    }
    macosX64 {
        binaries {
            executable {
                entryPoint = "main"
                runTask?.args("main.ulan", "--internal-errors", "--tokens")
                runTask?.workingDir = file("${rootProject.projectDir}/run/").also {
                    it.mkdirs()
                }
            }
        }
    }
    macosArm64 {
        binaries {
            executable {
                runTask?.args("main.ulan", "--internal-errors", "--tokens")
                runTask?.workingDir = file("${rootProject.projectDir}/run/").also {
                    it.mkdirs()
                }
                entryPoint = "main"
            }
        }
    }
    jvm {
        jvmToolchain(8)
        withJava()

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            repositories {
                mavenCentral()
            }

            dependencies {
                implementation("com.soywiz.korlibs.kbignum:kbignum:3.4.0")
                implementation("com.squareup.okio:okio:3.3.0")
                implementation("com.soywiz.korlibs.korio:korio:3.4.0")
                implementation("com.bkahlert.kommons:kommons:2.8.0")
                implementation("com.bkahlert.kommons:kommons-core:2.8.0")
                implementation("com.bkahlert.kommons:kommons-uri:2.8.0")
                implementation("com.bkahlert.kommons:kommons-time:2.8.0")
                implementation("com.bkahlert.kommons:kommons-text:2.8.0")
            }
        }
        val commonTest by getting
        val linuxX64Main by getting {
            repositories {
                mavenCentral()
            }
        }
        val linuxX64Test by getting
        val mingwX64Main by getting {
            repositories {
                mavenCentral()
            }
        }
        val mingwX64Test by getting
//        val mingwX86Main by getting {
//            repositories {
//                mavenCentral()
//            }
//        }
//        val mingwX86Test by getting
        val macosX64Main by getting {
            repositories {
                mavenCentral()
            }
        }
        val macosX64Test by getting
        val macosArm64Main by getting {
            repositories {
                mavenCentral()
            }
        }
        val macosArm64Test by getting
        val jvmMain by getting {
            repositories {
                mavenCentral()
            }
        }
        val jvmTest by getting
    }
}

application {
    mainClass.set("MainKt")
    applicationName = "UltranLang"
}

tasks.run.configure {
    args("main.ulan", "--internal-errors", "--tokens")
    workingDir = file("$projectDir/run/").also {
        it.mkdirs()
    }
}
