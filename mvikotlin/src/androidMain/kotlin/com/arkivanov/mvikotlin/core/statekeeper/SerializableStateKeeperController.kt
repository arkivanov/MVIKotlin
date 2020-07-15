package com.arkivanov.mvikotlin.core.statekeeper

import android.os.Bundle
import java.io.Serializable

interface SerializableStateKeeperController : StateKeeperController<Bundle, Serializable>
