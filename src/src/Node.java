public class Node 
{
    public MyProcess proc;
    public Node next_node;
    public Node(MyProcess proc) 
    {
        this.next_node = null;
        this.proc = proc;
    }
}
