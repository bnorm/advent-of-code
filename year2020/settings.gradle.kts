rootProject.name = "year2020"

include(":util")
for (i in 1..25) {
    include(":day-${i.toString().padStart(2, '0')}")
}
