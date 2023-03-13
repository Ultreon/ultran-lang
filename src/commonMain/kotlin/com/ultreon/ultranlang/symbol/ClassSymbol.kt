package com.ultreon.ultranlang.symbol

import com.ultreon.ultranlang.ActivationRecord
import com.ultreon.ultranlang.ast.*
import com.ultreon.ultranlang.classes.ClassHolder
import com.ultreon.ultranlang.classes.ULClass
import com.ultreon.ultranlang.classes.ULClasses
import com.ultreon.ultranlang.func.NativeCalls
import kotlin.String

class ClassSymbol(name: String, private val classes: ULClasses, val parentCalls: NativeCalls) : Symbol(name) {
    lateinit var classInitDecl: ClassInitDecl
    lateinit var staticFieldsDecl: MutableList<FieldDecl>
    lateinit var staticMethodsDecl: MutableList<MethodDeclaration>
    lateinit var staticMembersDecl: MutableList<ClassMemberDecl>

    lateinit var constructorsDecl: MutableList<ConstructorDeclaration>
    lateinit var instanceFieldsDecl: MutableList<FieldDecl>
    lateinit var instanceMethodsDecl: MutableList<MethodDeclaration>
    lateinit var instanceMembersDecl: MutableList<ClassMemberDecl>

    lateinit var classInit: ClassInitDecl
    lateinit var staticFields: MutableList<VarSymbol>
    lateinit var staticMethods: MutableList<MethodDeclaration>
    lateinit var staticMembers: MutableList<ClassMemberDecl>

    lateinit var constructors: MutableList<ConstructorDeclaration>
    lateinit var instanceFields: MutableList<FieldDecl>
    lateinit var instanceMethods: MutableList<MethodDeclaration>
    lateinit var instanceMembers: MutableList<ClassMemberDecl>

    lateinit var ulClass: ULClass

    val staticCalls = NativeCalls(parent = parentCalls)
    val instanceCalls = NativeCalls(parent = staticCalls)

    val isNative: Boolean
        get() {
            return classes.exists(name)
        }
    lateinit var statements: List<LangObj>

    override fun toString(): String {
        return "<${this::class.simpleName}($name=$name, type=$type)>"
    }

    fun representation(): String = toString()
    fun getRef(ar: ActivationRecord): ClassHolder? {
        return classes.getRef(name)
    }
    fun getNative(ar: ActivationRecord): ULClass? {
        return classes[name]
    }

    fun invokeInternal(methodSymbol: MethodSymbol, formalParams: MutableList<VarSymbol>, ar: ActivationRecord): Any? {
        TODO("Not yet implemented")
    }
}