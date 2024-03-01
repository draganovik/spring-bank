package com.draganovik.tradeservice.models;

import com.draganovik.tradeservice.entities.TradeExchange;

import java.util.List;

public class TradeFallbackResponse {
    private String message;
    private List<TradeExchange> tradeExchangeList;

    public TradeFallbackResponse(String message, List<TradeExchange> tradeExchangeList) {
        this.message = message;
        this.tradeExchangeList = tradeExchangeList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TradeExchange> getTradeExchangeList() {
        return tradeExchangeList;
    }

    public void setTradeExchangeList(List<TradeExchange> tradeExchangeList) {
        this.tradeExchangeList = tradeExchangeList;
    }
}
