package com.cherrymooncake.wardrobe_android.core.navigation

sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object Wardrobe : NavRoutes("wardrobe")
    object Outfits : NavRoutes("outfits")
    object Profile : NavRoutes("profile")
    object Admin : NavRoutes("admin")

    object MaskEditor {
        const val route = "mask_editor/{itemId}"
        fun createRoute(itemId: String) = "mask_editor/$itemId"
    }

    object Ootd {
        const val route = "ootd?date={date}"
        fun createRoute(date: String? = null) = if (date != null) "ootd?date=$date" else "ootd"
    }

    object Calendar : NavRoutes("calendar")

    object OutfitConstructor {
        const val route = "outfit_constructor?outfitId={outfitId}&templateId={templateId}&mode={mode}&date={date}"

        fun createRoute(
            outfitId: String? = null,
            templateId: String? = null,
            isTemplateEdit: Boolean = false,
            date: String? = null
        ): String {
            val params = mutableListOf<String>()
            if (outfitId != null) params.add("outfitId=$outfitId")
            if (templateId != null) params.add("templateId=$templateId")
            if (isTemplateEdit) params.add("mode=edit_template")
            if (date != null) params.add("date=$date")

            return if (params.isEmpty()) "outfit_constructor" else "outfit_constructor?${params.joinToString("&")}"
        }
    }
}