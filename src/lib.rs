use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;
use rusqlite::{params, Connection, Result};

// --- Database Module ---
pub struct Database {
    conn: Connection,
}

impl Database {
    pub fn new(db_path: &str) -> Result<Self> {
        let conn = Connection::open(db_path)?;
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

    pub fn save_diary(&self, id: &str, timestamp: i64, content: &str) -> Result<()> {
        self.conn.execute(
            "INSERT INTO diary_entries (id, timestamp, content) VALUES (?1, ?2, ?3)",
            params![id, timestamp, content],
        )?;
        Ok(())
    }
}

// --- JNI Bridge Function ---
#[no_mangle]
pub extern "C" fn Java_com_example_app_NativeBridge_processInput(
    mut env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jstring {
    let input_str: String = match env.get_string(&input) {
        Ok(s) => s.into(),
        Err(_) => String::from("Hata: Girdi okunamadı"),
    };

    let timestamp = chrono::Utc::now().timestamp();
    let processed_output = format!(
        "Rust Çekirdeği İşledi: [{}] (Zaman: {})",
        input_str, timestamp
    );

    let output = env
        .new_string(processed_output)
        .expect("String oluşturulamadı");
    output.into_raw()
}
