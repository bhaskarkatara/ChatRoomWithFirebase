package data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
//import kotlin.Result

class MessageRepository(private val firestore: FirebaseFirestore) {

    suspend fun sendMessage(roomId: String, message: Message): Result<Unit> = try {
        Log.d(TAG, "Sending message to room: $roomId, message: $message")
        firestore.collection("rooms").document(roomId)
            .collection("messages").add(message).await()
        Log.d(TAG, "Message sent successfully")
        Result.Success(Unit)

    } catch (e: Exception) {
        Result.Error(e)
    }

    fun getChatMessages(roomId: String): Flow<List<Message>> = callbackFlow {
        val subscription = firestore.collection("rooms").document(roomId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    trySend(it.documents.map { doc ->
                        doc.toObject(Message::class.java)!!.copy()
                    }).isSuccess
                }
            }

        awaitClose { subscription.remove() }
    }
}
