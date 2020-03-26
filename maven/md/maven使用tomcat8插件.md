```xml
<!--tomcat插件仓库-->  
<pluginRepositories>
    <pluginRepository>
      <id>alfresco-public</id>
      <url>https://artifacts.alfresco.com/nexus/content/groups/public</url>
    </pluginRepository>
    <pluginRepository>
      <id>alfresco-public-snapshots</id>
      <url>https://artifacts.alfresco.com/nexus/content/groups/public-snapshots</url>
      <snapshots>
        <enabled>true</enabled>
        <updatePolicy>daily</updatePolicy>
      </snapshots>
    </pluginRepository>
    <pluginRepository>
      <id>beardedgeeks-releases</id>
      <url>http://beardedgeeks.googlecode.com/svn/repository/releases</url>
    </pluginRepository>
  </pluginRepositories>

<!--插件坐标-->

<plugin>
	<groupId>org.apache.tomcat.maven</groupId>
	<artifactId>tomcat8-maven-plugin</artifactId>
	<version>3.0-r1655215</version>
</plugin>
```

