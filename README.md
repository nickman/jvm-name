# jvm-name
A simple utility to set or update a JVM's display name as printed by jps


org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 46492 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test com.heliosapm.jvmid.TestJVMName:testSets

18587 RemoteTestRunner

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
-Xms40m
-Dosgi.module.lock.timeout=10
-Xverify:none
-Xms2400m
-Xmx2400m
-javaagent:/home/nwhitehead/hprojects/jvm-name/target/jvm-name-1.0-SNAPSHOT.jar=STS-3_9_1





