package viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatroom.Injection
import com.google.firebase.auth.FirebaseAuth
import data.Message
import data.MessageRepository
import data.Result
import data.Result.*
import data.User
import data.UserRepository
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {

    private val messageRepository: MessageRepository
    private val userRepository: UserRepository

    init {
        messageRepository = MessageRepository(Injection.instance())
        userRepository = UserRepository(
            FirebaseAuth.getInstance(),
            Injection.instance()
        )
        loadCurrentUser()
    }

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val _roomId = MutableLiveData<String>()
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    fun setRoomId(roomId: String) {
        _roomId.value = roomId
        loadMessages()
    }

    fun sendMessage(text: String) {
        Log.d(TAG, "Sending message: $text")
        if (_currentUser.value != null) {
            Log.d(TAG, "oye: $text")
            val message = Message(
                senderFirstName = _currentUser.value!!.firstName,
                senderId = _currentUser.value!!.email,
                text = text
            )
            viewModelScope.launch {
                when (messageRepository.sendMessage(_roomId.value.toString(), message)) {
                    is Success -> Log.d(TAG, "Message sent successfully: msg sent yes")
                    is Error -> {
                        // Handle error
                        Log.e(TAG, "Error sending message: no nhi gya")
                    }
                }
            }
        }else{
            Log.d(TAG, "error in message: error aagyi ")
        }
    }

    fun loadMessages() {
        viewModelScope.launch {
            messageRepository.getChatMessages(_roomId.value.toString())
                .collect { _messages.value = it }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            when (val result = userRepository.getCurrentUser()) {
                is Success -> {
                    Log.d(TAG, "Current user loaded successfully: ${result.data}")
                    _currentUser.value = result.data
                }
                is Error -> {
                    Log.e(TAG, "Error loading current user", result.exception)
                }

            }
        }
    }
}
