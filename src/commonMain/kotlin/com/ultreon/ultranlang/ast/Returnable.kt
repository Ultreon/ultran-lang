package com.ultreon.ultranlang.ast

interface Returnable {
    var child: Returnable?
    var parent: Returnable?
}
