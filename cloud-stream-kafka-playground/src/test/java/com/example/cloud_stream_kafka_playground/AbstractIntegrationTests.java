package com.example.cloud_stream_kafka_playground;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
@SpringBootTest(
		properties = {
				"logging.level.root=ERROR",
				"logging.level.com.example=INFO",
				"spring.cloud.stream.kafka.binder.configuration.auto-offset-reset=earliest"
		}
)
@EmbeddedKafka(
		partitions = 1,
		bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
public abstract class AbstractIntegrationTests {
	@Autowired
	private EmbeddedKafkaBroker embeddedKafka;


}
