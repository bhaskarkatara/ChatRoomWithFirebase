package data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
//import kotlin.Result

class RoomRepository(private val firestore: FirebaseFirestore) {

    suspend fun createRoom(name: String): Result<Unit> = try {
        val room = Room(name = name)
//        Log.d(TAG, "createRoom: $name")
        firestore.collection("rooms").add(room).await()
//        Log.d(TAG, "createRoom: ho gya +  $Result.Success(Unit)")
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)

    }


    suspend fun getRooms(): Result<List<Room>> = try {
        val querySnapshot = firestore.collection("rooms").get().await()
        val rooms = querySnapshot.documents.map { document ->
            document.toObject(Room::class.java)!!.copy(id = document.id)
        }
        Result.Success(rooms)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
