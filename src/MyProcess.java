public class MyProcess {
  public int process_id;
  public int processNumber;
  public int varisZamani;
  public int oncelik;
  public int prosessZamani;
  public int calismazamani;
  public Boolean running;
  public String renkler;
  public Boolean isReady;

  public MyProcess(int arrTime, int prio, int bt, int id, int zaman, String renk, Boolean rn, Boolean hz) {
    prosessZamani = bt;
    varisZamani = arrTime;
    oncelik = prio;
    process_id = id;
    calismazamani = zaman;
    renkler = renk;
    running = rn;
    isReady = hz;
  }
}
