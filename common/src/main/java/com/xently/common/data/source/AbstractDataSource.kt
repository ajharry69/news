package com.xently.common.data.source

import androidx.lifecycle.MutableLiveData
import com.xently.common.data.TaskResult
import com.xently.common.data.data
import com.xently.common.data.listData
import com.xently.common.data.models.PagedData

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
abstract class AbstractDataSource<M>(vararg objects: M) {
    protected val TAG = this::class.java.simpleName
    protected val MOCK_DATABASE = mutableSetOf<M>()
    protected val observables = MutableLiveData<List<M>>()

    init {
        MOCK_DATABASE.clear()
        updateObservables(*objects)
    }

    protected fun Collection<M>.updateObservables() {
        MOCK_DATABASE.addAll(this)
        observables.postValue(this.toList())
    }

    protected fun <T : TaskResult<PagedData<M>>> T.updateObservablesFromPagedData() =
        apply { data?.results?.updateObservables() }

    protected fun <T : TaskResult<M>> T.updateObservable() =
        apply { data?.run { updateObservables(this) } }

    protected fun <T : TaskResult<List<M>>> T.updateObservables() =
        apply { listData.updateObservables() }

    protected fun updateObservables(vararg objects: M) {
        objects.toList().updateObservables()
    }

    protected fun deleteAndUpdateObservable(obj: M?) {
        MOCK_DATABASE.run {
            remove(obj)
            updateObservables()
        }
    }

    protected fun cleanUpObservables() {
        MOCK_DATABASE.apply {
            clear()
            updateObservables()
        }
    }
}