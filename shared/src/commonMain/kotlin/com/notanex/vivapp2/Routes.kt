package com.notanex.vivapp2

import com.notanex.vivapp2.models.Products
import kotlinx.serialization.Serializable

@Serializable
data object LandingRoute
@Serializable
data object ProductsRoute

@Serializable
data object ScanRoute

@Serializable
data class ConfirmItemRoute(val sapNumber: String)