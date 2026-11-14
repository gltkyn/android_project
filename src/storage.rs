use rusqlite::{Connection, Result, params};

pub struct Database {
    conn: Connection,
}

impl Database {
    // Veritabanını başlatma ve tabloları oluşturma
    pub fn new(db_path: &str) -> Result<Self> {
        let conn = Connection::open(db_path)?;

        // 1. Task Tablosu (Daily, Todo, Done ortak veri tipi)
        conn.execute(
            "CREATE TABLE IF NOT EXISTS tasks (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                task_type TEXT NOT NULL,
                is_completed INTEGER NOT NULL,
                rule TEXT
            )",
            [],
        )?;

        // 2. Diary Tablosu (Günlük girdileri)
        conn.execute(
            "CREATE TABLE IF NOT EXISTS diary_entries (
                id TEXT PRIMARY KEY,
                timestamp INTEGER NOT NULL,
                content TEXT NOT NULL
            )",
            [],
        )?;

        Ok(Database { conn })
    }

    // Yeni Diary Girdisi Kaydetme
    pub fn save_diary(&self, id: &str, timestamp: i64, content: &str) -> Result<()> {
        self.conn.execute(
            "INSERT INTO diary_entries (id, timestamp, content) VALUES (?1, ?2, ?3)",
            params![id, timestamp, content],
        )?;
        Ok(())
    }

    // Görev Kaydetme veya Güncelleme
    pub fn save_task(
        &self,
        id: &str,
        title: &str,
        task_type: &str,
        is_completed: bool,
        rule: Option<&str>,
    ) -> Result<()> {
        self.conn.execute(
            "INSERT OR REPLACE INTO tasks (id, title, task_type, is_completed, rule) VALUES (?1, ?2, ?3, ?4, ?5)",
            params![id, title, task_type, if is_completed { 1 } else { 0 }, rule],
        )?;
        Ok(())
    }
}
