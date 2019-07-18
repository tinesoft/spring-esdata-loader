package com.github.spring.esdata.loader.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Utility class to interact with Spring.
 *
 * @author tinesoft
 *
 */
public final class SpringUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringUtils.class);

    private SpringUtils(){}

	/**
	 * Search recursively in current and parent applicationContext's for Bean of
	 * Type T
	 *
	 * @param applicationContext
	 *            Spring application context to start searching from
	 * @param aClass
	 *            Class type of bean to search
	 * @param <T>
	 *            Type of bean to search
	 * @return first matched bean found
	 */
	public static <T> T getBeanOfType(ApplicationContext applicationContext, final Class<T> aClass) {
		Map<String, T> beansOfType;
		do {
			beansOfType = applicationContext.getBeansOfType(aClass);
			if (beansOfType != null && beansOfType.size() > 0) {
				return beansOfType.values().stream().findAny().orElse(null);
			}
			applicationContext = applicationContext.getParent();
		} while (applicationContext != null);
		return null;
	}

    /**
     * Retrieve a {@link ElasticsearchTemplate} from the {@link ApplicationContext} and creates a data loader from it.
	 * @param appContext the Spring {@link ApplicationContext}
	 * @return a {@link Consumer} that accepts one or more {@link IndexData} objects and inserts them in the underlying ES Server.
     */
	public static Consumer<IndexData> getDataLoader(final ApplicationContext appContext) {

		if (appContext == null) {
			LOGGER.error(
					"No Spring's ApplicationContext field named 'applicationContext', 'appContext', nor 'context' was found!");
			throw new IllegalStateException("Missing 'ApplicationContext' field in class under test");
		}

		ElasticsearchTemplate esTemplate = SpringUtils.getBeanOfType(appContext, ElasticsearchTemplate.class);

		if (esTemplate == null) {
			LOGGER.error("No Spring's bean of type 'ElasticsearchTemplate' was found!");
			throw new IllegalStateException(
					"Missing bean of type 'ElasticsearchTemplate' in your Spring configuration");
		}

		return new SpringEsDataLoader(esTemplate)::load;
	}

}
