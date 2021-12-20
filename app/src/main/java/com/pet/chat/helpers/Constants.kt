package com.pet.chat.helpers

const val socketConnectionWorkerTag = "WorkerForSockets"
const val fileUploadWorkerTag = "WorkerForFileUpload"

const val networkIP = "185.26.121.63"
const val networkWSsocket = "3000"
const val networkHttpSocket = "3001"

const val networkIPKey = "NETWORK_IP_KEY"
const val networkHostKey = "NETWORK_HOST_KEY"
const val networkHostTypeKey = "NETWORK_HOST_TYPE_KEY"
const val networkSocketKey = "NETWORK_HOST_SOCKET_KEY"

enum class NetworkHostType{
    HTTP, HTTPS, WS
}