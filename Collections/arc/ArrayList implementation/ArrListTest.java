package leetcode.linkedlist.designLinkedlist707;

class MyLinkedList {
    int size = 0;
    Node first;
    Node last;
    /* determine the scope of delete operation*/
    public void elementRangeCheck(int index) {
        if(index <0 || index >= size)
            throw new IndexOutOfBoundsException();
    }
    /* determine the scope of add operation*/
    public void positionRangeCheck(int index) {
        if(index < 0 && index > size)
            throw new IndexOutOfBoundsException();
    }

    /**
     * Initialize your data structure here.
     */
    public MyLinkedList() {

    }

    /**
     * get node by specified index
     */
    private Node getNode(int index) {
        if (index > (size >> 1)) {
            Node temp = last;
            for (int i = size - 1; i > index; i--) {
                temp = temp.prev;
            }
            return temp;
        } else {
            Node temp = first;
            for (int i = 0; i < index; i++) {
                temp = temp.next;
            }
            return temp;
        }
    }

    /**
     * Get the value of the index-th node in the linked list. If the index is invalid, return -1.
     */
    public int get(int index) {
        try{
            elementRangeCheck(index);
        }catch (IndexOutOfBoundsException e){
            return -1;
        }
        return (Integer) getNode(index).item;
    }

    /**
     * Add a node of value val before the first element of the linked list. After the insertion, the new node will be the first node of the linked list.
     */
    public void addAtHead(int val) {
        final Node f = first;
        final Node newNode = new Node(null, val, f);
        if (f == null)
            last = newNode;
        else
            f.prev = newNode;
        first = newNode;
        size++;
    }

    /**
     * Append a node of value val to the last element of the linked list.
     */
    public void addAtTail(int val) {
        final Node l = last;
        final Node newNode = new Node(l, val, null);
        if (l == null)
            first = newNode;
        else
            l.next = newNode;

        last = newNode;
        size++;
    }

    /**
     * Add a node of value val before the index-th node in the linked list. If index equals to the length of linked list, the node will be appended to the end of linked list. If index is greater than the length, the node will not be inserted.
     */
    public void addAtIndex(int index, int val) {
        positionRangeCheck(index);
        if (index == size) {
            addAtTail(val);
            return;
        }
        final Node currNode = getNode(index);
        final Node preNode = currNode.prev;
        final Node newNode = new Node(preNode, val, currNode);
        currNode.prev = newNode;
        if (null == preNode)
            first = newNode;
        else
            preNode.next = newNode;
        size++;
    }

    /**
     * Delete the index-th node in the linked list, if the index is valid.
     */
    public void deleteAtIndex(int index) {
        elementRangeCheck(index);
        final Node succ = getNode(index);
        final Node prev = succ.prev;
        final Node next = succ.next;
        succ.next = null;
        succ.prev = null;
        if (null == prev) {
            first = next;
        } else {
            prev.next = next;
        }

        if (null == next)
            last = prev;
        else
            next.prev = prev;
        size--;
    }

    private static class Node {
        int item;//store value;
        Node next;//point to next node;
        Node prev;//point to prev node;

        public Node(Node prev, int item, Node next) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }
}
/*Test*/
class Test{
    public static void main(String[] args) {
        MyLinkedList lk = new MyLinkedList();
        lk.addAtHead(1);
        lk.addAtTail(3);
        lk.addAtIndex(1, 2);
        System.out.println(lk.get(1));
        lk.deleteAtIndex(1);
        System.out.println(lk.get(1));
    }

}
/**
 * Your MyLinkedList object will be instantiated and called as such:
 * MyLinkedList obj = new MyLinkedList();
 * int param_1 = obj.get(index);
 * obj.addAtHead(val);
 * obj.addAtTail(val);
 * obj.addAtIndex(index,val);
 * obj.deleteAtIndex(index);
 */

