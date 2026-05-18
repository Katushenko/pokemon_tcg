package com.pokemontcg.collector.ui.carddetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.pokemontcg.collector.domain.model.Card

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: CardDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = (uiState as? CardDetailUiState.Success)?.card?.name ?: "Card Detail"
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (uiState is CardDetailUiState.Success) {
                        IconButton(
                            onClick = { viewModel.removeFromCollection(onNavigateBack) }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Remove from collection",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is CardDetailUiState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CardDetailUiState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is CardDetailUiState.Success -> {
                CardDetailContent(
                    card = state.card,
                    onWishlistToggle = viewModel::toggleWishlist,
                    onForTradeToggle = viewModel::toggleForTrade,
                    onNotesUpdate = viewModel::updateNotes,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun CardDetailContent(
    card: Card,
    onWishlistToggle: () -> Unit,
    onForTradeToggle: () -> Unit,
    onNotesUpdate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var notesText by remember(card.notes) { mutableStateOf(card.notes) }
    var notesEditing by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = card.imageUrl,
            contentDescription = card.name,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .aspectRatio(0.72f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(16.dp))

        CardMetadataSection(card = card)

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = card.inWishlist,
                onClick = onWishlistToggle,
                label = { Text("Wishlist") },
                leadingIcon = {
                    Icon(
                        if (card.inWishlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            )
            FilterChip(
                selected = card.forTrade,
                onClick = onForTradeToggle,
                label = { Text("For Trade") },
                leadingIcon = {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        if (card.attacks.isNotEmpty()) {
            AttacksSection(attacks = card.attacks)
            Spacer(Modifier.height(16.dp))
        }

        NotesSection(
            notes = notesText,
            editing = notesEditing,
            onNotesChange = { notesText = it },
            onEditToggle = {
                if (notesEditing) onNotesUpdate(notesText)
                notesEditing = !notesEditing
            }
        )
    }
}

@Composable
private fun CardMetadataSection(card: Card) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MetadataRow("Name", card.name)
            MetadataRow("Set", card.setName)
            MetadataRow("Number", card.number)
            card.pokemonType?.let { MetadataRow("Type", it) }
            card.hp?.let { MetadataRow("HP", it.toString()) }
            card.rarity?.let { MetadataRow("Rarity", it) }
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun AttacksSection(attacks: List<com.pokemontcg.collector.domain.model.Attack>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Attacks",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            attacks.forEach { attack ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(attack.name, style = MaterialTheme.typography.labelLarge)
                        if (attack.text.isNotBlank()) {
                            Text(
                                attack.text,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (attack.damage.isNotBlank()) {
                        Text(
                            text = attack.damage,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun NotesSection(
    notes: String,
    editing: Boolean,
    onNotesChange: (String) -> Unit,
    onEditToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notes",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(onClick = onEditToggle) {
                    Text(if (editing) "Save" else "Edit")
                }
            }
            if (editing) {
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Add personal notes…") },
                    minLines = 3
                )
            } else {
                Text(
                    text = notes.ifBlank { "No notes yet. Tap Edit to add." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (notes.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
