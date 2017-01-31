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
	public static void main(String[] args) {
		//System.out.println(args[0]);
		HashMap<String,Long>branchSaledmap = new HashMap<String,Long>();
		HashMap<String,Long>commoditySaledmap = new HashMap<String,Long>();

		BufferedReader br = null;

		if (args.length != 1) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		//１＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
		HashMap<String, String> branchmap = new HashMap<String,String>();
		// 支店定義ファイルの読み込み
		String readFileNameBranch = args[0] + File.separator + "branch.lst";
		String definitionBranch = "支店定義";
		String branchRegex = "\\d{3}$";
		if(!readFile(readFileNameBranch, definitionBranch, br, branchRegex, branchmap, branchSaledmap)){
			return;
		}
		//２＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
		HashMap<String, String> commoditymap = new HashMap<String,String>();
		// 商品定義ファイルの読み込み
		String readFileNameCommodity= args[0] + File.separator + "commodity.lst";
		String definitionCommodity = "商品定義";
		String commodityRegex = "^[0-9a-zA-Z]{8}$";
		if(!readFile(readFileNameCommodity, definitionCommodity, br, commodityRegex, commoditymap, commoditySaledmap)){
			return;
		}

		//３＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
		//ファイル検索
		ArrayList<File> rcdlist = new ArrayList<>();
		File rcd = new File(args[0]);
		String fileRegex = "\\d{8}.rcd";
		if(!findFile(rcd.listFiles(), fileRegex, rcdlist)){
			return;
		}
		//連番チェック
		int count = 1;
		if(!numberCheck(rcdlist, count)){
			return;
		}
		//売上げファイルの読み込み
		if(!readFileSales(rcdlist, br, branchSaledmap, commoditySaledmap)){
			return;
		}

		//４＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
		// branch.outの出力
		String outFileNameBranch = args[0] + File.separator + "branch.out";
		if(!outputFile(outFileNameBranch, branchmap, branchSaledmap)){
			return;
		}
		// commodity.outの出力
		String outFileNameCommodity = args[0] + File.separator + "commodity.out";
		if(!outputFile(outFileNameCommodity, commoditymap, commoditySaledmap)){
			return;
		}
	}
//＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	// ファイルの読み込み
	private static boolean readFile(String fileName, String definition, BufferedReader br, String regex, HashMap<String, String> nameMap, HashMap<String, Long> saleMap) {
		try{
			File file = new File(fileName);
			if(!(file.exists())) {
				System.out.println(definition + "ファイルが存在しません");
				return false;
			}

			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);
			String s;

			while((s = br.readLine()) != null) {
				String[] column = s.split(",");

				if( (column.length != 2) || (!(column[0].matches(regex))) ) {
					System.out.println(definition + "ファイルのフォーマットが不正です");
					return false;
				}

				nameMap.put(column[0], column[1]);
				saleMap.put(column[0],(long) 0);
			}

		} catch(FileNotFoundException e) {
			System.out.println("予期せぬエラーが発生しました");
			return false;

		} catch(Exception ce) {
			System.out.println("予期せぬエラーが発生しました");
			return false;
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return false;
				}
			}
		}return true;
	}

	//拡張子が.rcd 8桁の数字のファイルの検索メソッド
	private static boolean findFile(File[] rcdFile, String regex, ArrayList<File> rcdlist) {
		File[] files = rcdFile;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if(file.getName().matches(regex)&&file.isFile()) {
				rcdlist.add(file);
				}
			}return true;
	}

	//連番チェックメソッド
	private static boolean numberCheck(ArrayList<File> rcdlist, int count) {
		for(int i = 0; i <rcdlist.size(); i++) {
			File rcdfile = rcdlist.get(i);
			String number = rcdfile.getName().substring(0, rcdfile.getName().indexOf("."));
			int fileNumber = Integer.parseInt(number);
			if (count == fileNumber) {
				count++;
				} else {
					System.out.println("売上ファイル名が連番になっていません");
					return false;
				}
			}return true;
	}

	//売上ファイルの読み込みメソッド
	private static boolean readFileSales(ArrayList<File> rcdlist, BufferedReader br, HashMap<String, Long> branch, HashMap<String, Long> commodity) {
		for(int i = 0; i < rcdlist.size(); i++) {
			try {
				FileReader fr = new FileReader(rcdlist.get(i));
				br = new BufferedReader(fr);
				String s;
				List<String> list = new ArrayList<String>();
				while((s = br.readLine()) != null) {
					list.add(s);
				}

				File rcdfile = rcdlist.get(i);
				String number = rcdfile.getName().substring(0, rcdfile.getName().indexOf("."));


				if(!(list.size()==3 )) {
					System.out.println(number+".rcdのフォーマットが不正です");
					return false;

				}else if(!(branch.containsKey((list.get(0))))){
					System.out.println(number+".rcdの支店コードが不正です");
					return false;


				}else if(!(commodity.containsKey((list.get(1))))){
					System.out.println(number+".rcdの商品コードが不正です");
					return false;

				}

				long conversion = Long.parseLong(list.get(2));
				long sales = branch.get(list.get(0))+ conversion;
				branch.put(list.get(0), sales);

				long sales2 = commodity.get(list.get(1))+ conversion;
				commodity.put(list.get(1),sales2);
				long val = 9999999999l;
				if(sales>val||sales2>val){
					System.out.println("合計金額が10桁を超えました");
					return false;

				}

			} catch(FileNotFoundException e){
				System.out.println("予期せぬエラーが発生しました");
				return false;
			} catch(Exception e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			} finally {
				if(br != null)
					try {
						br.close();
					} catch (IOException e) {
						System.out.println("予期せぬエラーが発生しました");
						return false;
					}
			}
		}return true;
		}


	// ファイル出力メソッド
	private static boolean outputFile(String filaName, HashMap<String, String> nameMap ,  HashMap<String, Long> saleMap ) {

		List<HashMap.Entry<String,Long>> saleList =
				new ArrayList<HashMap.Entry<String,Long>>(saleMap.entrySet());
		Collections.sort(saleList, new Comparator<Map.Entry<String,Long>>() {
			@Override
			public int compare(
					Entry<String,Long> entry1, Entry<String,Long> entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			}
		});

		BufferedWriter bw = null;
		try{
			File file = new File( filaName);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			for(Entry<String,Long> s : saleList) {
				bw.write(s.getKey() + ","+ nameMap.get(s.getKey())+"," + s.getValue() + System.lineSeparator());
			}
			bw.close();

		}catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			if(bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return false;
				}
		}
		return true;
	}
}