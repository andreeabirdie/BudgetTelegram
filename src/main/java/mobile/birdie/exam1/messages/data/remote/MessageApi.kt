package mobile.birdie.exam1.messages.data.remote

import mobile.birdie.exam1.core.Api
import mobile.birdie.exam1.messages.data.Message
import retrofit2.http.*

object MessageApi{
    interface Service {
        @GET("/message")
        suspend fun find(): List<Message>

        @Headers("Content-Type: application/json")
        @PUT("/message/{id}")
        suspend fun readMessage(@Path("id") id: Int, @Body msg: Message)
    }

    val service: Service = Api.retrofit.create(Service::class.java)
}