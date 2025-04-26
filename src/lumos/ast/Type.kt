package lumos.ast

import lumos.Env
import lumos.helper.*
import lumos.logger.defaultLogger
import lumos.logger.internalError
import lumos.token.TokenPos
import lumos.token.emptyTokenPos
import lumos.token.invalidTokenPos
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM.*

interface Type : Manglingable {
    val llvmRef: LLVMTypeRef

    override fun codegen(env: Env): LLVMTypeRef {
        defaultLogger.warning("Use `llvmRef` instead of `codegen(env)`")
        return llvmRef
    }

    val size: Int // 类型占用的字节数

    // 前缀操作符、后缀操作符、二元操作符
    val prefixOps: Map<String, (Expr) -> Expr>
    val postfixOps: Map<String, (Expr) -> Expr>
    val binaryOps: Map<String, (Expr, Expr) -> Expr>

    // 允许从哪些类型转换和转换到哪些类型，按注册顺序查找
    val implicitConvertFrom: List<Type>
    val implicitConvertTo: List<Type>
    val explicitConvertFrom: List<Type>
    val explicitConvertTo: List<Type>

    val constructor: Map<FuncType, Func> // 构造函数
    val destructor: Func? // 析构函数

    val init get() = constructor[FuncType(emptyTokenPos, this, TupleType(emptyTokenPos))]

    val kind: TypeKind
        get() = when (this) {
            is VoidType -> TypeKind.Void
            is IntType -> TypeKind.Int
            is BoolType -> TypeKind.Bool
            else -> TypeKind.Internal
        }
}

// 自定义类型
abstract class CustomType(parent: Container) : UnnamedContainer(parent), Type {
    abstract override var size: Int
    override val prefixOps: MutableMap<String, (Expr) -> Expr> = mutableMapOf()
    override val postfixOps: MutableMap<String, (Expr) -> Expr> = mutableMapOf()
    override val binaryOps: MutableMap<String, (Expr, Expr) -> Expr> = mutableMapOf()
    override val implicitConvertFrom: MutableList<Type> = mutableListOf()
    override val implicitConvertTo: MutableList<Type> = mutableListOf()
    override val explicitConvertFrom: MutableList<Type> = mutableListOf()
    override val explicitConvertTo: MutableList<Type> = mutableListOf()
    override val constructor: MutableMap<FuncType, Func> = mutableMapOf()
    override var destructor: Func? = null
}

// 空类型
abstract class InternalType : Type {
    override val prefixOps: Map<String, (Expr) -> Expr> = mapOf()
    override val postfixOps: Map<String, (Expr) -> Expr> = mapOf()
    override val binaryOps: Map<String, (Expr, Expr) -> Expr> = mapOf()
    override val implicitConvertFrom: MutableList<Type> = mutableListOf()
    override val implicitConvertTo: MutableList<Type> = mutableListOf()
    override val explicitConvertFrom: MutableList<Type> = mutableListOf()
    override val explicitConvertTo: MutableList<Type> = mutableListOf()
    override val constructor: MutableMap<FuncType, Func> = mutableMapOf()
    override var destructor: Func? = null
}

abstract class BaseType : InternalType() {
    override val prefixOps: Map<String, (Expr) -> Expr> = mapOf()
    override val postfixOps: Map<String, (Expr) -> Expr> = mapOf()
    override val binaryOps: Map<String, (Expr, Expr) -> Expr> = mapOf()
    override val implicitConvertFrom: MutableList<Type> = mutableListOf()
    override val implicitConvertTo: MutableList<Type> = mutableListOf()
    override val explicitConvertFrom: MutableList<Type> = mutableListOf()
    override val explicitConvertTo: MutableList<Type> = mutableListOf()
    override val constructor: MutableMap<FuncType, Func> = mutableMapOf()
    override var destructor: Func? = null
}

class StringType(
    override val pos: TokenPos,
) : InternalType() {
    override val size: Int = 0 // 字符串类型的大小是不确定的
    override val llvmRef: LLVMTypeRef = LLVMPointerType(LLVMInt8Type(), 0)
    override fun mangling(): String {
        return "string"
    }

    override val prefixOps: Map<String, (Expr) -> Expr> = mapOf()
    override val postfixOps: Map<String, (Expr) -> Expr> = mapOf()
    override val binaryOps: Map<String, (Expr, Expr) -> Expr> = mapOf()
}

class IntType(
    override val pos: TokenPos,
    val nbits: Int = 32,
) : InternalType() {
    // 对齐到 1 字节
    override val size get() = (nbits + 7) / 8

    override val llvmRef: LLVMTypeRef = when (nbits) {
        8 -> LLVMInt8Type()
        16 -> LLVMInt16Type()
        32 -> LLVMInt32Type()
        64 -> LLVMInt64Type()
        else -> LLVMIntType(nbits)
    }

    override fun mangling(): String = "i${nbits}"

    companion object {
        val _prefixOps: Map<String, (Expr) -> Expr> = mapOf()
        val _postfixOps: Map<String, (Expr) -> Expr> = mapOf()
        val _binaryOps: Map<String, (Expr, Expr) -> Expr> = mapOf()
    }

    override val prefixOps get() = _prefixOps
    override val postfixOps get() = _postfixOps
    override val binaryOps get() = _binaryOps
}

class VoidType(
    override val pos: TokenPos,
) : InternalType() {
    override val size = 1 // 我们让 void 类型占用一个字节，便于 void * 指针的计算

    override val llvmRef: LLVMTypeRef = LLVMVoidType()

    override fun mangling(): String {
        return "v"
    }
}

class ClassType(
    override val pos: TokenPos,
    parent: Container,
) : CustomType(parent) {
    override val llvmRef: LLVMTypeRef = LLVMStructCreateNamed(LLVMGetGlobalContext(), "class")

    override var size: Int = 0

    var var_map: MutableMap<String, NamedType> = mutableMapOf()
    var var_list: MutableList<NamedType> = mutableListOf()

    var fn_map: MutableMap<String, NamedFunc> = mutableMapOf()
    var fn_list: MutableList<NamedFunc> = mutableListOf()

    override fun mangling(): String {
        TODO()
    }

    override fun find(name: String): AST? {
        TODO("Not yet implemented")
    }

    override fun findChild(name: String): AST? {
        TODO("Not yet implemented")
    }

    override fun append(ast: AST) {
        TODO("Not yet implemented")
    }

    override fun codegen(env: Env): LLVMTypeRef {
        TODO()
    }

    override val prefixOps: PrefixOpMap = prefixOpMapOf()
    override val postfixOps: PostfixOpMap = postfixOpMapOf()
    override val binaryOps: BinaryOpMap = binaryOpMapOf()
}

data class FuncType(
    override val pos: TokenPos,
    val returnType: Type,
    val paramsType: TupleType,
) : InternalType() {
    override val llvmRef: LLVMTypeRef = LLVMFunctionType(returnType.llvmRef, paramsType.llvmRef, 0, 0)
    override val size: Int get() = internalError("FuncType.size")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FuncType
        return returnType == other.returnType && paramsType == other.paramsType
    }

    // 判断两个函数类型是否冲突
    // 两个函数类型冲突的条件是：参数类型相同
    infix fun conflictsWith(other: FuncType): Boolean {
        return this === other || paramsType == other.paramsType
    }

    override fun mangling(): String {
        TODO()
    }

    override fun hashCode(): Int {
        return 31 * returnType.hashCode() + paramsType.hashCode()
    }

    companion object {
        val _binaryOps: Map<String, (Expr, Expr) -> Expr> =
            mapOf("call" to { lhs, rhs -> UndefinedValue(invalidTokenPos) })
    }

    override val binaryOps: Map<String, (Expr, Expr) -> Expr> get() = _binaryOps
}

class BoolType(
    override val pos: TokenPos,
) : InternalType() {
    override val size: Int = 1
    override val llvmRef: LLVMTypeRef = LLVMInt1Type()

    override fun mangling(): String {
        return "b"
    }

    override val prefixOps: Map<String, (Expr) -> Expr> = mapOf()
    override val postfixOps: Map<String, (Expr) -> Expr> = mapOf()
    override val binaryOps: Map<String, (Expr, Expr) -> Expr> = mapOf()
}
