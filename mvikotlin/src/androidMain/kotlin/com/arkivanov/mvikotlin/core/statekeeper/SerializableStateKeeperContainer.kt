package com.arkivanov.mvikotlin.core.statekeeper

import android.os.Bundle
import java.io.Serializable

interface SerializableStateKeeperContainer : StateKeeperContainer<Bundle, Serializable>
