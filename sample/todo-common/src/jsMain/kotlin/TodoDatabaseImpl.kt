import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.setValue
import kotlin.random.Random

class TodoDatabaseImpl : TodoDatabase {

    private var map by atomic<Map<String, TodoItem>>(emptyMap())

    init {
        create(TodoItem.Data(text = "Item 1"))
        create(TodoItem.Data(text = "Item 2", isDone = true))
        create(TodoItem.Data(text = "Item 3"))
    }

    override fun get(id: String): TodoItem? = map[id]

    override fun create(data: TodoItem.Data): TodoItem {
        val item = TodoItem(id = Random.nextInt().toString(), data = data)
        this.map += item.id to item

        return item
    }

    override fun save(id: String, data: TodoItem.Data) {
        this.map += id to requireNotNull(map[id]).copy(data = data)
    }

    override fun delete(id: String) {
        this.map -= id
    }

    override fun getAll(): List<TodoItem> = map.values.toList()
}
