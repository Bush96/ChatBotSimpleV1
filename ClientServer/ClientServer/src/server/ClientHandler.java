package server;

import client.ClientWindow;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    // экземпляр нашего сервера
    private Server server;
    // исходящее сообщение
    private PrintWriter outMessage;
    // входящее собщение
    private Scanner inMessage;
    private static final String HOST = "localhost";
    private static final int PORT = 3443;
    // клиентский сокет
    private Socket clientSocket = null;


    // конструктор, который принимает клиентский сокет и сервер
    public ClientHandler(Socket socket, Server server) {
        try {
//            clients_count++;
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Переопределяем метод run(), который вызывается когда
    // мы вызываем new Thread(client).start();
    @Override
    public void run() {
        try {
            while (true) {
                // сервер отправляет сообщение

                server.sendMessageToAllClients("Короче, Меченый. Я тебя спас и в благородство" +
                        " играть не буду. Выполнишь для меня пару заданий " +
                        "— и мы в расчёте. Заодно посмотрим, как быстро у тебя " +
                        "башка после амнезии прояснится. А по твоей теме постараюсь " +
                        "разузнать. Хрен его знает, на кой ляд тебе этот Стрелок" +
                        " сдался, ну а я в чужие дела не лезу. Хочешь убить — значит есть за что.");

                break;
            }

            while (true) {
                // Если от клиента пришло сообщение
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                    // если клиент отправляет данное сообщение, то цикл прерывается и
                    // клиент выходит из чата
                    if (clientMessage.equalsIgnoreCase("##session##end##")) {
                        break;
                    }

                    server.sendMessageToAllClients(clientMessage);
                    Thread.sleep(1000);
                    server.sendAnswerToAllClients();

                }
                // останавливаем выполнение потока на 100 мс
                Thread.sleep(100);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            this.close();
        }
    }

    public static <String> String getRandom(List<String> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));

    }

    public String botEntity() {

        List<String> answer = new ArrayList<>();
        answer.add("Братан, ты не врубаешься? Мне нужен реальный товар!");
        answer.add("Иди, не мозоль глаза.");
        answer.add("Хабар принёс?");
        answer.add("Есть что-то стоящее?");
        answer.add("Нафига ты эту дрянь тягаешь?[2]");
        answer.add(" Как дела, сталкер?");
        answer.add("Не стой, как истукан — давай, что принёс, и вали.");

        return getRandom(answer);
    }

    // отправляем сообщение
    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendAnswer() {
        try {

            outMessage.println(botEntity());
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // клиент выходит из чата
    public void close() {
        // удаляем клиента из списка
        server.removeClient(this);

    }
}
