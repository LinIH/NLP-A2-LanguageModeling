package ce7024.hw2v2.s103502004;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class LanguageModeling {
																					/************************************/
	static int V = 80000;															/****	V						*****/
	static int k = 10;																/****	k						*****/											
																					/************************************/
	static int N_u;																	/****	Nu						*****/
	static int N_b;																	/****	Nb						*****/
	static int[] N_u_c = new int[10];												/****	Nu_c					*****/
	static int[] N_b_c = new int[10];												/****	Nb_c					*****/
	static int N_u_0;																/****	Nu_0					*****/
	static long N_b_0;																/****	Nb_0					*****/
																					/************************************/
	static ArrayList<String> target_U = new ArrayList<String>();					/****	uni_string				*****/
	static ArrayList<String> target_B = new ArrayList<String>();					/****	bi_string				*****/
	static ArrayList<Integer> target_U_count = new ArrayList<Integer>();			/****	count(wi)				*****/
	static ArrayList<Integer> target_B_count = new ArrayList<Integer>();			/****	count(wi,wi+1)			*****/
	static ArrayList<Integer> target_B_N_count = new ArrayList<Integer>();			/****	N [count(wi,wi+1)]		*****/
	static ArrayList<Integer> target_B_N_count_addone = new ArrayList<Integer>();	/****	N [count(wi,wi+1)+1]	*****/
	static ArrayList<Double> target_U_N_count_Star = new ArrayList<Double>();		/****	count*(wi)			*****/
	static ArrayList<Double> target_B_N_count_Star = new ArrayList<Double>();		/****	count*(wi,wi+1)			*****/
	static ArrayList<Double> U_Prob = new ArrayList<Double>();						/****	P (wi)					*****/
	static ArrayList<Double> B_Prob = new ArrayList<Double>();						/****	P (wi,wi+1)				*****/
	static ArrayList<Double> B_Prob_con = new ArrayList<Double>();						/****	P_con(wi,wi+1)			*****/
																					/************************************/

	
	static Map<String, Integer> uni_count = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
	static Map<String, Integer> bi_count = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
	
	public static void LoadData() throws FileNotFoundException{
		Scanner fin = new Scanner(new File("dataset.txt"));
		String temp_str;
		String[] tokens;
		int cnt;
		
		while(fin.hasNextLine()){
			temp_str = fin.nextLine();
			tokens = temp_str.split(" ");
			for (int i=0; i<tokens.length; i++){
				if(uni_count.containsKey(tokens[i])){
					cnt = uni_count.get(tokens[i]);
					uni_count.put(tokens[i], cnt+1);
				}
				else{
					uni_count.put(tokens[i], 1);
				}
				
				if(i<tokens.length-1){
					if(bi_count.containsKey(tokens[i]+" "+tokens[i+1])){
						cnt = bi_count.get(tokens[i]+" "+tokens[i+1]);
						bi_count.put(tokens[i]+" "+tokens[i+1], cnt+1);
					}
					else{
						bi_count.put(tokens[i]+" "+tokens[i+1], 1);
					}
				}
			}
		}
		
		for(int i:uni_count.values()){
			N_u+=i;
			if(i<10){
				N_u_c[i]++;
			}
		}
		for(int i:bi_count.values()){
			N_b+=i;
			if(i<10){
				N_b_c[i]++;
			}
		}
		N_u_0 = V - uni_count.size();
		N_b_0 = (long) V*V - bi_count.size();
		
		fin.close();
	}
	
	public static void Smoothing(){
		double temp = 0;

		for(int i=0; i<target_U_count.size(); i++){
			if(target_U_count.get(i)==0){
				temp = 0;
			}
			else if(target_U_count.get(i)<k){
				temp = target_U_count.get(i);
			}
			else{
				temp = target_U_count.get(i);
			}
			target_U_N_count_Star.add(temp);
		}
		
		
		
		for(int i=0; i<target_B_count.size(); i++){
			if(target_B_count.get(i)==0){
				temp = 0;
			}
			else if(target_B_count.get(i)<k){
				temp = div(mul(add(target_B_count.get(i), 1), target_B_N_count_addone.get(i)), target_B_N_count.get(i));
			}
			else{
				temp = target_B_count.get(i);
			}
			target_B_N_count_Star.add(temp);
		}
		
	}
	public static double add(double v1,double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}
	public static double mul(double v1,double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}
	public static double div(double v1,double v2) {
		int scale = 15;
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub

		LoadData();
		
		Scanner sysin = new Scanner(System.in);
		String input_str = new String();
		
		System.out.print("Input sentence: ");
		input_str = sysin.nextLine();
		
		String[] tokens = input_str.split(" ");
		String bi_temp = new String();
		for (int i=0; i<tokens.length; i++){
			if(!target_U.contains(tokens[i])){
				target_U.add(tokens[i]);
				if(uni_count.containsKey(tokens[i])){
					target_U_count.add(uni_count.get(tokens[i]));
				}
				else{
					target_U_count.add(0);
				}
			}
			
			if(i<tokens.length-1){
				bi_temp = tokens[i] + " " + tokens[i+1];
				if(!target_B.contains(bi_temp)){
					target_B.add(bi_temp);
					target_B_N_count.add(0);
					target_B_N_count_addone.add(0);
					if(bi_count.containsKey(bi_temp)){
						target_B_count.add(bi_count.get(bi_temp));
					}
					else{
						target_B_count.add(0);
					}
				}
			}
		}
		int ptr;
		int sum;
		/****N [count(wi,wi+1)]*****/
		for(int i=0; i<target_B.size(); i++){
			ptr = target_B_count.get(i);
			sum = 0;
			for(int value:bi_count.values()){
				if(ptr == value){
					sum++;
				}
			}
			target_B_N_count.add(i, sum);
		}
		/****N [count(wi,wi+1)+1]*****/
		for(int i=0; i<target_B.size(); i++){
			ptr = target_B_count.get(i)+1;
			sum = 0;
			for(int value:bi_count.values()){
				if(ptr == value){
					sum++;
				}
			}
			target_B_N_count_addone.add(i, sum);
		}
		
		Smoothing();
		
		for(int i=0; i<target_U.size(); i++){
			if(target_U_count.get(i)==0){
				U_Prob.add(div(N_u_c[1], mul(N_u_0, N_u)));
			}
			else{
				U_Prob.add(div(target_U_N_count_Star.get(i), N_u));
			}
		}
		for(int i=0; i<target_B.size(); i++){
			if(target_B_count.get(i)==0){
				B_Prob.add(div(N_b_c[1], mul(N_b_0, N_b)));
			}
			else{
				B_Prob.add(div(target_B_N_count_Star.get(i), N_b));
			}
		}
		for(int i=0; i<target_B.size(); i++){
			B_Prob_con.add(div(B_Prob.get(i), U_Prob.get(i)));
			
		}
		
		double ans = 1;
		for(int i=0; i<target_U.size()-1; i++){
			if(i==0){
				ans = mul(U_Prob.get(i), B_Prob_con.get(i));
			}
			else{
				ans = mul(ans, B_Prob_con.get(i));
			}
		}
		
		System.out.println("P*('" + input_str + "') : " + ans);
		
		
		sysin.close();
	}

}
