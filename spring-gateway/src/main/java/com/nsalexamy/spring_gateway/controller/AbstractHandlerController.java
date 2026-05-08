package com.nsalexamy.spring_gateway.controller;

import com.nsalexamy.spring_gateway.handler.HandlerContainer;
import com.nsalexamy.spring_gateway.model.PageSupport;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractHandlerController {

    @Autowired
    private HandlerContainer handlerContainer;

    public <T, R> R handle(T request) {
        return handlerContainer.handle(request);
    }

    public <T, R> PageSupport<R> handlePagination(T request) {
        return handlerContainer.handlePagination(request);
    }

}
