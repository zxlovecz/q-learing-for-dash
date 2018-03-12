package com.wspn.jetty;

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class suanfa {

	public static void main(String[] args) {
		int[] a = {1, 2, 4, 5, 7, 4, 5 ,3 ,9 ,0};  
        System.out.println(Arrays.toString(a));  
        sort(a,0,a.length-1);  
        System.out.println(Arrays.toString(a));  

	}

	static void sort(int [] p,int low,int high) {
		if(low>high)
			return;
		int key=p[low];
		int i=low;
		int j=high;
		
	
		
		while(i<j) {
			while(i<j&&p[j]>key) {
				j--;
			}
			while(i<j&&p[i]<=key) {
				i++;
			}
			if(i<j) {
				int t=p[i];
				p[i]=p[j];
				p[j]=t;
			}
		}
		int tmp=p[i];
		p[i]=p[low];
		p[low]=tmp;
		
		sort(p, low, i-1);
		sort(p, i+1, high);
	}
}
