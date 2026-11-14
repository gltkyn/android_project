package com.example.app

object NativeBridge {
    // Rust kütüphanesini yüklüyoruz (Android build çıktısında .so olarak yer alacak)
    init {
        System.loadLibrary("rust_core")
    }

    // Rust tarafındaki JNI fonksiyonunun imzası
    external fun processInput(input: String): String
}
