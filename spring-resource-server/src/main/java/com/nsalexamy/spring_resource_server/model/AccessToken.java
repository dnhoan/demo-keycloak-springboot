package com.nsalexamy.spring_resource_server.model;

public record AccessToken(String principal, String accessToken, String authorities, String scope) {

}
