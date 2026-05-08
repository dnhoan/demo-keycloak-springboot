package com.nsalexamy.spring_gateway.handler;

import com.nsalexamy.spring_gateway.model.PageSupport;

public interface PageSupportHandler<T, I> {

    PageSupport<I> handlePagination(T request);

}
