package LC.chat.network;

public interface TCPConnectionListener {
    void onConnectionReady (TCPConnection tcpConnection);
    void onReciveString (TCPConnection tcpConnection, String value);
    void onDisconnect (TCPConnection tcpConnection);
    void onException (TCPConnection tcpConnection, Exception e);
}
