package ua.com.compose.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.com.compose.R
import ua.com.compose.composable.DialogBottomSheet
import ua.com.compose.composable.DialogConfirmButton
import ua.com.compose.data.ESortDirection
import ua.com.compose.data.ESortType
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DialogSort(type: ESortType?, direction: ESortDirection?, onDone: (type: ESortType?, direction: ESortDirection?) -> Unit, onDismissRequest: () -> Unit) {
    DialogBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {

        val view = LocalView.current
        var _type: ESortType? by remember { mutableStateOf(type) }
        var _direction: ESortDirection? by remember { mutableStateOf(direction) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = stringResource(id = R.string.color_pick_sort_type),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight(400),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 24.dp, bottom = 5.dp, end = 24.dp)
                )

                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp))

                FlowRow(verticalArrangement = Arrangement.spacedBy((-8).dp, Alignment.Top), modifier = Modifier.padding(start = 25.dp, end = 25.dp)) {
                    ESortType.values().forEach { item ->
                        FilterChip(
                            selected = item == _type ,
                            border = null,
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            onClick = {
                                view.vibrate(EVibrate.BUTTON)
                                _type = item
                            },
                            label = {
                                Text(text = stringResource(id = item.stringResId), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontSize = 16.sp)
                            }
                        )
                    }

                }

                Text(
                    text = stringResource(id = R.string.color_pick_sort_direction),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight(400),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 24.dp, bottom = 5.dp, end = 24.dp)
                )

                FlowRow(verticalArrangement = Arrangement.spacedBy((-8).dp, Alignment.Top), modifier = Modifier.padding(start = 25.dp, end = 25.dp)) {
                    ESortDirection.values().forEach { item ->
                        FilterChip(
                            selected = item == _direction ,
                            border = null,
                            trailingIcon = {
                                val icon = if(item == ESortDirection.ASC) {
                                    painterResource(id = R.drawable.ic_sort_down)
                                } else {
                                    painterResource(id = R.drawable.ic_sort_up)
                                }
                                Icon(painter = icon, modifier = Modifier.size(24.dp), contentDescription = null)
                            },
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
                                iconColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            onClick = {
                                view.vibrate(EVibrate.BUTTON)
                                _direction = item
                            },
                            label = {
                                Text(text = stringResource(id = item.stringResId), fontWeight = FontWeight(600), modifier = Modifier.fillMaxWidth(), fontSize = 16.sp)
                            }
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(end = 8.dp, top = 16.dp, bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    DialogConfirmButton(text = stringResource(id = R.string.color_pick_cancel)) {
                        onDismissRequest.invoke()
                    }
                    DialogConfirmButton(text = stringResource(id = R.string.color_pick_ok)) {
                        onDone.invoke(_type, _direction)
                        onDismissRequest.invoke()
                    }
                }
        }
    }
}