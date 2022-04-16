package pl.gbanasik;

public class Main {

    private static UI ui;
    private static ChatClient net;

    public static void main(String[] args) {
	    net = new ChatClient();
        ui = new UI(net);
    }
}
