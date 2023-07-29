package com.demo.job;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.cluster.leader.event.LeaderEventPublisher;
import org.springframework.cloud.cluster.leader.event.LeaderEventPublisherConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration for generic leader election components.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
@ConditionalOnClass(LeaderEventPublisher.class)
@ConditionalOnProperty(value = { "spring.cloud.cluster.leader.enabled" }, matchIfMissing = true)
@Import(LeaderEventPublisherConfiguration.class)
public class LeaderAutoConfiguration {
}