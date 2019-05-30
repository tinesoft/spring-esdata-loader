package com.github.spring.esdata.loader.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class SpringEsDataLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringEsDataLoader.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final ElasticsearchTemplate esTemplate;

	/**.
	 * Data loader that use Spring's {@link ElasticsearchTemplate} to load data into Elasticsearch
	 * @param esTemplate the {@link ElasticsearchTemplate}
	 */
	@Autowired
	public SpringEsDataLoader(final ElasticsearchTemplate esTemplate) {
		this.esTemplate = esTemplate;
	}

	public <T> void load(final IndexData<T> d) {

		// first recreate the index
		LOGGER.info("Recreating Index for '{}'...", d.getEsEntityClass().getSimpleName());
		esTemplate.deleteIndex(d.esEntityClass);
		esTemplate.createIndex(d.esEntityClass);
		esTemplate.putMapping(d.esEntityClass);
		esTemplate.refresh(d.esEntityClass);

		ElasticsearchPersistentEntity<?> esEntityInfo = esTemplate.getPersistentEntityFor(d.esEntityClass);

		LOGGER.debug("Inserting data in Index of '{}'. Please wait...", d.getEsEntityClass().getSimpleName());

		// then insert data into it
		try (InputStream is = this.getClass().getResourceAsStream(d.getLocation()); //
				BufferedReader br = new BufferedReader(
						new InputStreamReader(d.gzipped ? new GZIPInputStream(is) : is, StandardCharsets.UTF_8));) {

			List<IndexQuery> indexQueries = br.lines()// each line represent a document to be indexed
					.peek((l) -> LOGGER.debug("Preparing IndexQuery for line: '{}'", l))//
					.map(line -> this.getIndexQuery(line, esEntityInfo.getIndexName(), esEntityInfo.getIndexType()))//
					.skip(d.nbSkipItems)//
					.limit(d.nbMaxItems)//
					.collect(Collectors.toList());

			esTemplate.bulkIndex(indexQueries);
			esTemplate.refresh(d.esEntityClass);

			LOGGER.debug("Insertion successfully done");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Builds an {@link IndexQuery} based on the provided JSON (a line extracted using 'elasticdump' tool for example) <br>
	 * Data can be extracted using:<br>
	 * <br>
	 * <code>$ npx elasticdump --type=data --input=http://localhost:9200/my_index --output=data.json</code>
	 * or
	 * <code>$ npx elasticdump --type=data --input=http://localhost:9200/my_index --output=$ | gzip > data.json.gz</code>
	 * @param jsonLine
	 * @param indexName
	 * @param indexType
	 * @return the {@link IndexQuery} built from the parsed json line
	 */
	private IndexQuery getIndexQuery(final String jsonLine, final String indexName, final String indexType) {

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
