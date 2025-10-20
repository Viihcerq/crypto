package viihcerq.com.github.crypto.model

data class TickerResponse(
    val ticker: Ticker
)

data class Ticker(
    val high: String,
    val low: String,
    val vol: String,
    val last: String,
    val buy: String,
    val sell: String,
    val date: Long
)