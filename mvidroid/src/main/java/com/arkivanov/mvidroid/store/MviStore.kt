package com.arkivanov.mvidroid.store

import android.support.annotation.MainThread
import com.arkivanov.kfunction.KConsumer
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Base interface of Store. Store consumes Intents and produces States. It also can produce Labels as side effects.
 * Use [MviBoundary][com.arkivanov.mvidroid.boundary.MviBoundary] to glue Store with UI.
 *
 * Implementation example:
 * ```
 * class ExampleStore(storeFactory: MviStoreFactory) : MviStore<ExampleStore.State, ExampleStore.Intent> by storeFactory(
 *     initialState = State(),
 *     bootstrapper = Bootstrapper,
 *     intentToAction = IntentToAction,
 *     reducer = Reducer
 * ) {
 *
 *     data class State()
 *
 *     sealed class Intent {
 *         // Your Intents here
 *     }
 *
 *     private sealed class Result {
 *         // Your Results here
 *     }
 *
 *     private abstract class Action : MviAction<State, Result> {
 *         object ExampleAction : Action() {
 *             override fun invoke(getState: KSupplier<S>, dispatch: KConsumer<R>, publish: KConsumer<Any>): Disposable? {
 *                 // Process your action here. Return Disposable if you are doing any background work, return null otherwise.
 *                 // You can access current State by using property "state"
 *                 // You can dispatch Results using method "dispatch"
 *                 // You can publish Labels using method "publish"
 *             }
 *         }
 *         // More Actions here
 *     }
 *
 *     private object Bootstrapper : MviBootstrapper<Intent> {
 *         override fun invoke(dispatch: KConsumer<Intent>): Disposable? {
 *             // Initialize you store here, e.g. subscribe to data sources
 *         }
 *     }
 *
 *     private object IntentToAction : MviIntentToAction<Intent, Action> {
 *         override fun invoke(intent: Intent): Action {
 *             // Map provided Intent to Action here
 *         }
 *     }
 *
 *     private object Reducer : MviReducer<State, Result> {
 *         override fun State.invoke(result: Result): State {
 *             // Transform current State into a new State using provided Result
 *         }
 *     }
 * }
 * ```
 *
 * @param S type of State
 * @param I type of Intent
 */
interface MviStore<S : Any, in I : Any> : KConsumer<I>, Disposable {

    /**
     * Provides access to current state, must be accessed only from Main thread
     */
    @get:MainThread
    val state: S

    /**
     * Observable of States. Emissions are performed on Main thread.
     */
    val states: Observable<S>

    /**
     * Observable of Labels. emissions are performed on Main thread.
     */
    val labels: Observable<*>

    /**
     * Sends Intent to Store, must me called only on Main thread
     */
    @MainThread
    override fun invoke(intent: I)

    /**
     * Disposes the Store and all its active operations, must be called only on Main thread
     */
    @MainThread
    override fun dispose()

    /**
     * Checks whether Store is disposed or not, must be called only on Main thread
     *
     * @return true if Store is disposed, false otherwise
     */
    @MainThread
    override fun isDisposed(): Boolean
}
