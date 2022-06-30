package org.techtown.k_networkhttpurlconnection

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import org.techtown.k_networkhttpurlconnection.databinding.ActivityMainBinding

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

import kotlinx.coroutines.Dispatchers


import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonRequest.setOnClickListener {
            //백그라운드에서 처리하기 위해 디스패처 IO를 사용해서 CoroutineScope생성
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var urlText = binding.editUrl.text.toString()
                    // 주소를 가져와 https로 시작하지않으면 붙여준다.
                    if (!urlText.startsWith("https")) {
                        urlText = "https://#{urlText}"
                    }
                    // URL객체로 변환
                    val url = URL(urlText)
                    val urlConnection = url.openConnection() as HttpURLConnection
                    urlConnection.requestMethod = "GET"
                    if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                        val streamReader = InputStreamReader(urlConnection.inputStream)
                        val buffered = BufferedReader(streamReader)

                        val content = StringBuilder()
                        while (true) {
                            val line = buffered.readLine() ?: break
                            content.append(line)
                        }
                        buffered.close()
                        urlConnection.disconnect()
                        launch(Dispatchers.Main) {
                            binding.textContent.text = content.toString()
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }
    }
}