use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

// Örnek JNI Fonksiyonu: Diary girdisini kalıcı olarak veritabanına yaz
#[no_mangle]
pub extern "C" fn Java_com_example_app_NativeBridge_saveDiaryNative(
    mut env: JNIEnv,
    _class: JClass,
    db_path_jstr: JString,
    id_jstr: JString,
    content_jstr: JString,
) -> jstring {
    let db_path: String = env.get_string(&db_path_jstr).unwrap().into();
    let id: String = env.get_string(&id_jstr).unwrap().into();
    let content: String = env.get_string(&content_jstr).unwrap().into();
    let timestamp = chrono::Utc::now().timestamp(); // Zaman damgası

    let response = match Database::new(&db_path) {
        Ok(db) => match db.save_diary(&id, timestamp, &content) {
            Ok(_) => "SUCCESS".to_string(),
            Err(e) => format!("DB_ERROR: {}", e),
        },
        Err(e) => format!("INIT_ERROR: {}", e),
    };

    let output = env.new_string(response).unwrap();
    output.into_raw()
}
