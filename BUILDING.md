# Building
YAP is built with [Gradle], the following sections explain how to use it to build and run YAP.
The Gradle tasks are expected to be executed with the provided [Gradle Wrapper].

## IDEs
The following page provides in depth guides on how to import, build, and run YAP (core and add-ons) with commonly used IDEs:
https://www.yaproxy.org/docs/developer/

## Run YAP
To run YAP directly from the source run the task `:yap:run`. It will use any add-ons available in the [yap/src/main/dist/plugin/] directory.

**NOTE:** No add-on is included in the repository, they need to be built/copied separately into the `plugin` directory.

### Tests
To execute the tests run the task `:yap:test`.

## Distributions
The distributions bundle YAP and its dependencies, all necessary to run YAP standalone. By default the distributions of development
versions (SNAPSHOT) bundle the add-ons present in the dist `plugin` directory, main versions (non-SNAPSHOT) bundle a [predefined
list of add-ons] (downloaded automatically when the distribution is built).

The distributions are built into `yap/build/distributions/`.

### Daily
A zip package with a day stamped version, does not target any specific OS, it bundles all add-ons present in the `plugin` directory always.

To build it run the task `:yap:distDaily`.

(This distribution is built by default, it is a dependency of `assemble` task.)

### Weekly
A zip package with a day stamped version, does not target any specific OS, it bundles only [weekly add-ons] (built automatically from
source when the distribution is built).
This distribution is used for weekly releases.

To build it run the task `:yap:distWeekly`.

The build also provides the task `:yap:copyWeeklyAddOns` which builds and copies the weekly add-ons into the plugin directory,
using existing repositories in the file system at the same level as yaproxy.

### Cross Platform
A zip package, does not target any specific OS.

To build it run the task `:yap:distCrossplatform`.

### Core
Same as cross platform distribution but with just the essential add-ons, making the distribution smaller.

To build it run the task `:yap:distCore`.

### Linux
A tar.gz package, the macOS/Windows specific add-ons are excluded from this distribution.

To build it run the task `:yap:distLinux`.

### macOS
A dmg bundling YAP, its dependencies, and a JRE. The Linux/Windows specific add-ons are excluded from this distribution.

To build the macOS distributions run the tasks `:yap:distMac` and `:yap:distMacArm64`, for the architectures `x64` and `aarch64` respectively.

**NOTE:** Needs to be executed on macOS, it requires `hdiutil`.

## Installers
The installers for Linux and Windows are built with [install4j]. The Windows executable is built with the [launch4j], invoked with Gradle plugin [gradle-launch4j].

To build the installers run the task `:yap:installers`.

Once the build is finished the installers will be located in the directory `yap/build/install4j/`.

**NOTE:** The following properties must be defined (e.g. in file `GRADLE_HOME/gradle.properties` ) to successfully and properly build the installers:
 - `install4jHomeDir` - install4j installation directory;
 - `install4jLicense` - install4j license key.

[Gradle]: https://gradle.org/
[Gradle Wrapper]: https://docs.gradle.org/current/userguide/gradle_wrapper.html
[yap/src/main/dist/plugin/]: yap/src/main/dist/plugin/
[predefined list of add-ons]: yap/src/main/add-ons.txt
[weekly add-ons]: yap/src/main/weekly-add-ons.json
[install4j]: https://www.ej-technologies.com/products/install4j/overview.html
[launch4j]: http://launch4j.sourceforge.net/
[gradle-launch4j]: https://github.com/TheBoegl/gradle-launch4j
