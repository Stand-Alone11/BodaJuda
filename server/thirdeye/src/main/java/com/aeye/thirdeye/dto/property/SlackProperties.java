package com.aeye.thirdeye.dto.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties("notification.slack")
@Primary
public class SlackProperties {

    @Value("${notification.slack.enabled}")
    private boolean enabled;

    @Value("${notification.slack.webhook.url}")
    private String webhookUrl;

    @Value("${notification.slack.channel}")
    private String channel;

    @Value("${notification.slack.botName}")
    private String botName;

    @Value("${notification.slack.icon.emoji}")
    private String iconEmoji;

    @Value("${notification.slack.icon.url}")
    private String iconUrl;

}