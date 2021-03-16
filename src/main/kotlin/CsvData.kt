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

sealed class CsvState {
    sealed class List : CsvState() {
        object NoData : List()
        object DataAvailable : List()
        object MultipleData : List()
    }

    sealed class State : CsvState() {
        object CorruptedKey : State()
        object MissingTranslation : State()
        object AlreadyTranslated : State()
        object CanBeTranslate : State()
    }
}

fun List<CsvData>.state(): CsvState.List = when {
    this.isEmpty() -> CsvState.List.NoData
    this.size == 1 -> CsvState.List.DataAvailable
    this.size > 1 -> CsvState.List.MultipleData
    else -> throw Exception("Impossible case")
}

fun CsvData.state(toTranslate: String): CsvState.State = when {
    this.originalText != toTranslate -> CsvState.State.CorruptedKey
    this.translatedText == null -> CsvState.State.MissingTranslation
    this.translatedText == toTranslate -> CsvState.State.AlreadyTranslated
    else -> CsvState.State.CanBeTranslate
}
