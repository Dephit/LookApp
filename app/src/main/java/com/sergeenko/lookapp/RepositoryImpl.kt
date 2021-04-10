package com.sergeenko.lookapp

import android.util.Log
import com.sergeenko.lookapp.interfaces.Api
import com.sergeenko.lookapp.interfaces.Repository
import com.sergeenko.lookapp.models.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Singleton
import kotlin.random.Random


@Singleton
class RepositoryImpl(val api: Api, private val database: AppDatabase): Repository {

    private var token: String = ""
        get() = if(field == "") getTokenFromDB() else field

    private var user: SocialResponse? = null
        get() = if(field == null) database.socialResponseDao().get() else field

    private fun getTokenFromDB(): String {
        val tok = database.socialResponseDao().get()?.data?.token
        return "${tok?.type} ${tok?.value}"
    }

    override fun getAccessToken(code: String): Flow<GoogleSignInAccessTokenDataClass>{
        return flow {
            emit(
                api.getAccessTokenGoogle(
                    url = "https://www.googleapis.com/oauth2/v4/token",
                    grant_type = "authorization_code",
                    client_id = "513283860171-s3r32tb8o809ihh7nctoinqnm2plic67.apps.googleusercontent.com",
                    client_secret = "2bC9VBkKSAx8WyUQ5vG1IcEq",
                    authCode = code
                )
            )
        }
    }

    override fun getDB(): AppDatabase {
        return database
    }

    override fun getUserFromDb(): SocialResponse? {
        return user
    }

    override fun authByPhone(phone: String): Flow<AuthMessage> {
        return flow {
            val log = api.logIn(phone)
            if(log.status == 200) {
                emit(log)
            }else {
                throw Exception(log.message)
            }
            emit(log)
        }
    }

    override fun logIn(accessToken: String, provider: String): Flow<SocialResponse> {
        return flow {
            val login = api.logIn(provider = provider, accessToken = accessToken)
            if(login.status == 0) {
                saveLogin(login)
                emit(login)
            }else {
                throw Exception(login.message)
            }
        }.flowOn(IO)
    }

    override fun updateUser(login: String, profile: Profile?): Flow<SocialResponse> {
        return flow {
            val log = api.updateUser(
                auth = token,
                username = login,
                gender = profile?.gender,
                height = profile?.height,
                weight = profile?.weight,
                waist = profile?.waist,
                chest = profile?.chest,
                hips = profile?.hips
            )
            if(log.status == 200) {
                saveLogin(log)
                emit(log)
            }else{
                throw try {
                    Exception(log.errors.username[0])
                }catch (e: Exception){
                    Exception(log.message)
                }

            }
        }.flowOn(IO)
    }

    private fun saveLogin(login: SocialResponse) {
        try {
            database.socialResponseDao().insert(login)
        }catch (e: Exception){
            login.data.token = user!!.data.token
            database.socialResponseDao().update(login)
        }
        token = getTokenFromDB()
    }

    override fun logout(): Flow<AuthMessage> {
        return flow {
            val login = api.logOut(token)
            val sr = database.socialResponseDao().get()
            if (sr != null) {
                database.socialResponseDao().delete(sr)
            }
            emit(login)
        }.flowOn(IO)
    }

    override suspend fun getCountryCodes() {
        try {
            val data = api.phones().data
            data.forEach { code ->
                if(code.dialCode.equals("+7"))
                    code.isSelected = true
                try {
                    database.codeDao().insert(code)
                }catch (e: Exception){
                    database.codeDao().update(code)
                }
            }
        }catch (e: Exception){
            Log.e("REPOSITORY_ERRO", "can't get brands, error: ${e.message}")
        }
    }

    override suspend fun getBrands() {
        try {
            val data = api.brands(token).data
            data.forEach { brand ->
                try {
                    database.brandDao().insert(brand)
                }catch (e: Exception){
                    database.brandDao().update(brand)
                }
            }
        }catch (e: Exception){
            Log.e("REPOSITORY_ERRO", "can't get brands, error: ${e.message}")
        }
    }

    override suspend fun getCurrency() {
        try {
            val data = api.currencies(token).data
            data.forEach { currency ->
                try {
                    database.currencyDao().insert(currency)
                }catch (e: Exception){
                    database.currencyDao().update(currency)
                }
            }
        }catch (e: Exception){
            Log.e("REPOSITORY_ERRO", "can't get currencies, error: ${e.message}")
        }
    }

    override fun checkCode(code: String, phone: String): Flow<SocialResponse> {
        return flow {
            val login = api.verify(code = code, phone = phone)
            saveLogin(login)
            emit(login)
        }.flowOn(IO)
    }

    override fun checkUsername(username: String): Flow<CheckUserNameResponse> {
        return flow {
            val log = api.checkUserName(username = username, auth = token)
            if(log.data.username_free) {
                emit(log)
            }else{
                throw Exception()
            }
        }.flowOn(IO)
    }

    override fun getLook(a: Int, i: Int, requestedLoadSize: Int): FakeLook {
        val isPost = Random.nextBoolean()
        return FakeLook(a, i, requestedLoadSize, isPost,
            List(if (isPost) 1 else Random.nextInt(1, 10)) {
                val w = 1080 + Random.nextInt(-100, 100)
                val h = 1920 + Random.nextInt(-100, 100)
                Picasso.get()
                    .load("https://picsum.photos/${w}/${h}")
                    .fetch()
                getImg(w, h, a, i, isPost)
            }
        )

    }

    override fun getImg(w: Int, h: Int, a: Int, i: Int, isPost: Boolean): Img {
        return Img(
            w, h,
            Random.nextLong(5000, 10000000),
            Random.nextLong(5000, 10000000),
            Random.nextLong(5000, 10000000),
            List(4) { Pair(Random.nextInt(10, 100), Random.nextInt(10, 100)) },
            author = "Vasian $a$i",
            text = "Nickname Текст лука будет написан тут будет",
            postText = "Как подобрать одежду на выпускной балл в стиле 60-х",
            isPost = isPost,
            postDate = "2 д. назад"
        )
    }

    override fun loadLooks(i: Int, requestedLoadSize: Int): Flow<List<FakeLook>> {
        return flow {
            if(Random.nextInt(0, 100) > 80){
                throw Exception()
            }
            val list = List(requestedLoadSize) { a -> getLook(a, i, requestedLoadSize)}
            emit(list)
        }
    }

    override fun getLooks(i: Int): Flow<PostResponse> {
        return flow {
            val data = api.posts(token, i)
            data.data.forEach {
                it.images.forEach { img->
                    Picasso.get()
                        .load(img.url)
                        .fetch()
                }
            }
            emit(data)
        }.flowOn(IO)
    }

    override fun getComments(postID: Int?, page: Int, commentId: Int?, url: String?): Flow<CommentResponse> {
        return flow {
            val data = when {
                postID != null -> api.comments(auth = token, postId = postID, page = page)
                commentId != null -> api.commentsBranch(
                    auth = token,
                    commentId = commentId,
                    page = page
                )
                else -> throw Exception()
            }
            emit(data)
        }.flowOn(IO)
    }


    override fun dislike(dislike: Boolean, postID: Int): Flow<Boolean> {
        return flow {
            api.dislike(auth = token, postId = postID, dislike = if (dislike) 1 else 0)
            emit(true)
        }.flowOn(IO)
    }

    override fun like(like: Boolean, postID: Int): Flow<Boolean> {
        return flow {
            api.like(auth = token, postId = postID, like = if (like) 1 else 0)
            emit(true)
        }.flowOn(IO)
    }

    override fun favorite(like: Boolean, post: Look): Flow<Boolean> {
        return flow {
            if(like){
                api.addToFavorite(auth = token, postId = post.id)
            }else{
                api.deleteFromFavorite(auth = token, postId = post.favorite_id)
            }
            emit(true)
        }.flowOn(IO)
    }

    override fun claim(type: String, postId: Int?, commentId: Int?): Flow<AuthMessage> {
        return flow {
            val data = api.claims(auth = token, type = type, postId = postId, commentId = commentId)
            if(!data.ok){
                throw Exception("HTTP 400 Bad Request")
            }
            emit(data)
        }.flowOn(IO)
    }

    override fun addComment(text: String, postId: Int, commentId: Int?): Flow<Comment> {
        return flow {
            val data = api.createComment(token, text, postId, commentId)
            emit(data.data)
        }.flowOn(IO)

    }

    override fun deleteComment(selectedComment: Comment?): Flow<AuthMessage> {
        return flow {
            val data = api.deleteComment(token, selectedComment?.id)
            emit(data)
        }.flowOn(IO)
    }

    override fun createLook(look: Look): Flow<Response?>{
        return flow{
            val client = OkHttpClient().newBuilder()
                .build()
            val mediaType = "application/json".toMediaTypeOrNull()
            val requetBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("title", look.title)
                .addFormDataPart("type_id", "1")
            look.images.forEachIndexed { index, image ->
                requetBodyBuilder.addFormDataPart(
                    "images[$index][file]",
                    image.url,
                    RequestBody.create("application/octet-stream".toMediaTypeOrNull(), File(image.imagePath)
                    )
                )

                image.marks.forEachIndexed { indexMark, mark ->
                    requetBodyBuilder.addFormDataPart(
                        "images[$index][marks][$indexMark]",
                        "{\"coordinate_x\": ${mark.coordinate_x}, \"coordinate_y\":${mark.coordinate_y}, \"brand_id\": ${mark.brand}, \"price\": 5000, \"currency_id\": ${mark.currency}, \"label\": ${mark.label}}"
                    )

                }

            }
            val requestBody = requetBodyBuilder.build()
            val request: Request = Request.Builder()
                .url("https://lookatback.com/api/posts")
                .method("POST", requestBody)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .build()
            val response = client.newCall(request).execute()
            emit(response)
        }
    }

    override fun createPost(look: Look): Flow<Response?>{
        return flow {
            val client = OkHttpClient().newBuilder()
                .build()
            val mediaType: MediaType? = "application/json".toMediaTypeOrNull()

            val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("title", look.title)
                .addFormDataPart("type_id", "2")
                .addFormDataPart(
                    "preview", look.preview,
                    RequestBody.create(
                        "application/octet-stream".toMediaTypeOrNull(), File(look.previewPath)
                    )
                )
            look.body.forEachIndexed { index, postBodyItem ->
                val typeName = "body[${index}][type]"
                val contentName = "body[$index][content]"

                requestBodyBuilder.addFormDataPart(typeName, postBodyItem.type!!)

                when (postBodyItem.type) {
                    "image" -> {
                        requestBodyBuilder.addFormDataPart(
                            contentName,
                            postBodyItem.content as String,
                            RequestBody.create(
                                "application/octet-stream".toMediaTypeOrNull(), File(
                                    postBodyItem.imagePath
                                )
                            )
                        )
                    }
                    "text" -> {
                        requestBodyBuilder.addFormDataPart(
                            contentName,
                            postBodyItem.content as String
                        )
                    }
                    "post" -> {
                        requestBodyBuilder.addFormDataPart(
                            contentName,
                            (postBodyItem.content as Look).id.toString()
                        )
                    }
                }
            }
            val requetBody = requestBodyBuilder.build()
            val request: Request = Request.Builder()
                .url("https://lookatback.com/api/posts")
                .method("POST", requetBody)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .build()
            val response: Response = client.newCall(request).execute()
            emit(response)
        }.flowOn(IO)
    }


}
