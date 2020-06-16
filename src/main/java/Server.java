
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket ss=new ServerSocket(9999);
        System.out.println("服务器已启动，端口9999");
        boolean flag=true;
        while(flag){
            System.out.println("等待9999端口客户端联接");
            Socket s=ss.accept(); //只有有客户端联接，则生成新线程处理任务
            new Thread(new Task(s)).start();
        }
    }
}
class Task implements Runnable{
    private Socket s;
    public Task(Socket s){
        this.s=s;
    }

    @Override
    public void run() {
        Random r=new Random();
        boolean flag=true;
        try {
            OutputStream oos=s.getOutputStream();
            PrintWriter out=new PrintWriter(oos,true);
            while(flag){
                Thread.sleep(r.nextInt(100));
                String s="char:"+r.nextInt(50); //ASCII
                out.println(s);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}