package com.bobbyesp.spowlo.presentation.ui.pages.settings.downloader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.bobbyesp.spowlo.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.database.CommandTemplate
import com.bobbyesp.spowlo.presentation.ui.components.ConfirmButton
import com.bobbyesp.spowlo.presentation.ui.components.LinkButton
import com.bobbyesp.spowlo.util.DatabaseUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CommandTemplateDialog(
    commandTemplate: CommandTemplate = CommandTemplate(0, "", ""),
    newTemplate: Boolean = false,
    onDismissRequest: () -> Unit = {},
    confirmationCallback: () -> Unit = {},
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var templateText by remember { mutableStateOf(commandTemplate.template) }
    var templateName by remember { mutableStateOf(commandTemplate.name) }
    var isError by remember { mutableStateOf(false) }
    AlertDialog(
        icon = { Icon(if (newTemplate) Icons.Outlined.Add else Icons.Outlined.EditNote, null) },
        title = {
            Text(
                stringResource(if (newTemplate) R.string.new_template else R.string.edit_custom_command_template)
            )
        },
        onDismissRequest = {},
        confirmButton = {
            ConfirmButton {
                if (templateName.isBlank() || templateName.isEmpty()) {
                    isError = true
                } else {
                    scope.launch {
                        if (newTemplate) {
                            DatabaseUtil.insertTemplate(
                                CommandTemplate(0, templateName, templateText)
                            )
                        } else {
                            DatabaseUtil.updateTemplate(
                                commandTemplate.copy(
                                    name = templateName, template = templateText
                                )
                            )
                        }
                    }
                    confirmationCallback()
                    onDismissRequest()
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dismiss))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.edit_template_desc),
                    style = MaterialTheme.typography.bodyLarge
                )
                OutlinedTextField(
                    modifier = Modifier.padding(top = 16.dp),
                    value = templateName,
                    onValueChange = {
                        templateName = it
                        isError = false
                    },
                    label = { Text(stringResource(R.string.template_label)) },
                    maxLines = 1,
                    isError = isError
                )
                OutlinedTextField(
                    modifier = Modifier.padding(vertical = 12.dp),
                    value = templateText,
                    onValueChange = { templateText = it },
                    trailingIcon = {
                        IconButton(onClick = {
                            clipboardManager.getText().toString().let { templateText = it }
                        }) { Icon(Icons.Outlined.ContentPaste, stringResource(R.string.paste)) }
                    },
                    label = { Text(stringResource(R.string.custom_command_template)) },
                    maxLines = 12
                )
                LinkButton()
            }
        })
}