# spring-esdata-loader-junit-jupiter submodule

**JUnit Jupiter** implementation of the library.


The module is all you need  to start using the library with the new **JUnit Jupiter**. It defines an Extension named `LoadEsDataExtention` that you can use to insert data into your ES server,
before all tests are run (class level), or just before a specific test is run (method level).

Here is an example:

```java
import com.github.spring.esdata.loader.core.LoadEsData;
import com.github.spring.esdata.loader.junit.jupiter.LoadEsDataConfig;
import org.junit.jupiter.api.Test;

//@SpringBootTest or any @ContextConfiguration(..) to initialize the Spring context that contains the ElasticsearchTemplate

@LoadEsDataConfig({ // @LoadEsDataConfig is a metat annotation that is itself annotated with @ExtendWith(LoadEsDataExtension.class)
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
*  [LoadEsDataExtensionTest.java](/demo/src/test/java/com/github/spring/esdata/loader/demo/junit/juipter/LoadEsDataExtensionTest.java)
