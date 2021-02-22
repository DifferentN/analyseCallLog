package test;

import java.util.*;

public class TestMatchCallTree {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashSet<String> hashSet = new HashSet<>();
		Iterator<String> iterator = hashSet.iterator();
		while(iterator.hasNext()){
			String s = iterator.next();
		}
		ArrayList<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		List<Integer> list2 = list.subList(1,2);
//		list2.remove(0);
		System.out.println(list2.size());
		ArrayList<Integer> list3 = (ArrayList<Integer>) list.clone();
		list3.add(10);
		System.out.println(list.get(list.size()-1));
		int as[] = new int[]{1,2,3,4,5};
		int as1[] = Arrays.copyOfRange(as,0,4);
		as[0] = 2;
		System.out.println(as1[0]);
	}

}
