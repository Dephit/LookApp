package com.sergeenko.lookapp

import com.sergeenko.lookapp.models.*
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getDB(): AppDatabase

    fun authByPhone(phone: String): Flow<AuthMessage>
    suspend fun getCountryCodes()
    suspend fun getBrands()
    suspend fun getCurrency()

    fun checkCode(code: String, phone: String): Flow<SocialResponse>

    fun checkUsername(username: String): Flow<CheckUserNameResponse>

    fun updateUser(login: String, profile: Profile?): Flow<SocialResponse>
    fun logIn(accessToken: String, provider: String): Flow<SocialResponse>
    fun logout(): Flow<AuthMessage>

    fun getAccessToken(code: String): Flow<GoogleSignInAccessTokenDataClass>
    fun getLook(a: Int, i: Int, requestedLoadSize: Int): FakeLook
    fun getImg(w: Int, h: Int, a: Int, i: Int, isPost: Boolean): Img

    fun loadLooks(i: Int, requestedLoadSize: Int) : Flow<List<FakeLook>>

    fun getLooks(i: Int, requestedLoadSize: Int) : Flow<List<Look>>

    fun getComments(postID: Int, key: Int, requestedLoadSize: Int): Flow<List<Comment>>

    fun dislike(dislike: Boolean, postID: Int): Flow<Boolean>
    fun like(like: Boolean, postID: Int): Flow<Boolean>
    fun favorite(like: Boolean, post: Look): Flow<Boolean>
    fun addComment(text: String, postId: Int, commentId: Int? = null): Flow<Comment>
}
