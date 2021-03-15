import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CsvData(
    @JsonProperty("ID-libelle")
    val libelleId: String?,
    @JsonProperty("Texte")
    val texte: String?,
    @JsonProperty("Libellé à traduire")
    val originalText: String,
    @JsonProperty("Traduction")
    val translatedText: String?
)
