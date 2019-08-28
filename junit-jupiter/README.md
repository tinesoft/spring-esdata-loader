# spring-esdata-loader-junit-jupiter

[![Maven Central](https://img.shields.io/maven-central/v/com.github.tinesoft/spring-esdata-loader-junit-jupiter)](https://search.maven.org/artifact/com.github.tinesoft/spring-esdata-loader-junit-jupiter/?/jar)
[![JCenter](https://img.shields.io/bintray/v/tinesoft/maven/spring-esdata-loader-junit-jupiter)](https://bintray.com/tinesoft/maven/spring-esdata-loader-junit-jupiter/_latestVersion)

**JUnit Jupiter** implementation of the library.

The sub-module is all that is needed to start using the library with the brand new **JUnit Jupiter**. 
It defines `Extension`s named `LoadEsDataExtention` (resp. `DeleteEsDataExtension`) that can be used to insert data into (resp. remove data from) Elasticsearch,
before all tests are run (class level), or just before a specific test is run (method level).

Here is an example:

```java
import com.github.spring.esdata.loader.core.LoadEsData;
import com.github.spring.esdata.loader.junit.jupiter.LoadEsDataConfig;
import org.junit.jupiter.api.Test;

//@SpringBootTest or any @ContextConfiguration(..) to initialize the Spring context that contains the ElasticsearchOperations

@LoadEsDataConfig({ // @LoadEsDataConfig is a meta annotation that is itself annotated with @ExtendWith(LoadEsDataExtension.class)
    @LoadEsData(esEntityClass=MyEsEntity1.class, location="/path/to/data1.json"),
    @LoadEsData(esEntityClass=MyEsEntity2.class, location="/path/to/data2.json")
})
public class MyJunitJupiterTestClass{

    public void testThatUsesEsDataLoadedAtClassLevel()
    {
        // make your assertions here
    }

    @LoadEsData(esEntityClass=MyEsEntity3.class, location="/path/to/data3.json")
    public void testThatUsesEsDataLoadedAtClassLevelAndAtThisMethodLevel()
    {
        // make your assertions here
    }
}
```

A full example can be seen in demo project:
*  [LoadEsDataExtensionTest.java](/demo/src/test/java/com/github/spring/esdata/loader/demo/junit/jupiter/LoadEsDataExtensionTest.java)

Similarly, you can use `DeleteEsDataExtension` to remove data from Elasticsearch indices

Here is an example:

```java
import com.github.spring.esdata.loader.core.LoadEsData;
import com.github.spring.esdata.loader.junit.jupiter.LoadEsDataConfig;
import org.junit.jupiter.api.Test;

//@SpringBootTest or any @ContextConfiguration(..) to initialize the Spring context that contains the ElasticsearchOperations

@DeleteEsDataConfig({ // @DeleteEsDataConfig is a meta annotation that is itself annotated with @ExtendWith(LoadEsDataExtension.class)
   MyEsEntity1.class,
   MyEsEntity2.class
})
public class MyJunitJupiterTestClass{

    public void testThatUsesEsDataRemovedAtClassLevel()
    {
        // make your assertions here
    }

    @DeleteEsDataConfig(esEntityClasses={MyEsEntity3.class})
    public void testThatUsesEsDataRemovedAtClassLevelAndAtThisMethodLevel()
    {
        // make your assertions here
    }
}
```

A full example can be seen in demo project:
*  [DeleteEsDataExtensionTest.java](/demo/src/test/java/com/github/spring/esdata/loader/demo/junit/jupiter/DeleteEsDataExtensionTest.java)
