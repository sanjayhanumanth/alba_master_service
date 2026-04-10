package com.alba.master.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
@Getter @Setter
public class JwtProperties {
    private String secret;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration expiration = Duration.ofHours(12);
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration refreshExpiry = Duration.ofHours(24);
}
