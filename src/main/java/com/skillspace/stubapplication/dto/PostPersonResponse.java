package com.skillspace.stubapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostPersonResponse {
    @JsonProperty("Person1")
    private PostPersonDto person1;
    @JsonProperty("Person2")
    private PostPersonDto person2;
}
