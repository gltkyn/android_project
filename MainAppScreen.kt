import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainAppScreen(viewModel: MainViewModel = viewModel()) {
    val pagerState = rememberPagerState(initialPage = 1) { 4 }

    Scaffold(
        bottomBar = { PageIndicatorBar(pagerState = pagerState) }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> DailyDoScreen(viewModel)
                1 -> DiaryScreen(viewModel)
                2 -> TodoScreen(viewModel)
                3 -> DoneScreen(viewModel)
            }
        }
    }
}

// 1. Sol Sayfa: Daily Do (Kurallar ve Rutinler)
@Composable
fun DailyDoScreen(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val dailyTasks = tasks.filter { it.type == TaskType.DAILY }
    var newRuleText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Daily Do (Kurallar ve Rutinler)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = newRuleText,
            onValueChange = { newRuleText = it },
            placeholder = { Text("Yeni kural ekle (örn: Her gün su iç)...") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                viewModel.addTask(newRuleText, TaskType.DAILY, rule = "Otomatik Tetik")
                newRuleText = ""
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Kural Ekle")
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(dailyTasks) { task ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(task.title, style = MaterialTheme.typography.bodyLarge)
                        task.rule?.let { Text("Kural: $it", style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }
    }
}

// 2. Orta Sayfa: Diary (Varsayılan Açılış Ekranı)
@Composable
fun DiaryScreen(viewModel: MainViewModel) {
    val diaryEntries by viewModel.diaryEntries.collectAsState()
    var textInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Diary (Günlük / Ana Ekran)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            placeholder = { Text("Aklındakileri özgürce yaz...") },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )
        
        Button(
            onClick = {
                viewModel.addDiaryEntry(textInput)
                textInput = ""
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Kaydet")
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(diaryEntries) { entry ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(entry.content, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// 3. Sağ 1: Todo (Aktif Görevler)
@Composable
fun TodoScreen(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val todoTasks = tasks.filter { it.type == TaskType.TODO }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Todo (Aktif Görevler)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(todoTasks) { task ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(task.title, style = MaterialTheme.typography.bodyLarge)
                        Button(onClick = { viewModel.toggleTaskStatus(task.id) }) {
                            Text("Tamamla")
                        }
                    }
                }
            }
        }
    }
}

// 4. Sağ 2: Done (Tamamlananlar)
@Composable
fun DoneScreen(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val doneTasks = tasks.filter { it.type == TaskType.DONE }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Done (Tamamlanan İşler)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(doneTasks) { task ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(task.title, style = MaterialTheme.typography.bodyLarge)
                        TextButton(onClick = { viewModel.toggleTaskStatus(task.id) }) {
                            Text("Geri Al")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PageIndicatorBar(pagerState: PagerState) {
    val titles = listOf("Daily Do", "Diary", "Todo", "Done")
    Row(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        titles.forEachIndexed { index, title ->
            val isSelected = pagerState.currentPage == index
            val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            Text(text = " [$title] ", color = color, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
