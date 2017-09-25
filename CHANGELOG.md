# C(ontinuous)DeliveryBoy changelog

## 0.8.0 - 2017-09-25

 - Functional smoke tests
 - Upgrade to Gradle 4.1
 - Internal Gradle configuration refactoring
 - Java 7 compatibility check with Animal Sniffer

## 0.7.1 - 2017-09-15

 - Internal refactoring and improved testing

## 0.7.0 - 2017-09-12

 - Display release conditions in more human friendly way - [#8](https://github.com/szpak/CDeliveryBoy/issues/8)
 - Disable remote branch validation by default - Travis works on detached branches
 - Deprecate `isInReleaseMode` property in release tasks (replaced with `inReleaseMode`)

## 0.6.0 - 2017-07-04

 - Upgrade gradle-nexus-staging-plugin, Axion plugin and Spock
 - Tune Gradle Portal Plugin release from Travis

## 0.5.1 - 2017-07-04

 - Release to Gradle Portal Plugin - [#5](https://github.com/szpak/CDeliveryBoy/issues/5)
 - Synchronize CHANGELOG with GitHub release notes - [#6](https://github.com/szpak/CDeliveryBoy/issues/6) 

## 0.5.0 - 2017-07-04

 - Ability to override release version in commit message - [#4](https://github.com/szpak/CDeliveryBoy/issues/4)
 - Ability to override incrementer in commit message - [#4](https://github.com/szpak/CDeliveryBoy/issues/4)
 - Increment minor version number by default 
 - Validate existence of required environment variables in advance
  
## 0.4.0 - 2017-06-02

 - Rework plugin configuration (to more component oriented)
 - Support for SKIP_RELEASE environment variable
 - Add (optional) "Powered by" banner in release commit message
 - Bring compatibility with Gradle 3
 - Upgrade key dependencies
 - Workaround for 3 release commits bug in newer Axion versions - [#132](https://github.com/allegro/axion-release-plugin/issues/132) 

## 0.3.0 - 2016-10-09

 - Initial (self-hosted) release 
