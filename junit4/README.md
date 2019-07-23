# spring-esdata-loader-junit4

**JUnit 4** implementation of the library.

This sub-module is that is needed to start using the library with **JUnit 4**.
It defines a `TestRule` named `LoadEsDataRule` that can be used to insert data into Eelasticsearch,
before all tests are run (class level), or just before a specific test is run (method level).

Here is an example:

```java
import com.github.spring.esdata.loader.core.LoadEsData;
import com.github.spring.esdata.loader.junit4.LoadEsDataRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class) // required to run JUnit 4 tests with Spring magic
//@SpringBootTest or any @ContextConfiguration(..) to initialize the Spring context that contains the ElasticsearchTemplate

@LoadEsData(esEntityClass=MyEsEntity1.class, location="/path/to/data1.json")
@LoadEsData(esEntityClass=MyEsEntity2.class, location="/path/to/data2.json")
public class MyJunit4TestClass{

    @ClassRule // to load data defined at class level, via the @LoadEsData annotation(s) on the tested class
    public static final LoadEsDataRule ES_DATA_LOADER = new LoadEsDataRule();

    @Rule //to load data defined at method level, via  the @LoadEsData annotation(s) on the tested method
    public final LoadEsDataRule esDataLoader = ES_DATA_LOADER;


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
*  [LoadEsDataRuleTest.java](/demo/src/test/java/com/github/spring/esdata/loader/demo/junit4/LoadEsDataRuleTest.java)
