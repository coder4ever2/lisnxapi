grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

//Forked execution
forkConfig = [maxMemory: 1024, minMemory: 64, debug: true, maxPerm: 256]
grails.project.fork = [
   test: false, // configure settings for the test-app JVM
   run: false, // configure settings for the run-app JVM
   war: forkConfig, // configure settings for the run-war JVM
   console: forkConfig // configure settings for the Swing console JVM
]

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	checksums true // Whether to verify checksums on resolve
	legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
    repositories {
       inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
		mavenRepo "http://repo.grails.org/grails/core"
        mavenCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		runtime('com.notnoop.apns:apns:0.1.6') { 
                        excludes([ group: 'org.slf4j', name: 'slf4j-api', version: '1.6.1']) 
        } 
        runtime 'mysql:mysql-connector-java:5.1.5'
		runtime 'com.javadocmd:simplelatlng:1.0.0','org.apache.httpcomponents:httpcore:4.3.3',
				'org.apache.httpcomponents:httpcore-nio:4.3.3','commons-logging:commons-logging:1.2'
		compile 'com.googlecode.json-simple:json-simple:1.1.1','com.mashape.unirest:unirest-java:1.3.26',
				'org.json:json:20140107','org.apache.httpcomponents:httpclient:4.3.6',
				'org.apache.httpcomponents:httpmime:4.3.6','org.apache.httpcomponents:httpasyncclient:4.0.2'
        build('org.apache.xbean:xbean-spring:3.7') {
            excludes 'commons-logging'
        }
    }
	plugins {
		compile ":hibernate:3.6.10.13",":apns:1.0", ":asynchronous-mail:1.1",
				":build-info:1.1", ":famfamfam:1.0.1", ":geolocation:0.4.1", ":jquery:1.11.1",
				":jquery-ui:1.10.3", ":mail:1.0.1", ":searchable:0.6.9", ":spring-security-core:1.2.7.3", 
				":spring-security-ui:0.2", ":quartz:1.0.2", ":oauth:2.1.0", ":scaffolding:2.0.0", ':image-gallery:0.1.1'
		//runtime ":resources:1.2.8"
		runtime ":console:1.5.0"
		build ":tomcat:2.2.1"
	 }
}

grails.server.port.http = 9990
