package com.woojin.prography_assignment.cohort.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "current-cohort")
public class CohortProperties {

    private Integer generation;
}
