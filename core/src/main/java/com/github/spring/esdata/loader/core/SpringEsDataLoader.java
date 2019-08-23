package com.github.spring.esdata.loader.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * Loader that use Spring Data's {@link ElasticsearchOperations} to load data into Elasticsearch.
 *
 * @author tinesoft
 *
 */
public class SpringEsDataLoader implements EsDataLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringEsDataLoader.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final ElasticsearchOperations esOperations;

	/**.
	 * Data loader that use Spring's {@link ElasticsearchOperations} to load data into Elasticsearch
	 * @param esOperations the {@link ElasticsearchOperations}
	 */
	@Autowired
	public SpringEsDataLoader(final ElasticsearchOperations esOperations) {
		this.esOperations = esOperations;
	}

  /**
   * Deletes data from Elasticsearch using provided class to retrieve related index.
   *
   * @param esEntityClass the data to load
   */
  @Override
  public void delete(Class<?> esEntityClass) {
    LOGGER.debug("Dropping data in Index '{}'...", esEntityClass.getSimpleName());
    this.esOperations.deleteIndex(esEntityClass);
    this.esOperations.createIndex(esEntityClass);
    this.esOperations.putMapping(esEntityClass);
    this.esOperations.refresh(esEntityClass);

  }

  /**
   * Loads given data into Elasticsearch. Target indices are dropped and recreated before data are inserted in bulk.
   * @param d the data to load
     */
  @Override
  public void load(final IndexData d) {

		// first recreate the index
    LOGGER.debug("Recreating Index for '{}'...", d.getEsEntityClass().getSimpleName());
		this.esOperations.deleteIndex(d.esEntityClass);
		this.esOperations.createIndex(d.esEntityClass);
		this.esOperations.putMapping(d.esEntityClass);
		this.esOperations.refresh(d.esEntityClass);

		ElasticsearchPersistentEntity<?> esEntityInfo = this.esOperations.getPersistentEntityFor(d.esEntityClass);

		LOGGER.debug("Inserting data in Index of '{}'. Please wait...", d.getEsEntityClass().getSimpleName());

		// then insert data into it
		try (InputStream is = this.getClass().getResourceAsStream(d.getLocation()); //
				BufferedReader br = new BufferedReader(
						new InputStreamReader(d.gzipped ? new GZIPInputStream(is) : is, StandardCharsets.UTF_8))) {

			List<IndexQuery> indexQueries = br.lines()// each line represent a document to be indexed
        .parallel()// let's speed things up a lil'bit :)
					.peek((l) -> LOGGER.debug("Preparing IndexQuery for line: '{}'", l))//
					.map(line -> SpringEsDataLoader.getIndexQuery(line, esEntityInfo.getIndexName(), esEntityInfo.getIndexType()))//
					.skip(d.nbSkipItems)//
					.limit(d.nbMaxItems)//
					.collect(Collectors.toList());

			this.esOperations.bulkIndex(indexQueries);
			this.esOperations.refresh(d.esEntityClass);

			LOGGER.debug("Insertion successfully done");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Builds an {@link IndexQuery} based on the provided JSON line, representing the data to be inserted into ES.
	 * <p>Data can be extracted using a tool like <code>elasticdump</code> (requires NodeJS):</p>
	 * <p>
	 * <code>$ npx elasticdump --type=data --input=http://localhost:9200/my_index --output=data.json</code>
	 * or
	 * <code>$ npx elasticdump --type=data --input=http://localhost:9200/my_index --output=$ | gzip > data.json.gz</code>
     * </p>
	 * @param jsonLine the data to be inserted, expressed as JSON
	 * @param indexName the name of the target index (if <code>null</code>, will be retrieved from the JSON line)
	 * @param indexType the type of the target index (if <code>null</code>, will be retrieved from the JSON line)
	 * @return the {@link IndexQuery} built from the parsed JSON line
     * @see <a href="https://www.npmjs.com/package/elasticdump">elasticdump</a> (requires NodeJS)
	 */
	private static IndexQuery getIndexQuery(final String jsonLine, final String indexName, final String indexType) {

		JsonNode jsonNode;
		try {
			jsonNode = OBJECT_MAPPER.readTree(jsonLine);
			return new IndexQueryBuilder()//
					.withId(jsonNode.get("_id").textValue())// override the index name and type
					.withIndexName(indexName != null ? indexName : jsonNode.get("_index").textValue())//
					.withType(indexType != null ? indexType : jsonNode.get("_type").textValue())//
					.withSource(jsonNode.get("_source").toString())//
					.build();

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}
}
