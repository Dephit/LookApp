package com.sergeenko.lookapp

import androidx.paging.DataSource
import com.sergeenko.lookapp.models.Comment
import com.sergeenko.lookapp.models.Look
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CommentDataSourceFactory(
        private val post: Look? = null,
        private val comment: Comment? = null,
        private val repository: Repository,
        private val viewModelScope: CoroutineScope,
        private val errorState: MutableStateFlow<ModelState>
) : MyDataSource<Comment>() {

    private lateinit var lookDataSource: CommentDataSource

    override fun invalidate() {
        lookDataSource.addComment()
    }

    override fun create(): DataSource<Int, Comment> {
        lookDataSource = CommentDataSource(post = post, comment = comment, repository = repository, viewModelScope = viewModelScope, errorState = errorState)
        viewModelScope.launch {
            lookDataSourceFlow.emit(lookDataSource)
        }
        return lookDataSource
    }
}