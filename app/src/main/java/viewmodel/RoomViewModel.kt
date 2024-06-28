package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatroom.Injection
import data.Result
import data.Room
import data.RoomRepository
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {

    private val roomRepository: RoomRepository
    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms

    init {
        roomRepository = RoomRepository(Injection.instance())
        loadRooms() // Load rooms initially when ViewModel is initialized
    }

    fun createRoom(name: String) {
        viewModelScope.launch {
            roomRepository.createRoom(name)
            // After creating room, reload rooms list
            loadRooms()
        }
    }

    private fun loadRooms() {
        viewModelScope.launch {
            when (val result = roomRepository.getRooms()) {
                is Result.Success -> _rooms.value = result.data
                is Result.Error -> {
                    // Handle error if needed
                }
            }
        }
    }
}
