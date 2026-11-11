package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AuraColorScheme = darkColorScheme(
    primary = AuraCyan,
    onPrimary = AuraDarkBackground,
    primaryContainer = AuraDarkCardElevated,
    onPrimaryContainer = AuraCyan,
    secondary = AuraElectricBlue,
    onSecondary = AuraTextPrimary,
    secondaryContainer = AuraDarkCard,
    onSecondaryContainer = AuraTextPrimary,
    tertiary = AuraViolet,
    onTertiary = AuraTextPrimary,
    tertiaryContainer = AuraVioletDim,
    onTertiaryContainer = AuraCyan,
    background = AuraDarkBackground,
    onBackground = AuraTextPrimary,
    surface = AuraDarkSurface,
    onSurface = AuraTextPrimary,
    surfaceVariant = AuraDarkCard,
    onSurfaceVariant = AuraTextSecondary,
    outline = AuraBorder,
    outlineVariant = AuraBorderLight,
    error = AuraEmergencyRed,
    onError = AuraTextPrimary,
    errorContainer = AuraDarkCardElevated,
    onErrorContainer = AuraEmergencyGlow
)

@Composable
fun AuraMeshTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AuraColorScheme,
        typography = Typography,
        content = content
    )
}
