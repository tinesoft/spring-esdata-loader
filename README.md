
# spring-esdata-loader
[![Build  Status](https://travis-ci.org/tinesoft/spring-esdata-loader.svg?branch=master)](https://travis-ci.org/tinesoft/spring-esdata-loader)

`spring-esdata-loader` is a Java testing library to help write integration tests for [spring-data elasticsearch](https://spring.io/projects/spring-data-elasticsearch)-based projects, by allowing to easily load data into Elasticsearch, using entity mappings (i.e domain classes annotated with `@Document`, `@Field`, etc) and via a specfic **Junit 4**'s Rule or **JUnit Jupiter**'s Extension.

The library reads all the metadata it needs from the entity class (index name, index type, etc) , uses them to create/refresh the index on the ES server and feeds it with the data using the `ElasticsearchTemplate` present in your test application context.

## Features

*  **Simple API** and no configuration required
*  Support  for  **JUnit 4** via `LoadEsDataRule`
*  Support  for  **JUnit  Jupiter** via `@LoadEsDataConfig` or `@LoadEsDataExtension`
*  Built-in support for GZip
*  Written  in  **Java  8**
*  Based on **Spring (Data, Test)**


## Dependencies

`spring-esdata-loader` is based on dependencies that you already have in your Spring (Boot) project, if you are doing Elasticsearch with Spring :

* [Spring  Data  Elasticsearch](https://mvnrepository.com/artifact/org.springframework.data/spring-data-elasticsearch) (*requires* Spring Data Elasticsearch 3+, tested with [v3.1.8.RELEASE](https://mvnrepository.com/artifact/org.springframework.data/spring-data-elasticsearch/3.1.8.RELEASE )
* [Spring  Test](https://mvnrepository.com/artifact/org.springframework/spring-test) (*requires* Spring Test 5+, tested with [5.1.7.RELEASE](https://mvnrepository.com/artifact/org.springframework/spring-test/5.1.7.RELEASE)

## Installation & Usage

The library is splitted into 2 independent sub-modules, both will be available on Maven Central (*soon*):

* `spring-esdata-loader-junit4` for testing with **JUnit 4**
* `spring-esdata-loader-junit-jupiter` for testing with **JUnit Jupiter**

To get started,

1. add the appropriate dependency to your gradle or maven project

<table>
    <tr>
        <th></th>
        <th>Gradle</th>
        <th>Maven</th>
    </tr>
    <tr>
        <td>JUnit 4</td>
        <td>
<pre lang="groovy">dependencies {
    testImplementation 'com.github.spring-esdata-loader:spring-esdata-loader-junit4:1.0.0'
}</pre>
        </td>
        <td>
<pre lang="xml">&lt;dependency&gt;
    &lt;groupId&gt;com.github.spring-esdata-loader&lt;/groupId&gt;
    &lt;artifactId>spring-esdata-loader-junit4&lt;/artifactId&gt;
    &lt;version>1.0.0&lt;/version&gt;
    &lt;scope>test&lt;/scope&gt;
&lt;/dependency&gt;</pre>
        </td>
    </tr>
    <tr>
        <td>JUnit Jupiter</td>
        <td>
<pre lang="groovy">dependencies {
    testImplementation 'com.github.spring-esdata-loader:spring-esdata-loader-junit-jupiter:1.0.0'
}</pre>
        </td>
        <td>
<pre lang="xml">&lt;dependency&gt;
    &lt;groupId&gt;com.github.spring-esdata-loader&lt;/groupId&gt;
    &lt;artifactId>spring-esdata-loader-junit-jupiter&lt;/artifactId&gt;
    &lt;version>1.0.0&lt;/version&gt;
    &lt;scope>test&lt;/scope&gt;
&lt;/dependency&gt;</pre>
        </td>
    </tr>
</table>

2. write your test class. You can have a look at:

  * [junit4](/junit4) - if your are using **JUnit 4**
  * [junit-jupiter](/junit/jupiter) - if you are using **JUnit Jupiter**

## Data Format

The data to be loaded must follow the appropriate format.
Here is an example:
```json
{"_index":"author","_type":"Author","_id":"5","_score":1,"_source":{"id":"5","firstName":"firstName5","lastName":"lastName5"}}
{"_index":"author","_type":"Author","_id":"8","_score":1,"_source":{"id":"8","firstName":"firstName8","lastName":"lastName8"}}
{"_index":"author","_type":"Author","_id":"9","_score":1,"_source":{"id":"9","firstName":"firstName9","lastName":"lastName9"}}
{"_index":"author","_type":"Author","_id":"10","_score":1,"_source":{"id":"10","firstName":"firstName10","lastName":"lastName10"}}
{"_index":"author","_type":"Author","_id":"2","_score":1,"_source":{"id":"2","firstName":"firstName2","lastName":"lastName2"}}
{"_index":"author","_type":"Author","_id":"4","_score":1,"_source":{"id":"4","firstName":"firstName4","lastName":"lastName4"}}
{"_index":"author","_type":"Author","_id":"6","_score":1,"_source":{"id":"6","firstName":"firstName6","lastName":"lastName6"}}
{"_index":"author","_type":"Author","_id":"1","_score":1,"_source":{"id":"1","firstName":"firstName1","lastName":"lastName1"}}
{"_index":"author","_type":"Author","_id":"7","_score":1,"_source":{"id":"7","firstName":"firstName7","lastName":"lastName7"}}
{"_index":"author","_type":"Author","_id":"3","_score":1,"_source":{"id":"3","firstName":"firstName3","lastName":"lastName3"}}

```
You can use a tool like [elasticdump](https://npmjs.com/package/elasticdump) (*requires* [NodeJS](https://nodejs.org/)) to extract existing data
from your Elasticseach server, and them dump them into a JSON file.

```
$ npx elasticdump --input=http://localhost:9200/my_index --output=my_index_data.json
```
> The above command will run `elasticdump` to extract data from an index named `my_index` on a ES server located at http://localhost:9200 and then save the result into a file named `my_index_data.json`

> If you change the `--output` part above into `--output=$ | gzip my_data.json.gz` the data will be automatically gzipped

## Contributing

Contributions are always welcome! Just fork the project, work on your feature/bug fix, and submit it.
You can also contribute by creating issues.

## License

Copyright (c) 2019  Tine  Kondo. Licensed under the MIT License (MIT)
