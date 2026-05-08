package com.nsalexamy.spring_gateway.handler;

public interface RequestHandler<T, R> {

    R handle(T t);
}
