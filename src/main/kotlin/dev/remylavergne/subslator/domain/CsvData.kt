package dev.remylavergne.subslator.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CsvData(
    @JsonProperty("json_key")
    val jsonKey: String?,
    @JsonProperty("original_text")
    val originalText: String,
    @JsonProperty("original_text_translated")
    val translatedText: String?
)