# Keycloak + Spring Boot — OAuth 2.0 & OpenID Connect (OIDC) Demo

A full end-to-end demonstration of **OAuth 2.0 Authorization Code Flow** and **OpenID Connect (OIDC)** authentication using Keycloak, Spring Cloud Gateway (OAuth2 Client), and a Spring Boot Resource Server (JWT).

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Services & Ports](#services--ports)
- [Getting Started](#getting-started)
  - [1. Start Keycloak Server](#1-start-keycloak-server)
  - [2. Configure Keycloak Realm](#2-configure-keycloak-realm)
  - [3. Run Spring Resource Server](#3-run-spring-resource-server)
  - [4. Run Spring Gateway](#4-run-spring-gateway)
- [Configuration](#configuration)
  - [Spring Gateway](#spring-gateway-configuration)
  - [Spring Resource Server](#spring-resource-server-configuration)
- [API Endpoints](#api-endpoints)
  - [Spring Gateway (port 8080)](#spring-gateway-port-8080)
  - [Spring Resource Server (port 8082)](#spring-resource-server-port-8082)
- [Security Design](#security-design)
- [Role-Based Access Control](#role-based-access-control)
- [Tech Stack](#tech-stack)

---

## Overview

Keycloak is an open-source Identity and Access Management (IAM) server that provides Single Sign-On (SSO) for web applications and RESTful services. This project demonstrates how to integrate Keycloak with a Spring Boot ecosystem using OAuth 2.0 and OIDC.

---

## Architecture

```
Browser / Client
      |
      v
Spring Gateway (OAuth2 Client -- port 8080)
      |  Login via Keycloak (Authorization Code Flow)
      |  Token Relay (forwards Bearer token downstream)
      v
Spring Resource Server (OAuth2 Resource Server -- port 8082)
      |  Validates JWT via Keycloak JWKS endpoint
      v
Keycloak Server (port 9000)
```

The **Gateway** acts as the OAuth2 Client: it redirects unauthenticated users to Keycloak, obtains an access token, and relays it downstream to the Resource Server via the `TokenRelay` filter.

The **Resource Server** validates the JWT against Keycloak's JWKS endpoint and enforces role-based access control using custom authorities extracted from the `resource_access` claim.

---

## Project Structure

```
demo-keycloak/
+-- keycloak-server/
|   +-- docker-compose.yaml              # Keycloak + PostgreSQL stack
+-- spring-gateway/                      # OAuth2 Client + API Gateway
|   +-- src/main/java/com/nsalexamy/spring_gateway/
|       +-- SpringGatewayApplication.java
|       +-- config/
|       |   +-- SecurityConfig.java      # OAuth2 login, CORS
|       |   +-- GatewayConfig.java       # Route definitions + TokenRelay
|       +-- controller/
|       |   +-- UserController.java      # /user/username, /user/profile
|       +-- handler/
|           +-- RequestHandler.java
|           +-- PageSupportHandler.java
|           +-- HandlerContainer.java
+-- spring-resource-server/              # OAuth2 Resource Server (JWT)
|   +-- src/main/java/com/nsalexamy/spring_resource_server/
|       +-- SpringResourceServerApplication.java
|       +-- config/
|       |   +-- SecurityConfig.java                        # JWT validation
|       |   +-- CustomJwtGrantedAuthoritiesConverter.java  # Role extraction
|       +-- controller/
|           +-- SecureController.java    # /secure/* endpoints
+-- README.md
```

---

## Prerequisites

| Tool | Version |
|---|---|
| Java | 21+ |
| Gradle | Wrapper included (`./gradlew`) |
| Docker & Docker Compose | Any recent version |

---

## Services & Ports

| Service | URL | Description |
|---|---|---|
| Keycloak | `http://localhost:9000` | Identity Provider |
| Spring Gateway | `http://localhost:8080` | OAuth2 Client + Reverse Proxy |
| Spring Resource Server | `http://localhost:8082` | Protected REST API |

---

## Getting Started

### 1. Start Keycloak Server

```bash
cd keycloak-server
docker compose up -d
```

Keycloak will be available at `http://localhost:9000`.
Admin credentials: **username** `admin` / **password** `changeit`

> PostgreSQL 16.8 is used as the backing database. Data is persisted in a named Docker volume (`pg_data`).

### 2. Configure Keycloak Realm

Log in to the Keycloak Admin Console (`http://localhost:9000`) and create the following:

1. **Realm**: `nsa2-realm`
2. **Client**: `nsa2-gateway`
   - Client authentication: **enabled**
   - Authorization Code Flow: **enabled**
   - Valid redirect URIs: `http://localhost:8080/login/oauth2/code/nsa2-gateway`
   - Copy the generated **client secret** into `spring-gateway/src/main/resources/application.yaml` under `client-secret`
3. **Client Roles** (on `nsa2-gateway`): `NSA2_USER`, `NSA2_ADMIN`
4. **Users**: create a test user and assign the appropriate client roles

### 3. Run Spring Resource Server

```bash
cd spring-resource-server
./gradlew bootRun
```

The server starts on port **8082**.

### 4. Run Spring Gateway

```bash
cd spring-gateway
./gradlew bootRun
```

The gateway starts on port **8080**. Open `http://localhost:8080` in a browser — you will be redirected to Keycloak for login.

---

## Configuration

### Spring Gateway Configuration

File: `spring-gateway/src/main/resources/application.yaml`

| Property / Env Var | Default | Description |
|---|---|---|
| `client-secret` (in yaml) | *(from Keycloak)* | Client secret for `nsa2-gateway` client |
| `NSA2_OAUTH_ISSUER_URI` | `http://localhost:9000/realms/nsa2-realm` | Keycloak issuer URI |
| `RESOURCE_SERVER_URI` | `http://localhost:8082` | Downstream resource server base URL |

The gateway proxies all `/resource/**` requests to the resource server, strips the `/resource` prefix, and relays the OAuth2 Bearer token automatically via `TokenRelayFilterFunctions.tokenRelay()`.

### Spring Resource Server Configuration

File: `spring-resource-server/src/main/resources/application.yaml`

| Property / Env Var | Default | Description |
|---|---|---|
| `server.port` | `8082` | Server listening port |
| `NSA2_JWT_ISSUER_URI` | `http://localhost:9000/realms/nsa2-realm` | JWT issuer URI for token validation |

JWT public keys are fetched from Keycloak's JWKS endpoint:
`http://localhost:9000/realms/nsa2-realm/protocol/openid-connect/certs`

---

## API Endpoints

### Spring Gateway (port 8080)

All endpoints require the user to be authenticated via Keycloak (OIDC login). `/actuator/**` is publicly accessible.

| Method | Path | Description |
|---|---|---|
| `GET` | `/user/username` | Returns the authenticated user's username |
| `GET` | `/user/profile` | Returns OIDC ID token claims (full user profile) |
| `GET/POST/PUT/DELETE` | `/resource/**` | Proxied and token-relayed to the Resource Server |

### Spring Resource Server (port 8082)

All endpoints require a valid Bearer JWT. `/actuator/**` is publicly accessible.

| Method | Path | Required Role | Description |
|---|---|---|---|
| `GET` | `/secure/hello` | `NSA2_USER` or `NSA2_ADMIN` | Returns a greeting message |
| `GET` | `/secure/admin/hello` | `NSA2_ADMIN` | Admin-only greeting |
| `GET` | `/secure/access_token` | Any authenticated | Returns token value, authorities, and scopes |

---

## Security Design

**Spring Gateway (`SecurityConfig`)**
- All requests require authentication except `/actuator/**`
- OAuth2 Authorization Code login is enabled via `oauth2Login()`
- CORS allows origins from Keycloak (`localhost:9000`) and the gateway (`localhost:8080`)
- CSRF is disabled for API usage

**Spring Resource Server (`SecurityConfig`)**
- Validates JWTs using Keycloak's JWKS endpoint (configured with `jwkSetUri`)
- Uses a custom `JwtAuthenticationConverter` backed by `CustomJwtGrantedAuthoritiesConverter` to extract client roles from the JWT
- Method-level security is enabled via `@EnableMethodSecurity` and enforced with `@PreAuthorize` annotations on controller methods

---

## Role-Based Access Control

Roles are stored in the JWT under the `resource_access` claim:

```json
{
  "resource_access": {
    "nsa2-gateway": {
      "roles": ["NSA2_USER", "NSA2_ADMIN"]
    }
  }
}
```

`CustomJwtGrantedAuthoritiesConverter` reads the `resource_access.nsa2-gateway.roles` array and maps each role to a Spring Security `GrantedAuthority` prefixed with `ROLE_` (e.g., `ROLE_NSA2_USER`, `ROLE_NSA2_ADMIN`), enabling `@PreAuthorize("hasRole('NSA2_ADMIN')")` checks.

---

## Tech Stack

| Component | Technology |
|---|---|
| Identity Provider | Keycloak 26.1.4 |
| Keycloak Database | PostgreSQL 16.8 (Docker) |
| API Gateway | Spring Boot 4.0.6 + Spring Cloud Gateway MVC 2025.1.1 |
| Resource Server | Spring Boot 4.0.6 + Spring Security OAuth2 Resource Server |
| Java Version | Java 21 (Virtual Threads enabled) |
| Build Tool | Gradle (Wrapper) |
| Containerization | Docker Compose |
