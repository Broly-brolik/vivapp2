package com.notanex.vivapp2.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import vivapp2.shared.generated.resources.Res
import vivapp2.shared.generated.resources.action_replay_tutorial
import vivapp2.shared.generated.resources.faq1_a
import vivapp2.shared.generated.resources.faq1_q
import vivapp2.shared.generated.resources.faq2_a
import vivapp2.shared.generated.resources.faq2_q
import vivapp2.shared.generated.resources.faq3_a
import vivapp2.shared.generated.resources.faq3_q
import vivapp2.shared.generated.resources.faq4_a
import vivapp2.shared.generated.resources.faq4_q
import vivapp2.shared.generated.resources.faq5_a
import vivapp2.shared.generated.resources.faq5_q
import vivapp2.shared.generated.resources.faq6_a
import vivapp2.shared.generated.resources.faq6_q
import vivapp2.shared.generated.resources.faq7_a
import vivapp2.shared.generated.resources.faq7_q
import vivapp2.shared.generated.resources.faq8_a
import vivapp2.shared.generated.resources.faq8_q
import vivapp2.shared.generated.resources.help_intro
import vivapp2.shared.generated.resources.help_more

private val faqs: List<Pair<StringResource, StringResource>> = listOf(
    Res.string.faq1_q to Res.string.faq1_a,
    Res.string.faq2_q to Res.string.faq2_a,
    Res.string.faq3_q to Res.string.faq3_a,
    Res.string.faq4_q to Res.string.faq4_a,
    Res.string.faq5_q to Res.string.faq5_a,
    Res.string.faq6_q to Res.string.faq6_a,
    Res.string.faq7_q to Res.string.faq7_a,
    Res.string.faq8_q to Res.string.faq8_a,
)

@Composable
fun HelpScreen(
    onReplayTutorial: () -> Unit,
    contentPadding: PaddingValues,
) {
    var expandedIndex by rememberSaveable { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                stringResource(Res.string.help_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )
        }
        itemsIndexed(faqs) { index, (question, answer) ->
            FaqCard(
                question = stringResource(question),
                answer = stringResource(answer),
                expanded = expandedIndex == index,
                onToggle = { expandedIndex = if (expandedIndex == index) -1 else index }
            )
        }
        item {
            OutlinedButton(
                onClick = onReplayTutorial,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.School, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.action_replay_tutorial))
            }
        }
        item {
            Text(
                stringResource(Res.string.help_more),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
private fun FaqCard(
    question: String,
    answer: String,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)
    Surface(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    question,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(rotation)
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Text(
                    answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
