package com.arkivanov.mvikotlin.timetravel.server

import com.arkivanov.mvikotlin.timetravel.proto.internal.thread.Thread
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import platform.posix.AF_INET
import platform.posix.INADDR_ANY
import platform.posix.IPPROTO_TCP
import platform.posix.PF_INET
import platform.posix.SOCK_STREAM
import platform.posix.accept
import platform.posix.bind
import platform.posix.close
import platform.posix.errno
import platform.posix.listen
import platform.posix.memset
import platform.posix.sockaddr_in
import platform.posix.socket
import kotlin.native.concurrent.AtomicInt

@Suppress("EXPERIMENTAL_API_USAGE")
internal class ConnectionThread(
    private val port: Int,
    private val onClientConnected: (Int) -> Unit,
    private val onError: (Throwable) -> Unit
) : Thread() {

    private val socketRef = AtomicInt(-1)

    override fun run() {
        val serverSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP)
        if (serverSocket < 0) {
            onError(Exception("Error open socket: $errno"))
            return
        }

        socketRef.value = serverSocket

        memScoped {
            val sin = alloc<sockaddr_in>()
            memset(sin.ptr, 0, sockaddr_in.size.convert())
            sin.sin_len = sizeOf<sockaddr_in>().convert()
            sin.sin_family = AF_INET.convert()
            sin.sin_port = getSinPort()
            sin.sin_addr.s_addr = INADDR_ANY

            if (bind(serverSocket, sin.ptr.reinterpret(), sockaddr_in.size.convert()) < 0) {
                onError(Exception("Error bind socket: $errno"))
                return
            }
        }

        if (listen(serverSocket, SOCKET_BACKLOG) < 0) {
            onError(Exception("Error listen socket: $errno"))
            return
        }

        while (!isInterrupted) {
            val socket = accept(serverSocket, null, null)
            if (socket < 0) {
                onError(Exception("Error accept socket: $errno"))
                return
            }

            onClientConnected(socket)
        }
    }

    @Suppress("MagicNumber")
    private fun getSinPort(): UShort = ((port shr 8) or ((port and 0xff) shl 8)).toUShort()

    override fun interrupt() {
        super.interrupt()

        socketRef
            .value
            .takeIf { it >= 0 }
            ?.also { close(it) }
    }

    private companion object {
        private const val SOCKET_BACKLOG = 16
    }
}
