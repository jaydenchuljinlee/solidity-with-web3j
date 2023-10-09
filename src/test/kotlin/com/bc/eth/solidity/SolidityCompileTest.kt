package com.bc.eth.solidity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.web3j.sokt.SolcArguments
import org.web3j.sokt.SolidityFile
import java.nio.file.Files
import java.nio.file.Paths

class SolidityCompileTest {
//    @ParameterizedTest
//    @ValueSource(strings = ["Token.sol"])
    fun compilerCompilesSolidityFiles(solFileName: String) {
        val outputDir = Paths.get("./build/contracts")
        Files.createDirectory(outputDir)

        val solidityToCompile = this.javaClass.getResource("/$solFileName")!!.readText()

        val solidityFile = Paths.get(outputDir.toString(), solFileName).toFile()
        solidityFile.writeText(solidityToCompile)



        val compilerInstance = SolidityFile(solidityFile.absolutePath).getCompilerInstance()
        val result = compilerInstance.execute(
            SolcArguments.OUTPUT_DIR.param { outputDir.toString() },
            SolcArguments.AST,
            SolcArguments.BIN,
            SolcArguments.OVERWRITE
        )
        assertEquals(0, result.exitCode)
        assertNotEquals(0, result.stdOut.length + result.stdErr.length)
        assertTrue(Paths.get(outputDir.toString(), "${solFileName.dropLast(4)}.bin").toFile().exists())
        assertTrue(Paths.get(outputDir.toString(), "$solFileName.ast").toFile().exists())
    }
}