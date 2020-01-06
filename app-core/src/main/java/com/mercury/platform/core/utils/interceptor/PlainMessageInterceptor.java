package com.mercury.platform.core.utils.interceptor;


import com.mercury.platform.core.utils.interceptor.filter.MessageMatcher;
import com.mercury.platform.core.utils.interceptor.plain.*;
import com.mercury.platform.shared.entity.message.PlainMessageDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;

import java.util.ArrayList;
import java.util.List;

public class PlainMessageInterceptor extends MessageInterceptor {
    private List<LocalizationMatcher> clients = new ArrayList<>();

    public PlainMessageInterceptor() {
        this.clients.add(new EngIncLocalizationMatcher());
        this.clients.add(new EngOutLocalizationMatcher());
        this.clients.add(new RuIncLocalizationMatcher());
        this.clients.add(new RuOutLocalizationMatcher());
        this.clients.add(new ArabicInLocalizationMatcher());
        this.clients.add(new ArabicOutLocalizationMatcher());
        this.clients.add(new BZIncLocalizationMatcher());
        this.clients.add(new BZOutLocalizationMatcher());
        this.clients.add(new FrenchIncLocalizationMatcher());
        this.clients.add(new FrenchOutLocalizationMatcher());
        this.clients.add(new GermanIncLocalizationMatcher());
        this.clients.add(new GermanOutLocalizationMatcher());
        this.clients.add(new KoreanIncLocalizationMatcher());
        this.clients.add(new KoreanOutLocalizationMatcher());
    }

    @Override
    protected void process(String message) {
        LocalizationMatcher localizationMatcher = this.clients.stream()
                .filter(matcher -> matcher.isSuitableFor(message))
                .findAny().orElse(null);
        if (localizationMatcher != null) {
            PlainMessageDescriptor plainMessage = localizationMatcher.getPlainMessage(message);
            if (plainMessage != null) {
                MercuryStoreCore.plainMessageSubject.onNext(plainMessage);
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
}
