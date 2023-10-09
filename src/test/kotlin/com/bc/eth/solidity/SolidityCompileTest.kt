package com.bc.eth.solidity

import org.junit.jupiter.api.Test
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.tx.gas.StaticGasProvider
import java.io.BufferedReader
import java.io.FileReader
import java.math.BigInteger
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class SolidityCompileTest {
    val GAS_PRICE: Long = 986181641
    val GAS_LIMIT: Long = 2656250000

    private lateinit var token: Web3jHelper.InnerClass

    private fun deploy() {
        var bin = "0x"
        val projectDir = Paths.get("").absolutePathString()
        val solidityFile = Paths.get("$projectDir/build/resources/main/solidity", "Token.bin").toFile()
        val reader = BufferedReader(FileReader(solidityFile, Charsets.UTF_8))
        reader.lines().forEach { bin += it }
        val input = listOf<Type<*>>(Utf8String("SOL_TEST"), Utf8String("SOL_TEST_V1"))
        val emptyConstructor = FunctionEncoder.encodeConstructor(input)

        val gasProvider = StaticGasProvider(BigInteger.valueOf(GAS_PRICE), BigInteger.valueOf(GAS_LIMIT))

        token = Web3jHelper.create(bin, gasProvider).deploy(emptyConstructor)
    }

    @Test
    fun compilerCompilesSolidityFiles() {
        deploy()
    }
}