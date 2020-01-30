
import java.util.Arrays;
import java.util.Comparator;

/**
 * @auther Summerday
 */
public class ArrList {
    //记录元素个数
    private int size;

    //存储元素
    private String[] data;

    private int initialCapacity = -1;


    ArrList() {
        this.data = new String[10];
    }

    ArrList(int initialCapacity) {

        //校验容量是否合理
        if (initialCapacity < 0)
            throw new IllegalArgumentException();
        this.initialCapacity = initialCapacity;
        this.data = new String[initialCapacity];
    }

    //添加元素
    public void add(String s) {
        //判断是否需要扩容
        if (size >= data.length) {
            grow();
        }
        data[size++] = s;
    }

    //指定位置添加元素
    public void add(int index, String s) {
        //index==aize不算越界
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException("Index:" + index + ",Size: " + size);
        if (size >= data.length) {
            grow();
        }
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = s;
        size++;

    }

    //清空集合
    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        data = new String[initialCapacity == -1 ? 10 : initialCapacity];
        size = 0;

    }

    //判断是否包含指定的元素
    public boolean contains(String s) {
        return this.indexOf(s) != -1;
    }

    //好多都需要判断越界，所以写个判断方法
    private void outOfBounds(int index) {
        if (index < 0 || index < 0)
            throw new IndexOutOfBoundsException("Index:" + index + ",size:" + size);

    }

    //获取指定下标的元素
    public String get(int index) {
        this.outOfBounds(index);
        return data[index];
    }

    //获取指定元素第一次出现的下标
    public int indexOf(String s) {
        if (s == null) {
            for (int i = 0; i < size; i++) {
                if (data[i] == null) return i;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (s.equals(data[i])) return i;
            }
        }
        return -1;
    }

    //获取指定元素最后一次出现的下标
    public int lastIndexOf(String s) {
        if (s == null) {
            for (int i = size - 1; i >= 0; i--) {
                if (data[i] == null) return i;
            }
        } else {
            for (int i = size - 1; i >= 0; i--) {
                if (s.equals(data[i])) return i;
            }
        }
        return -1;
    }

    //移除指定索引的元素
    public void remove(int index) {
        this.outOfBounds(index);
        //从index+1整体向前
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        data[--size] = null;
    }

    //移除指定元素
    public void remove(String s) {
        //找到元素
        int index = indexOf(s);
        if (index != -1)
            this.remove(index);
    }

    //替换指定位置上的元素
    public void set(int index, String s) {
        this.outOfBounds(index);
        data[index] = s;
    }
    //元素数量
    public int size() {
        return size;
    }

    //进行排序
    public void sort(Comparator<String> c) {
        //冒泡排序
        for (int i = 1; i < size; i++) {
            for (int j = 1; j <= size - i; j++) {
                if (c.compare(data[j - 1], data[j]) > 0) {
                    String temp = data[j - 1];
                    data[j - 1] = data[j];
                    data[j] = temp;
                }
            }
        }

    }

    //截取子列表
    public ArrList subListTraversal(int fromIndex, int toIndex) {
        //下标校验
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex)
            throw new IllegalArgumentException();
        //1. 遍历复制
        ArrList sub = new ArrList();
        for (int i = fromIndex; i < toIndex; i++) sub.add(data[i]);

        return sub;
    }

    public ArrList subListCopyArray(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex)
            throw new IllegalArgumentException();
        //2.数组复制
        int newLen = toIndex - fromIndex;
        ArrList sub = new ArrList(newLen);
        sub.size = newLen;
        System.arraycopy(data, fromIndex, sub.data, 0, sub.size);
        return sub;
    }

    //重写toString方法
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        if (isEmpty()) return "[]";
        for (int i = 0; i < size; i++) {
            sb.append(data[i]).append(", ");
        }
        String s = sb.toString();
        s = s.substring(0, s.length()-2);
        return s += "]";

    }
    //判断是否为空
    public boolean isEmpty() {
        return size == 0;
    }
    //扩容
    private void grow() {
        //this.data = Arrays.copyOf(data, initialSize + (initialSize >> 1));
        int newCapacitiy = data.length < 2 ? data.length + 1 : data.length + (data.length >> 1);
        //越界
        if (newCapacitiy < data.length)
            newCapacitiy = data.length;
        this.data = Arrays.copyOf(data, newCapacitiy);
    }

    //将集合转化为数组
    public String[] toArray() {
        return Arrays.copyOf(data, size);
    }

    //将集合容量变为当前元素个数
    public void trimToSize() {
        this.data = Arrays.copyOf(data, size);
    }
}


