package LC.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    private final Socket socket;
    private final Thread rxThread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnectionListener eventListener;

    public TCPConnection (TCPConnectionListener eventListener, String ipAdd, int port) throws IOException{
        this(eventListener, new Socket(ipAdd, port));

    }

    public TCPConnection (TCPConnectionListener eventListener, Socket socket) throws IOException{
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()){
                        eventListener.onReciveString(TCPConnection.this, in.readLine());
                    }

                }catch (IOException e){
                    eventListener.onException(TCPConnection.this, e);
                }finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void sentString(String value){
        try {
            out.write(value + "\n\r");
            out.flush();
        }catch (IOException e){
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }

    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        }catch (IOException e){
            eventListener.onException(TCPConnection.this, e);
        }

    }
    @Override
    public String toString(){
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
