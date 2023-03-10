package com.ultreon.ultranlang.ast

class VarDecl(var varRefNode: VarRef, var typeNode: Type) : LangObj(), ClassMemberDecl, FieldDecl