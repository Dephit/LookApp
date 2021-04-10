package com.sergeenko.lookapp.interfaces

import com.sergeenko.lookapp.models.GoogleSignInAccessTokenDataClass
import com.sergeenko.lookapp.models.*
import retrofit2.http.*


interface Api {

    @FormUrlEncoded
    @POST
    suspend fun getAccessTokenGoogle(
            @Url url: String,
            @Field("grant_type") grant_type: String,
            @Field("client_id") client_id: String,
            @Field("client_secret") client_secret: String,
            @Field("code") authCode: String,
    ): GoogleSignInAccessTokenDataClass

    @FormUrlEncoded
    @POST("auth/{provider}")
    suspend fun logIn(
            @Path("provider") provider: String,
            @Field("token") accessToken: String
    ): SocialResponse

    @POST("login")
    suspend fun logIn(
            @Query("phone") phone: String
    ): AuthMessage

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("profile")
    suspend fun updateUser(
            @Header("Authorization") auth: String,
            @Query("username") username: String,
            @Query("gender") gender: String? = null,
            @Query("height") height: Int? = null,
            @Query("weight") weight: Int? = null,
            @Query("chest") chest: Int? = null,
            @Query("waist") waist: Int? = null,
            @Query("hips") hips: Int? = null,
    ): SocialResponse


    @POST("login/verify")
    suspend fun verify(
            @Query("code") code: String,
            @Query("phone") phone: String
    ): SocialResponse

    @FormUrlEncoded
    @POST("check-username")
    suspend fun checkUserName(
            @Header("Authorization") auth: String,
            @Field("username") username: String
    ): CheckUserNameResponse

    @GET("phones")
    suspend fun phones(): CodeModel

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("brands")
    suspend fun brands(
            @Header("Authorization") auth: String
    ): BrandsResponse

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("currencies")
    suspend fun currencies(
            @Header("Authorization") auth: String
    ): CurrenciesRespone

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("types")
    suspend fun types(
            @Header("Authorization") auth: String
    ): CurrenciesRespone

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("posts")
    suspend fun posts(
            @Header("Authorization") auth: String,
            @Query("page") page: Int,
    ): PostResponse

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("comments")
    suspend fun comments(
            @Header("Authorization") auth: String,
            @Query("page") page: Int,
            @Query("post") postId: Int
    ): CommentResponse

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("comments/{comment}")
    suspend fun commentsBranch(
            @Header("Authorization") auth: String,
            @Path("comment") commentId: Int,
            @Query("page") page: Int,
    ): CommentResponse

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET
    fun getCommentsByUrl(
            @Header("Authorization") auth: String,
            @Query("post") postId: Int,
            @Url url: String?): CommentResponse

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("logout")
    suspend fun logOut(
            @Header("Authorization") auth: String
    ): AuthMessage

    @FormUrlEncoded
    @PATCH("rating/post/{post}")
    suspend fun dislike(
            @Header("Authorization") auth: String,
            @Path("post") postId: Int,
            @Field("dislike") dislike: Int
    ): AuthMessage

    @FormUrlEncoded
    @POST("rating/post/{post}")
    suspend fun like(
            @Header("Authorization") auth: String,
            @Path("post") postId: Int,
            @Field("like") like: Int
    ): AuthMessage

    @DELETE("favorites/{favorite}")
    suspend fun deleteFromFavorite(
            @Header("Authorization") auth: String,
            @Path("favorite") postId: Int,
    ): AuthMessage

    @FormUrlEncoded
    @POST("favorites")
    suspend fun addToFavorite(
            @Header("Authorization") auth: String,
            @Field("post_id") postId: Int
    ): AddToFavMessage


    @FormUrlEncoded
    @POST("claims")
    suspend fun claims(
            @Header("Authorization") auth: String,
            @Field("post_id") postId: Int? = null,
            @Field("comment_id") commentId: Int? = null,
            @Field("type") type: String
    ): AuthMessage

    @FormUrlEncoded
    @POST("comments")
    suspend  fun createComment(
            @Header("Authorization") auth: String,
            @Field("text") text: String,
            @Field("post_id") postId: Int,
            @Field("comment_id") commentId: Int?): CommentResponse2

    @DELETE("comments/{comment}")
    suspend  fun deleteComment(
            @Header("Authorization") auth: String,
            @Path("comment") commentId: Int?): AuthMessage


}
