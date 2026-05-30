package com.familyquran.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.familyquran.app.auth.AuthManager
import com.familyquran.app.auth.FAMILY_USERNAME
import com.familyquran.app.auth.FamilyAccount
import com.familyquran.app.auth.FamilyAccounts
import com.familyquran.app.core.theme.QuranThemeColors
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private enum class AppScreen(val title: String) {
    Home("Beranda"),
    Reader("Baca"),
    Family("Keluarga"),
    Settings("Atur")
}

private enum class ReaderTheme(val title: String) {
    Light("Terang"),
    Sepia("Sepia"),
    Dark("Gelap")
}

private data class FamilyMember(
    val name: String,
    val avatar: String,
    val lastSeen: String,
    val lastUpdate: String
)

private data class FamilyActivity(
    val member: String,
    val action: String,
    val surah: String,
    val ayat: Int,
    val time: String
)

private data class VersePlaceholder(
    val number: Int,
    val arabic: String,
    val translation: String
)

private data class ToastMessage(
    val text: String,
    val isError: Boolean = false
)

private data class QuranAppState(
    val isOnboarded: Boolean = false,
    val familyName: String = "Keluarga Hasan",
    val familyCode: String = "HSN-204",
    val currentSurah: String = "Al-Baqarah",
    val currentAyat: Int = 45,
    val lastUpdatedBy: String = "Ibu",
    val lastUpdatedTime: String = "05:42",
    val personalBookmarkSurah: String = "An-Nisa",
    val personalBookmarkAyat: Int = 12,
    val targetKhatam: String = "Ramadan 1446",
    val fontSize: Int = 30,
    val showTranslation: Boolean = true,
    val readerTheme: ReaderTheme = ReaderTheme.Light,
    val members: List<FamilyMember> = listOf(
        FamilyMember("Ayah", "A", "Online", "Hari ini, 05:42"),
        FamilyMember("Ibu", "I", "Online", "Hari ini, 05:42"),
        FamilyMember("Aisyah", "A", "2 jam lalu", "Kemarin, 19:30"),
        FamilyMember("Ahmad", "A", "5 jam lalu", "2 hari lalu")
    ),
    val activities: List<FamilyActivity> = listOf(
        FamilyActivity("Ibu", "memperbarui progress ke", "Al-Baqarah", 45, "05:42"),
        FamilyActivity("Ayah", "memindahkan progress ke", "Al-Baqarah", 40, "Kemarin"),
        FamilyActivity("Aisyah", "menandai", "An-Nisa", 12, "Kemarin")
    )
)

private val placeholderVerses = listOf(
    VersePlaceholder(
        43,
        "هٰذَا نَصٌّ عَرَبِيٌّ لِلْعَرْضِ فَقَطْ وَلَيْسَ آيَةً قُرْآنِيَّةً",
        "[Ini adalah teks placeholder Arab untuk tampilan saja, bukan ayat Al-Quran yang sebenarnya]"
    ),
    VersePlaceholder(
        44,
        "هٰذَا مِثَالٌ آخَرُ لِنَصٍّ عَرَبِيٍّ لِأَغْرَاضِ التَّصْمِيمِ",
        "[Contoh lain teks placeholder Arab untuk keperluan desain UI]"
    ),
    VersePlaceholder(
        45,
        "نَصٌّ عَرَبِيٌّ ثَالِثٌ يُسْتَخْدَمُ لِتَوْضِيحِ التَّخْطِيطِ",
        "[Teks placeholder ketiga yang digunakan untuk menunjukkan tata letak aplikasi]"
    ),
    VersePlaceholder(
        46,
        "هٰذَا النَّصُّ لِلْعَرْضِ التَّوْضِيحِيِّ فَحَسْبُ",
        "[Teks ini hanya untuk tujuan demonstrasi tampilan]"
    ),
    VersePlaceholder(
        47,
        "نَصٌّ عَرَبِيٌّ نَمُوذَجِيٌّ لِتَصْمِيمِ الْوَاجِهَةِ",
        "[Teks placeholder Arab untuk desain antarmuka pengguna]"
    )
)

@Composable
fun RainaraQuranApp() {
    val authManager = remember { AuthManager() }
    var currentUser by remember { mutableStateOf(authManager.currentUser) }
    var selectedMember by remember { mutableStateOf<FamilyAccount?>(null) }
    var state by remember { mutableStateOf(QuranAppState()) }
    var screen by remember { mutableStateOf(AppScreen.Home) }
    var selectedVerse by remember { mutableIntStateOf(state.currentAyat) }
    var toast by remember { mutableStateOf<ToastMessage?>(null) }

    DisposableEffect(authManager) {
        val listener = FirebaseAuth.AuthStateListener { currentUser = it.currentUser }
        authManager.addAuthStateListener(listener)
        onDispose { authManager.removeAuthStateListener(listener) }
    }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(2200)
            toast = null
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(QuranThemeColors.ivory)) {
        Crossfade(
            targetState = when {
                !state.isOnboarded -> 0
                currentUser == null -> 1
                selectedMember == null -> 2
                else -> 3
            },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            label = "screen_transition"
        ) { screenIndex ->
            when (screenIndex) {
                0 -> WelcomeScreen(
                    onStart = { state = state.copy(isOnboarded = true) }
                )
                1 -> LoginScreen(
                    authManager = authManager,
                    onToast = { toast = ToastMessage(it) },
                    onError = { toast = ToastMessage(it, isError = true) }
                )
                2 -> MemberPickerScreen(
                    onMemberPicked = { selectedMember = it },
                    onLogout = { authManager.signOut() }
                )
                3 -> {
                    Scaffold(
                        containerColor = QuranThemeColors.ivory,
                        bottomBar = {
                            QuranBottomBar(
                                currentScreen = screen,
                                onScreenSelected = { screen = it }
                            )
                        }
                    ) { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)) {
                            when (screen) {
                                AppScreen.Home -> HomeScreen(
                                    state = state,
                                    onContinueReading = {
                                        selectedVerse = state.currentAyat
                                        screen = AppScreen.Reader
                                    },
                                    onFamily = { screen = AppScreen.Family }
                                )
                                AppScreen.Reader -> ReaderScreen(
                                    state = state,
                                    selectedVerse = selectedVerse,
                                    onSelectedVerseChanged = { selectedVerse = it },
                                    onBack = { screen = AppScreen.Home },
                                    onSetPersonalBookmark = {
                                        state = state.copy(
                                            personalBookmarkSurah = state.currentSurah,
                                            personalBookmarkAyat = selectedVerse
                                        )
                                        toast = ToastMessage("Bookmark pribadi disimpan")
                                    },
                                    onUpdateFamilyProgress = {
                                        val time = SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date())
                                        state = state.copy(
                                            currentAyat = selectedVerse,
                                            lastUpdatedBy = "Anda",
                                            lastUpdatedTime = time,
                                            activities = listOf(
                                                FamilyActivity("Anda", "memperbarui progress ke", state.currentSurah, selectedVerse, time)
                                            ) + state.activities
                                        )
                                        toast = ToastMessage("Progress keluarga diperbarui")
                                    }
                                )
                                AppScreen.Family -> FamilyScreen(
                                    state = state,
                                    onCopied = { toast = ToastMessage("Kode keluarga disalin") }
                                )
                                AppScreen.Settings -> SettingsScreen(
                                    state = state,
                                    onFontSizeChanged = { state = state.copy(fontSize = it) },
                                    onTranslationChanged = { state = state.copy(showTranslation = it) },
                                    onThemeChanged = { state = state.copy(readerTheme = it) }
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = toast != null,
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 18.dp)
        ) {
            ToastContent(toast)
        }
    }
}

@Composable
private fun ToastContent(toast: ToastMessage?) {
    var lastToast by remember { mutableStateOf<ToastMessage?>(null) }
    if (toast != null) lastToast = toast
    val t = lastToast ?: return
    Text(
        text = t.text,
        color = QuranThemeColors.card,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (t.isError) Color(0xFFC62828) else QuranThemeColors.emerald)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
}

@Composable
private fun WelcomeScreen(onStart: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.rainara),
                contentDescription = "Rainara Quran",
                modifier = Modifier.size(140.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Rainara Quran",
                color = QuranThemeColors.ink,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Pantau bacaan keluarga, bersama-sama.",
                color = QuranThemeColors.muted,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ChecklistItem(
                    icon = { BookIcon() },
                    text = "Baca Al-Quran",
                    desc = "Tampilan bersih untuk ibadah yang fokus"
                )
                HorizontalDivider(color = QuranThemeColors.line, modifier = Modifier.padding(vertical = 8.dp))
                ChecklistItem(
                    icon = { PeopleIcon() },
                    text = "Progress keluarga",
                    desc = "Satu bookmark bersama untuk semua anggota"
                )
                HorizontalDivider(color = QuranThemeColors.line, modifier = Modifier.padding(vertical = 8.dp))
                ChecklistItem(
                    icon = { ShieldIcon() },
                    text = "Tanpa iklan",
                    desc = "Tenang, privat, dan tidak terasa komersial"
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
            PrimaryButton(
                text = "Mulai",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Aplikasi ini bebas iklan.",
                color = QuranThemeColors.muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun BookIcon() {
    Canvas(modifier = Modifier.size(22.dp)) {
        val c = QuranThemeColors.emerald
        val w = size.width
        val h = size.height
        val p = 2.dp.toPx()
        drawRoundRect(c, topLeft = androidx.compose.ui.geometry.Offset(p, p), size = androidx.compose.ui.geometry.Size(w - p * 2, h - p * 2), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()), style = androidx.compose.ui.graphics.drawscope.Stroke(1.5.dp.toPx()))
        drawLine(c, start = androidx.compose.ui.geometry.Offset(w / 2, p), end = androidx.compose.ui.geometry.Offset(w / 2, h - p), strokeWidth = 1.2.dp.toPx())
    }
}

@Composable
private fun PeopleIcon() {
    Canvas(modifier = Modifier.size(22.dp)) {
        val c = QuranThemeColors.emerald
        val w = size.width
        val h = size.height
        val r1 = 3.dp.toPx()
        val r2 = 2.2.dp.toPx()
        drawCircle(c, radius = r1, center = androidx.compose.ui.geometry.Offset(w * 0.3f, h * 0.3f), style = androidx.compose.ui.graphics.drawscope.Stroke(1.2.dp.toPx()))
        drawArc(c, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = androidx.compose.ui.geometry.Offset(w * 0.1f, h * 0.52f), size = androidx.compose.ui.geometry.Size(w * 0.4f, h * 0.28f), style = androidx.compose.ui.graphics.drawscope.Stroke(1.2.dp.toPx()))
        drawCircle(c, radius = r2, center = androidx.compose.ui.geometry.Offset(w * 0.72f, h * 0.3f), style = androidx.compose.ui.graphics.drawscope.Stroke(1.2.dp.toPx()))
        drawArc(c, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = androidx.compose.ui.geometry.Offset(w * 0.55f, h * 0.52f), size = androidx.compose.ui.geometry.Size(w * 0.34f, h * 0.28f), style = androidx.compose.ui.graphics.drawscope.Stroke(1.2.dp.toPx()))
    }
}

@Composable
private fun ShieldIcon() {
    Canvas(modifier = Modifier.size(22.dp)) {
        val c = QuranThemeColors.emerald
        val w = size.width
        val h = size.height
        val p = 2.dp.toPx()
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.5f, p)
            lineTo(w - p, h * 0.3f)
            lineTo(w - p, h * 0.65f)
            lineTo(w * 0.5f, h - p)
            lineTo(p, h * 0.65f)
            lineTo(p, h * 0.3f)
            close()
        }
        drawPath(path, c, style = androidx.compose.ui.graphics.drawscope.Stroke(1.5.dp.toPx()))
        drawLine(c, start = androidx.compose.ui.geometry.Offset(w * 0.32f, h * 0.48f), end = androidx.compose.ui.geometry.Offset(w * 0.46f, h * 0.62f), strokeWidth = 1.5.dp.toPx())
        drawLine(c, start = androidx.compose.ui.geometry.Offset(w * 0.46f, h * 0.62f), end = androidx.compose.ui.geometry.Offset(w * 0.7f, h * 0.35f), strokeWidth = 1.5.dp.toPx())
    }
}

@Composable
private fun LoginScreen(
    authManager: AuthManager,
    onToast: (String) -> Unit,
    onError: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(QuranThemeColors.emeraldSoft),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.rainara),
                contentDescription = "Rainara Quran",
                modifier = Modifier.size(74.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Masuk ke Rainara",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = QuranThemeColors.ink
        )
        Text(
            "Akses khusus keluarga",
            color = QuranThemeColors.muted,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it.lowercase().trim() },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = QuranThemeColors.emerald,
                unfocusedBorderColor = QuranThemeColors.line,
                focusedLabelColor = QuranThemeColors.emerald,
                cursorColor = QuranThemeColors.emerald
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = QuranThemeColors.emerald,
                unfocusedBorderColor = QuranThemeColors.line,
                focusedLabelColor = QuranThemeColors.emerald,
                cursorColor = QuranThemeColors.emerald
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (username.isBlank()) {
                    onError("Masukkan username")
                } else if (username != FAMILY_USERNAME) {
                    onError("Akun tidak ditemukan")
                } else if (password.isBlank()) {
                    onError("Masukkan password")
                } else {
                    isLoading = true
                    scope.launch {
                        val result = authManager.signIn("$username@keluarga.app", password)
                        isLoading = false
                        result.onFailure {
                            if (authManager.currentUser == null) {
                                onError("Password salah")
                            }
                        }
                    }
                }
            })
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (username.isBlank()) {
                    onError("Masukkan username")
                    return@Button
                }
                if (username != FAMILY_USERNAME) {
                    onError("Akun tidak ditemukan")
                    return@Button
                }
                if (password.isBlank()) {
                    onError("Masukkan password")
                    return@Button
                }
                isLoading = true
                scope.launch {
                    val result = authManager.signIn("$username@keluarga.app", password)
                    isLoading = false
                    result.onFailure {
                        if (authManager.currentUser == null) {
                            onError("Password salah")
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QuranThemeColors.emerald)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = QuranThemeColors.card,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Masuk", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { onToast("Fitur pendaftaran akan segera hadir untuk publik") }) {
            Text("Daftar", color = QuranThemeColors.muted, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun MemberPickerScreen(
    onMemberPicked: (FamilyAccount) -> Unit,
    onLogout: () -> Unit
) {
    var selectedId by remember { mutableStateOf("raffa") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            "Pilih Anggota",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = QuranThemeColors.ink
        )
        Text(
            "Siapa yang akan membaca?",
            color = QuranThemeColors.muted,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(28.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            FamilyAccounts.all.forEach { account ->
                MemberPickerCard(
                    account = account,
                    isSelected = account.id == selectedId,
                    onClick = { selectedId = account.id }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                FamilyAccounts.all.find { it.id == selectedId }?.let(onMemberPicked)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QuranThemeColors.emerald)
        ) {
            Text("Lanjut", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onLogout) {
            Text("Keluar", color = QuranThemeColors.muted, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun MemberPickerCard(
    account: FamilyAccount,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) QuranThemeColors.emerald else QuranThemeColors.line

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = QuranThemeColors.card),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(account.photoRes),
                contentDescription = account.name,
                modifier = Modifier.size(52.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    account.name,
                    fontWeight = FontWeight.SemiBold,
                    color = QuranThemeColors.ink,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
                Text(
                    account.label,
                    color = QuranThemeColors.muted,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(state: QuranAppState, onContinueReading: () -> Unit, onFamily: () -> Unit) {
    ScreenColumn {
        Text("Assalamu'alaikum", color = QuranThemeColors.muted)
        Text("Selamat Pagi", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(22.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = QuranThemeColors.emerald),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text(state.familyName, color = QuranThemeColors.card.copy(alpha = 0.9f), fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(20.dp))
                Text("Progress saat ini", color = QuranThemeColors.card.copy(alpha = 0.75f), fontSize = 12.sp)
                Text(
                    "${state.currentSurah} - Ayat ${state.currentAyat}",
                    color = QuranThemeColors.card,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Diupdate oleh ${state.lastUpdatedBy}, ${state.lastUpdatedTime}",
                    color = QuranThemeColors.card.copy(alpha = 0.78f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onContinueReading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.18f),
                        contentColor = QuranThemeColors.card
                    )
                ) {
                    Text("Lanjut Baca", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            QuickCard("Bookmark", "${state.personalBookmarkSurah.take(8)}...", Modifier.weight(1f), onContinueReading)
            QuickCard("Target", state.targetKhatam.substringBefore(" "), Modifier.weight(1f))
            QuickCard("Aktif", "${state.members.count { it.lastSeen == "Online" }} orang", Modifier.weight(1f), onFamily)
        }

        SectionTitle("Aktivitas Terbaru")
        AppCard {
            state.activities.take(3).forEachIndexed { index, activity ->
                ActivityRow(activity)
                if (index < state.activities.take(3).lastIndex) HorizontalDivider(color = QuranThemeColors.line)
            }
        }
    }
}

@Composable
private fun ReaderScreen(
    state: QuranAppState,
    selectedVerse: Int,
    onSelectedVerseChanged: (Int) -> Unit,
    onBack: () -> Unit,
    onSetPersonalBookmark: () -> Unit,
    onUpdateFamilyProgress: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }
    val selectedIndex = placeholderVerses.indexOfFirst { it.number == selectedVerse }.coerceAtLeast(0)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) { Text("Kembali") }
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(state.currentSurah, fontWeight = FontWeight.SemiBold)
                    Text("Ayat 43 - 47", color = QuranThemeColors.muted, fontSize = 12.sp)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    "Teks Arab di bawah ini adalah placeholder, bukan ayat Al-Quran yang sebenarnya.",
                    color = QuranThemeColors.muted,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(QuranThemeColors.goldSoft)
                        .padding(14.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))
                placeholderVerses.forEachIndexed { index, verse ->
                    VerseCard(
                        verse = verse,
                        state = state,
                        isSelected = index == selectedIndex,
                        isFamilyBookmark = verse.number == state.currentAyat,
                        onClick = { onSelectedVerseChanged(verse.number) }
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).navigationBarsPadding()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onSetPersonalBookmark,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Bookmark") }
                PrimaryButton(
                    text = "Update",
                    onClick = { showConfirm = true },
                    modifier = Modifier.weight(1f).height(52.dp)
                )
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Update Progress Keluarga") },
            text = { Text("Update progress keluarga ke ${state.currentSurah} ayat $selectedVerse? Semua anggota keluarga akan melihat progress terbaru.") },
            confirmButton = {
                Button(onClick = {
                    onUpdateFamilyProgress()
                    showConfirm = false
                }) { Text("Update Progress") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun FamilyScreen(state: QuranAppState, onCopied: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    val currentProgress = 331
    val totalVerses = 6236
    val percent = ((currentProgress.toFloat() / totalVerses.toFloat()) * 100f).roundToInt()

    ScreenColumn {
        Text(state.familyName, style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        Text("${state.members.size} anggota", color = QuranThemeColors.muted)

        Spacer(modifier = Modifier.height(18.dp))
        AppCard {
            Text("Progress Khatam", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Text("$percent%", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = QuranThemeColors.emerald)
            Text("${state.currentSurah} - Ayat ${state.currentAyat}", color = QuranThemeColors.muted)
            Text("$currentProgress / $totalVerses ayat", color = QuranThemeColors.muted, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))
        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Kode Keluarga", color = QuranThemeColors.muted, fontSize = 12.sp)
                    Text(state.familyCode, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = {
                    clipboard.setText(AnnotatedString(state.familyCode))
                    onCopied()
                }) { Text("Salin") }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        PrimaryButton(text = "Undang Anggota Baru", onClick = {})

        SectionTitle("Anggota Keluarga")
        AppCard {
            state.members.forEachIndexed { index, member ->
                MemberRow(member)
                if (index < state.members.lastIndex) HorizontalDivider(color = QuranThemeColors.line)
            }
        }

        SectionTitle("Riwayat Aktivitas")
        AppCard {
            state.activities.forEachIndexed { index, activity ->
                ActivityRow(activity)
                if (index < state.activities.lastIndex) HorizontalDivider(color = QuranThemeColors.line)
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    state: QuranAppState,
    onFontSizeChanged: (Int) -> Unit,
    onTranslationChanged: (Boolean) -> Unit,
    onThemeChanged: (ReaderTheme) -> Unit
) {
    ScreenColumn {
        Text("Pengaturan", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        SectionTitle("Tampilan Bacaan")
        AppCard {
            Text("Ukuran Font Arab", fontWeight = FontWeight.SemiBold)
            Text("${state.fontSize}px", color = QuranThemeColors.muted, fontSize = 12.sp)
            Slider(
                value = state.fontSize.toFloat(),
                onValueChange = { onFontSizeChanged(it.roundToInt()) },
                valueRange = 22f..40f
            )
            HorizontalDivider(color = QuranThemeColors.line)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tampilkan Terjemahan", fontWeight = FontWeight.SemiBold)
                    Text("Bahasa Indonesia", color = QuranThemeColors.muted, fontSize = 12.sp)
                }
                Switch(checked = state.showTranslation, onCheckedChange = onTranslationChanged)
            }
        }

        SectionTitle("Tema")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            ReaderTheme.values().forEach { theme ->
                ThemeChip(
                    title = theme.title,
                    selected = state.readerTheme == theme,
                    modifier = Modifier.weight(1f),
                    onClick = { onThemeChanged(theme) }
                )
            }
        }

        SectionTitle("Akun")
        AppCard {
            SettingsRow("Privasi & Keamanan")
            HorizontalDivider(color = QuranThemeColors.line)
            SettingsRow("Tentang Aplikasi")
            HorizontalDivider(color = QuranThemeColors.line)
            SettingsRow("Keluar")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Aplikasi ini sepenuhnya bebas iklan untuk pengalaman ibadah yang fokus.",
            color = QuranThemeColors.muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun QuranBottomBar(currentScreen: AppScreen, onScreenSelected: (AppScreen) -> Unit) {
    NavigationBar(containerColor = QuranThemeColors.card) {
        AppScreen.values().forEach { screen ->
            NavigationBarItem(
                selected = currentScreen == screen,
                onClick = { onScreenSelected(screen) },
                icon = { Text(screen.title.first().toString(), fontWeight = FontWeight.Bold) },
                label = { Text(screen.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = QuranThemeColors.emerald,
                    selectedTextColor = QuranThemeColors.emerald,
                    indicatorColor = QuranThemeColors.emeraldSoft,
                    unselectedIconColor = QuranThemeColors.muted,
                    unselectedTextColor = QuranThemeColors.muted
                )
            )
        }
    }
}

@Composable
private fun ScreenColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        content = content
    )
}

@Composable
private fun AppCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = QuranThemeColors.card),
        border = BorderStroke(1.dp, QuranThemeColors.line),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier.fillMaxWidth().height(54.dp)) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = QuranThemeColors.emerald)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FeatureRow(title: String, subtitle: String) {
    AppCard {
        Text(title, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = QuranThemeColors.muted, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun ChecklistItem(icon: @Composable () -> Unit, text: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(QuranThemeColors.emeraldSoft),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text, fontWeight = FontWeight.SemiBold, color = QuranThemeColors.ink, fontSize = 14.sp)
            Text(desc, color = QuranThemeColors.muted, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun QuickCard(title: String, value: String, modifier: Modifier, onClick: (() -> Unit)? = null) {
    Card(
        modifier = modifier.clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = QuranThemeColors.card),
        border = BorderStroke(1.dp, QuranThemeColors.line)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = QuranThemeColors.muted, fontSize = 12.sp)
            Text(value, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

@Composable
private fun VerseCard(
    verse: VersePlaceholder,
    state: QuranAppState,
    isSelected: Boolean,
    isFamilyBookmark: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isFamilyBookmark -> QuranThemeColors.emerald
        isSelected -> QuranThemeColors.gold
        else -> QuranThemeColors.line
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = QuranThemeColors.card),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isFamilyBookmark) QuranThemeColors.emerald else QuranThemeColors.goldSoft),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    verse.number.toString(),
                    color = if (isFamilyBookmark) QuranThemeColors.card else QuranThemeColors.ink,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                verse.arabic,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right,
                fontFamily = FontFamily.Serif,
                fontSize = state.fontSize.sp,
                lineHeight = (state.fontSize + 22).sp,
                color = QuranThemeColors.ink
            )
            if (state.showTranslation) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(verse.translation, color = QuranThemeColors.muted, textAlign = TextAlign.Center)
            }
            if (isFamilyBookmark) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    "Bookmark Keluarga",
                    color = QuranThemeColors.emerald,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(QuranThemeColors.emeraldSoft)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun ActivityRow(activity: FamilyActivity) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
        Avatar(activity.member.first().toString())
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("${activity.member} ${activity.action}", color = QuranThemeColors.muted)
            Text("${activity.surah} ${activity.ayat}", fontWeight = FontWeight.SemiBold)
        }
        Text(activity.time, color = QuranThemeColors.muted, fontSize = 12.sp)
    }
}

@Composable
private fun MemberRow(member: FamilyMember) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
        Avatar(member.avatar)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(member.name, fontWeight = FontWeight.SemiBold)
            Text(
                if (member.lastSeen == "Online") "Online" else "Terakhir aktif ${member.lastSeen}",
                color = QuranThemeColors.muted,
                fontSize = 12.sp
            )
            Text("Update: ${member.lastUpdate}", color = QuranThemeColors.muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun Avatar(text: String) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(QuranThemeColors.emeraldSoft),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = QuranThemeColors.emerald, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionTitle(title: String) {
    Spacer(modifier = Modifier.height(22.dp))
    Text(title, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 10.dp))
}

@Composable
private fun ThemeChip(title: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) QuranThemeColors.emeraldSoft else QuranThemeColors.card)
            .border(1.dp, if (selected) QuranThemeColors.emerald else QuranThemeColors.line, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(title, color = if (selected) QuranThemeColors.emerald else QuranThemeColors.ink, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SettingsRow(title: String) {
    Text(title, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp))
}
