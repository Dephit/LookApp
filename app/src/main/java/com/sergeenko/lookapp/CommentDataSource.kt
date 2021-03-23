package com.sergeenko.lookapp

import android.util.Log
import com.sergeenko.lookapp.models.Comment
import com.sergeenko.lookapp.models.Look
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class CommentDataSource(
        private val post: Look,
        private val repository: Repository,
        private val viewModelScope: CoroutineScope,
        private val errorState: MutableStateFlow<ModelState>
) : MyPageKeyedDataSource<Comment>(errorState) {

    fun addComment() {
        lastID = null
        invalidate()
    }

    var lastID: Int? = null

    override fun load(key: Int, requestedLoadSize: Int, onDone: (List<Comment>) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            repository.getComments(post.id, key, requestedLoadSize)
                    .onStart {}
                    .catch {
                        updateState(ModelState.Error(null))
                        onError()
                    }
                    .collect {
                        val list = it.toMutableList()
                        if (it.last().id != lastID) {
                            if (lastID == null) {
                                val com = Comment()
                                com.isPost = true
                                com.user = post.user
                                com.created = post.created
                                com.text = post.title
                                list.add(0, com)
                            }
                            lastID = it.last().id
                            updateState(ModelState.Success(null))
                            onDone(list)
                        }
                    }
        }
    }
}