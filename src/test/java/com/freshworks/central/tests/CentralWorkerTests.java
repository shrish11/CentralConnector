package com.freshworks.central.tests;

import com.freshworks.central.worker.CentralWorker;
import com.netflix.conductor.common.metadata.tasks.Task;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import java.util.UUID;

@SpringBootTest
@TestPropertySource(properties = {
		"DEFAULT_PERCENTILE=0.9,0.95",
		"DEFAULT_LATENCY_PERCENTILE=0.9,0.95",
		"taskToDomain=TEST"
})
@Import(CentralWorkerTests.PrometheusTestConfig.class)
class CentralWorkerTests {



	@Autowired
	private PrometheusMeterRegistry prometheusMeterRegistry;

	@Test
	void contextLoads(ApplicationContext applicationContext) {

		CentralWorker centralWorker = applicationContext.getBean(CentralWorker.class);
		Task task = new Task();
        task.setWorkflowInstanceId(UUID.randomUUID().toString());
		centralWorker.work(task);

	}



	@TestConfiguration
	static class PrometheusTestConfig {

		@Bean
		public PrometheusMeterRegistry prometheusMeterRegistry() {
			return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
		}
	}


}
