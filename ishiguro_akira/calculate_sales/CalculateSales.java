package jp.co.plusize.ishiguro_akira.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CalculateSales {

//１－①②＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public static void main(String[] args) {
		 //System.out.println(args[0]);
		 HashMap<String, String> branchmap = new HashMap<String,String>();
		 HashMap<String,Long>branchSaledmap = new HashMap<String,Long>();
		 HashMap<String,Long>commoditySaledmap = new HashMap<String,Long>();

		 BufferedReader br =null;

		 try{
			 if (args.length != 1){
			      System.out.println("予期せぬエラーが発生しました");
			      return;
			 }

			 File file = new File(args[0], "branch.lst");
			 if(!(file.exists())){
				 System.out.println("支店定義ファイルが存在しません");
				 return;
			 }

			 FileReader fr = new FileReader(file);
			 br = new BufferedReader(fr);
			 String s;

			 while((s = br.readLine()) != null){
				 String[] column = s.split(",");
				 String regex = "\\d{3}";

				 if((column.length !=2)&&(column[0].matches(regex)) ){
					 System.out.println("支店定義ファイルのフォーマットが不正です");
					 return;
				 }

				 branchmap.put(column[0], column[1]);
				 branchSaledmap.put(column[0],(long) 0);
			 }

		 }catch(FileNotFoundException e){
			 System.out.println("予期せぬエラーが発生しました");
			 return;

		 } catch(Exception ce) {
			 System.out.println("予期せぬエラーが発生しました");
			 return;
		 }finally{
			 if(br != null) {
				 try {
					 br.close();
				} catch (IOException e) {
					 System.out.println("予期せぬエラーが発生しました");
					 return;
				}
			 }
		 }


//２－①②＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
		 HashMap<String, String> commoditymap = new HashMap<String,String>();
		 try{
			 File file = new File(args[0], "commodity.lst");

			 if (!(file.exists())){
				 System.out.println("商品定義ファイルが存在しません");
				 return;
			 }

			 FileReader fr = new FileReader(file);
			 br = new BufferedReader(fr);
			 String s;

			 while((s=br.readLine())!= null){
				 String[] column2 = s.split(",");
				 String number2 = column2[0];
				 String name2 = column2[1];
				 String regex ="^[a-zA-Z0-9]{8}$";

				 if(!(number2.matches(regex))){
					 System.out.println("商品定義ファイルのフォーマットが不正です");
					 return;
				 }

				 if(column2.length != 2){
					 System.out.println("商品定義ファイルのフォーマットが不正です");
					 return;
				 }

				 commoditymap.put(number2, name2);
				 commoditySaledmap.put(number2,(long) 0);
			 }

		 } catch(FileNotFoundException e){
			 System.out.println("予期せぬエラーが発生しました");
			 return;

		 } catch(Exception e) {
			 System.out.println("予期せぬエラーが発生しました");
			 return;

		 }finally{
			 if(br != null) {
				 try {
					 br.close();
				 } catch (IOException e) {
					 System.out.println("予期せぬエラーが発生しました");
					 return;
				 }
			 }
		 }


//３＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
		 File crd = new File(args[0]);
		 File[] files = crd.listFiles();
		 int count=1;


		 ArrayList<File> rcdlist = new ArrayList<>();

		 //拡張子が.rcd 8桁の数字のファイルの検索

		 for (int i = 0; i < files.length; i++) {
			 File file = files[i];
			 //System.out.println((i + 1) + ": " + file);
			 String regex = "\\d{8}.rcd";

			
			 if(file.getName().matches(regex)&&file.isFile()){
				 rcdlist.add(file);
			 }
		 }

		 //連番チェック


		 for(int i=0;i<rcdlist.size();i++){
			 File rcdfile = rcdlist.get(i);
			 String number = rcdfile.getName().substring(0, rcdfile.getName().indexOf("."));
			 int fileNumber = Integer.parseInt(number);

			 if (count == fileNumber) {
				 count++;

			 }else{
				 System.out.println("売上ファイル名が連番になっていません");
				 return;
			 }
		 }

		 //売上ファイルの読み込み

		 for(int i = 0; i < rcdlist.size(); i++){
			 try{
				 FileReader fr = new FileReader(rcdlist.get(i));
				 br = new BufferedReader(fr);
				 String s;
				 List<String> list = new ArrayList<String>();
				 while((s = br.readLine()) != null){
					 list.add(s);

				 }

				 File rcdfile = rcdlist.get(i);
				 String number = rcdfile.getName().substring(0, rcdfile.getName().indexOf("."));

				 if(!(list.size()==3)){
					 System.out.println(number+".rcdのフォーマットが不正です");
					 return;

				 }else if(!(branchSaledmap.containsKey((list.get(0))))){
					System.out.println(number+".rcdの支店コードが不正です");
					return;

				 }else if(!(commoditySaledmap.containsKey((list.get(1))))){
					System.out.println(number+".rcdの商品コードが不正です");
					return;
				 }

				long conversion = Long.parseLong(list.get(2));
				long sales = branchSaledmap.get(list.get(0))+conversion;
				branchSaledmap.put(list.get(0), sales);

				long sales2 = commoditySaledmap.get(list.get(1))+conversion;
				commoditySaledmap.put(list.get(1),sales2);

				long val = 9999999999l;
				if(sales>val||sales2>val){
					System.out.println("合計金額が10桁を超えました");
					return;
				}

//４＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
			 } catch(FileNotFoundException e){
				 System.out.println("予期せぬエラーが発生しました");
				 return;

			 } catch(Exception e) {
				 System.out.println("予期せぬエラーが発生しました");
				 return;

			 }finally{
				 if(br != null)
					 try {
						 br.close();
					 } catch (IOException e) {
						 System.out.println("予期せぬエラーが発生しました");
						 return;
					 }
			 }
		 }
			List<HashMap.Entry<String,Long>> branchSale =
					new ArrayList<HashMap.Entry<String,Long>>(branchSaledmap.entrySet());
			Collections.sort(branchSale, new Comparator<Map.Entry<String,Long>>() {
				@Override
				public int compare(
						Entry<String,Long> entry1, Entry<String,Long> entry2) {
							return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
							}
				});


			List<HashMap.Entry<String,Long>> commoditySale =
					new ArrayList<HashMap.Entry<String,Long>>(commoditySaledmap.entrySet());
			Collections.sort(commoditySale, new Comparator<Map.Entry<String,Long>>() {
				@Override
				public int compare(
						Entry<String,Long> entry1, Entry<String,Long> entry2) {
							return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
							}
				});

			BufferedWriter bw = null;


			try{
				File file = new File(args[0], "branch.out");
				FileWriter fw = new FileWriter(file);
				bw = new BufferedWriter(fw);

				for (Entry<String,Long> s : branchSale) {
					bw.write(s.getKey() + "," + branchmap.get(s.getKey())+"," + s.getValue() + System.lineSeparator());
				}


			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				 if(bw != null)
					 try {
						 bw.close();
					 } catch (IOException e) {
						 System.out.println("予期せぬエラーが発生しました");
						 return;
					 }
			}
			try{
				File file = new File(args[0],"commodity.out");
				FileWriter fw = new FileWriter(file);
				bw = new BufferedWriter(fw);

				for(Entry<String,Long> s : commoditySale) {
					bw.write(s.getKey() + ","+commoditymap.get(s.getKey())+"," + s.getValue() + System.lineSeparator());
				}
				bw.close();

			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}finally{
				 if(bw != null)
					 try {
						 bw.close();
					 } catch (IOException e) {
						 System.out.println("予期せぬエラーが発生しました");
						 return;
					 }
			}
	}
}





