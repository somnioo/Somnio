@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
package com.example.ui.screens

import android.widget.Toast
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.ActiveScreen
import com.example.ui.DreamViewModel
import com.example.ui.ScreenStatus
import com.example.ui.components.DreamProceduralVisualizer
import com.example.ui.components.SomnioGlowingBanner
import com.example.ui.theme.*
import kotlinx.coroutines.launch

// -------------------------------------------------------------
// SPLASH SCREEN
// -------------------------------------------------------------
@Composable
fun SomnioSplash() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutBack),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(CyberPink.copy(alpha = 0.5f), Color.Transparent)
                        )
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.ic_somnio_logo),
                    contentDescription = "Somnio Logo",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SOMNIO",
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 8.sp,
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "THE AI DREAM METAVERSE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = NebulaCyan,
                letterSpacing = 4.sp
            )
        }
    }
}

// -------------------------------------------------------------
// ONBOARDING SCREEN
// -------------------------------------------------------------
@Composable
fun SomnioOnboarding(onFinish: () -> Unit) {
    var step by remember { mutableStateOf(0) }
    val steps = listOf(
        Triple(
            "Share Imagination",
            "Say goodbye to daily realities. Somnio is where your deepest nighttime travels, thoughts, and fantasy alternate worlds come alive.",
            Icons.Default.CloudQueue
        ),
        Triple(
            "AI Consciousness Model",
            "Our Gemini engines transform simple descriptions into high-fidelity artistic stories, deep psychological analyses, and visual parameters.",
            Icons.Default.AutoAwesome
        ),
        Triple(
            "Enter the Multiverse",
            "Combine dream spaces with friends, join collaborative nexus chambers, and navigate procedural collective worlds.",
            Icons.Default.Hub
        )
    )

    Box(
        modifier = modifierGradientBackground()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SOMNIO",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp,
                    fontSize = 18.sp
                )
                TextButton(onClick = onFinish) {
                    Text("Skip", color = Color.Gray)
                }
            }

            // Central showcase
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(GlassCardColor)
                        .border(1.dp, GlassCardBorder, RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = steps[step].third,
                        contentDescription = "Onboarding Icon",
                        tint = DreamPurple,
                        modifier = Modifier.size(72.dp)
                    )
                }
                Spacer(modifier = Modifier.height(36.dp))
                Text(
                    text = steps[step].first,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = steps[step].second,
                    fontSize = 15.sp,
                    color = TextSecondaryDim,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Bottom Buttons & Stepper indicator
            Column(modifier = Modifier.fillMaxWidth()) {
                // Indicators Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    steps.forEachIndexed { idx, _ ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (idx == step) 28.dp else 8.dp, height = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (idx == step) CyberPink else Color.DarkGray)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (step < steps.size - 1) {
                            step++
                        } else {
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("onboarding_next_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DreamPurple),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (step == steps.size - 1) "Enter Sanctuary" else "Continue Wave",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// AUTH LOGIN / REGISTER
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SomnioAuthScreen(
    registerMode: Boolean,
    viewModel: DreamViewModel,
    onToggleMode: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    Box(
        modifier = modifierGradientBackground()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = com.example.R.drawable.ic_somnio_logo),
                contentDescription = "Somnio Logo",
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, CyberPink.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (registerMode) "Create Dream Profile" else "Access Sanctuary",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Secure passwordless access to your imagination feed",
                fontSize = 12.sp,
                color = TextSecondaryDim,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Dream Wave Address (Email)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_input"),
                colors = textFieldGlowColors(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = "Email") }
            )

            if (registerMode) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Choose Dream Username") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input"),
                    colors = textFieldGlowColors(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username") }
                )
            }

            if (authState.error != null) {
                Text(
                    text = authState.error ?: "",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (registerMode) {
                        viewModel.performRegister(email, username)
                    } else {
                        viewModel.performLogin(email)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (registerMode) "Initiate Alignment" else "Beam In",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google login mock
            OutlinedButton(
                onClick = { viewModel.performLogin("google_explorer@gmail.com") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterVintage,
                        contentDescription = "Google",
                        tint = NebulaCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Align with Google Waves", color = Color.White, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onToggleMode) {
                Text(
                    text = if (registerMode) "Already mapped with Somnio? Login" else "New consciousness? Set up a dream grid",
                    color = NebulaCyan,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// MASTER SCREEN SHELL MAP
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SomnioMainScaffold(viewModel: DreamViewModel) {
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
    val currUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    val unreadCount = notifications.count { !it.isRead }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Modal navigation drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MidnightNavy,
                modifier = Modifier.width(290.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(MidnightNavy, DeepSpaceBlack)
                            )
                        )
                        .padding(18.dp)
                ) {
                    // Drawer profile header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(CyberPink, DreamPurple))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currUser?.username?.take(2)?.uppercase() ?: "SO",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = currUser?.username ?: "Explorer",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            if (currUser?.badgeName != null) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .background(CyberPink.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = currUser?.badgeName!!,
                                        fontSize = 9.sp,
                                        color = CyberPink,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = GlassCardBorder.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Secondary lists
                    val drawerItems = listOf(
                        Triple("Private Messages", ActiveScreen.MESSAGES, Icons.Outlined.Chat),
                        Triple("Dream Communities", ActiveScreen.COMMUNITIES, Icons.Outlined.Group),
                        Triple("Saved Bookmarks", ActiveScreen.BOOKMARKS, Icons.Outlined.Bookmarks),
                        Triple("Creator Studio", ActiveScreen.CREATOR_STUDIO, Icons.Outlined.Analytics),
                        Triple("Core Settings", ActiveScreen.SETTINGS, Icons.Outlined.Settings),
                        Triple("Nexus Support (Help)", ActiveScreen.HELP, Icons.Outlined.HelpOutline)
                    )

                    drawerItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.first, color = Color.White) },
                            selected = activeScreen == item.second,
                            onClick = {
                                viewModel.changeActiveTab(item.second)
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(item.third, contentDescription = item.first, tint = NebulaCyan) },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = DreamPurple.copy(alpha = 0.35f),
                                unselectedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    // Admin view link
                    if (currUser?.isAdmin == true || currUser?.username == "Somnio_Explorer") {
                        Spacer(modifier = Modifier.weight(1f))
                        NavigationDrawerItem(
                            label = { Text("Nexus Super Admin", color = CyberPink) },
                            selected = activeScreen == ActiveScreen.ADMIN,
                            onClick = {
                                viewModel.changeActiveTab(ActiveScreen.ADMIN)
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = CyberPink) },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = CyberPink.copy(alpha = 0.2f),
                                unselectedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    HorizontalDivider(color = GlassCardBorder.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 12.dp))

                    TextButton(
                        onClick = { viewModel.performLogout() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.LightGray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dismount Mindset (Logout)", color = Color.LightGray)
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Brightness2,
                                contentDescription = "Somnio Icon",
                                tint = CyberPink,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SOMNIO",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 4.sp,
                                fontSize = 18.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open Drawer", tint = Color.White)
                        }
                    },
                    actions = {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(containerColor = CyberPink) {
                                        Text(unreadCount.toString(), color = Color.White)
                                    }
                                }
                            }
                        ) {
                            IconButton(onClick = { viewModel.changeActiveTab(ActiveScreen.NOTIFICATIONS) }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MidnightNavy
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MidnightNavy,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    val tabs = listOf(
                        Triple("Home", ActiveScreen.HOME, Icons.Default.Home),
                        Triple("Explore", ActiveScreen.EXPLORE, Icons.Default.Search),
                        Triple("Create", ActiveScreen.CREATE, Icons.Default.AddCircle),
                        Triple("Inboxes", ActiveScreen.MESSAGES, Icons.Default.Mail),
                        Triple("Galaxy", ActiveScreen.PROFILE, Icons.Default.Person)
                    )

                    tabs.forEach { tab ->
                        val selected = activeScreen == tab.second
                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.changeActiveTab(tab.second) },
                            icon = {
                                Icon(
                                    imageVector = tab.third,
                                    contentDescription = tab.first,
                                    tint = if (selected) CyberPink else Color.LightGray
                                )
                            },
                            label = { Text(tab.first) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = DreamPurple.copy(alpha = 0.25f),
                                selectedTextColor = Color.White,
                                unselectedTextColor = Color.LightGray
                            )
                        )
                    }
                }
            },
            containerColor = DeepSpaceBlack
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Outer transition
                AnimatedContent(
                    targetState = activeScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200))
                    },
                    label = "main_content"
                ) { screen ->
                    when (screen) {
                        ActiveScreen.HOME -> HomeScreen(viewModel)
                        ActiveScreen.EXPLORE -> ExploreScreen(viewModel)
                        ActiveScreen.CREATE -> CreateScreen(viewModel)
                        ActiveScreen.NOTIFICATIONS -> NotificationsScreen(viewModel)
                        ActiveScreen.PROFILE -> ProfileScreen(viewModel)
                        ActiveScreen.MESSAGES -> ChatThreadScreen(viewModel)
                        ActiveScreen.COMMUNITIES -> CommunitiesScreen(viewModel)
                        ActiveScreen.BOOKMARKS -> BookmarksScreen(viewModel)
                        ActiveScreen.CREATOR_STUDIO -> CreatorStudioScreen(viewModel)
                        ActiveScreen.SETTINGS -> SettingsScreen(viewModel)
                        ActiveScreen.ADMIN -> AdminDashboardScreen(viewModel)
                        ActiveScreen.HELP -> HelpScreen(viewModel)
                    }
                }

                // Comment Overlay Sheet / Dialog if an active comment is pending
                val selectingCommentId by viewModel.activeCommentDreamId.collectAsStateWithLifecycle()
                if (selectingCommentId != null) {
                    CommentDialogSheet(
                        dreamId = selectingCommentId!!,
                        viewModel = viewModel,
                        onDismiss = { viewModel.selectCommentDream(null) }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CHILD SCREEN 1: HOME SCREEN (FEED + COMMUNITIES CHEVRONS)
// -------------------------------------------------------------
@Composable
fun HomeScreen(viewModel: DreamViewModel) {
    val feedList by viewModel.filteredFeed.collectAsStateWithLifecycle()
    val communities by viewModel.communities.collectAsStateWithLifecycle()
    val selectedCommFeed by viewModel.selectedCommunityForFeed.collectAsStateWithLifecycle()
    val communityPostFeed by viewModel.communityFeedList.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            SomnioGlowingBanner(
                title = "Imaging Sanctuary",
                tagline = "Transmuting electrical impulses into interstellar feeds."
            )
        }

        // Horizontal list of communities
        item {
            Column {
                Text(
                    text = "COMMUNITY CHAMBERS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = NebulaCyan,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCommFeed == null,
                            onClick = { viewModel.selectCommunityForFeed(null) },
                            label = { Text("Global Feed") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberPink.copy(alpha = 0.35f),
                                labelColor = Color.White
                            )
                        )
                    }
                    items(communities) { comm ->
                        FilterChip(
                            selected = selectedCommFeed?.id == comm.id,
                            onClick = { viewModel.selectCommunityForFeed(comm) },
                            label = { Text(comm.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DreamPurple.copy(alpha = 0.35f),
                                labelColor = Color.White
                            ),
                            leadingIcon = {
                                if (comm.isJoined) {
                                    Icon(Icons.Default.Check, contentDescription = "Joined", modifier = Modifier.size(12.dp))
                                }
                            }
                        )
                    }
                }
            }
        }

        // Sub feed description
        item {
            Text(
                text = if (selectedCommFeed != null) "CHAMBER WAVES: ${selectedCommFeed?.name?.uppercase()}" else "RECENT CONVEX WAVES",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
        }

        val activeList = if (selectedCommFeed != null) communityPostFeed else feedList

        if (activeList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CloudOff, contentDescription = "Empty", tint = Color.DarkGray, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Void state: No visual echoes mapped here yet.", color = Color.LightGray, fontSize = 14.sp)
                        Text("Deploy your imagination under 'Create' to begin.", color = Color.Gray, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(activeList, key = { it.id }) { post ->
                DreamPostCard(post, viewModel)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DreamPostCard(post: DreamPost, viewModel: DreamViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dream_post_card_${post.id}"),
        colors = CardDefaults.cardColors(containerColor = GlassCardColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GlassCardBorder.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Profile row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(NebulaCyan, DreamPurple))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorUsername.take(2).uppercase(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.authorUsername,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Vibrating at: ${formatTime(post.timestamp)}",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }

                // Audio track info bubble
                Box(
                    modifier = Modifier
                        .background(DreamPurple.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MusicNote, contentDescription = "Track", tint = NebulaCyan, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(post.ambientTrack, fontSize = 9.sp, color = NebulaCyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dream Title & Prompt
            Text(
                text = post.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Promp t: \"${post.originalText}\"",
                fontSize = 11.sp,
                color = TextSecondaryDim,
                style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Expanded Story block
            Text(
                text = post.aiStory,
                fontSize = 13.sp,
                color = Color.LightGray,
                lineHeight = 19.sp,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dream Procedural art representation
            DreamProceduralVisualizer(seed = post.aiImageSeed, moodTags = post.moodTags, height = 150.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Subconscious analysis panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Analysis",
                        tint = CyberPink,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Subconscious Mapping Analysis:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberPink)
                        Text(post.aiInterpretation, fontSize = 11.sp, color = Color.LightGray, lineHeight = 15.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tag badges row
            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                post.moodTags.split(",").forEach { tag ->
                    if (tag.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp, bottom = 4.dp)
                                .background(Color.Black, RoundedCornerShape(4.dp))
                                .border(0.5.dp, DreamIndigo, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("#${tag.trim()}", color = NebulaCyan, fontSize = 9.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive comment list count showing or comments clicker
            HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            // Actions row: Like, Comment, Bookmark, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.toggleLike(post.id) },
                        modifier = Modifier.testTag("like_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.hasLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.hasLiked) CyberPink else Color.LightGray
                        )
                    }
                    Text(post.likesCount.toString(), color = Color.White, fontSize = 12.sp)

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        onClick = { viewModel.selectCommentDream(post.id) },
                        modifier = Modifier.testTag("comment_button_${post.id}")
                    ) {
                        Icon(Icons.Outlined.ModeComment, contentDescription = "Comments", tint = Color.LightGray)
                    }
                    Text(if (post.id <= 3) "2" else "0", color = Color.White, fontSize = 12.sp)
                }

                Row {
                    IconButton(onClick = { viewModel.toggleBookmark(post.id) }) {
                        Icon(
                            imageVector = if (post.hasBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (post.hasBookmarked) NebulaCyan else Color.LightGray
                        )
                    }
                    IconButton(onClick = { /* Share dialog action simulation */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.LightGray)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CHILD SCREEN 2: EXPLORE (SEMANTIC NEURAL SCANNING)
// -------------------------------------------------------------
@Composable
fun ExploreScreen(viewModel: DreamViewModel) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filteredList by viewModel.filteredFeed.collectAsStateWithLifecycle()
    val selectedMood by viewModel.selectedMoodFilter.collectAsStateWithLifecycle()

    val moodChips = listOf("cyberpunk", "cosmic", "ambient", "fluid", "academic", "recursive")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.updateQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldGlowColors(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Search dreaming spaces, tags, users...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = CyberPink) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mood filters
        Text(
            text = "NEURAL MOOD FILTERS",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = CyberPink,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedMood == null,
                    onClick = { viewModel.selectMoodFilter(null) },
                    label = { Text("All Vibrations") },
                    colors = FilterChipDefaults.filterChipColors()
                )
            }
            items(moodChips) { mood ->
                FilterChip(
                    selected = selectedMood == mood,
                    onClick = { viewModel.selectMoodFilter(mood) },
                    label = { Text("#$mood") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DreamPurple.copy(alpha = 0.35f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SCANNING RESULTS (${filteredList.size})",
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.QueryStats, contentDescription = "Scans", tint = Color.DarkGray, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No resonance found matching query.", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredList) { post ->
                    DreamPostCard(post, viewModel)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CHILD SCREEN 3: CREATE SCREEN (AI DREAMPLEX CONVERTOR)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateScreen(viewModel: DreamViewModel) {
    val prompt by viewModel.dreamPromptInput.collectAsStateWithLifecycle()
    val loading by viewModel.aiExpansionLoading.collectAsStateWithLifecycle()
    val result by viewModel.scannedDreamResult.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (result == null) {
            Text(
                text = "IMAGINE NEW COGNIZANCE",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 24.sp
            )
            Text(
                text = "Describe your vision, and the core AI will weave a cohesive alternate reality, stories, interpretive data, and soundtracks.",
                fontSize = 12.sp,
                color = TextSecondaryDim,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = prompt,
                onValueChange = { viewModel.updatePromptInput(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .testTag("dream_input_field"),
                placeholder = {
                    Text(
                        "e.g., 'I walked through a neon cyberpunk forest where stars grew on trees and a deep indigo liquid spilled from crystal columns...'",
                        fontSize = 13.sp
                    )
                },
                colors = textFieldGlowColors(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Voice recorder mock indicator
                OutlinedButton(
                    onClick = { viewModel.updatePromptInput("A quiet glass garden under a dying scarlet star planetary belt") },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice", tint = NebulaCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Inject Idea", color = Color.White)
                    }
                }

                // Draft button
                OutlinedButton(
                    onClick = { viewModel.saveDraftDream() },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = "Drafts", tint = Color.LightGray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Draft", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.triggerAiDreamExpansion() },
                enabled = prompt.trim().isNotEmpty() && !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("expand_dream_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Expand", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Deconstruct & Generate", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        } else {
            // Expanded preview of dream awaiting confirmation to publish!
            Text(
                text = "NEXUS GENERATED WAVES",
                fontWeight = FontWeight.Bold,
                color = NebulaCyan,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MidnightNavy),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GlassCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Expanded Title:", fontSize = 11.sp, color = CyberPink, fontWeight = FontWeight.Bold)
                    Text(text = result!!.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "AI Woven Storyscape:", fontSize = 11.sp, color = CyberPink, fontWeight = FontWeight.Bold)
                    Text(text = result!!.aiStory, fontSize = 14.sp, color = Color.LightGray, lineHeight = 20.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Dream Soundscapes:", fontSize = 11.sp, color = CyberPink, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MusicNote, contentDescription = "Music", tint = NebulaCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = result!!.ambientTrack, fontSize = 13.sp, color = NebulaCyan)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Subconscious Analysis Interpretation:", fontSize = 11.sp, color = CyberPink, fontWeight = FontWeight.Bold)
                    Text(text = result!!.interpretation, fontSize = 12.sp, color = Color.LightGray, lineHeight = 17.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Alternative Dream Branches:", fontSize = 11.sp, color = CyberPink, fontWeight = FontWeight.Bold)
                    result!!.alternateEndings.forEach { choice ->
                        Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                            Text("• ", color = NebulaCyan, fontSize = 14.sp)
                            Text(choice, fontSize = 11.sp, color = Color.LightGray, lineHeight = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Semantic Tags:", fontSize = 11.sp, color = CyberPink, fontWeight = FontWeight.Bold)
                    FlowRow(modifier = Modifier.fillMaxWidth()) {
                        result!!.tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .padding(end = 6.dp, top = 4.dp)
                                    .background(Color.Black, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("#$tag", color = NebulaCyan, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.clearCreatedResult() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Scrub (Edit)", color = Color.White)
                }

                Button(
                    onClick = { viewModel.publishGeneratedDream() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("publish_dream_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Broadcast Dream", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CHILD SCREEN 4: NOTIFICATIONS
// -------------------------------------------------------------
@Composable
fun NotificationsScreen(viewModel: DreamViewModel) {
    val list by viewModel.notifications.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "NEXUS SIGNAL INTERCEPTORS",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 24.sp
        )
        Text(
            text = "Physical connections, likes, and node alignments mapped in high fidelity.",
            fontSize = 12.sp,
            color = TextSecondaryDim,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (list.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No signals mapping context currently.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(list) { notif ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = GlassCardColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (notif.type) {
                                            "LIKE" -> CyberPink.copy(alpha = 0.2f)
                                            "COMMENT" -> DreamPurple.copy(alpha = 0.2f)
                                            else -> NebulaCyan.copy(alpha = 0.2f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        "LIKE" -> Icons.Default.Favorite
                                        "COMMENT" -> Icons.Default.Forum
                                        else -> Icons.Default.AutoAwesome
                                    },
                                    contentDescription = null,
                                    tint = when (notif.type) {
                                        "LIKE" -> CyberPink
                                        "COMMENT" -> DreamPurple
                                        else -> NebulaCyan
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = notif.title,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = notif.message,
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CHILD SCREEN 5: PROFILE SCREEN (GALAXY PROFILE SHEETS)
// -------------------------------------------------------------
@Composable
fun ProfileScreen(viewModel: DreamViewModel) {
    val currUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val feedList by viewModel.feed.collectAsStateWithLifecycle()
    var editMode by remember { mutableStateOf(false) }
    var bioText by remember { mutableStateOf(currUser?.bio ?: "") }
    var userText by remember { mutableStateOf(currUser?.username ?: "") }

    val myDreams = feedList.filter { it.userId == currUser?.id || it.userId == "current_user" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GlassCardColor),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, GlassCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(CyberPink, DreamPurple))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currUser?.username?.take(2)?.uppercase() ?: "SO",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (editMode) {
                    OutlinedTextField(
                        value = userText,
                        onValueChange = { userText = it },
                        colors = textFieldGlowColors(),
                        shape = RoundedCornerShape(8.dp),
                        label = { Text("Display Username") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bioText,
                        onValueChange = { bioText = it },
                        colors = textFieldGlowColors(),
                        shape = RoundedCornerShape(8.dp),
                        label = { Text("Bio description") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.editProfile(bioText, userText)
                            editMode = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPink)
                    ) {
                        Text("Save System Configuration")
                    }
                } else {
                    Text(
                        text = currUser?.username ?: "Explorer",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = currUser?.bio ?: "",
                        fontSize = 12.sp,
                        color = TextSecondaryDim,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    if (currUser?.badgeName != null) {
                        Box(
                            modifier = Modifier
                                .background(CyberPink.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(currUser?.badgeName!!, fontSize = 9.sp, color = CyberPink, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStat("Frequencies", currUser?.followersCount ?: 0)
                        ProfileStat("Resonance", currUser?.likesCount ?: 0)
                        ProfileStat("Worlds", myDreams.size)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                bioText = currUser?.bio ?: ""
                                userText = currUser?.username ?: ""
                                editMode = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DreamPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Configure Grid")
                        }

                        if (currUser?.isPremium == true) {
                            Box(
                                modifier = Modifier
                                    .background(NebulaCyan.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                    .border(1.dp, NebulaCyan, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("PREMIUM VISIONS", color = NebulaCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SAVED DREAM WORLDS (${myDreams.size})",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 15.sp,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (myDreams.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No persistent spaces localized yet.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                myDreams.forEach { post ->
                    DreamPostCard(post, viewModel)
                }
            }
        }
    }
}

@Composable
fun ProfileStat(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}

// -------------------------------------------------------------
// DRAWER PAGES & INTEGRATED VIEW CHANNELS
// -------------------------------------------------------------

// Messages/Direct Chat
@Composable
fun ChatThreadScreen(viewModel: DreamViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    var inputMsg by remember { mutableStateOf("") }
    val listState = rememberScrollState()

    val stickList = listOf("🌌 Nebula Sparkle", "👾 Cyber Core", "💮 Zen Wave", "🌀 Nexus Vortex")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("NEXUS TELEPATHY (CHAT)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp)
        Text("Direct, low-latency telepathic messaging stream.", fontSize = 12.sp, color = TextSecondaryDim)

        Spacer(modifier = Modifier.height(16.dp))

        // Chat stream box
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Color.Black, RoundedCornerShape(12.dp))
                .border(0.5.dp, GlassCardBorder)
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    messages.forEach { msg ->
                        val isMe = msg.senderId == "current_user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isMe) DreamPurple else MidnightNavy)
                                    .padding(10.dp)
                                    .widthIn(max = 210.dp)
                            ) {
                                Column {
                                    Text(msg.senderUsername, fontWeight = FontWeight.Bold, color = NebulaCyan, fontSize = 10.sp)
                                    Text(msg.text, color = Color.White, fontSize = 13.sp, lineHeight = 17.sp)
                                    if (msg.stickerName != null) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .background(CyberPink.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                .padding(4.dp)
                                        ) {
                                            Text("🤖 AI STICKER: ${msg.stickerName}", fontSize = 9.sp, color = CyberPink, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Stencils sticker selectors
            Text("DEPLOY AI STICKER WAVE:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyberPink, modifier = Modifier.padding(top = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                stickList.forEach { sticker ->
                    Box(
                        modifier = Modifier
                            .background(Color.DarkGray, RoundedCornerShape(4.dp))
                            .clickable { viewModel.sendChatMessage("Look at this alignment wave!", sticker) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(sticker, fontSize = 9.sp, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMsg,
                onValueChange = { inputMsg = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                colors = textFieldGlowColors(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Compile messages...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (inputMsg.isNotBlank()) {
                        viewModel.sendChatMessage(inputMsg)
                        inputMsg = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPink)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

// Community lists page
@Composable
fun CommunitiesScreen(viewModel: DreamViewModel) {
    val communities by viewModel.communities.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("DREAM COMMUNITIES", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp)
        Text("Join communal chambers to co-author alternate spatial universes.", fontSize = 12.sp, color = TextSecondaryDim)

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(communities) { comm ->
                // Custom gradient background based on colors stored as comma string in DB
                val colorStringList = comm.bgColors.split(",")
                val gradient = remember(comm.bgColors) {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(android.graphics.Color.parseColor(colorStringList[0])),
                            Color(android.graphics.Color.parseColor(colorStringList[1]))
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(gradient)
                        .padding(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color(0xF207070B))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = comm.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Button(
                                    onClick = { viewModel.toggleCommunityJoinState(comm.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (comm.isJoined) Color.DarkGray else CyberPink)
                                ) {
                                    Text(if (comm.isJoined) "Exit Chamber" else "Synthesise Joined")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = comm.description,
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "${comm.membersCount} dreamers mapped in alignment",
                                fontSize = 10.sp,
                                color = NebulaCyan,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// Bookmarks dashboard
@Composable
fun BookmarksScreen(viewModel: DreamViewModel) {
    val list by viewModel.bookmarks.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("MARKED SPATIAL WORLDLINES", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp)
        Text("Your pinned vectors and alternate reality bookmarks.", fontSize = 12.sp, color = TextSecondaryDim)

        Spacer(modifier = Modifier.height(20.dp))

        if (list.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No bookmarks cataloged currently.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(list) { post ->
                    DreamPostCard(post, viewModel)
                }
            }
        }
    }
}

// Creator Studio analytics dashboard
@Composable
fun CreatorStudioScreen(viewModel: DreamViewModel) {
    val stats by viewModel.creatorStats.collectAsStateWithLifecycle()
    val drafts by viewModel.drafts.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("SOMNIO CREATOR STUDIO", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp)
        Text("Draft layouts, telemetry maps, and node synchronization.", fontSize = 12.sp, color = TextSecondaryDim)

        Spacer(modifier = Modifier.height(20.dp))

        // Analytical indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatsGridCard("Total Sim-Waves", stats.totalSimulations.toString(), Modifier.weight(1f))
            StatsGridCard("Nexus Nodes", stats.activeNexusNodes.toString(), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatsGridCard("Engagement Rates", "${stats.averageEngagement}%", Modifier.weight(1f))
            StatsGridCard("Remix Chains", stats.totalRemixes.toString(), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("DRAFT BOXES & SANDBOXES (${drafts.size})", fontWeight = FontWeight.Bold, color = CyberPink, fontSize = 12.sp, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))

        if (drafts.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GlassCardColor)
            ) {
                Box(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Draft sandbox is safe and empty.", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                drafts.forEach { draft ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MidnightNavy)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(draft.title, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Raw descriptions: \"${draft.originalText}\"", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Last saved local cache mapping", fontSize = 9.sp, color = NebulaCyan)
                                Button(
                                    onClick = {
                                        viewModel.updatePromptInput(draft.originalText)
                                        viewModel.deleteDreamFromFeed(draft.id)
                                        viewModel.changeActiveTab(ActiveScreen.CREATE)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DreamPurple)
                                ) {
                                    Text("Load into Core Converter", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsGridCard(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GlassCardColor),
        border = BorderStroke(0.5.dp, GlassCardBorder.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, fontSize = 10.sp, color = TextSecondaryDim)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }
}

// Core Settings Configurator
@Composable
fun SettingsScreen(viewModel: DreamViewModel) {
    val userAccount by viewModel.currentUser.collectAsStateWithLifecycle()
    var premiumToggled by remember { mutableStateOf(userAccount?.isPremium ?: false) }
    var fontScalingToggled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("SYSTEM CONFIGURATIONS", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp)
        Text("Theme synchronization, network limits, and bio deletion logs.", fontSize = 12.sp, color = TextSecondaryDim)

        Spacer(modifier = Modifier.height(20.dp))

        // Premium tier activation
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightNavy),
            border = BorderStroke(1.dp, GlassCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("SOMNIO PREMIUM ACCESS", fontWeight = FontWeight.Bold, color = CyberPink, fontSize = 14.sp)
                Text("Unlock ultra high definition visual seeds, multi-track audio synths, and cooperative multiverse persistence rooms.", fontSize = 11.sp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        userAccount?.let {
                            viewModel.toggleUserPremiumState(it.id)
                            premiumToggled = !premiumToggled
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (premiumToggled) Color.DarkGray else CyberPink),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (premiumToggled) "PREMIUM MEMBERSHIP CONFIGURED ✅" else "SUBSCRIBE WITH WAVE ENERGY")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GlassCardColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("ACCESSIBILITY & DISPLAY", fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enhance display contrasts (Default)", fontSize = 13.sp, color = Color.White)
                    Switch(checked = true, onCheckedChange = {})
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dynamic font tracking (Talkback)", fontSize = 13.sp, color = Color.White)
                    Switch(checked = fontScalingToggled, onCheckedChange = { fontScalingToggled = it })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Deletion section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GlassCardColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("BIO CONSCIOUSNESS (ACCOUNT)", fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { viewModel.deleteProfileAccount() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Terminate Somnio Profile Map")
                }
            }
        }
    }
}

// Global Super Admin Screen
@Composable
fun AdminDashboardScreen(viewModel: DreamViewModel) {
    val monitored by viewModel.systemMonitoringEnabled.collectAsStateWithLifecycle()
    val provider by viewModel.aiProvider.collectAsStateWithLifecycle()
    val userList by viewModel.userAccountsList.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("NEXUS SUPER ADMIN CONTROL", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp)
        Text("Direct management dashboard of overall database schemas, role grants, and AI moderation parameters.", fontSize = 12.sp, color = TextSecondaryDim)

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightNavy)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AI ENGINE DRIVER CORES", fontWeight = FontWeight.Bold, color = CyberPink)
                Text("Select modular provider algorithms representing dream structures.", fontSize = 11.sp)
                Spacer(modifier = Modifier.height(12.dp))

                val providers = listOf("Gemini (Auto)", "OpenAI", "DeepMind Local", "Custom REST Sync")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    providers.take(2).forEach { prov ->
                        FilterChip(
                            selected = provider == prov,
                            onClick = { viewModel.changeAiProvider(prov) },
                            label = { Text(prov) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GlassCardColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Abuse Prevention Shields", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Real-time AI text moderation", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(checked = monitored, onCheckedChange = { viewModel.toggleMonitoring() })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("REGISTERED CONSCIOUSNESS WORKBOOK (${userList.size})", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        userList.forEach { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightNavy)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(user.username, fontWeight = FontWeight.Bold, color = NebulaCyan)
                        Text("Followers: ${user.followersCount} | Badge: ${user.badgeName ?: "None"}", fontSize = 11.sp)
                    }

                    Row {
                        TextButton(onClick = { viewModel.toggleUserAdmin(user.id) }) {
                            Text(if (user.isAdmin) "Revoke Admin" else "Grant Admin", color = CyberPink, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// Help and Support Channel
@Composable
fun HelpScreen(viewModel: DreamViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("NEXUS CUSTOMER SUPPORT", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 24.sp)
        Text("Frequently compiled wave sequences and support vectors.", fontSize = 12.sp, color = TextSecondaryDim)

        Spacer(modifier = Modifier.height(20.dp))

        HelpFAQExpandable("What is basic seed tracking?", "Seeds allow recreating precise fractal structures. Entering the exact seed into other user boards lets you synchronize environmental dimensions.")
        HelpFAQExpandable("How does Gemini expand my dream text?", "Gemini structures prose pipelines using system tags, generating coherent storytelling contexts from short sentences.")
        HelpFAQExpandable("Can other waves alter my dreams?", "Only if you publish under a collaborative worldline. Otherwise, your draft boxes are localized solely on your current phone memory.")
    }
}

@Composable
fun HelpFAQExpandable(title: String, desc: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardColor),
        border = BorderStroke(0.5.dp, Color.DarkGray)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(6.dp))
            Text(desc, fontSize = 12.sp, color = Color.LightGray, lineHeight = 16.sp)
        }
    }
}

// -------------------------------------------------------------
// COMMENTS DIALOG CONTROLLER (INTEGRATED FULL SHEET MODAL)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommentDialogSheet(
    dreamId: Long,
    viewModel: DreamViewModel,
    onDismiss: () -> Unit
) {
    val comments by viewModel.activeComments.collectAsStateWithLifecycle()
    var textInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clickable(enabled = false) {}, // prevent click-through
            colors = CardDefaults.cardColors(containerColor = MidnightNavy),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            border = BorderStroke(1.dp, GlassCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Toolbar header
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("NEXUS WAVE DISCUSSIONS", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))

                // Scrollable comments section
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (comments.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No wave alignments recorded yet. Speak up traveler!", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    } else {
                        items(comments) { comment ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = GlassCardColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Brush.linearGradient(listOf(NebulaCyan, DreamPurple))),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(comment.authorUsername.take(2).uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(comment.authorUsername, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                                        Text(comment.text, color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp), lineHeight = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Send comment box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("comment_input_field"),
                        placeholder = { Text("Publish feedback to core node...") },
                        colors = textFieldGlowColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                viewModel.postComment(dreamId, textInput)
                                textInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPink)
                    ) {
                        Text("Broadcast")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// UTILITIES AND HELPERS
// -------------------------------------------------------------
@Composable
fun modifierGradientBackground(modifier: Modifier = Modifier): Modifier {
    return modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                listOf(MidnightNavy, DeepSpaceBlack)
            )
        )
}

@Composable
fun textFieldGlowColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.LightGray,
    focusedContainerColor = Color.Black,
    unfocusedContainerColor = Color.Black.copy(alpha = 0.5f),
    focusedBorderColor = CyberPink,
    unfocusedBorderColor = Color.DarkGray
)

fun formatTime(timestamp: Long): String {
    val duration = System.currentTimeMillis() - timestamp
    return when {
        duration < 60000 -> "Just Now"
        duration < 3600000 -> "${duration / 60000}m ago"
        duration < 86400000 -> "${duration / 3600000}h ago"
        else -> "${duration / 86400000}d ago"
    }
}
