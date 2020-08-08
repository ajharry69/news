package com.xently.common.utils.web

enum class AuthScheme(val value: String) {
    BEARER("Bearer"),
    BASIC("Basic"),
    DIGEST("Digest"),
    HOBA("Hoba"),
    MUTUAL("Mutual"),
    NEGOTIATE("Negotiation"),
    OAUTH("OAuth"),
}