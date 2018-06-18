package imageservice.imageserviceapp;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {

    private InetAddress serverAddr;
    private Socket socket;

    public TCPClient() {
        try {
            this.serverAddr = InetAddress.getByName("10.0.2.2");
            this.socket = new Socket(serverAddr, 7999);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] byteArr) {
        try {
            OutputStream output = socket.getOutputStream();
            output.write(byteArr, 0, byteArr.length);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeClient() {
        try {
            this.socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
