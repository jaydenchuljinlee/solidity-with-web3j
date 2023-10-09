package com.bc.eth.solidity

import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.crypto.Credentials
import org.web3j.crypto.Hash
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.RemoteFunctionCall
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.exceptions.TransactionException
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ClientTransactionManager
import org.web3j.tx.Contract
import org.web3j.tx.TransactionManager
import org.web3j.tx.exceptions.TxHashMismatchException
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Numeric
import org.web3j.utils.TxHashVerifier
import java.io.IOException
import java.math.BigInteger
import java.util.*

class Web3jHelper {
    class InnerClass: Contract {
        constructor(byteCode: String,
                    contractAddress: String,
                    web3j: Web3j,
                    transactionManager: TransactionManager,
                    gasProvider: ContractGasProvider
        ): super(byteCode, contractAddress, web3j, transactionManager, gasProvider)

        // Contract 배포 시에 필요한 생성자
        constructor(to: String?,
                    web3j: Web3j,
                    transactionManager: TransactionManager,
                    gasProvider: ContractGasProvider): super(to, "", web3j, transactionManager, gasProvider)

        // TODO *주의* Contract 클래스에서 사용할 메서드만 여기에 작성한다.

        fun deploy(encodedConstructor: String): InnerClass {
            return deploy(InnerClass::class.java, web3j, transactionManager, gasProvider, contractBinary, encodedConstructor, BigInteger.ZERO)
        }

        fun <T: Type<*>> remoteCallSingleValueReturn(function: Function): RemoteFunctionCall<T> {
            return executeRemoteCallSingleValueReturn(function)
        }

        fun remoteCallMultiValueReturn(function: Function): RemoteFunctionCall<List<Type<*>>> {
            return executeRemoteCallMultipleValueReturn(function)
        }

        fun remoteCallTransaction(function: Function): RemoteFunctionCall<TransactionReceipt> {
            return executeRemoteCallTransaction(function)
        }

        fun remoteCallRawTransaction(function: Function, userAddress: String, privateKey: String): TransactionReceipt {
            val nonce = web3j.ethGetTransactionCount(userAddress, DefaultBlockParameterName.LATEST).send().transactionCount

            val data = FunctionEncoder.encode(function)

            val rawTransaction = RawTransaction.createTransaction(nonce, gasProvider.getGasPrice(data), gasProvider.getGasLimit(data), contractAddress, BigInteger.ZERO, data)

            val credentials = Credentials.create(privateKey)

            val signedRawBytes = TransactionEncoder.signMessage(rawTransaction, credentials)
            val hexStr = Numeric.toHexString(signedRawBytes)

            val ethSendRawTransaction = web3j.ethSendRawTransaction(hexStr).send()

            val txHashVerifier = TxHashVerifier()

            if (ethSendRawTransaction != null && !ethSendRawTransaction.hasError()) {
                val txHashLocal: String = Hash.sha3(hexStr)
                val txHashRemote = ethSendRawTransaction.transactionHash
                if (!txHashVerifier.verify(txHashLocal, txHashRemote)) {
                    throw TxHashMismatchException(txHashLocal, txHashRemote)
                }
            }

            return processResponse(ethSendRawTransaction)
        }

        @Throws(IOException::class, TransactionException::class)
        private fun processResponse(transactionResponse: EthSendTransaction): TransactionReceipt {
            if (transactionResponse.hasError()) {
                throw RuntimeException(
                    "Error processing transaction request: "
                            + transactionResponse.error.message
                )
            }
            val transactionHash = transactionResponse.transactionHash
            return waitForTransactionReceipt(transactionHash)
        }

        private fun waitForTransactionReceipt(transactionHash: String): TransactionReceipt {
            //        val sleepDuration = DEFAULT_BLOCK_TIME.toLong()
            val sleepDuration = 100L * 1 // 100 milliseconds
            val attempts = 40

            var receiptOptional: Optional<out TransactionReceipt?> = sendTransactionReceiptRequest(transactionHash)
            for (i in 0 until attempts) {
                receiptOptional = if (!receiptOptional.isPresent) {
                    try {
                        Thread.sleep(sleepDuration)
                    } catch (e: InterruptedException) {
                        throw TransactionException(e)
                    }
                    sendTransactionReceiptRequest(transactionHash)
                } else {
                    return receiptOptional.get()!!
                }
            }

            throw TransactionException(
                "Transaction receipt was not generated after "
                        + ((sleepDuration * attempts / 1000
                        ).toString() + " seconds for transaction: "
                        + transactionHash),
                transactionHash
            )
        }

        @Throws(IOException::class, TransactionException::class)
        private fun sendTransactionReceiptRequest(transactionHash: String?): Optional<TransactionReceipt> {
            val transactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send()
            if (transactionReceipt.hasError()) {
                throw TransactionException(
                    "Error processing request: " + transactionReceipt.error.message
                )
            }

            return transactionReceipt.transactionReceipt
        }
    }

    companion object {
        private const val HOST = "http://localhost:7545"
        private const val EMPTY = "" // Contract Address

        fun create(byteCode: String, gasProvider: ContractGasProvider) : InnerClass {
            val web3j = Web3j.build(HttpService(HOST))
            val account = web3j.ethAccounts().send().accounts[0]
            val transactionManager = ClientTransactionManager(web3j, account)

            return InnerClass(byteCode, EMPTY, web3j, transactionManager, gasProvider)
        }

        fun getAccounts(): List<String> {
            val web3j = Web3j.build(HttpService(HOST))

            return web3j.ethAccounts().send().accounts
        }
    }
}