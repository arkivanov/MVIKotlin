package com.arkivanov.mvidroid.sample.component

import com.arkivanov.mvidroid.component.MviComponent
import com.arkivanov.mvidroid.sample.app.App
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module

internal inline fun <reified C : MviComponent<*, *>> createComponent(
    scope: Scope? = null,
    moduleBindings: Module.() -> Unit = {}
): C {
    val componentScope = scope ?: Toothpick.openScopes(App.SCOPE_NAME, Any())
    componentScope.installModules(
        Module().apply {
            moduleBindings()

            if (scope == null) {
                bind(Function0::class.java)
                    .withName(OnDisposeAction::class.java)
                    .toInstance { Toothpick.closeScope(componentScope.name) }
            }
        }
    )

    return componentScope.getInstance(C::class.java)
}
