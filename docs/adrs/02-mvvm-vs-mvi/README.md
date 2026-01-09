# ADR: Preferred presentation pattern (MVVM vs. MVI)

- Status: Accepted
- Date: 9/1/2025
- Deciders: Fakhry
- Drivers: maintainability, testability, consistency across features, and alignment with Jetpack/Compose.

## Table of contents
- [Context](#context)
- [Decision](#decision)
- [Options considered](#options-considered)
- [Pros and cons](#pros-and-cons)
  - [MVVM as default](#mvvm-as-default)
  - [MVI/Redux-style](#mvi-redux-style)
  - [Mixed MVVM + MVI](#mixed-mvvm--mvi)
- [Comparison](#comparison)
- [Performance and size](#performance-and-size)
- [Consequences and guidelines](#consequences-and-guidelines)
- [References](#references)

## Context
We need a consistent presentation-layer pattern across Android modules to improve readability, testability, and onboarding. MVVM is the de facto Jetpack-aligned pattern for both Views and Compose, while alternatives like MVI/Redux-style or MVP exist with different trade-offs.

## Decision
Use MVVM by default. Apply MVI only on complex flows that need strict unidirectional data flow and traceability, keeping MVI implementations ViewModel-centric.

## Options considered
1) MVVM
2) MVI/Redux-style for select flows requiring strict unidirectional data flow  
3) Mixed MVVM + MVI (MVVM overall, MVI-like reducers for complex screens)

## Pros and cons

### MVVM
- Pros:
  - Native fit with Jetpack ViewModel, LiveData/Flow, and Compose state management.
  - Clear separation of UI and logic; straightforward unit testing of ViewModel.
  - Less boilerplate than older patterns; simpler mental model than heavy MVI.
  - Simple example (Compose + StateFlow):
    ```kotlin
    data class UiState(val loading: Boolean = false, val items: List<String> = emptyList())

    class SampleViewModel(private val repo: ItemsRepo) : ViewModel() {
        private val _state = MutableStateFlow(UiState())
        val state: StateFlow<UiState> = _state

        fun load() = viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            val data = repo.fetch()
            _state.value = UiState(items = data)
        }
    }

    @Composable
    fun SampleScreen(vm: SampleViewModel) {
        val ui = vm.state.collectAsState()
        if (ui.value.loading) CircularProgressIndicator()
        LazyColumn { items(ui.value.items) { Text(it) } }
    }
    ```
- Cons:
  - Can devolve into “Massive ViewModel” without discipline.
  - Needs clear state management to avoid ad-hoc mutable fields.

### MVI/Redux-style
- Pros:
  - Explicit, unidirectional data flow; events -> reducer -> state.
  - Strong traceability/debugging; time-travel debugging possible.
  - Works well with Compose’s declarative model.
  - Simple reducer example:
    ```kotlin
    sealed interface Action { object Load : Action; data class Loaded(val items: List<String>) : Action }
    data class State(val loading: Boolean = false, val items: List<String> = emptyList())

    fun reduce(state: State, action: Action): State = when (action) {
        Action.Load -> state.copy(loading = true)
        is Action.Loaded -> State(loading = false, items = action.items)
    }

    class Store(private val repo: ItemsRepo) {
        private val _state = MutableStateFlow(State())
        val state: StateFlow<State> = _state

        suspend fun dispatch(action: Action) {
            when (action) {
                Action.Load -> {
                    _state.value = reduce(_state.value, action)
                    val items = repo.fetch()
                    _state.value = reduce(_state.value, Action.Loaded(items))
                }
                is Action.Loaded -> _state.value = reduce(_state.value, action)
            }
        }
    }
    
    @Composable
    fun StoreScreen(store: Store) {
        val state by store.state.collectAsState()
        LaunchedEffect(Unit) { store.dispatch(Action.Load) }

        if (state.loading) CircularProgressIndicator()
        LazyColumn { items(state.items) { Text(it) } }
    }
    ```
- Cons:
  - More boilerplate (actions, reducers, middleware).
  - Overkill for simple screens; state explosion risk.

### Mixed MVVM + MVI
- Pros:
  - Uses MVVM as the baseline while applying reducer-style state handling on complex screens.
  - Keeps boilerplate lower than full MVI across the app.
  - Preserves traceability for critical flows without imposing MVI everywhere.
  - Example approach: ViewModel exposes `StateFlow` like MVVM but internally routes events through a reducer for select complex screens; other screens stay plain MVVM.
- External library example (Orbit MVI—an MVVM+MVI hybrid):
  ```kotlin
  data class UiState(val loading: Boolean = false, val items: List<String> = emptyList())
  sealed interface SideEffect { data class Error(val msg: String) : SideEffect }

  class OrbitVm(private val repo: ItemsRepo) : ContainerHost<UiState, SideEffect>, ViewModel() {
      override val container = container<UiState, SideEffect>(UiState())

      fun load() = intent {
          reduce { state.copy(loading = true) }
          runCatching { repo.fetch() }
              .onSuccess { items -> reduce { state.copy(loading = false, items = items) } }
              .onFailure { postSideEffect(SideEffect.Error(it.message ?: "Unknown error")) }
      }
  }

  @Composable
  fun OrbitScreen(vm: OrbitVm = viewModel()) {
      val state by vm.container.stateFlow.collectAsState()
      val sideEffects = vm.container.sideEffectFlow

      LaunchedEffect(Unit) { vm.load() }
      LaunchedEffect(sideEffects) {
          sideEffects.collect { /* handle errors/snacks */ }
      }

      if (state.loading) CircularProgressIndicator()
      LazyColumn { items(state.items) { Text(it) } }
  }
  ```
- Cons:
  - Inconsistent patterns across screens if not well documented.
  - Still adds reducer/action overhead on selected screens.
  - Pulls in an external library (e.g., Orbit); must be justified and standardized if adopted.

## Comparison
| Criteria                   | MVVM (default)                               | MVI/Redux-style                               | Mixed MVVM + MVI (e.g., Orbit)          |
|----------------------------|----------------------------------------------|-----------------------------------------------|-----------------------------------------|
| Fit with Jetpack/Compose   | Strong (ViewModel, State/Flow)               | Good (unidirectional data fits Compose)       | Good; ViewModel + reducer container     |
| Boilerplate                | Moderate                                     | Higher (actions/reducers/middleware)          | Moderate; container/reducers per screen |
| Testability                | High (ViewModel isolation)                   | High (pure reducers, state testing)           | High; reducers testable where applied   |
| State management clarity   | Good with discipline                         | Strong; explicit state transitions            | Strong on reducer screens; else MVVM    |
| Lifecycle handling         | Built into ViewModel scopes                  | Via ViewModel or custom scopes                | Built into ViewModel scopes             |
| Onboarding                 | Easier for Android teams                     | Steeper; more concepts                        | Moderate; must learn library patterns   |
| Traceability/debugging     | Moderate; depends on logging                 | Strong; time-travel/loggable state            | Strong on reducer screens               |

## Performance and size
- Runtime differences among MVVM, MVI, and mixed approaches are negligible; impacts are mainly in boilerplate and indirection.
- Build size impact is minimal; differences come from supporting libraries (e.g., Redux/MVI helpers).

## Consequences and guidelines
- Default to MVVM for new screens (Views or Compose) using Jetpack ViewModel + State/Flow.
- Apply unidirectional data flow within MVVM where practical to avoid ad-hoc mutable state.
- Allow MVI for complex flows needing strict traceability; use a light wrapper to limit boilerplate.
- Provide shared guidance on ViewModel scoping, state holders, and event handling to prevent “Massive ViewModel.”

## References
- Android app architecture: https://developer.android.com/topic/architecture
- Guide to app architecture (Jetpack): https://developer.android.com/topic/architecture#recommended-app-arch
- UDF in Compose: https://developer.android.com/jetpack/compose/architecture
