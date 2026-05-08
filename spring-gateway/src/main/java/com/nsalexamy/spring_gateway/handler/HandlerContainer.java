package com.nsalexamy.spring_gateway.handler;


import com.nsalexamy.spring_gateway.model.PageSupport;

public interface HandlerContainer {

    <T, R> R handle(T t);

    <T, R> PageSupport<R> handlePagination(T request);

}
