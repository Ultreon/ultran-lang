package com.ultreon.ultranlang

import com.soywiz.kbignum.BigInt
import com.soywiz.kbignum.BigNum
import com.ultreon.ultranlang.classes.*
import com.ultreon.ultranlang.classes.internal.*
import kotlin.reflect.KClass

object Runtime {
    private val primitives: MutableMap<KClass<out Any>, PrimitiveClass<out Any>> = mutableMapOf()
    private val primitiveNames: MutableMap<String, PrimitiveClass<out Any>> = mutableMapOf()
    lateinit var classes: ULClasses private set

    fun init(launch: LaunchProperties) {
        classes = ULClasses()
        registerPrimitiveClass("byte", Byte::class) { PrimitiveByte(it) }
        registerPrimitiveClass("short", Short::class) { PrimitiveShort(it) }
        registerPrimitiveClass("int", Int::class) { PrimitiveInt(it) }
        registerPrimitiveClass("long", Long::class) { PrimitiveLong(it) }
        registerPrimitiveClass("float", Float::class) { PrimitiveFloat(it) }
        registerPrimitiveClass("double", Double::class) { PrimitiveDouble(it) }
        registerPrimitiveClass("bigint", BigInt::class) { PrimitiveBigInt(it) }
        registerPrimitiveClass("bigdec", BigNum::class) { PrimitiveBigDec(it) }
        registerPrimitiveClass("char", Char::class) { PrimitiveChar(it) }
        registerPrimitiveClass("string", String::class) { PrimitiveString(it) }
        registerPrimitiveClass("void", Unit::class) { PrimitiveVoid }
    }

    private fun <T : Any> registerPrimitiveClass(name: String, kClass: KClass<T>, convert: (T) -> ULObject) {
        val primitiveClass = createPrimitiveClass(name, kClass, convert)
        primitives[kClass] = primitiveClass
        primitiveNames[name] = primitiveClass
    }

    fun crash(t: Throwable) {
        TODO("Not yet implemented")
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getPrimitiveClass(clazz: KClass<T>): PrimitiveClass<T> {
        return primitives[clazz] as PrimitiveClass<T>? ?: throw Error("Unregistered primitive class: ${clazz.qualifiedName}")
    }

    fun getPrimitiveClass(name: String): PrimitiveClass<*>? {
        return primitiveNames[name]
    }

    private fun <T : Any> createPrimitiveClass(name: String, clazz: KClass<T>, convert: (T) -> ULObject): PrimitiveClass<T> {
        return object : PrimitiveClass<T>(name) {
            override fun createObject(langObj: T): ULObject = convert(langObj)

            override fun init() {
                // LOL
            }

            override fun getConstructor(formalTypes: List<ULClass>): ULConstructor? = null

            override fun getStaticMethod(name: String, formalTypes: List<ULClass>): ULMethod? = null

            override fun getInstanceMethod(name: String, formalTypes: List<ULClass>): ULMethod? = null

            override fun getStaticField(name: String): ULField? = null

            override fun getInstanceField(name: String): ULField? = null

        }
    }
}