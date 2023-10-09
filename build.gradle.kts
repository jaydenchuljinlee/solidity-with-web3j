import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.17-SNAPSHOT"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	id("org.web3j.solidity") version "0.3.6"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

solidity {
	version = "0.8.18"
}

apply(plugin = "org.web3j.solidity")

group = "com.bc.eth"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
	maven { url = uri("https://plugins.gradle.org/m2/") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	implementation("org.web3j:web3j-sokt:0.2.3")
	implementation("org.web3j.solidity:solidity-gradle-plugin:0.3.6")
	implementation("org.web3j:core:4.9.8")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {


	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
