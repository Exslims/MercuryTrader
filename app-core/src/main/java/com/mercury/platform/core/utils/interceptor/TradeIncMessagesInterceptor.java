package com.mercury.platform.core.utils.interceptor;

import com.mercury.platform.core.utils.interceptor.filter.MessageMatcher;
import com.mercury.platform.shared.MessageParser;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.configration.PlainConfigurationService;
import com.mercury.platform.shared.config.descriptor.NotificationSettingsDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TradeIncMessagesInterceptor extends MessageInterceptor {
    private MessageParser messageParser = new MessageParser();
    private PlainConfigurationService<NotificationSettingsDescriptor> config;
    private List<LocalizationMatcher> clients = new ArrayList<>();

    public TradeIncMessagesInterceptor() {
        this.config = Configuration.get().notificationConfiguration();
        this.clients.add(new EngIncLocalizationMatcher());
        this.clients.add(new RuIncLocalizationMatcher());
        this.clients.add(new ArabicInLocalizationMatcher());
        this.clients.add(new BZIncLocalizationMatcher());
        this.clients.add(new FrenchIncLocalizationMatcher());
        this.clients.add(new GermanIncLocalizationMatcher());
        this.clients.add(new KoreanIncLocalizationMatcher());
    }

    @Override
    protected void process(String message) {
        if (this.config.get().isIncNotificationEnable()) {
            LocalizationMatcher localizationMatcher = this.clients.stream()
                    .filter(matcher -> matcher.isSuitableFor(message))
                    .findAny().orElse(null);
            if (localizationMatcher != null) {
                localizationMatcher.processMessage(message);
            }
        }
    }

    @Override
    protected MessageMatcher match() {
        return message ->
                this.clients.stream()
                        .filter(matcher -> matcher.isSuitableFor(message))
                        .findAny().orElse(null) != null;
    }

    private abstract class LocalizationMatcher {
        public boolean isSuitableFor(String message) {
            return message.contains("Hi, I would like") ||
                    message.contains("Hi, I'd like") || message.contains("I'd like") ||
                    message.contains("안녕하세요, ") || message.contains("구매하고 싶습니다") ||
                    (message.contains("wtb") && message.contains("(stash"));
        }

        public abstract String trimString(String src);

        public NotificationDescriptor getDescriptor(String message) {
            return messageParser.parse(this.trimString(message));
        }

        public void processMessage(String message) {
            NotificationDescriptor notificationDescriptor = this.getDescriptor(message);
            if (notificationDescriptor != null) {
                MercuryStoreCore.newNotificationSubject.onNext(notificationDescriptor);
            }
        }
    }

    private class EngIncLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@From") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@From");
        }
    }

    private class RuIncLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@От кого") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@От кого");
        }
    }

    private class ArabicInLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@จาก") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@จาก");
        }
    }

    private class BZIncLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@De") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@De");
        }
    }

    private class FrenchIncLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@De") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@De");
        }
    }

    private class GermanIncLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@Von") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@Von");
        }
    }
    
    private class KoreanIncLocalizationMatcher extends LocalizationMatcher {
        @Override
        public boolean isSuitableFor(String message) {
            return message.contains("@수신") && super.isSuitableFor(message);
        }

        @Override
        public String trimString(String src) {
            return StringUtils.substringAfter(src, "@수신");
        }
    }

}
