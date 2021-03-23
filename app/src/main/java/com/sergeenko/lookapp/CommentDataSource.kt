package com.sergeenko.lookapp

import com.sergeenko.lookapp.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class CommentDataSource(
        private val post: Look? = null,
        private val comment: Comment? = null,
        private val repository: Repository,
        private val viewModelScope: CoroutineScope,
        private val errorState: MutableStateFlow<ModelState>
) : MyPageKeyedDataSource<Comment>(errorState) {

    fun addComment() {
        invalidate()
    }

    var links: Links? = null
    var lastID: CommentResponse? = null
    var last: Map<Int, CommentResponse>? = mapOf()


    override fun load(key: Int, requestedLoadSize: Int, onDone: (List<Comment>) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            if(lastID != null && lastID!!.data.size  < requestedLoadSize){
                updateState(ModelState.Success(null))
                onDone(lastID!!.data)
            }else {
                val flow = repository.getComments(postID = post?.id, page = key, commentId = comment?.id, url = links?.next)
                flow.onStart {
                }.catch {
                    //updateState(ModelState.Error(null))
                    //onError()
                }.collect {
                    val list = it.data.toMutableList()
                    if (it.links.last != links?.prev) {
                        if (links == null && post != null) {
                            val com = Comment()
                            com.isPost = true
                            com.user = post.user ?: Data()
                            com.created = post.created ?: ""
                            com.text = post.title ?: ""
                            list.add(0, com)
                        }
                        //  lastID = it
                        links = it.links
                        updateState(ModelState.Success(null))
                        onDone(list)
                    }
                }
            }
        }
    }
}


