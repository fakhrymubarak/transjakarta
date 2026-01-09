plugins {
    id("org.jetbrains.kotlinx.kover")
}

kover {
    reports {
        verify {
            rule {
                minBound(0.90)
            }
        }
    }
}
