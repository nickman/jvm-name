# jvm-name

## What is jvm-name
A simple utility to programatically set a JVM's DisplayName.

## Intro to the JVM DisplayName
The JVM DisplayName is a string that describes a running JVM. THe actual content varies from case to case, but the easiest way of viewing some example names is to use the [jps](https://docs.oracle.com/javase/7/docs/technotes/tools/share/jps.html) (Java Virtual Machine Process Status Tool) command. jps prints the JVM id (always the pid, so far as I have seen) and the [shortened] DisplayName.

```bash
$ jps
29718 GroovyStarter
27911 JConsole
12760 HMaster
442 Jps
24778 Elasticsearch
28140 org.eclipse.equinox.launcher_1.4.0.v20161219-1356.jar
```

Depending on how the JVM was started, the DisplayName produced by jps will usually be one of:
1. The simple class name of the main bootstrapped class.
2. The jar name if the JVM was started as **java -jar &lt;name&gt;.jar**.
  
Note that jps supports multiple arguments that add additional output such as command line arguments and fully qualified bootstrap class names.

Under the covers, jps uses the [Attach API](https://docs.oracle.com/javase/7/docs/jdk/api/attach/spec/index.html) to capture this information. This quickie groovy script uses the Attach API to display the same basic data as jps, but without the display shortening:

```groovy
import com.sun.tools.attach.*
VirtualMachine.list().each() { vmd ->  
    println "ID: ${vmd.id()}, Display: ${vmd.displayName()}"
}
```

Output:
```
ID: 29718, Display: org.codehaus.groovy.tools.GroovyStarter --main groovy.ui.Console --conf /home/nwhitehead/.sdkman/candidates/groovy/current/conf/groovy-starter.conf --classpath .
ID: 27911, Display: sun.tools.jconsole.JConsole 25884
ID: 12760, Display: org.apache.hadoop.hbase.master.HMaster start
ID: 24778, Display: org.elasticsearch.bootstrap.Elasticsearch
ID: 28140, Display: /home/nwhitehead/eclipse/java-oxygen2/eclipse//plugins/org.eclipse.equinox.launcher_1.4.0.v20161219-1356.jar -os linux -ws gtk -arch x86_64 -showsplash -launcher /home/nwhitehead/eclipse/java-oxygen2/eclipse/eclipse -name Eclipse --launcher.library /home/nwhitehead/.p2/pool/plugins/org.eclipse.equinox.launcher.gtk.linux.x86_64_1.1.551.v20171108-1834/eclipse_1630.so -startup /home/nwhitehead/eclipse/java-oxygen2/eclipse//plugins/org.eclipse.equinox.launcher_1.4.0.v20161219-1356.jar --launcher.appendVmargs -exitdata 3f6b8014 -product org.eclipse.epp.package.java.product -vm /usr/lib/jvm/jdk1.8.0_152/jre/bin/java -vmargs -Dosgi.requiredJavaVersion=1.8 -Dosgi.instance.area.default=@user.home/eclipse-workspace -XX:+UseG1GC -XX:+UseStringDeduplication -Dosgi.requiredJavaVersion=1.8 -Xms256m -Xmx1024m -Declipse.p2.max.threads=10 -Doomph.update.url=http://download.eclipse.org/oomph/updates/milestone/latest -Doomph.redirection.index.redirection=index:/->http://git.eclipse.org/c/oomph/org.eclipse.oomph.git/plain/setups

```

## Using jvm-name in your code
I created jvm-name because sometimes it is useful to modify the DisplayName to a more intuitive or useful value. The procedure is a simple static call, and you can also access the current DisplayName.

```java
import com.heliosapm.jvmid.JVMName;
...
public boolean setName(String name) {
  return JVMName.setDisplay(name);
}

public String getName() {
  return JVMName.getDisplay();
}
```

That's it. You can ignore the boolean return, but it indicates if the procedure was successful or not, however, the buffer that holds the DisplayName has a limited size (typically 244 to 250 bytes) so if the supplied name is too long, it will throw an **IllegalArgumentException**.

Version 1.0 of jvm-name is published into the https://oss.sonatype.org public maven repository at:

```XML
<dependency>
  <groupId>com.heliosapm.jvm</groupId>
  <artifactId>jvm-name</artifactId>
  <version>1.0</version>
</dependency>
```
## The jvm-name Java Agent

jvm-name also provides a Java Agent that you can define in your JVM startup command line. The syntax is:

```bash
java -javaagent:jvm-name-1.0.jar=MyNewJVM .....
```
The Java Agent does a couple of extra things when it successfully sets the new DisplayName:
1. Sets the system property **sun.rt.javaCommand** to the new DisplayName.
2. Sets the system property **original.display.name** to the prior DisplayName.

## More Examples

### OpenTSDB

I use an uber-jar build of [OpenTSDB](http://opentsdb.net/) which is launched using a **java -jar ...** launch. Normally, the DisplayName looks like this:

```bash
$ jps
6329 opentsdb-fatjar-2.4.0-SNAPSHOT-fat.jar
```
Using jvm-name, I rename the DisplayName to a shorter name that includes the main lisetning port which helps identify which JVM is which if I am running several instances on the same host. Running jps now displays:

```bash
$ jps
6917 OpenTSDB-5252
25884 OpenTSDB-4242
7071 OpenTSDB-6262
```

### Eclipse

Not as useful, but for demonstration purposes, we can change the DisplayName of Eclipse instances. I use both core Eclipse and Spring STS. Using jps, they display as:

```bash
$ jps
4851 org.eclipse.equinox.launcher_1.4.0.v20161219-1356.jar
28140 org.eclipse.equinox.launcher_1.4.0.v20161219-1356.jar
```

Hmmm... which is which ? By modifying the Eclipse ini file (eclipse.ini for core Eclipse, sts.ini for Spring STS) I can rename them.

Sample sts.ini:

```
-startup
plugins/org.eclipse.equinox.launcher_1.4.0.v20161219-1356.jar
--launcher.library
plugins/org.eclipse.equinox.launcher.gtk.linux.x86_64_1.1.550.v20170928-1359
-product
org.springsource.sts.ide
--launcher.defaultAction
openFile
-vmargs
-Dosgi.requiredJavaVersion=1.8
--add-modules=ALL-SYSTEM
-Dosgi.module.lock.timeout=10
-Xverify:none
-Xms2400m
-Xmx2400m
-javaagent:jvm-name-1.0.jar=STS-3_9_1
```

Now I see:

```bash
$ jps
8613 STS-3_9_1
8398 EclipseOxygen2
```

## Building jvm-name

1. Clone the project
2. Go into the jvm-name project directory
3. **mvn clean install**




