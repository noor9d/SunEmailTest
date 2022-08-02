package com.example.sendmailtest

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sendmailtest.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.button.setOnClickListener{
            if (isOnline(this)) {
                Log.d("kaka", "onCreate: online!")
                CoroutineScope(Dispatchers.IO).launch {
                    sendEmailToUserCode("noor.ullah9d@gmail.com", "2")
                }
            } else Log.d("kaka", "onCreate: no internet")
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun sendEmailToUserCode(to: String, review: String) {
        Log.d("kaka", "sendEmailToUserCode: called!")
        try {
            val deviceName = Build.MODEL
            val androidVersion = getAndroidVersion()
            val username = "donotreply-secureapps@mobipixels.com"
            val password = "Secure#1234"

            val props = Properties()
            props["mail.smtp.starttls.enable"] = "true"
            props["mail.smtp.host"] = "smtp.ipage.com"
            props["mail.smtp.socketFactory.port"] = "587"
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.port"] = "587"

            val session = Session.getDefaultInstance(props,
                object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(username, password)
                    }
                })

            // Create a default MimeMessage object.
            val message: Message = MimeMessage(session)

            // Set From: header field of the header.
            message.setFrom(InternetAddress(username))

            // Set To: header field of the header.
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(to)
            )

            // Set Subject: header field
            message.subject = "Digital Compass Review"

            // Create the message part
            val messageBodyPart: BodyPart = MimeBodyPart()

            // Now set the actual message
            messageBodyPart.setText(
                "A new review for Digital Compass!"
            )

            // Create a multipart message
            val multipart: Multipart = MimeMultipart()
            // Set text message part
            multipart.addBodyPart(messageBodyPart)
            // Part two is attachment

            // second attachment
            val messageBodyPart2: BodyPart = MimeBodyPart()
            messageBodyPart2.setText(
                "\nReview: $review\nDevice: $deviceName\nAndroid Version: $androidVersion"
            )
            multipart.addBodyPart(messageBodyPart2)

            // Send the complete message parts
            message.setContent(multipart)
            // Send message
            Transport.send(message)
            Log.d("kaka", "sendEmail: Email sent successfully!")
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

    private fun getAndroidVersion(): String {
        val release = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT
        return "Android SDK: $sdkVersion ($release)"
    }
}

