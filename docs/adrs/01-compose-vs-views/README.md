# ADR: Jetpack Compose vs. XML Views for Android UI

- Status: Accepted
- Date: 9/1/2026
- Deciders: Fakhry
- Drivers: developer productivity, UI consistency, maintainability, and long-term platform alignment.

## Table of contents
- [Context](#context)
- [Decision](#decision)
- [Options considered](#options-considered)
- [Pros and cons](#pros-and-cons)
  - [Compose-first](#compose-first)
  - [XML Views](#xml-views)
- [Comparison](#comparison)
- [Performance and size](#performance-and-size)
- [Consequences and guidelines](#consequences-and-guidelines)
- [References](#references)

## Context
We need a clear UI framework strategy. Jetpack Compose is the modern declarative UI toolkit with strong momentum and better state handling, while XML Views remain widely used with mature tooling and existing codebases. The choice affects developer velocity, theming consistency, testability, and future platform support.

## Decision
Adopt Jetpack Compose as the primary UI stack considering developer productivity, UI consistency, maintainability, and long-term stability.

## Options considered
1) Compose-first for the new UI, Views only for legacy/interop  
2) XML Views as primary, Compose only when necessary

## Pros and cons

### Compose
- Pros:
  - Declarative UI with built-in state handling; fewer XML/layout files and reduced boilerplate.
  - Strong alignment with modern Android guidance; better integration with Kotlin/coroutines.
  - Easier theming and dynamic UI (e.g., dark mode, adaptive layouts).
  - UI tests can target semantics; previews speed up iteration.
  - Less code to review during PR reviews.
- Cons:
  - Requires newer tooling and developer upskilling.
  - Some components or edge cases are still maturing.
  - Performance tuning may be needed for complex, long lists if recomposition is not well managed.

### XML Views
- Pros:
  - Stable, mature ecosystem;
  - Wide library support and predictable performance characteristics.
  - Straightforward interoperability with legacy code and existing layouts.
- Cons:
  - Imperative/update-heavy; higher risk of state bugs and boilerplate.
  - Theming and dynamic layouts are more cumbersome than Compose.
  - Diverging from Google’s forward roadmap; fewer new features will target Views first.
  - Not prioritized by Google for further development.

## Comparison
| Criteria               | Compose-first                                                      | XML Views                                          |
|------------------------|--------------------------------------------------------------------|----------------------------------------------------|
| Developer productivity | Higher; declarative, fewer files, fast previews                    | Lower; XML + imperative updates, more boilerplate  |
| Line of Code           | 65% lesser LoC compared with XML Views.                            | More LoC due to XML + Kotlin updates               |
| State handling         | Built-in; composable state and Flow/LiveData friendly              | Manual; must sync views with state changes         |
| Theming/adaptivity     | Easier dynamic theming and responsive UIs                          | More manual; styles/themes XML heavier to manage   |
| Ecosystem/roadmap      | Google’s primary investment; rapid updates                         | Stable but lower priority for new features         |
| Interop/legacy         | Needs interop for existing Views; ComposeView adds glue            | Native; fits legacy code directly                  |
| Performance            | Good when recomposition managed; needs profiling for complex lists | Predictable; mature performance characteristics    |
| Testing                | Semantics-based UI testing                                         | Espresso/UIAutomator; more boilerplate             |
| Build toolchain        | Requires recent AGP/Kotlin and Compose compiler plugin             | Works with older toolchains; Java/Kotlin supported |

## Performance and size
- Runtime performance: Comparable for typical screens; Compose needs recomposition discipline for very dynamic lists. Views have predictable performance but can be verbose to update. A 2023 study (Szczukin, J. – “Performance analysis of user interface implementation methods in mobile applications”) found negligible average latency differences between declarative and imperative UIs on Android, with Compose incurring small spikes when recomposition was poorly constrained.
- Binary size: Compose adds runtime libs; R8/Proguard trims unused parts. Views rely on platform widgets; the difference is small.
- Build times: Compose may add overhead due to the Compose compiler plugin; Views avoid this but require more XML/boilerplate.

## Consequences and guidelines
- Establish recomposition best practices (stable params, hoist state, avoid unnecessary recomposition in lists).
- Standardize theming with Material 3 in Compose; align any remaining Views with the same design tokens.
- Keep the toolchain (AGP/Kotlin) aligned with current Compose requirements.

## References
- Compose overview: https://developer.android.com/jetpack/compose
- Interop guidance: https://developer.android.com/jetpack/compose/interop/interop-apis
- Performance tips: https://developer.android.com/jetpack/compose/performance
- Szczukin, J. (2023). Performance analysis of user interface implementation methods in mobile applications. Journal of Computer Sciences Institute.

## Line of Code Comparison Creating a Simple of List of Text

### Compose
It took only **22 LoC** to show a simple list of text.
```kotlin
val dummyTexts = List(100) { "Text $it" }
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        items(dummyTexts.size) {
                            Text(dummyTexts[it])
                        }
                    }
                }
            }
        }
    }
}
```

### XML Views
It took **65 LoC** to show a simple list of text.
```xml
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
          android:id="@+id/itemText"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:textAppearance="?attr/textAppearanceBodyLarge" />
```
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:paddingHorizontal="16dp"
        android:paddingVertical="12dp"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:listitem="@layout/item_text" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

```kotlin
val dummyTexts = List(100) { "Text $it" }
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TextListAdapter(dummyTexts)
    }
}
private class TextListAdapter(
    private val items: List<String>
) : RecyclerView.Adapter<TextListAdapter.TextViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_text, parent, false)
        return TextViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.itemText)

        fun bind(text: String) {
            textView.text = text
        }
    }
}
```