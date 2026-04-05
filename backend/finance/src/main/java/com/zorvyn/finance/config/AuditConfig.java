package com.zorvyn.finance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class to enable JPA Auditing.
 * Allows the application to automatically track entity lifecycle events
 * such as creation and modification timestamps.
 */
@Configuration
@EnableJpaAuditing
public class AuditConfig {
}
