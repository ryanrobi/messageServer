import javax.swing.JFrame;

public class ServerTest {
    public static void main(String[] args) {
        Server emma = new Server();
        emma.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        emma.startRunning();
        System.out.println("test");
    }
}
