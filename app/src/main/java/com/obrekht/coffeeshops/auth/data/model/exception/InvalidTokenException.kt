package com.obrekht.coffeeshops.auth.data.model.exception

class InvalidTokenException : RuntimeException("Token is not valid or has expired")
