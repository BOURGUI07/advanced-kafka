package com.example.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.function.UnaryOperator;

@DirtiesContext
@SpringBootTest(
		properties = {
				"logging.level.root=INFO",
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
	private EmbeddedKafkaBroker broker;

}
