package com.ultreon.ultranlang;

import com.ultreon.ultranlang.annotations.Visit;
import com.ultreon.ultranlang.ast.*;
import com.ultreon.ultranlang.symbol.FuncSymbol;
import com.ultreon.ultranlang.symbol.VarSymbol;
import com.ultreon.ultranlang.token.TokenType;
import com.ultreon.ultranlang.utils.CollectionUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class Interpreter extends NodeVisitor {
    @Nullable
    private final Program tree;
    private final CallStack callStack = new CallStack();

    public Interpreter(@Nullable Program tree) {
        this.tree = tree;
    }

    public void log(Object msg) {
        if (Spi.SHOULD_LOG_STACK) {
            System.out.println(msg);
        }
    }

    @Visit(Program.class)
    public void visitProgram(Program node) {
        String programName = node.getName();
        log("ENTER: PROGRAM " + programName);

        ActivationRecord ar = new ActivationRecord(programName, ARType.PROGRAM, 1);
        callStack.push(ar);

        log(callStack.toString());

        for (AST statement : node.getStatements()) {
            visit(statement);
        }
    }

    @Visit(Block.class)
    public void visitBlock(Block node) {
        for (AST declaration : node.getDeclarations()) {
            visit(declaration);
        }
        visit(node.getCompoundStatement());
    }

    @Visit(VarDecl.class)
    public void visitVarDecl(VarDecl node) {
        // do nothing
    }

    @Visit(Type.class)
    public void visitType(Type node) {
        // do nothing
    }

    @Nullable
    @Visit(BinOp.class)
    public Object visitBinOp(BinOp node) {
        if (node.getOp().getType().equals(TokenType.PLUS)) {
            return plus(visit(node.getLeft()), visit(node.getRight()));
        } else if (node.getOp().getType().equals(TokenType.MINUS)) {
            return minus(visit(node.getLeft()), visit(node.getRight()));
        } else if (node.getOp().getType().equals(TokenType.MUL)) {
            return mul(visit(node.getLeft()), visit(node.getRight()));
        } else if (node.getOp().getType().equals(TokenType.INTEGER_DIV)) {
            return div(visit(node.getLeft()), visit(node.getRight()));
        } else if (node.getOp().getType().equals(TokenType.FLOAT_DIV)) {
            return div(visit(node.getLeft()), visit(node.getRight()));
//        } else if (node.getOp().getType().equals(TokenType.EQ)) {
//            return eq(visit(node.getLeft()), visit(node.getRight()));
//        } else if (node.getOp().getType().equals(TokenType.NEQ)) {
//            return neq(visit(node.getLeft()), visit(node.getRight()));
//        } else if (node.getOp().getType().equals(TokenType.LT)) {
//            return lt(visit(node.getLeft()), visit(node.getRight()));
//        } else if (node.getOp().getType().equals(TokenType.LE)) {
//            return le(visit(node.getLeft()), visit(node.getRight()));
//        } else if (node.getOp().getType().equals(TokenType.GT)) {
//            return gt(visit(node.getLeft()), visit(node.getRight()));
//        } else if (node.getOp().getType().equals(TokenType.GE)) {
//            return ge(visit(node.getLeft()), visit(node.getRight()));
        } else {
            throw new RuntimeException("Unknown operator: " + node.getOp().getType());
        }
    }

    @Visit(Num.class)
    public Object visitNum(Num node) {
        return node.getValue();
    }

    @Visit(Str.class)
    public Object visitStr(Str node) {
        return node.getValue();
    }

    @Visit(UnaryOp.class)
    public Object visitUnaryOp(UnaryOp node) {
        if (node.getOp().getType().equals(TokenType.PLUS)) {
            return unaryPlus(visit(node.getExpr()));
        } else if (node.getOp().getType().equals(TokenType.MINUS)) {
            return unaryMinus(visit(node.getExpr()));
//        } else if (node.getOp().getType().equals(TokenType.NOT)) {
//            return not(visit(node.getExpr()));
        } else {
            throw new RuntimeException("Unknown operator: " + node.getOp().getType());
        }
    }

    @Nullable
    @Contract("_->null")
    @Visit(Compound.class)
    public Object visitCompound(Compound node) {
        for (AST child : node.getChildren()) {
            visit(child);
        }
        return null;
    }

    @Visit(Assign.class)
    public Object visitAssign(Assign node) {
        String varName = (String) node.getLeft().getValue();
        Object value = visit(node.getRight());

        ActivationRecord ar = callStack.peek();
        ar.set(varName, value);

        // TODO: return set value
        return null;
    }

    @Visit(Var.class)
    public Object visitVar(Var node) {
        String varName = (String) node.getValue();

        ActivationRecord ar = callStack.peek();
        return ar.get(varName);
    }

    @Visit(NoOp.class)
    public void visitNoOp(NoOp node) {
        // do nothing
    }

    @Visit(FuncDeclaration.class)
    public void visitFuncDeclaration(FuncDeclaration node) {

    }

    @Visit(FuncCall.class)
    @SuppressWarnings("ConstantConditions")
    public Object visitFuncCall(FuncCall node) {
        String funcName = node.getFuncName();
        FuncSymbol funcSymbol = node.getFuncSymbol();

        ActivationRecord ar = new ActivationRecord(funcName, ARType.FUNCTION, funcSymbol.getScopeLevel() + 1);

        List<VarSymbol> formalParams = funcSymbol.getFormalParams();
        List<AST> actualParams = node.getActualParams();

        for (Map.Entry<VarSymbol, AST> entry : CollectionUtils.zip(formalParams, actualParams).entrySet()) {
            ar.set(entry.getKey().getName(), visit(entry.getValue()));
        }

        this.callStack.push(ar);

        this.log("ENTER: FUNCTION " + funcName);
        this.log(callStack.toString());

        // evaluate function body
        Object value;

        if (funcSymbol.isNative()) {
            Object returned = funcSymbol.callNative(ar);
            if (returned instanceof Void) {
                returned = null;
            }
            value = returned;
        } else {
            for (AST statement : funcSymbol.getStatements()) {
                visit(statement);
            }
            value = null;
        }

        this.log("LEAVE: FUNCTION " + funcName);
        this.log(callStack.toString());

        this.callStack.pop();

        return value;
    }

    public boolean interpret() {
        if (this.tree == null) {
            return false;
        }
        this.visit(tree);
        return true;
    }

    private Object plus(Object a, Object b) {
        if (a instanceof BigInteger && b instanceof BigInteger) {
            return ((BigInteger) a).add((BigInteger) b);
        } else if (a instanceof BigDecimal && b instanceof BigDecimal) {
            return ((BigDecimal) a).add((BigDecimal) b);
        } else if (a instanceof BigInteger && b instanceof BigDecimal) {
            return new BigDecimal((BigInteger) a).add((BigDecimal) b);
        } else if (a instanceof BigDecimal && b instanceof BigInteger) {
            return ((BigDecimal) a).add(new BigDecimal(((BigInteger) b)));
        } else if (a instanceof String && b instanceof String) {
            return a + (String) b;
        } else if (a != null && b instanceof String) {
            return a.toString() + b;
        } else if (a instanceof String && b != null) {
            return a + b.toString();
        } else {
            throw new RuntimeException("Cannot add " + (a == null ? "null" : a.getClass().getSimpleName()) + " and " + (b == null ? "null" : b.getClass().getSimpleName()));
        }
    }

    private Object minus(Object a, Object b) {
        if (a instanceof BigInteger && b instanceof BigInteger) {
            return ((BigInteger) a).subtract((BigInteger) b);
        } else if (a instanceof BigDecimal && b instanceof BigDecimal) {
            return ((BigDecimal) a).subtract((BigDecimal) b);
        } else if (a instanceof BigInteger && b instanceof BigDecimal) {
            return new BigDecimal((BigInteger) a).subtract((BigDecimal) b);
        } else if (a instanceof BigDecimal && b instanceof BigInteger) {
            return ((BigDecimal) a).subtract(new BigDecimal(((BigInteger) b)));
        } else {
            throw new RuntimeException("Cannot subtract " + (a == null ? "null" : a.getClass().getSimpleName()) + " and " + (b == null ? "null" : b.getClass().getSimpleName()));
        }
    }

    private Object mul(Object a, Object b) {
        if (a instanceof BigInteger && b instanceof BigInteger) {
            return ((BigInteger) a).multiply((BigInteger) b);
        } else if (a instanceof BigDecimal && b instanceof BigDecimal) {
            return ((BigDecimal) a).multiply((BigDecimal) b);
        } else if (a instanceof BigInteger && b instanceof BigDecimal) {
            return new BigDecimal((BigInteger) a).multiply((BigDecimal) b);
        } else if (a instanceof BigDecimal && b instanceof BigInteger) {
            return ((BigDecimal) a).multiply(new BigDecimal(((BigInteger) b)));
        } else {
            throw new RuntimeException("Cannot multiply " + (a == null ? "null" : a.getClass().getSimpleName()) + " and " + (b == null ? "null" : b.getClass().getSimpleName()));
        }
    }

    private Object div(Object a, Object b) {
        if (a instanceof BigInteger && b instanceof BigInteger) {
            return ((BigInteger) a).divide((BigInteger) b);
        } else if (a instanceof BigDecimal && b instanceof BigDecimal) {
            return ((BigDecimal) a).divide((BigDecimal) b, RoundingMode.HALF_UP);
        } else if (a instanceof BigInteger && b instanceof BigDecimal) {
            return new BigDecimal((BigInteger) a).divide((BigDecimal) b, RoundingMode.HALF_UP);
        } else if (a instanceof BigDecimal && b instanceof BigInteger) {
            return ((BigDecimal) a).divide(new BigDecimal(((BigInteger) b)), RoundingMode.HALF_UP);
        } else {
            throw new RuntimeException("Cannot divide " + (a == null ? "null" : a.getClass().getSimpleName()) + " and " + (b == null ? "null" : b.getClass().getSimpleName()));
        }
    }

    private Object unaryPlus(Object a) {
        if (a instanceof BigInteger) {
            return ((BigInteger) a).abs();
        } else if (a instanceof BigDecimal) {
            return ((BigDecimal) a).abs();
        } else {
            throw new RuntimeException("Cannot unary plus " + (a == null ? "null" : a.getClass().getSimpleName()));
        }
    }

    private Object unaryMinus(Object a) {
        if (a instanceof BigInteger) {
            return ((BigInteger) a).negate();
        } else if (a instanceof BigDecimal) {
            return ((BigDecimal) a).negate();
        } else {
            throw new RuntimeException("Cannot unary minus " + (a == null ? "null" : a.getClass().getSimpleName()));
        }
    }
}
