public class Queue {
	public Node head;
	public Node last;
	public int length;

	public Queue() {
		head = last = null;
	}

	public MyProcess getProc(int x) {
		Node gec = head;
		for (int i = 0; i < x; i++) {
			gec = gec.next_node;
		}
		return gec.proc;
	}

	public void push(MyProcess proc) {
		Node newNode = new Node(proc);
		if (head == null) {
			head = newNode;
			last = head;
		} else {
			last.next_node = newNode;
			last = newNode;
		}
		length++;
	}

	public MyProcess pop() {
		Node willbeDeleted = head;
		if (head != null) {
			Node temp = head.next_node;
			head = temp;
			if (head == null)
				last = null;
		}
		length--;
		return willbeDeleted.proc;
	}

	public MyProcess headProcess() {
		Node w = head;
		return w.proc;
	}

	public Boolean isReady(MyProcess proc, int veri) {
		if (proc.varisZamani == veri) {
			return true;
		} else {
			return false;
		}
	}

	public void sort(int sayac) {
		Node temp;
		Node gec = head;
		MyProcess swap;
		for (int i = 0; i < sayac; i++) {
			temp = gec;
			while (temp.next_node != null) {
				if (temp.proc.varisZamani > temp.next_node.proc.varisZamani) {
					swap = temp.next_node.proc;
					temp.next_node.proc = temp.proc;
					temp.proc = swap;
					if (temp == head) {
						head = temp;
					}
				}

				temp = temp.next_node;
			}
		}
	}
}
