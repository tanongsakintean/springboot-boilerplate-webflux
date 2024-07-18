plugins {
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("plugin.jpa") version "1.9.24"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
}

group = "com.springboot"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("io.r2dbc:r2dbc-postgresql:0.8.13.RELEASE")
	implementation("javax.xml.bind:jaxb-api:2.3.1")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.apache.logging.log4j:log4j-api:2.17.0")
	implementation("org.apache.logging.log4j:log4j-to-slf4j:2.17.0")
	implementation ("com.google.code.gson:gson:2.8.9")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:r2dbc-postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.opentelemetry:opentelemetry-exporter-zipkin:1.39.0")
	implementation("io.opentelemetry:opentelemetry-api:1.28.0")
	implementation("io.opentelemetry:opentelemetry-sdk:1.28.0")
	implementation("io.opentelemetry:opentelemetry-exporter-otlp:1.28.0")
	implementation("io.opentelemetry:opentelemetry-semconv:1.28.0-alpha")

	// cache redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("io.projectreactor:reactor-core")

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<JavaExec> {
	jvmArgs = listOf(
		"-Dcom.sun.management.jmxremote.local.only=false",
		"-Djava.util.logging.config.file=${projectDir}/src/main/resources/management.properties"
	)
}

tasks.register<Copy>("copyJar") {
	dependsOn("bootJar")
	from(file("build/libs/${project.name}-${project.version}.jar"))
	into("build/docker")
	rename { fileName ->
		fileName.replace("-${project.version}.jar", ".jar")
	}
}
tasks.build {
	dependsOn("copyJar")
}
tasks.register("version") {
	println(project.version)
}