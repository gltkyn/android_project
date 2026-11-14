import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

// Görev Tipleri (Aynı veri tipi üzerinden filtrelenir)
enum class TaskType { DAILY, TODO, DONE }

data class TaskEntity(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: TaskType,
    val isCompleted: Boolean = false,
    val rule: String? = null // Örn: "Her gün" kuralı
)

data class DiaryEntity(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val content: String
)

class MainViewModel : ViewModel() {
    // 1. Günlük Girdileri (Diary Ekranı için)
    private val _diaryEntries = MutableStateFlow<List<DiaryEntity>>(emptyList())
    val diaryEntries: StateFlow<List<DiaryEntity>> = _diaryEntries.asStateFlow()

    // 2. Ortak Görev Listesi (Daily Do, Todo ve Done bu listeden beslenir)
    private val _tasks = MutableStateFlow<List<TaskEntity>>(
        listOf(
            TaskEntity(title = "Her gün 30 dk spor yap", type = TaskType.DAILY, rule = "Günlük Tetikleyici"),
            TaskEntity(title = "Rust JNI köprüsünü tasarla", type = TaskType.TODO),
            TaskEntity(title = "Kotlin Pager yapısını kur", type = TaskType.DONE, isCompleted = true)
        )
    )
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()

    // Diary girdisi eklenirken Rust çekirdeğini tetikleme örneği
    fun addDiaryEntry(content: String) {
        if (content.isBlank()) return
    
        // Veriyi Rust çekirdeğine gönderip işlenmiş/filtrelenmiş sonucu alabiliriz
        val rustResponse = try {
            NativeBridge.processInput(content)
        } catch (e: Exception) {
            "Rust Köprü Hatası: ${e.localizedMessage}"
        }
    
        val newEntry = DiaryEntity(content = rustResponse)
        _diaryEntries.value = listOf(newEntry) + _diaryEntries.value
    }

    // Yeni Görev / Kural Ekleme (Daily veya Todo olarak)
    fun addTask(title: String, type: TaskType, rule: String? = null) {
        if (title.isBlank()) return
        val newTask = TaskEntity(title = title, type = type, rule = rule)
        _tasks.value = _tasks.value + newTask
    }

    // Görev Durumunu Değiştirme (Todo <-> Done geçişi)
    fun toggleTaskStatus(taskId: String) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) {
                val newType = if (task.type == TaskType.TODO) TaskType.DONE else TaskType.TODO
                task.copy(type = newType, isCompleted = newType == TaskType.DONE)
            } else task
        }
    }
}
