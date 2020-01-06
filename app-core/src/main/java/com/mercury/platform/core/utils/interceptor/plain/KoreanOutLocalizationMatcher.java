package com.mercury.platform.core.utils.interceptor.plain;

import org.apache.commons.lang3.StringUtils;

public class KoreanOutLocalizationMatcher extends LocalizationMatcher {
    @Override
    public boolean isSuitableFor(String message) {
        return message.contains("@발신");
    }

    @Override
    public boolean isIncoming() {
        return false;
    }

    @Override
    public String trimString(String src) {
        return StringUtils.substringAfter(src, "@발신 ");
    }

}