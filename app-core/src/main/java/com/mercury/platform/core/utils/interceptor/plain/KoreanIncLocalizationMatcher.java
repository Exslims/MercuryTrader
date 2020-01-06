package com.mercury.platform.core.utils.interceptor.plain;

import org.apache.commons.lang3.StringUtils;

public class KoreanIncLocalizationMatcher extends LocalizationMatcher {
    @Override
    public boolean isSuitableFor(String message) {
        return message.contains("@수신");
    }

    @Override
    public boolean isIncoming() {
        return true;
    }

    @Override
    public String trimString(String message) {
        return StringUtils.substringAfter(message, "@수신 ");
    }
}
