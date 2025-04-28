package viihcerq.com.github.crypto.service

import retrofit2.Response
import retrofit2.http.GET
import viihcerq.com.github.crypto.model.TickerResponse

interface MercadoBitcoinService {

    @GET("api/BTC/ticker/")
    suspend fun getTicker(): Response<TickerResponse>
}