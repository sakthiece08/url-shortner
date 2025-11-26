package com.teqmonic.urlshortner.model;

import java.io.Serializable;

public record UserDto(Long id, String name) implements Serializable {
}
