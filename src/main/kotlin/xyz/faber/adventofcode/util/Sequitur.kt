package xyz.faber.adventofcode.util

private const val prime = 2265539
private const val maxTerminals = 1000000


abstract class Symbol(protected var sequitur: Sequitur, val value: Int, previous: Symbol?, next: Symbol?) {
    constructor(sequitur: Sequitur, value: Int):this(sequitur, value, null, null)

    var previous: Symbol? = previous
    var next: Symbol? = next


    /**
     * Abstract method: cleans up for symbol deletion.
     */
    abstract fun cleanUp()

    /**
     * Inserts a symbol after this one.
     */
    fun insertAfter(toInsert: Symbol) {
        sequitur.join(toInsert, next!!)
        sequitur.join(this, toInsert)
    }

    /**
     * Removes the digram from the hash table. Overwritten in sub class guard.
     */
    open fun deleteDigram() {
        if (next!!.isGuard) {
            return
        }
        val dummy = sequitur.digrams[this]
        // Only delete digram if its exactly the stored one.
        if (dummy === this) {
            sequitur.digrams.remove(this)
        }
    }

    /**
     * Returns true if this is the guard symbol. Overwritten in subclass guard.
     */
    open val isGuard: Boolean
         get() = false

    /**
     * Returns true if this is a non-terminal. Overwritten in subclass
     * nonTerminal.
     */
    open val isNonTerminal: Boolean
        get() = false

    /**
     * Checks a new digram. If it appears elsewhere, deals with it by calling
     * match(), otherwise inserts it into the hash table. Overwritten in
     * subclass guard.
     */
    open fun check(): Boolean {
        if (next!!.isGuard) {
            return false
        }
        val found = sequitur.digrams[this]
        if (found == null) {
            sequitur.digrams.put(this, this)
            return false
        }
        if (found.next !== this) {
            match(this, found)
        }
        return true
    }

    /**
     * Replace a digram with a non-terminal.
     */
    fun substituteExisting(r: Rule) {
        cleanUp()
        next!!.cleanUp()
        previous!!.insertAfter(NonTerminal(sequitur, r))
        if (!previous!!.check()) {
            previous!!.next!!.check()
        }
    }

    fun substitute(r: Rule) {
        cleanUp()
        next!!.cleanUp()
        previous!!.insertAfter(NonTerminal(sequitur, r))
    }

    fun checkAfterSubstitute(r: Rule) {
        if (!previous!!.check()) {
            previous!!.next!!.check()
        }
    }

    /**
     * Deal with a matching digram.
     */
    fun match(newD: Symbol, matching: Symbol) {
        val r: Rule
        if (matching.previous!!.isGuard && matching.next!!.next!!.isGuard) { // reuse an existing rule
            r = (matching.previous as Guard).rule!!
            newD.substitute(r)
            newD.checkAfterSubstitute(r)
        } else { // create a new rule
            r = Rule(sequitur)
            val first = newD.clone()
            val second = newD.next!!.clone()
            r.guard!!.next = first
            first.previous = r.guard
            first.next = second
            second.previous = first
            second.next = r.guard
            r!!.guard!!.previous = second
            matching.substitute(r)
            newD.substitute(r)
            matching.checkAfterSubstitute(r)
            if (matching.next!!.next!!.value != newD.value) {
                newD.checkAfterSubstitute(r)
            }
            sequitur.digrams.put(first, first)
        }
        // Check for an underused rule.
        if (r.first().isNonTerminal
                && (r.first() as NonTerminal).rule.count === 1) {
            (r.first() as NonTerminal).expand()
        }
    }

    override fun toString(): String {
        return if (next == null) {
            "Symbol(" + value + "," + next + ")"
        } else "Symbol(" + value + "," + next!!.value + ")"
    }

    /**
     * Produce the hashcode for a digram.
     */
    override fun hashCode(): Int {
        var code: Long = if (next == null) {
            21599 * value.toLong()
        } else {
            21599 * value.toLong() + 20507 * next!!.value.toLong()
        }
        code %= prime
        return code.toInt()
    }

    /**
     * Test if two digrams are equal. WARNING: don't use to compare two symbols.
     */
    override fun equals(obj: Any?): Boolean {
        val o = obj as Symbol?
        if (value != o!!.value) {
            return false
        }
        if (next == null && o.next == null) {
            return true
        }
        return if (next != null && o.next != null) {
            next!!.value == o.next!!.value
        } else false
    }

    abstract fun clone(): Symbol
}

class Terminal(sequitur: Sequitur, value: Int) : Symbol(sequitur, value) {
    override fun cleanUp() {
        sequitur.join(previous!!, next!!);
        deleteDigram();
    }

    override fun clone(): Symbol = Terminal(sequitur, value)
}


class NonTerminal(sequitur: Sequitur, var rule: Rule) : Symbol(sequitur, maxTerminals + rule.number) {
    init {
        rule.count++;
    }

    override fun cleanUp()
    {
        sequitur.join(previous!!, next!!);
        deleteDigram();
        rule.count--;
    }

    override val isNonTerminal = true

    fun expand() {
        // I think this one is missing in the original implementation!
        deleteDigram();

        sequitur.join(previous!!, rule.first());
        sequitur.join(rule.last(), next!!);

        // Bug fix (21.8.2012): digram consisting of the last element of
        // the inserted rule and the first element after the inserted rule
        // must be put into the hash table (Simon Schwarzer)

        sequitur.digrams.put(rule.last(), rule.last());

        // Necessary so that garbage collector
        // can delete rule and guard.

        rule.guard!!.rule = null;
        rule.guard = null;
    }

    override fun clone(): Symbol = NonTerminal(sequitur, rule)
}

class Guard(sequitur: Sequitur, var rule: Rule?) : Symbol(sequitur, 0) {
    init{
        previous = this
        next = this
    }
    override fun cleanUp() {
        sequitur.join(previous!!, next!!);
    }

    override val isGuard = true

    override fun deleteDigram() {
        // Do nothing
    }

    override fun check() = false

    override fun clone(): Symbol = Guard(sequitur, rule)
}

class Rule(sequitur: Sequitur) {
    var guard: Guard? = Guard(sequitur, this)
    var count: Int = 0
    var number: Int = sequitur.getNextRuleId()

    fun first(): Symbol {
        return guard!!.next!!
    }

    fun last(): Symbol {
        return guard!!.previous!!
    }

}

class Sequitur {
    private var numRules = 0
    val digrams = mutableMapOf<Symbol, Symbol>()
    val firstRule = Rule(this)

    fun process(c: Char) {
        firstRule.last().insertAfter(Terminal(this, c.toInt()))
        val check: Boolean = firstRule.last().previous!!.check()
    }

    fun process(input: String) {
        for (element in input) {
            process(element)
        }
    }

    /**
     * Links two symbols together, removing any old digram from the hash table.
     */
    fun join(left: Symbol, right: Symbol) {
        if (left.next != null) {
            left.deleteDigram()
            // Bug fix (21.8.2012): included two if statements, adapted from
// sequitur_simple.cc, to deal with triples
            if (right.previous != null && right.next != null
                    && right.value == right!!.previous!!.value && right.value == right!!.next!!.value) {
                digrams.put(right, right)
            }
            if (left.previous != null && left.next != null
                    && left.value == left!!.next!!.value && left.value == left!!.previous!!.value) {
                digrams.put(left!!.previous!!, left!!.previous!!)
            }
        }
        left.next = right
        right.previous = left
    }

    fun getNextRuleId(): Int {
            val n = numRules
            numRules++
            return n
        }
}
