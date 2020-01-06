package com.mercury.platform.shared;

import com.mercury.platform.shared.entity.message.CurrencyTradeNotificationDescriptor;
import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Константин on 11.01.2017.
 */
public class NotificationDescriptorParserTest {
    private MessageParser parser;
    @Before
    public void before(){
        parser = new MessageParser();
    }

    @Test
    public void testPoeTrade()  throws Exception{
        ItemTradeNotificationDescriptor message1 = (ItemTradeNotificationDescriptor) parser.parse("@From <TEST> Pubesmannen: Hi, I would like to buy your Bronn's Lithe Cutthroat's Garb listed for 1 alteration in Hardcore Legacy (stash tab \"Maps\"; position: left 5, top 6)");
        assertEquals(message1.getWhisperNickname(),"Pubesmannen");
        assertEquals(message1.getItemName(),"Bronn's Lithe Cutthroat's Garb");
        assertEquals(message1.getCurrency(),"alteration");
        assertEquals(message1.getCurCount(),new Double(1));
        assertEquals(message1.getOffer(),"");

        ItemTradeNotificationDescriptor message2 = (ItemTradeNotificationDescriptor) parser.parse("@From <TEST> Pubesmannen: Hi, I would like to buy your Bronn's Lithe Cutthroat's Garb listed for 15 exalt in Legacy (stash tab \"Maps\"; position: left 5, top 6) offer 32");
        assertEquals(message2.getWhisperNickname(),"Pubesmannen");
        assertEquals(message2.getItemName(),"Bronn's Lithe Cutthroat's Garb");
        assertEquals(message2.getCurrency(),"exalt");
        assertEquals(message2.getCurCount(),new Double(15));

        assertEquals(message2.getOffer()," offer 32");
    }
    @Test
    public void testPoeTradeNoBuyout()  throws Exception{
        ItemTradeNotificationDescriptor message1 = (ItemTradeNotificationDescriptor) parser.parse("@From Pubesmannen: Hi, I would like to buy your level 1 4% Reduced Mana Support in Hardcore Legacy (stash tab \"qgems\"; position: left 12, top 4)");
        assertEquals(message1.getWhisperNickname(),"Pubesmannen");
        assertEquals(message1.getItemName(),"level 1 4% Reduced Mana Support");
        assertEquals(message1.getCurCount(),new Double(0d));
        assertEquals(message1.getCurrency(),"???");
        assertEquals(message1.getOffer(),"");
        assertEquals(message1.getLeague(),"Hardcore Legacy");

        ItemTradeNotificationDescriptor message2 = (ItemTradeNotificationDescriptor) parser.parse("@From Pubesmannen: Hi, I would like to buy your level 1 4% Reduced Mana Support in Hardcore (stash tab \"qgems\"; position: left 12, top 4)");
        assertEquals(message2.getWhisperNickname(),"Pubesmannen");
        assertEquals(message2.getItemName(),"level 1 4% Reduced Mana Support");
        assertEquals(message2.getCurCount(),new Double(0d));
        assertEquals(message2.getCurrency(),"???");
        assertEquals(message2.getOffer(),"");
        assertEquals(message2.getLeague(),"Hardcore");
    }

    @Test
    public void testPoeCurrency()  throws Exception{
        CurrencyTradeNotificationDescriptor message1 = (CurrencyTradeNotificationDescriptor) parser.parse("@From tradeeer: Hi, I'd like to buy your 366 chaos for my 5 exalted in Legacy.");
        assertEquals(message1.getWhisperNickname(),"tradeeer");
        assertEquals(message1.getCurrForSaleCount(),new Double(366));
        assertEquals(message1.getCurrForSaleTitle(),"chaos");
        assertEquals(message1.getCurrency(),"exalted");
        assertEquals(message1.getCurCount(),new Double(5));
        assertEquals(message1.getOffer(),"");

        CurrencyTradeNotificationDescriptor message2 = (CurrencyTradeNotificationDescriptor) parser.parse("@From <qwe> tradeeer: Hi, I'd like to buy your 366 chaos for my 5 exalted in Legacy. 123");
        assertEquals(message2.getWhisperNickname(),"tradeeer");
        assertEquals(message2.getCurrForSaleCount(),new Double(366));
        assertEquals(message2.getCurrForSaleTitle(),"chaos");
        assertEquals(message2.getCurrency(),"exalted");
        assertEquals(message2.getCurCount(),new Double(5));
        assertEquals(message2.getOffer(),"123");
    }
    
    @Test
    public void testKor() throws Exception{
    	ItemTradeNotificationDescriptor message1 = (ItemTradeNotificationDescriptor) parser.parse("@수신 테스트: 안녕하세요, 챌린지(보관함 탭 \"~price 3 chaos\", 위치: 왼쪽 1, 상단 1)에 3 chaos(으)로 올려놓은 역병 걸린 묘지 지도(Graveyard Map)(T8)을(를) 구매하고 싶습니다");
    	assertEquals(message1.getWhisperNickname(),"테스트");
        assertEquals(message1.getItemName(),"역병 걸린 묘지 지도(Graveyard Map)(T8)");
        assertEquals(message1.getCurrency(),"chaos");
        assertEquals(message1.getCurCount(),new Double(3));
        assertEquals(message1.getOffer(),"");
    }

}