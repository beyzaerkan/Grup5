import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread;
import java.util.ArrayList;
import java.lang.ProcessBuilder;
import java.lang.Process;

public class Program {

    // prosesi calistirma fonksiyonu, gercek prosesi dondurur
    public static Process run(MyProcess proc, int timer, ProcessBuilder pb, Process realProcess) throws IOException {
        // eger gelen proses daha onceden calistirilmamissa gercek bir proses olusturur,
        // idsini proses idye esitler
        if (proc.running == false) {
            realProcess = pb.start();
            proc.process_id = (int) realProcess.pid();
            System.out.println(proc.renkler + timer + ".000 sn proses basladi (id:" + proc.processNumber + " oncelik: "
                    + proc.oncelik + " kalan sure:" + proc.prosessZamani + " sn) ");
            proc.prosessZamani--;
            proc.running = true;
        } else if (proc.running == true) {
            System.out.println(
                    proc.renkler + timer + ".000 sn proses yurutuluyor (id:" + proc.processNumber + " oncelik: "
                            + proc.oncelik + " kalan sure:" + proc.prosessZamani + " sn) ");
            proc.prosessZamani--;
        }
        return realProcess;
    }

    // proses sonlandırma fonksiyonu
    public static Boolean finish(Queue queue, MyProcess currentProcess, int timer, Process realProcess) {
        // eger prosesin suresi bittiyse kuyruktan cikarir aynı zamanda gerçek prosesi
        // yok eder
        if (realProcess != null) {

            if (currentProcess.prosessZamani == 0) {
                MyProcess proc = queue.pop();
                realProcess.destroy();
                System.out.println(
                        proc.renkler + timer + ".000 sn proses sonlandi (id:" + proc.processNumber + " oncelik: "
                                + proc.oncelik + " kalan sure:" + proc.prosessZamani + " sn) ");
                return true;
            }
        }
        return false;
    }

    public static void timeOut(Queue kqQueue, int timer, Process realProcess) {
        if (kqQueue.length != 0) {

            if (kqQueue.head.proc.varisZamani + 20 < timer) {
                MyProcess proc = kqQueue.pop();
                realProcess.destroy();
                System.out.println(
                        proc.renkler + timer + ".000 sn proses zaman asimi (id:" + proc.processNumber + " oncelik: "
                                + proc.oncelik + " kalan sure:" + proc.prosessZamani + " sn) ");
            }
        }

    }

    public static Boolean suspend(Queue queue, MyProcess proc, int timer) {
        if (queue.length != 0) {
            if (queue.head.proc.varisZamani == timer) {
                System.out.println(proc.renkler + timer + ".0000 sn proses askida (id:"
                        + proc.processNumber
                        + " oncelik: "
                        + proc.oncelik + " kalan sure:" + proc.prosessZamani
                        + " sn) ");
                return true;

            }
        }
        return false;
    }

    // prosesin zamaninin gelip gelmedigini kontrol eder
    public static int isReady(Queue kQueue, int timer) {
        if (kQueue.length != 0) {
            MyProcess p = kQueue.getProc(0);
            if (p.varisZamani <= timer) {

                return 1;
            }
        }
        return 0;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // arguman girilmemisse
        if (args.length == 0) {
            System.out.println("Komut satirindan arguman girilmedi");
            return;
        }

        // kuyruklar
        Queue realtime = new Queue();
        Queue firstPriority = new Queue();
        Queue secondPriority = new Queue();
        Queue thirdPriority = new Queue();

        int i = 0, colorSelect = 0;

        // prosesler
        Process realProcess = null;
        MyProcess process = null;

        // dosya okuma islemleri icin gerekli degiskenler
        FileReader file = new FileReader(args[0]);
        BufferedReader bufferedReader = new BufferedReader(file);
        String line;
        String[] token = new String[3];

        ArrayList<String> renkler = new ArrayList<String>();
        for (int k = 0; k < 256; k++) {
            renkler.add("\u001b[38;5;" + k * 5 + "m ");
        }

        // dosya okuma, txtdeki verileri ", " karakterine gore ayirir, verileri
        // proseslere ilgili sekilde gonderir
        while ((line = bufferedReader.readLine()) != null) {
            token = line.split(", ");
            switch (Integer.parseInt(token[1])) {
                case 0:
                    process = new MyProcess(Integer.parseInt(token[0]), Integer.parseInt(token[1]),
                            Integer.parseInt(token[2]), i, 0, renkler.get(colorSelect), false, false);
                    colorSelect++;
                    realtime.push(process);
                    break;
                case 1:
                    process = new MyProcess(Integer.parseInt(token[0]), Integer.parseInt(token[1]),
                            Integer.parseInt(token[2]), i, 0, renkler.get(colorSelect), false, false);
                    colorSelect++;
                    firstPriority.push(process);
                    break;
                case 2:
                    process = new MyProcess(Integer.parseInt(token[0]), Integer.parseInt(token[1]),
                            Integer.parseInt(token[2]), i, 0, renkler.get(colorSelect), false, false);
                    colorSelect++;
                    secondPriority.push(process);
                    break;
                case 3:
                    process = new MyProcess(Integer.parseInt(token[0]), Integer.parseInt(token[1]),
                            Integer.parseInt(token[2]), i, 0, renkler.get(colorSelect), false, false);
                    colorSelect++;
                    thirdPriority.push(process);
                    break;
                default:
                    break;
            }
            process.processNumber = i;
            i++;
        }
        bufferedReader.close();

        realtime.sort(realtime.length);
        firstPriority.sort(firstPriority.length);
        secondPriority.sort(secondPriority.length);
        thirdPriority.sort(thirdPriority.length);

        int c = 0;
        Boolean terminate = false;

        ProcessBuilder pb = new ProcessBuilder("java", "-version");

        // tum prosesler sonlandırılmadıgı surece calisir
        while (terminate == false) {
            // prosesler bittiginde donguden cikar

            switch (isReady(realtime, c)) {
                case 1:
                    timeOut(firstPriority, c, realProcess);
                    timeOut(secondPriority, c, realProcess);
                    timeOut(thirdPriority, c, realProcess);
                    if (realtime.length != 0) {
                        realProcess = run(realtime.head.proc, c++, pb, realProcess);
                        Thread.sleep(1000);
                        finish(realtime, realtime.headProcess(), c, realProcess);
                    }

                    break;

                default:
                    break;
            }
            switch (isReady(firstPriority, c)) {
                case 1:
                    switch (isReady(realtime, c)) {
                        case 1:
                            break;
                        case 0:
                            timeOut(firstPriority, c, realProcess);
                            timeOut(secondPriority, c, realProcess);
                            timeOut(thirdPriority, c, realProcess);
                            if (firstPriority.length != 0) {
                                realProcess = run(firstPriority.head.proc, c++, pb, realProcess);
                                Thread.sleep(1000);
                                if (finish(firstPriority, firstPriority.headProcess(), c, realProcess)) {
                                    break;
                                }
                                suspend(realtime, firstPriority.headProcess(), c);
                            }

                            break;
                    }
                    break;

                default:
                    break;
            }
            switch (isReady(secondPriority, c)) {
                case 1:
                    switch (isReady(realtime, c)) {
                        case 1:

                            break;

                        case 0:
                            switch (isReady(firstPriority, c)) {
                                case 1:

                                    break;

                                case 0:
                                    timeOut(firstPriority, c, realProcess);
                                    timeOut(secondPriority, c, realProcess);
                                    timeOut(thirdPriority, c, realProcess);
                                    if (secondPriority.length != 0) {
                                        realProcess = run(secondPriority.head.proc, c++, pb, realProcess);
                                        Thread.sleep(1000);
                                        if (finish(secondPriority, secondPriority.headProcess(), c, realProcess)) {
                                            break;
                                        }
                                        suspend(realtime, secondPriority.headProcess(), c);
                                        suspend(firstPriority, secondPriority.headProcess(), c);
                                    }
                                    break;
                            }
                            break;
                    }
                    break;

                default:
                    break;
            }
            switch (isReady(thirdPriority, c)) {
                case 1:
                    switch (isReady(realtime, c)) {
                        case 1:

                            break;

                        case 0:
                            switch (isReady(firstPriority, c)) {
                                case 1:

                                    break;

                                case 0:
                                    switch (isReady(secondPriority, c)) {
                                        case 1:

                                            break;

                                        case 0:
                                            timeOut(firstPriority, c, realProcess);
                                            timeOut(secondPriority, c, realProcess);
                                            timeOut(thirdPriority, c, realProcess);
                                            if (thirdPriority.length != 0) {
                                                realProcess = run(thirdPriority.head.proc, c++, pb, realProcess);
                                                Thread.sleep(1000);
                                                if (finish(thirdPriority, thirdPriority.headProcess(), c,
                                                        realProcess)) {
                                                    break;
                                                }
                                                suspend(realtime, thirdPriority.headProcess(), c);
                                                suspend(firstPriority, thirdPriority.headProcess(), c);
                                                suspend(secondPriority, thirdPriority.headProcess(), c);
                                            }
                                            break;
                                    }
                                    break;
                            }
                            break;
                    }
                    break;

                default:
                    break;
            }
            if (isReady(realtime, c) == 0 && isReady(firstPriority, c) == 0 && isReady(secondPriority, c) == 0
                    && isReady(thirdPriority, c) == 0) {
                c++;
            }
            if (realtime.length + firstPriority.length + secondPriority.length + thirdPriority.length == 0) {
                terminate = true;
            }
        }
    }
}
