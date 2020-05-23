import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update
import kotlin.random.Random

class TodoDatabaseImpl : TodoDatabase {

    private val map = AtomicReference<Map<String, TodoItem>>(emptyMap())

    init {
        create(TodoItem.Data(text = "Item 1"))
        create(TodoItem.Data(text = "Item 2", isDone = true))
        create(TodoItem.Data(text = "Item 3"))
    }

    override fun get(id: String): TodoItem? = map.value[id]

    override fun create(data: TodoItem.Data): TodoItem {
        val item = TodoItem(id = Random.nextInt().toString(), data = data)
        map.update { it.plus(item.id to item) }

        return item
    }

    override fun save(id: String, data: TodoItem.Data) {
        map.update {
            it.plus(id to requireNotNull(it[id]).copy(data = data))
        }
    }

    override fun delete(id: String) {
        map.update { it.minus(id) }
    }

    override fun getAll(): List<TodoItem> = map.value.values.toList()
}