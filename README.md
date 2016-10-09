# CDeliveryBoy [![Build Status](https://travis-ci.org/szpak/CDeliveryBoy.svg?branch=master)](https://travis-ci.org/szpak/CDeliveryBoy) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/info.solidsoft.gradle/cdeliveryboy/badge.svg)](https://maven-badges.herokuapp.com/maven-central/info.solidsoft.gradle/cdeliveryboy)

Continuous Delivery solution for Gradle based FOSS projects.

## Quick start

To be precisely defined. In the meantime please take a look at the [Dummy CD project](https://github.com/szpak/dummy-cd-project/tree/cdeliveryBoy)
(a cDeliveryBoy branch) with sample (working) configuration.
 
## Features

This plugin provides (on its own or by internally orchestrating other plugins) the following release steps:
 - version management
 - artifacts building
 - tagging and pushing changes back to GitHub
 - promotion of uploaded artifacts to Maven Central

After initial configuration the whole release process is performed automatically on Travis when requested by a command placed in a commit message.

## Requirements

The project should fulfill the following conditions:
 - configured artifacts upload to Sonatype Nexus OSS (with artifacts signing)
 - configured Travis build

Hint: [gradle-template](https://github.com/szpak/gradle-template/tree/cDeliveryBoy) could be as a starting point. 

## Sample configuration

```
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'info.solidsoft.gradle:cdeliveryboy:0.3.0'
    }
}

apply plugin: 'info.solidsoft.cdeliveryboy'

cDeliveryBoy {
    autoPromote = false
}

nexusStaging {
    packageGroup = "YOUR-PACKAGE-GROUP-IN-NEXUS"
}

scmVersion {
}

project.version = scmVersion.version
```

## Rationale

I've been tired of manual releasing the FOSS projects I maintain. Being professionally deeply involved in Continuous Delivery of high quality
"Enterprise-Class applications" I wanted to have a light version of CD available for me and other people prefering automation and convenience.

## Additional information 

CDeliveryBoy has been written by Marcin Zajączkowski. The author can be contacted directly via email: mszpak ATT wp DOTT pl.
There is also Marcin's blog available: [Solid Soft](http://blog.solidsoft.info/) - working code is not enough.

The project is licensed under the terms of [the Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).
