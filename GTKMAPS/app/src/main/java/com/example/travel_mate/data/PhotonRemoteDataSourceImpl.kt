package com.example.travel_mate.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PhotonRemoteDataSourceImpl: PhotonRemoteDataSource {

    /**
     * a client for the request made with Photon for autocomplete
     * or reverse geoCode to find the address of the current location of the user
     * If this is not set the data source results inconsistent or wrong data
     * when searching autocomplete
     *
     * ps. feel free to try it just comment the .client() part from [photonRetrofit] builder
     */
    private var client: OkHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .callTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .addInterceptor(Interceptor.Companion { chain: Interceptor.Chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0"
                )
                .header("Accept-Language", "hu-HU,hu;q=0.8,en-US;q=0.5,en;q=0.3")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        })
        .build()

    val photonRetrofit: ClassRequests.PhotonApi = Retrofit.Builder()
        .baseUrl("https://photon.komoot.io/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ClassRequests.PhotonApi::class.java)

    /** [getStartPlaces]
     *  get the potential start [Place]s [List] that match the given text while typing in the
     *  input field in [com.example.travel_mate.ui.FragmentMain]
     */
    override suspend fun getStartPlaces(query: String): Flow<PhotonResponse> {

        return withContext(Dispatchers.IO) {

            flowOf(photonRetrofit.getAutocomplete(query, "5"))
        }
    }

    /** [getReverseGeoCode]
     * get the [Place] that matches the location of the user's device
     * when using location for searching start place
     */
    override suspend fun getReverseGeoCode(coordinates: Coordinates): Flow<PhotonResponse> {

        return withContext(Dispatchers.IO) {

            flowOf(
                photonRetrofit.getReverseGeoCode(
                    coordinates.getLongitude(),
                    coordinates.getLatitude()
                )
            )
        }
    }
}