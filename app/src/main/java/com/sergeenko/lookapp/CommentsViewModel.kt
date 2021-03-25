package com.sergeenko.lookapp

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sergeenko.lookapp.models.Comment
import com.sergeenko.lookapp.models.Look
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentsViewModel  @ViewModelInject constructor(
        private val repository: Repository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : PaggingListViewModel<Comment>(repository, savedStateHandle) {

    private var commentToRespond: Comment? = null
    var selectedComment: Comment? = null
    var look: Look? = null
    var userText = ""

    fun set(look: Look){
        this.look = look
        adapter = createAdapter()
        val config = PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setInitialLoadSizeHint(pageSize)
                .setEnablePlaceholders(false)
                .build()
        lookList = LivePagedListBuilder(newsDataSourceFactory, config).build().asFlow()
    }

    override fun createAdapter(): MyBaseAdapter<Comment> {
        return CommentsAdapter(
                repository = repository,
                viewModelScope = viewModelScope,
                onCommentSelected = {com->
                    viewModelScope.launch {
                        selectedComment = com
                        modelState.emit(ModelState.Success(com.id))
                    }
                },
                onCommentPress = {com->
                    viewModelScope.launch {
                        selectedComment = com
                        modelState.emit(ModelState.Success(com))
                    }
                },
                onError = { retry() },
                onRespond = {comment, position ->
                    commentToRespond = comment
                    viewModelScope.launch {
                        userText = "@${comment.user.username}"
                        modelState.emit(ModelState.Success(Pair(userText, position)))
                    }
                }
        )
    }

    override fun getDataSourceFactory(): MyDataSource<Comment> {
        return CommentDataSourceFactory(post = look!!, repository = repository, viewModelScope = viewModelScope, errorState = errorState)
    }

    @ExperimentalCoroutinesApi
    fun addComment(toString: String) {
        if (modelState.value !is ModelState.Loading) {
            viewModelScope.launch(IO) {
                repository.addComment(text = toString, postId = look!!.id, commentId = commentToRespond?.id)
                        .onStart { modelState.emit(ModelState.Loading) }
                        .catch { modelState.emit(ModelState.Error(it.message)) }
                        .collect {
                            modelState.emit(ModelState.Success(null))
                            withContext(Main){
                                clearSelection()
                                invalidate()
                            }
                        }
            }
        }
    }

    private fun invalidate() {
        newsDataSourceFactory.invalidate()
    }


    fun clearSelection() {
        commentToRespond = null
        (adapter as CommentsAdapter).clearSelection()
        userText = ""
    }

    suspend fun isMyAnswer(): Boolean {
        if(repository.getUserFromDb()?.data?.id == selectedComment?.parent?.user?.id){
            return true
        }
        return false
    }

    fun claim(string: String){
        viewModelScope.launch {
            repository.claim(type = string, commentId = selectedComment?.id)
                    .onStart { modelState.emit(ModelState.Loading) }
                    .catch { modelState.emit(ModelState.Error(it.message)) }
                    .collect {
                        modelState.emit(ModelState.Success(null))
                    }
        }
    }

    fun deleteComment(){
        viewModelScope.launch {
            repository.deleteComment(selectedComment)
                    .onStart { modelState.emit(ModelState.Loading) }
                    //.catch { modelState.emit(ModelState.Error(it.message)) }
                    .collect {
                        withContext(Main){
                            (adapter as CommentsAdapter).deleteComment()
                        }
                        invalidate()
                        modelState.emit(ModelState.Success(null))
                    }
        }
    }

    suspend fun isMyPost(): Boolean {
        if(repository.getUserFromDb()?.data?.id == selectedComment?.user?.id){
            return true
        }
        return false
    }

}