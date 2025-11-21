package com.uvg.rachapp
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.uvg.rachapp.ui.navigation.AppNavigation
import com.uvg.rachapp.ui.navigation.AuthNavHost
import com.uvg.rachapp.ui.theme.RachAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uvg.rachapp.data.repository.NoteRepository
import com.uvg.rachapp.repository.AuthRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var firestoreRepository: com.uvg.rachapp.data.repository.FirestoreRepository

    @Inject
    lateinit var noteRepository: NoteRepository

    @Inject
    lateinit var authRepository: AuthRepository

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth


        setContent {
            RachAppTheme {
                var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }


                LaunchedEffect(Unit) {
                    auth.addAuthStateListener { firebaseAuth ->
                        isLoggedIn = firebaseAuth.currentUser != null
                    }
                }

                val authNavController = rememberNavController()

                if (isLoggedIn) {
                    AppNavigation(
                        modifier = Modifier.fillMaxSize(),
                        onLogout = {

                        }
                    )
                } else {
                    AuthNavHost(
                        navController = authNavController,
                        modifier = Modifier.fillMaxSize(),
                        onLoginSuccess = {

                        }
                    )
                }
            }
        }

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        auth.addAuthStateListener { firebaseAuth ->
            lifecycleScope.launch {
                val currentUid = firebaseAuth.currentUser?.uid
                val lastUid = prefs.getString("last_user_uid", null)

                if (currentUid != null) {

                    android.util.Log.d("DEBUG-FIRE", "Firebase projectId = ${com.google.firebase.FirebaseApp.getInstance().options.projectId}")
                    android.util.Log.d("DEBUG-FIRE", "Firebase applicationId = ${com.google.firebase.FirebaseApp.getInstance().options.applicationId}")


                    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null) {
                        val colRef = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("users").document(uid).collection("notes")
                        android.util.Log.d("DEBUG-FIRE", "Querying collection path = ${colRef.path}")
                        colRef.get()
                            .addOnSuccessListener { snaps ->
                                android.util.Log.d("DEBUG-FIRE", "Raw snapshot size = ${snaps.size()}")
                                for (doc in snaps.documents) {
                                    android.util.Log.d("DEBUG-FIRE", "DOC ID=${doc.id} DATA=${doc.data}")
                                    val ts = doc.get("timestamp")
                                    android.util.Log.d("DEBUG-FIRE", "DOC ${doc.id} timestamp raw type = ${ts?.javaClass?.name} value = $ts")
                                }
                            }
                            .addOnFailureListener { e ->
                                android.util.Log.e("DEBUG-FIRE", "Query failed", e)
                            }
                    }


                    if (lastUid == null || lastUid != currentUid) {
                        launch(Dispatchers.IO) {
                            try {
                                noteRepository.clearAllNotes()
                            } catch (_: Exception) {}
                            prefs.edit().putString("last_user_uid", currentUid).apply()
                        }
                    }
                } else {

                    prefs.edit().remove("last_user_uid").apply()
                }
            }
        }
    }
}