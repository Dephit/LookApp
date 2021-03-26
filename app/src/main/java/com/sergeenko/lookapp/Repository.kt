package com.sergeenko.lookapp

import com.sergeenko.lookapp.models.*
import kotlinx.coroutines.flow.Flow
import okhttp3.Response

interface Repository {

    fun getDB(): AppDatabase
    fun getUserFromDb(): SocialResponse?

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

    fun getLooks(i: Int, requestedLoadSize: Int) : Flow<PostResponse>

    fun getComments(postID: Int? = null, page: Int, commentId: Int? = null, url: String? = null): Flow<CommentResponse>

    fun dislike(dislike: Boolean, postID: Int): Flow<Boolean>
    fun like(like: Boolean, postID: Int): Flow<Boolean>
    fun favorite(like: Boolean, post: Look): Flow<Boolean>
    fun claim(type: String, postId: Int? = null, commentId: Int? = null): Flow<AuthMessage>

    fun addComment(text: String, postId: Int, commentId: Int? = null): Flow<Comment>
    fun deleteComment(selectedComment: Comment?): Flow<AuthMessage>

    fun createPost(look: Look): Flow<Response?>
    fun createLook(look: Look): Flow<Response?>
}
