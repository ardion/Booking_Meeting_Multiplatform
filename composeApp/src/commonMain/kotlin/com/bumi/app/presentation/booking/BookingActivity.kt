package com.bumi.app.presentation.booking

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.bumi.app.Screen
import com.bumi.app.utils.Resource
import com.bumi.app.utils.SessionManager
import kotlinx.datetime.*
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// --- SCREEN UTAMA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullCalendarScreen(
    navController: NavController,
    viewModel: MeetingViewModel = koinViewModel()
) {
    val sessionManager: SessionManager = koinInject()
    val sheetState = rememberModalBottomSheetState()
    val meetingsResource by viewModel.meetingsState.collectAsState()

    var isSheetOpen by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedFilter by remember { mutableStateOf("All") }
    val filterOptions = listOf("All", "Pending", "Approved", "Rejected")

    LaunchedEffect(Unit) {
        viewModel.getAllBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Booking Calendar", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Halo, ${sessionManager.getUserName()}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.7f))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        sessionManager.logout()
                        navController.navigate("login") { popUpTo("calendar") { inclusive = true } }
                    }) { Icon(Icons.Default.ExitToApp, "Logout", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4675A6))
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().background(Color(0xFF4675A6)).padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            val isDesktop = maxWidth > 700.dp
            val scrollState = rememberScrollState()

            // KUNCI: Column utama ini harus memiliki scroll agar kalender bisa digeser ke atas
            Column(
                modifier = Modifier
                    .widthIn(max = 1000.dp)
                    .fillMaxSize()
                    .then(if (isDesktop) Modifier.verticalScroll(scrollState) else Modifier),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. KALENDER (Bagian yang bikin layar penuh)
                ModernCalendarCustom(onDateSelected = { date ->
                    selectedDate = date
                    isSheetOpen = true
                })

                if (isSheetOpen) {
                    ModalBottomSheet(
                        onDismissRequest = { isSheetOpen = false },
                        sheetState = sheetState,
                        modifier = Modifier.widthIn(max = 600.dp),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        BookingContent(date = selectedDate, viewModel = viewModel, onSuccess = { isSheetOpen = false })
                    }
                }

                Text(
                    text = "Daftar Pengajuan",
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, end = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = Color(0xFFEBEEEF)
                )

                // 2. FILTER
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 12.sp) },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.White,
                                selectedLabelColor = Color(0xFF4675A6),
                                containerColor = Color.White.copy(0.2f),
                                labelColor = Color.White
                            ),
                            border = null
                        )
                    }
                }

                // 3. AREA DATA / LOADING (Sekarang bisa di-scroll ke bawah)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .then(if (!isDesktop) Modifier.weight(1f) else Modifier.padding(bottom = 50.dp))
                ) {
                    when (val resource = meetingsResource) {
                        is Resource.Loading -> {
                            // TAMPILAN SAAT LOADING (SHIMMER)
                            if (isDesktop) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    repeat(3) {
                                        Box(Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(16.dp)).shimmerEffect())
                                    }
                                }
                            } else {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(3) {
                                        Box(Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(16.dp)).shimmerEffect())
                                    }
                                }
                            }
                        }
                        is Resource.Success -> {
                            val filtered = if (selectedFilter == "All") resource.data
                            else resource.data.filter { it.status.equals(selectedFilter, true) }

                            if (filtered.isEmpty()) {
                                Text("Tidak ada data", Modifier.align(Alignment.Center).padding(40.dp), color = Color.White)
                            } else {
                                if (isDesktop) {
                                    // GRID MANUAL UNTUK WEB AGAR BISA SCROLL SE-KALENDERNYA
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        filtered.chunked(2).forEach { rowMeetings ->
                                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                rowMeetings.forEach { meeting ->
                                                    Box(modifier = Modifier.weight(1f)) {
                                                        BookingItem(
                                                            pic = meeting.pic, unit = meeting.unit, tujuan = meeting.tujuan,
                                                            jam = meeting.jam, tanggal = meeting.tanggal, status = meeting.status,
                                                            onApprove = { viewModel.updateStatus(meeting.id, "Approved") },
                                                            onReject = { viewModel.updateStatus(meeting.id, "Rejected") },
                                                            onDelete = { viewModel.deleteBooking(meeting.id) }
                                                        )
                                                    }
                                                }
                                                if (rowMeetings.size == 1) Spacer(Modifier.weight(1f))
                                            }
                                        }
                                    }
                                } else {
                                    // MOBILE SCROLL
                                    LazyColumn(
                                        modifier = Modifier.fillMaxWidth(), // Pakai fillMaxWidth agar lebarnya konsisten
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        contentPadding = PaddingValues(bottom = 80.dp)
                                    ) {
                                        items(filtered) { meeting ->
                                            // Bungkus dengan Box atau Row dan beri fillMaxWidth
                                            Box(modifier = Modifier.fillMaxWidth()) {
                                                BookingItem(
                                                    pic = meeting.pic, unit = meeting.unit, tujuan = meeting.tujuan,
                                                    jam = meeting.jam, tanggal = meeting.tanggal, status = meeting.status,
                                                    onApprove = { viewModel.updateStatus(meeting.id, "Approved") },
                                                    onReject = { viewModel.updateStatus(meeting.id, "Rejected") },
                                                    onDelete = { viewModel.deleteBooking(meeting.id) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        is Resource.Error -> { Text("Gagal: ${resource.message}", Modifier.align(Alignment.Center), color = Color.Red) }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun ModernCalendarCustom(onDateSelected: (LocalDate) -> Unit) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    var currentMonth by remember { mutableStateOf(today.month) }
    var currentYear by remember { mutableStateOf(today.year) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val daysInMonth = remember(currentMonth, currentYear) {
        if (currentMonth == Month.FEBRUARY) {
            if ((currentYear % 4 == 0 && currentYear % 100 != 0) || (currentYear % 400 == 0)) 29 else 28
        } else if (currentMonth in listOf(Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER)) 30 else 31
    }

    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .widthIn(max = 800.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFEFF0F1))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Bulan dan Navigasi
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(
                    "${currentMonth.name} $currentYear",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4675A6)
                )
                Row {
                    IconButton(onClick = {
                        if (currentMonth == Month.JANUARY) { currentMonth = Month.DECEMBER; currentYear-- }
                        else currentMonth = Month(currentMonth.number - 1)
                    }) { Icon(Icons.Default.KeyboardArrowLeft, null) }
                    IconButton(onClick = {
                        if (currentMonth == Month.DECEMBER) { currentMonth = Month.JANUARY; currentYear++ }
                        else currentMonth = Month(currentMonth.number + 1)
                    }) { Icon(Icons.Default.KeyboardArrowRight, null) }
                }
            }

            // Days Header (Mon - Sun)
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            // Grid Tanggal
            val firstDay = LocalDate(currentYear, currentMonth, 1).dayOfWeek.ordinal
            var dayCounter = 1

            // Loop baris (maksimal 6 baris kalender)
            for (i in 0..5) {
                Row(Modifier.fillMaxWidth()) {
                    for (j in 0..6) {
                        val isVisible = (i == 0 && j >= firstDay) || (i > 0 && dayCounter <= daysInMonth)

                        // Modifier.weight(1f) memastikan setiap kolom sama lebar
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(if (dayCounter > daysInMonth && !isVisible) 1f else 1.2f), // Sedikit lebih tinggi agar proporsional
                            contentAlignment = Alignment.Center
                        ) {
                            if (isVisible && dayCounter <= daysInMonth) {
                                val date = LocalDate(currentYear, currentMonth, dayCounter)
                                val isSelected = selectedDate == date
                                val isToday = date == today

                                Box(
                                    Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> Color(0xFF4675A6)
                                                isToday -> Color(0xFF4675A6).copy(alpha = 0.1f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable {
                                            selectedDate = date
                                            onDateSelected(date)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$dayCounter",
                                        color = if (isSelected) Color.White else Color.Black,
                                        fontSize = 14.sp,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                dayCounter++
                            }
                        }
                    }
                }
                if (dayCounter > daysInMonth) break
            }
        }
    }
}
// --- KOMPONEN KALENDER CUSTOM (WEB READY) ---


// --- FORM CONTENT BOTTOM SHEET ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookingContent(
    date: LocalDate?,
    viewModel: MeetingViewModel,
    onSuccess: () -> Unit
) {
    var picPengaju by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var tujuanMeeting by remember { mutableStateOf("") }
    var selectedTimes by remember { mutableStateOf(setOf<String>()) }

    val bookingStatus by viewModel.createBookingResult.collectAsState()
    val bookedSlots = viewModel.bookedSlots

    LaunchedEffect(bookingStatus) {
        if (bookingStatus is Resource.Success) {
            onSuccess()
            viewModel.resetCreateState()
        }
    }

    LaunchedEffect(date) {
        date?.let { viewModel.onDateSelected(it.toString()) }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp).verticalScroll(rememberScrollState())
    ) {
        Text("Form Pengajuan Meeting", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = date?.toString() ?: "",
            onValueChange = {},
            label = { Text("Tanggal") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            leadingIcon = { Icon(Icons.Default.DateRange, null) },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(16.dp))

        // Time Slots Section (Harus kamu definisikan di file terpisah atau di sini)
        Text("Pilih Jam", style = MaterialTheme.typography.labelLarge)
        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val times = listOf("08:00", "09:00", "10:00", "11:00", "13:00", "14:00", "15:00")
            times.forEach { time ->
                val isBooked = bookedSlots.contains(time)
                FilterChip(
                    selected = selectedTimes.contains(time),
                    onClick = {
                        if (!isBooked) {
                            selectedTimes = if (selectedTimes.contains(time)) selectedTimes - time else selectedTimes + time
                        }
                    },
                    label = { Text(time) },
                    enabled = !isBooked
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = picPengaju, onValueChange = { picPengaju = it }, label = { Text("PIC Pengaju") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = tujuanMeeting, onValueChange = { tujuanMeeting = it }, label = { Text("Tujuan") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { viewModel.createBooking(picPengaju, unit, tujuanMeeting, date.toString(), selectedTimes.toList()) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = bookingStatus !is Resource.Loading && picPengaju.isNotBlank() && selectedTimes.isNotEmpty()
        ) {
            if (bookingStatus is Resource.Loading) CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
            else Text("Ajukan Meeting")
        }
    }
}

// --- EFEK SHIMMER ---
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart)
    )
    val brush = Brush.linearGradient(
        colors = listOf(Color.LightGray.copy(0.3f), Color.LightGray.copy(0.1f), Color.LightGray.copy(0.3f)),
        start = Offset.Zero, end = Offset(translateAnim, translateAnim)
    )
    background(brush)
}

@Composable
fun BookingItem(
    pic: String,
    unit: String,
    tujuan: String,
    jam: String,
    tanggal: String,
    status: String,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = pic, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = tanggal,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4675A6),
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "$unit • $jam", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7F8C8D))
                }

                // Badge Status
                val badgeColor = when (status.lowercase()) {
                    "approved" -> Color(0xFFD4EFDF)
                    "rejected" -> Color(0xFFFADBD8)
                    else -> Color(0xFFF1F2F6)
                }
                val textColor = when (status.lowercase()) {
                    "approved" -> Color(0xFF27AE60)
                    "rejected" -> Color(0xFFE74C3C)
                    else -> Color(0xFF7F8C8D)
                }

                Surface(color = badgeColor, shape = CircleShape) {
                    Text(
                        text = status.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = tujuan,
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            if (status.lowercase() == "pending") {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color(0xFFECF0F1))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Approve", fontSize = 12.sp, color = Color.White)
                    }

                    Button(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reject", fontSize = 12.sp, color = Color.Black)
                    }

                    IconButton(
                        onClick = onDelete,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color(0xFFE74C3C))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}