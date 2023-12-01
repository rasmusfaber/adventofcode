package xyz.faber.adventofcode.util

import java.util.function.BiFunction

fun <K, V> Map<K, V>.reversed(): Map<V, List<K>> = this.toList()
    .groupBy { pair -> pair.second }
    .mapValues { entry -> entry.value.map { it.first } }

fun <K, V> Map<K, V>.reversedUnique(): Map<V, K> = mutableMapOf<V, K>().also { newMap ->
    entries.forEach { newMap.put(it.value, it.key) }
}

fun <K, V> Iterable<Pair<K, V>>.toMap(mergeFunction: (V, V) -> V): Map<K, V> {
    if (this is Collection) {
        return when (size) {
            0 -> emptyMap()
            1 -> mapOf(if (this is List) this[0] else iterator().next())
            else -> toMap(LinkedHashMap<K, V>(), mergeFunction)
        }
    }
    return toMap(LinkedHashMap<K, V>(), mergeFunction)
}

private fun <K, V, M : MutableMap<K, V>> Iterable<Pair<K, V>>.toMap(destination: M, mergeFunction: BiFunction<in V, in V, out V?>): M {
    this.forEach { (k, v) -> destination.merge(k, v!!, mergeFunction) }
    return destination
}
