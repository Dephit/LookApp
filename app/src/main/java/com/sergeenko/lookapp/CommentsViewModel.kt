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

    private var commentAdapter: CommentsListAdapter? = null
    private var commentToRespond: Comment? = null
    var selectedComment: Comment? = null
    var look: Look? = null

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
                onCommentPress = {
                    viewModelScope.launch {
                        modelState.emit(ModelState.Success(it))
                    }
                },
                onError = { retry() },
                onRespond = {comment, position ->
                    commentToRespond = comment
                    viewModelScope.launch {
                        modelState.emit(ModelState.Success(Pair("@${comment.user.username}", position)))
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
                            commentToRespond = null
                            commentAdapter = null
                            (adapter as CommentsAdapter).clearSelection()
                            newsDataSourceFactory.invalidate()
                        }
            }
        }
    }

}